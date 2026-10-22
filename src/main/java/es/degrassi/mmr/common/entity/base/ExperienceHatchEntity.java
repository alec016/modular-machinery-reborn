package es.degrassi.mmr.common.entity.base;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.experiencelib.transfer.SimpleExperienceHandler;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.CapabilityInventoryEntity;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.ExperienceComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateExperienceComponentPacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public abstract class ExperienceHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<ExperienceComponent>,
    ControllerAccessible, TextureableMachineEntity, CapabilityInventoryEntity<IExperienceHandler>, ITickEntity, IServerTickEntity, ISyncableStuff,
    IAutoEntity<IExperienceHandler>, ISideConfigComponent<IOSideMode> {
  protected ExperienceHatchSize size;
  protected IOType ioType;
  @Getter
  @Nullable private BlockPos controllerPos;

  private final SimpleExperienceHandler experienceTank;

  @Getter
  @Setter
  private Identifier baseTexture;
  @Getter
  @Setter
  private Identifier overlayTexture;
  @Getter
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  @Getter
  private final ItemHandler capabilityInventory;

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;

  @Getter
  private final Map<Direction, BlockCapabilityCache<IExperienceHandler, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  @Getter
  private final IOSideConfig config;

  protected ExperienceHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ExperienceHatchSize size,
                          IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.ioType = ioType;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_experience" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.experienceTank = buildTank();
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.capabilityInventory = this.createCapabilityInventory();
    this.config.setCallback(this::configChanged);
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    tickInventory();
  }

  @Override
  public IOType getMode() {
    return ioType;
  }

  public boolean shouldTickInventory() {
    long gameTime = getLevel().getGameTime();
    if (!Utils.shouldRunPeriodicCheck(false, gameTime, lastCheckTick, tickOffset, 2))
      return false;
    lastCheckTick = gameTime;
    return true;
  }

  @Override
  public void tickInventory() {
    if (!shouldTickInventory()) return;
    try (Transaction root = Transaction.openRoot()) {
      capabilityInventory.forEach((stack, slot) -> {
        if (stack.isEmpty()) return;
        var cap = stack.getCapability(getCapability(), ItemAccess.forStack(stack));
        if (cap == null) return;
        if (ioType == IOType.NONE) return;
        if (ioType.isInput()) {
          if (this.getTank().getAmountAsLong(0) >= this.getTank().getCapacityAsLong(0, ExperienceResource.EMPTY))
            return;
          if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            int experienceBottles = stack.getCount();
            for (int i = 0; i < experienceBottles; i++) {
              try (Transaction subTx = Transaction.open(root)) {
                long simulatedCap = cap.extractRecipe(0, Long.MAX_VALUE, subTx);
                long simulatedInsert = getTank().insertRecipe(0, simulatedCap, subTx);
                if (simulatedInsert < 7) return;
                if (capabilityInventory.extractFromInputs(stack, 1) <= 0) return;
                subTx.commit();
              }
            }
          } else {
            try (Transaction subTx = Transaction.open(root)) {
              if (stack.is(Items.GLASS_BOTTLE)) return;
              long simulatedCap = cap.extractRecipe(0, Long.MAX_VALUE, subTx);
              long simulatedInsert = getTank().insertRecipe(0, simulatedCap, subTx);
              if (simulatedInsert > 0) subTx.commit();
            }
          }
        } else if (ioType.isOutput()) {
          if (this.getTank().getAmountAsLong(0) == 0) return;
          if (stack.is(Items.GLASS_BOTTLE)) {
            try (Transaction subTx = Transaction.open(root)) {
              int experienceBottles = stack.getCount();
              if (experienceBottles > 1) return;
              long simulatedCap = cap.insertRecipe(0, Long.MAX_VALUE, subTx);
              long simulatedInsert = getTank().extractRecipe(0, simulatedCap, subTx);
              if (simulatedInsert < 7) return;
              if (!capabilityInventory.canPlaceOutput(slot, new ItemStack(Items.EXPERIENCE_BOTTLE, 1))) return;
              if (capabilityInventory.extractFromInputs(stack, 1) <= 0) return;
              capabilityInventory.addToOutputs(new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 1);
              subTx.commit();
            }
          } else {
            try (Transaction subTx = Transaction.open(root)) {
              if (stack.is(Items.EXPERIENCE_BOTTLE)) return;
              long simulatedCap = cap.insertRecipe(0, Long.MAX_VALUE, subTx);
              long simulatedInsert = getTank().extractRecipe(0, simulatedCap, subTx);
              if (simulatedInsert > 0) subTx.commit();
            }
          }
        }
      });
      root.commit();
    }
  }

  @Override
  public ItemCapability<IExperienceHandler, ItemAccess> getCapability() {
    return ExperienceLibCapabilities.EXPERIENCE.item();
  }

  public SimpleExperienceHandler getTank() {
    return experienceTank;
  }

  @Nullable
  @Override
  public ExperienceComponent provideComponent() {
    return new ExperienceComponent(this.getTank(), ioType);
  }

  private SimpleExperienceHandler buildTank() {
    return new SimpleExperienceHandler(
        size == null ? 0 : size.getCapacity(),
        ioType == null || !ioType.isInput() ? size.getCapacity() : 0,
        ioType == null ||ioType.isInput() ? size.getCapacity() : 0
    ) {
      @Override
      public void onExperienceChange(long prevAmount) {
        super.onExperienceChange(prevAmount);
        getControllerPosSet().forEach(p -> {
          if (getLevel() == null) return;
          if (getLevel().isClientSide()) return;
          if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
            controller.getProcessor().setMachineInventoryChanged();
          }
        });
        // Live UI sync: every server-side change (manual XP buttons, bottle conversion, operator
        // fill, auto-IO) sends the experience sync packet, the same dedicated packet every other
        // component keeps (the block-entity update tag alone does not carry the tank amount, so a
        // bare markForUpdate left the UI bar stale until the next world load).
        if (getLevel() instanceof ServerLevel level && prevAmount != this.amount) {
          PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos.containing(getBlockPos()),
              new SUpdateExperienceComponentPacket(this.amount, getBlockPos()));
        }
      }
    };
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.size = ExperienceHatchSize.value(input.getString("hatchSize").orElseThrow().toUpperCase(Locale.ROOT));
    this.ioType = IOType.getByString(input.getString("ioType").orElseThrow());

    experienceTank.deserialize(input.childOrEmpty("experience"));

    for (int i = 0; i < experienceTank.size(); i++) {
      experienceTank.setCapacity(i, size.getCapacity());
    }
    input.getLong("controllerPos").ifPresent(value -> this.controllerPos = BlockPos.of(value));
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_experience" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    this.capabilityInventory.deserialize(input.childOrEmpty("inventory"));
    this.config.deserialize(input.childOrEmpty("config"));
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    output.putString("hatchSize", this.size.getSerializedName());
    if (ioType == null) {
      ioType = this instanceof ExperienceInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    output.putString("ioType", ioType.getSerializedName());

    output.putChild("experience", experienceTank);
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
    output.putChild("inventory", this.capabilityInventory);
    output.putChild("config", this.config);
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("all").build();
  }

  @Override
  public HatchTextureData getTextureData(String mode) {
    return MachineComponentEntity.super.getTextureData(mode).derive(
        "bg_all",
        baseTexture,
        defaultBaseTexture,
        "ov_all",
        overlayTexture,
        defaultOverlayTexture,
        false
    );
  }

  @Override
  public Identifier getMachineBaseTexture() {
    return baseTexture;
  }

  @Override
  public Identifier getMachineOverlayTexture() {
    return overlayTexture;
  }

  @Override
  public void setMachineBaseTexture(Identifier newTexture) {
    setChanged();
    this.baseTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
          new SUpdateMachineTexturePacket(baseTexture, true, getBlockPos()));
    }
  }

  @Override
  public void setMachineOverlayTexture(Identifier newTexture) {
    setChanged();
    this.overlayTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
          new SUpdateMachineTexturePacket(overlayTexture, false, getBlockPos()));
    }
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public MachineHatchType getHatchType() {
    return switch(ioType) {
      case INPUT -> (switch (size) {
        case TINY -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS;
        case VACUUM -> MachineHatchTypeRegistration.EXPERIENCE_INPUT_HATCH_VACUUM;
      }).get();
      case OUTPUT -> (switch(size) {
        case TINY -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS;
        case VACUUM -> MachineHatchTypeRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM;
      }).get();
      default -> null;
    };
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    container.sync(IOSideConfig.class, this::getConfig, this.config::set);
    experienceTank.getStuffToSync(container);
  }

  protected void attemptXPTransfer(IExperienceHandler from, IExperienceHandler to, long maxTransfer) {
    try (Transaction root = Transaction.open(null)) {
      fromExtract:
      for (int i = 0; i < from.size(); i++) {
        if (from.getMaxExtract(i) < 1) continue;
        try (Transaction tx = Transaction.open(root)) {
          long extracted = from.extract(i, maxTransfer, tx);
          if (extracted <= 0) continue;
          for (int j = 0; j < to.size(); j++) {
            if (to.getMaxInsert(i) < 1) continue;
            try (Transaction subTx = Transaction.open(tx)) {
              long inserted = to.insert(j, extracted, subTx);
              if (inserted < 1) continue;
              subTx.commit();
              tx.commit();
              continue fromExtract;
            }
          }
        }
      }
    }
  }
}
