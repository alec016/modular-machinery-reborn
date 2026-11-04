package es.degrassi.mmr.common.entity.base;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.CapabilityInventoryEntity;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.SimpleEnergyHandler;
import es.degrassi.mmr.common.manager.handler.UpdateValueListener;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateItemComponentPacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class EnergyHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<EnergyComponent>,
    ControllerAccessible, TextureableMachineEntity, CapabilityInventoryEntity<EnergyHandler>, ITickEntity, IServerTickEntity,
    ISyncableStuff, IAutoEntity<EnergyHandler>, ISideConfigComponent<IOSideMode>, EntityMenuHolder {
  protected EnergyHatchSize size;
  protected final IOType ioType;
  @Getter
  @Nullable private BlockPos controllerPos;

  @Getter
  @Setter
  private Identifier baseTexture;
  @Getter
  @Setter
  private Identifier overlayTexture;
  @Getter
  private Identifier defaultOverlayTexture;

  @Getter
  private final ItemHandler capabilityInventory;

  private final UpdateValueListener<ItemStack> capabilityItemListener;

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;

  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  @Getter
  private final Map<Direction, BlockCapabilityCache<EnergyHandler, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  @Getter
  private final IOSideConfig config;

  @Getter
  private final SimpleEnergyHandler energyHandler;

  protected EnergyHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, EnergyHatchSize size,
                              IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.ioType = ioType;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_energy" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.capabilityInventory = createCapabilityInventory();
    this.capabilityItemListener = (slot, stack) -> {
      getControllerPosSet().forEach(p -> {
        if (getLevel() == null) return;
        if (getLevel().isClientSide()) return;
        if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
          controller.getProcessor().setMachineInventoryChanged();
        }
      });
      if (getLevel() instanceof ServerLevel level) {
        PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos.containing(getBlockPos()),
            new SUpdateItemComponentPacket(slot, capabilityInventory.getStack(slot), getBlockPos()));
      }
    };
    this.capabilityInventory.setListener(capabilityItemListener);
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);
    this.energyHandler = new SimpleEnergyHandler(
        size.maxEnergy,
        transfer(ioType.isInput(), size.transferLimit),
        transfer(ioType.isOutput(), size.transferLimit)
    );

    energyHandler.setListener(this::onContentsChange);
    invalidateCapabilities();
  }

  private static long transfer(boolean predicate, long amount) {
    return predicate ? amount : 0;
  }

  @Override
  public ItemCapability<EnergyHandler, ItemAccess> getCapability() {
    return Capabilities.Energy.ITEM;
  }

  @Override
  public IOType getMode() {
    return ioType;
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    tickInventory();
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
    int inputSlots = capabilityInventory.getInputSlotCount();
    for (int slot = 0; slot < inputSlots; slot++) {
      ItemStack stack = capabilityInventory.getStack(slot);
      if (stack.isEmpty()) continue;
      var cap = stack.getCapability(getCapability(), null);
      if (cap == null || ioType == IOType.NONE) continue;
      try(var transaction = Transaction.openRoot()) {
          if (ioType.isInput()) {
            if (!this.canReceive()) continue;
            if (this.getEnergyHandler().getAmountAsLong() >= this.getEnergyHandler().getCapacityAsLong()) continue;
            int simulatedCap;
            try (var subTx = Transaction.open(transaction)) {
              simulatedCap = cap.extract(Integer.MAX_VALUE, subTx);
            }
            var inserted = energyHandler.insert(simulatedCap, transaction);
            cap.extract(inserted, transaction);
          } else if (ioType.isOutput()) {
            if (!this.canExtract()) continue;
            if (this.energyHandler.getAmountAsLong() == 0) continue;
            int simulatedExtract;
            try (var subTx = Transaction.open(transaction)) {
              simulatedExtract = energyHandler.extract(Integer.MAX_VALUE, subTx);
            }
            int simulatedCap = cap.insert(simulatedExtract, transaction);
            energyHandler.extract(simulatedCap, transaction);
          }
          transaction.commit();
      } catch (Exception ignored) {}
      try (var check = Transaction.openRoot()) {
        boolean done = ioType.isInput() ? cap.extract(1, check) == 0 : cap.insert(1, check) == 0;
        if (done) capabilityInventory.moveToOutputs(slot);
      } catch (Exception ignored) {}
    }
  }

  @Nullable
  @Override
  public EnergyComponent provideComponent() {
    return new EnergyComponent(energyHandler, ioType);
  }

  private void onContentsChange() {
    getControllerPosSet().forEach(p -> {
      if (getLevel() == null) return;
      if (getLevel().isClientSide()) return;
      if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
        controller.getProcessor().setMachineInventoryChanged();
      }
    });
    setChanged();
    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
          new SUpdateEnergyComponentPacket(this.energyHandler.getAmountAsLong(), getBlockPos()));
  }

  public boolean canExtract() {
    return ioType != null && !ioType.isInput();
  }

  public boolean canReceive() {
    return ioType != null && ioType.isInput();
  }
  @Override
  protected void loadAdditional(ValueInput compound) {
    super.loadAdditional(compound);
    compound.readChild("energyHandler", energyHandler);
    this.size = EnergyHatchSize.value(compound.getString("hatchSize").orElseThrow().toUpperCase(Locale.ENGLISH));
    compound.getLong("controllerPos").ifPresent(val -> controllerPos = BlockPos.of(val));
    onContentsChange();
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_energy" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());

    this.baseTexture = compound.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = compound.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    compound.readChild("inventory", capabilityInventory);
    compound.readChild("config", config);
    capabilityInventory.setListener(capabilityItemListener);
  }

  @Override
  protected void saveAdditional(ValueOutput compound) {
    super.saveAdditional(compound);

    compound.putChild("energyHandler", energyHandler);
    compound.putString("ioType", ioType.getSerializedName());
    compound.putString("hatchSize", this.size.getSerializedName());
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      compound.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      compound.putString("overlayTexture", overlayTexture.toString());
    if (getLevel() != null) {
      compound.putChild("inventory", capabilityInventory);
      compound.putChild("config", this.config);
    }
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  protected void attemptFETransfer(EnergyHandler from, EnergyHandler to, long maxTransferLeft) {
    if (from.getAmountAsLong() <= 0) return;
    if (to.getAmountAsLong() == to.getCapacityAsLong()) return;
    try (var root = Transaction.open(null)) {
      for (int i : parts(maxTransferLeft)) {
        try (var subTx = Transaction.open(root)) {
          var remaining = to.getCapacityAsLong() - to.getAmountAsLong();
          var available = from.getAmountAsLong();
          var toSimulate = Ints.saturatedCast(Math.min(i, Math.min(remaining, available)));
          int simulated;
          try (var simTx = Transaction.open(subTx)) {
            simulated = from.extract(toSimulate, simTx);
          }
          if (simulated <= 0) break;
          var done = to.insert(simulated, subTx);
          from.extract(done, subTx);
          subTx.commit();
        }
      }
      root.commit();
    }
  }

  private Integer[] parts(long amount) {
    List<Integer> list = new ArrayList<>();
    do {
      int amountToInsert = Math.clamp(amount = amount - Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
      if (amountToInsert <= 0) break;
      list.add(amountToInsert);
    } while(amount > Integer.MAX_VALUE);
    return list.toArray(Integer[]::new);
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

  @Override
  public MachineHatchType getHatchType() {
    return switch(ioType) {
      case INPUT -> (switch (size) {
        case TINY -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_LUDICROUS;
        case ULTIMATE -> MachineHatchTypeRegistration.ENERGY_INPUT_HATCH_ULTIMATE;
      }).get();
      case OUTPUT -> (switch(size) {
        case TINY -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS;
        case ULTIMATE -> MachineHatchTypeRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE;
      }).get();
      default -> null;
    };
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    container.sync(IOSideConfig.class, this::getConfig, this.config::set);
  }
}
