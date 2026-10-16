package es.degrassi.mmr.common.entity.base;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
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
import es.degrassi.mmr.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.MiscUtils;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class EnergyHatchEntity extends ColorableMachineComponentEntity implements EnergyHandler,
    MachineComponentEntity<EnergyComponent>, ControllerAccessible, TextureableMachineEntity, CapabilityInventoryEntity<EnergyHandler>, ITickEntity, IServerTickEntity,
    ISyncableStuff, IAutoEntity<EnergyHandler>, ISideConfigComponent<IOSideMode> {

  protected long energy = 0;
  protected EnergyHatchSize size;
  protected IOType ioType;
  @Getter
  @Nullable private BlockPos controllerPos;

  @Setter
  private boolean canExtract = false;
  @Setter
  private boolean canInsert = false;

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

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;

  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  @Getter
  private final Map<Direction, BlockCapabilityCache<EnergyHandler, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  @Getter
  private final IOSideConfig config;

  protected EnergyHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, EnergyHatchSize size,
                              IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.ioType = ioType;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_energy" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.capabilityInventory = createCapabilityInventory();
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);
    invalidateCapabilities();
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
    capabilityInventory.forEach(stack -> {
      Optional.ofNullable(stack.getCapability(getCapability(), null)).ifPresent(cap -> {
        if (ioType == IOType.NONE) return;
        try(var transaction = Transaction.openRoot()) {
          if (ioType.isInput()) {
            if (!this.canReceive()) return;
            if (this.getCurrentEnergy() >= this.getMaxEnergy()) return;
            int simulatedCap = cap.extract(Integer.MAX_VALUE, transaction);
            int simulatedInsert = receiveEnergy(simulatedCap, true);
            receiveEnergy(simulatedInsert, false);
          } else if (ioType.isOutput()) {
            if (!this.canExtract()) return;
            if (this.getEnergyStored() == 0) return;
            int simulatedExtract = extractEnergy(Integer.MAX_VALUE, true);
            int simulatedCap = cap.insert(simulatedExtract, transaction);
            extractEnergy(simulatedCap, false);
          }
          transaction.commit();
        } catch (Exception ignored) {}
      });
    });
  }

  @Nullable
  @Override
  public EnergyComponent provideComponent() {
    return new EnergyComponent(this, ioType);
  }

  private void onContentsChange() {
    getControllerPosSet().forEach(p -> {
      if (getLevel() == null) return;
      if (getLevel().isClientSide()) return;
      if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
        controller.getProcessor().setMachineInventoryChanged();
      }
    });
  }

  @Override
  public int insert(int amount, TransactionContext transaction) {
    return 0;
  }

  @Override
  public int extract(int amount, TransactionContext transaction) {
    return 0;
  }

  @Override
  public long getAmountAsLong() {
    return energy;
  }

  @Override
  public long getCapacityAsLong() {
    return this.size.maxEnergy;
  }

  //#region toRemove
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int insertable = this.energy + maxReceive > this.size.maxEnergy ? convertDownEnergy(this.size.maxEnergy - this.energy) : maxReceive;
    insertable = Math.min(insertable, convertDownEnergy(size.transferLimit));
    if (!simulate) {
      this.energy = MiscUtils.clamp(this.energy + insertable, 0, this.size.maxEnergy);
      markForUpdate();
      onContentsChange();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    }
    return insertable;
  }

  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }
    int extractable = this.energy - maxExtract < 0 ? convertDownEnergy(this.energy) : maxExtract;
    extractable = Math.min(extractable, convertDownEnergy(size.transferLimit));
    if (!simulate) {
      this.energy = MiscUtils.clamp(this.energy - extractable, 0, this.size.maxEnergy);
      onContentsChange();
      markForUpdate();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    }
    return extractable;
  }

  public int getEnergyStored() {
    return convertDownEnergy(this.energy);
  }

  public int getMaxEnergyStored() {
    return convertDownEnergy(this.size.maxEnergy);
  }

  public boolean canExtract() {
    return canExtract || ioType != null && !ioType.isInput();
  }

  public boolean canReceive() {
    return canInsert || ioType != null && ioType.isInput();
  }
//#endregion

  @Override
  protected void loadAdditional(ValueInput compound) {
    super.loadAdditional(compound);
    this.energy = compound.getLongOr("energy", 0L);
    this.ioType = IOType.getByString(compound.getStringOr("ioType", ""));
    this.size = EnergyHatchSize.value(compound.getStringOr("hatchSize", "").toUpperCase(Locale.ROOT));
    compound.getLong("controllerPos").ifPresent(val -> controllerPos = BlockPos.of(val));
    onContentsChange();
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_energy" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());

    this.baseTexture = compound.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = compound.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    compound.readChild("inventory", capabilityInventory);
    compound.readChild("config", config);
  }

  @Override
  protected void saveAdditional(ValueOutput compound) {
    super.saveAdditional(compound);

    compound.putLong("energy", this.energy);
    if (ioType == null) {
      ioType = this instanceof EnergyInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
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

  protected int convertDownEnergy(long energy) {
    return energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
  }

  public EnergyHatchSize getTier() {
    return size;
  }

  //#region toRemove
  public long getCurrentEnergy() {
    return this.energy;
  }

  public void setCurrentEnergy(long energy) {
    this.energy = MiscUtils.clamp(energy, 0, getMaxEnergy());

    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    onContentsChange();
    markForUpdate();
  }

  public long getMaxEnergy() {
    return this.size.maxEnergy;
  }

  //#endregion

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
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IOSideConfigSyncable.create(this::getConfig, this.config::set));
  }
}
