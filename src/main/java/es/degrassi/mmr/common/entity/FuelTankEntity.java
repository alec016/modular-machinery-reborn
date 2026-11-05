package es.degrassi.mmr.common.entity;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.capability.IContentsListener;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.BasicFuelHandler;
import es.degrassi.mmr.api.capability.IFuelHandler;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.codec.BigStackCodecs;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.data.config.FuelTankConfig;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.FuelComponent;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.UpdateValueListener;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateFuelComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateItemComponentPacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

@Getter
@Setter
public class FuelTankEntity extends TileInventory implements MachineComponentEntity<FuelComponent>,
    ControllerAccessible, TextureableMachineEntity, IServerTickEntity, ISyncableStuff, IAutoInputEntity<ResourceHandler<ItemResource>>,
    ISideConfigComponent<IOSideMode>, EntityMenuHolder, IPickResultEntity {
  @Nullable
  private BlockPos controllerPos;
  private final FuelTankSize size;

  private Identifier baseTexture;
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private final IFuelHandler fuelHandler;
  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick, lastCheckFuelTick;
  private final Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  private final IOSideConfig config;

  private final UpdateValueListener<ItemStack> listener;
  private final IContentsListener fuelListener;

  private FuelTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, FuelTankSize size) {
    super(type, pos, state, 2);
    this.size = size;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_fueltank_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
    this.listener = new UpdateValueListener<>() {
      @Override
      public void onChange(int slot, @NotNull ItemStack stack) {
        getControllerPosSet().forEach(p -> {
          if (getLevel() == null) return;
          if (getLevel().isClientSide()) return;
          if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
            controller.getProcessor().setMachineInventoryChanged();
          }
        });
        if (getLevel() instanceof ServerLevel l)
          PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
              new SUpdateItemComponentPacket(slot, inventory.getStack(slot), getBlockPos()));
      }

      @Override
      public void onChange() {
        for (int slot = 0; slot < inventory.size(); slot++) {
          onChange(slot, inventory.getStack(slot));
        }
      }
    };
    this.inventory.setListener(listener);
    this.fuelHandler = new BasicFuelHandler(this.getInventory());
    this.fuelHandler.setMaxFuel(size.burnTimeCapacity);
    this.fuelListener = () -> {
      if (getLevel() != null && !getLevel().isClientSide())
        PacketDistributor.sendToPlayersTrackingChunk(
            (ServerLevel) getLevel(),
            ChunkPos.containing(getBlockPos()),
            new SUpdateFuelComponentPacket(fuelHandler.getFuel(), fuelHandler.getMaxFuel(), getBlockPos())
        );
      if (getController() != null)
        getController().getProcessor().setMachineInventoryChanged();
    };
    this.fuelHandler.setListener(fuelListener);
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);
  }

  public FuelTankEntity(BlockPos pos, BlockState state, FuelTankSize size) {
    this(EntityRegistration.FUEL_TANK.get(), pos, state, size);
  }
  public FuelTankEntity(BlockPos pos, BlockState state){
    this(pos, state, FuelTankSize.TINY);
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    long gameTime = getLevel().getGameTime();
    if (FuelTankConfig.get().reduceFuelPerTick.get()) {
      if (Utils.shouldRunPeriodicCheck(false, gameTime, lastCheckFuelTick, tickOffset, 1))
        if (getFuelHandler().getFuel() > 0 && (getController() == null || getController().getStatus() != MachineStatus.RUNNING)) {
          getFuelHandler().removeFuel(1);
        }
      lastCheckFuelTick = gameTime;
    }
    if (!Utils.shouldRunPeriodicCheck(false, gameTime, lastCheckTick, tickOffset, MMRConfig.get().checkRecipeTicks.get()))
      return;
    lastCheckTick = gameTime;
    getFuelHandler().tryBurnItem(getLevel().fuelValues());
  }

  @Override
  public ItemHandler buildInventory(int slots, int stackSize) {
    ItemHandler handler = new ItemHandler(slots, stackSize, stackSize, stackSize, Direction.values());
    handler.setInputSlotCount(1);
    handler.setFilter(resource -> {
      var level = getLevel();
      return level != null && level.fuelValues().isFuel(resource.toStack());
    });
    return handler;
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    input.readChild("fuelTank", fuelHandler);
    input.getLong("controllerPos").ifPresent(value -> controllerPos = BlockPos.of(value));

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_fueltank_" + size.getSerializedName());
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    input.readChild("config", config);
    this.inventory.setListener(listener);
    this.fuelHandler.setListener(fuelListener);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    output.putChild("fuelTank", fuelHandler);

    output.putString("busSize", this.size.getSerializedName());
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
    output.putChild("config", this.config);
  }
  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("all").build();
  }

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
    return (switch (size) {
      case TINY -> MachineHatchTypeRegistration.FUEL_TANK_TINY;
      case SMALL -> MachineHatchTypeRegistration.FUEL_TANK_SMALL;
      case NORMAL -> MachineHatchTypeRegistration.FUEL_TANK_NORMAL;
      case REINFORCED -> MachineHatchTypeRegistration.FUEL_TANK_REINFORCED;
      case BIG -> MachineHatchTypeRegistration.FUEL_TANK_BIG;
      case HUGE -> MachineHatchTypeRegistration.FUEL_TANK_HUGE;
    }).get();
  }

  @Override
  public FuelComponent provideComponent() {
    return new FuelComponent(getFuelHandler());
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    container.syncLong(getFuelHandler()::getFuel, getFuelHandler()::setFuel);
    container.syncLong(getFuelHandler()::getMaxFuel, getFuelHandler()::setMaxFuel);
    container.sync(IOSideConfig.class, this::getConfig, this.config::set);
  }

  @Override
  public void tickAutoInput() {
    if (!getConfig().isEnabled()) return;
    for (Direction side : inventory.getAccessibleSides()) {
      if (!getConfig().canAutoIO(side)) continue;
      var neighbour = getNeighbour(Capabilities.Item.BLOCK, side);
      if (neighbour == null) continue;

      moveStacks(neighbour, inventory, Integer.MAX_VALUE);
    }
  }

  protected void moveStacks(ResourceHandler<ItemResource> from, ResourceHandler<ItemResource> to, int maxAmount) {
    try (Transaction root = Transaction.openRoot()) {
      ResourceHandlerUtil.moveStacking(from, to, _ -> true, maxAmount, root);
      root.commit();
    }
  }

  @Override
  public void decode(CompoundTag tag) {
    if (getInventory().isCompiling()) return;
    var amount = Math.clamp(tag.getLongOr("amount", 0L), 0, Integer.MAX_VALUE);
    var item = tag.read("item", BigStackCodecs.OPTIONAL.codec()).orElse(ItemStack.EMPTY);
    if (item.isEmpty()) return;
    if (getInventory().addToInputs(item, amount) > 0) setChanged();
  }
}
