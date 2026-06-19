package es.degrassi.mmreborn.common.entity.base;

import com.google.common.collect.Maps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.capability.config.IOSideConfig;
import es.degrassi.mmreborn.api.capability.config.IOSideMode;
import es.degrassi.mmreborn.api.capability.config.ISideConfigComponent;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.api.handler.FilterConverterRegistry;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmreborn.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.FluidInputHatchEntity;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineHatchType;
import es.degrassi.mmreborn.common.machine.component.FluidComponent;
import es.degrassi.mmreborn.common.manager.handler.FluidHandler;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import es.degrassi.mmreborn.common.network.server.SUpdateFilterInvPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateFluidFilterPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateFluidComponentPacket;
import es.degrassi.mmreborn.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Setter
public abstract class FluidTankEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<FluidComponent>, ControllerAccessible,
    TextureableMachineEntity, CapabilityInventoryEntity<IFluidHandlerItem>, ITickEntity, IServerTickEntity,
    ISyncableStuff, IAutoEntity<IFluidHandler>, ISideConfigComponent<IOSideMode>, FiltereableEntity<FluidStack, Fluid> {
  private FluidHandler tank;
  private IOType ioType;
  private FluidHatchSize hatchSize;
  @Nullable
  private BlockPos controllerPos;
  private ResourceLocation baseTexture;
  private ResourceLocation overlayTexture;
  private ResourceLocation defaultOverlayTexture;
  @Getter
  private static final ResourceLocation defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  @Getter
  private final ItemHandler capabilityInventory;
  @Getter
  private final ItemHandler filterInventory;

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;
  private final Map<Direction, BlockCapabilityCache<IFluidHandler, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  @Getter
  private final IOSideConfig config;

  @Nullable
  private FluidStack filter = null;

  protected FluidTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, FluidHatchSize size,
                           IOType ioType) {
    super(type, pos, state);
    this.tank = size.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    this.hatchSize = size;
    this.ioType = ioType;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_fluid" + ioType.getSerializedName() + "hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.capabilityInventory = createCapabilityInventory();
    this.filterInventory = createFilterInventory();

    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);

    this.tank.setListener((slot, value) -> {
      if(!getLevel().isClientSide()) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
            new SUpdateFluidComponentPacket(slot, value, getBlockPos()));
      }
      getControllerPosSet().forEach(p -> {
        if (getLevel() == null) return;
        if (getLevel().isClientSide()) return;
        if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
          controller.getProcessor().setMachineInventoryChanged();
        }
      });
    });
    if (filterInventory.getItem(0).isEmpty()) {
      setFilter(null);
      filterInventory.setItem(0, Items.BUCKET.getDefaultInstance());
    }

    filterInventory.setListener((slot, stack) -> {
      if (!getLevel().isClientSide()) {
        if (stack.isEmpty()) {
          setFilter(FluidStack.EMPTY);
          return;
        }
        var fluidCap = stack.getCapability(getCapability());
        if (fluidCap != null) {
          var fluidCandidate = fluidCap.getFluidInTank(0).copyWithAmount(1);
          if (fluidCandidate.isEmpty()) {
            setFilter(FluidStack.EMPTY);
            filterInventory.setItem(0, Items.BUCKET.getDefaultInstance());
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()), new SUpdateFilterInvPacket(getBlockPos(), Items.BUCKET.getDefaultInstance()));
            return;
          }
          if (FilterConverterRegistry.isConvertible(fluidCandidate)) {
            stack = FilterConverterRegistry.convertBack(fluidCandidate);
            filterInventory.setItem(slot, stack);
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()), new SUpdateFilterInvPacket(getBlockPos(), stack));
            this.setFilter(fluidCandidate);
            return;
          }
        }
        var hasConverter = FilterConverterRegistry.hasConverter(stack);
        var value = FilterConverterRegistry.convert(stack);
        if (hasConverter && value instanceof FluidStack fs) {
          setFilter(fs);
        }
      }
    });
  }

  public FluidStack getFilter() {
    return Optional.ofNullable(this.filter).orElse(FluidStack.EMPTY);
  }

  @Override
  public void setFilter(@Nullable FluidStack filter) {
    this.filter = filter == null || filter.isEmpty() ? null : filter;
    this.tank.setFilter(s -> this.filter == null || this.filter == FluidStack.EMPTY || FluidStack.isSameFluidSameComponents(this.filter, s));
    if (getLevel() != null && !getLevel().isClientSide())
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
        new SUpdateFluidFilterPacket(getBlockPos(), Optional.ofNullable(filter).orElse(FluidStack.EMPTY)));
  }

  @Override
  public CompoundTag serializeFilter(HolderLookup.Provider provider) {
    return (CompoundTag) getFilter().saveOptional(provider);
  }

  @Override
  public void deserializeFilter(CompoundTag tag, HolderLookup.Provider provider) {
    var possibleFilter = FluidStack.parseOptional(provider, tag);
    setFilter(possibleFilter);
  }

  @Override
  @Nullable
  public Component getFilterComponent() {
    if (getFilter().getFluidType().isAir()) return null;
    return getFilter().getHoverName();
  }

  @Override
  public Fluid toFilterRender() {
    return getFilter().getFluid();
  }

  @Override
  public ItemCapability<IFluidHandlerItem, Void> getCapability() {
    return Capabilities.FluidHandler.ITEM;
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    tickInventory();
  }

  @Override
  public void tickInventory() {
    if (!shouldTickInventory()) return;
    capabilityInventory.getInventory().forEach(slot -> {
      Optional.ofNullable(slot.getItemStack().getCapability(getCapability())).ifPresent(cap -> {
        if (ioType == IOType.NONE) return;
        if (ioType.isInput()) {
          if (getTank().isFull()) return;
          if (slot.getItemStack().getItem() instanceof BucketItem bucket) {
            if (bucket.content.isSame(Fluids.EMPTY)) return;
            FluidStack fluid = new FluidStack(bucket.content, 1000);
            int simulatedInsert = getTank().fill(fluid.copy(), IFluidHandler.FluidAction.SIMULATE);
            if (simulatedInsert < 1000) return;
            slot.setItemStack(new ItemStack(Items.BUCKET));
            getTank().fill(fluid.copy(), IFluidHandler.FluidAction.EXECUTE);
          } else {
            FluidStack simulatedCap = cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
            int simulatedInsert = getTank().fill(simulatedCap.copy(), IFluidHandler.FluidAction.SIMULATE);
            cap.drain(simulatedCap.copyWithAmount(simulatedInsert), IFluidHandler.FluidAction.EXECUTE);
            getTank().fill(simulatedCap.copyWithAmount(simulatedInsert), IFluidHandler.FluidAction.EXECUTE);
          }
        } else if (ioType.isOutput()) {
          if (this.getTank().isEmpty()) return;
          if (slot.getItemStack().getItem() instanceof BucketItem bucket) {
            if (!bucket.content.isSame(Fluids.EMPTY)) return;
            FluidStack simulatedExtract = getTank().drain(1000, IFluidHandler.FluidAction.SIMULATE);
            ItemStack fluidStack = BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof BucketItem b && b.content.isSame(simulatedExtract.getFluid()))
                .findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
            if (fluidStack.isEmpty() || simulatedExtract.isEmpty() || simulatedExtract.getAmount() < 1000) return;
            slot.setItemStack(fluidStack);
            getTank().drain(simulatedExtract, IFluidHandler.FluidAction.EXECUTE);
          } else {
            FluidStack simulatedExtract = getTank().drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
            int simulatedCap = cap.fill(simulatedExtract.copy(), IFluidHandler.FluidAction.SIMULATE);
            cap.fill(simulatedExtract.copyWithAmount(simulatedCap), IFluidHandler.FluidAction.EXECUTE);
            getTank().drain(simulatedExtract.copyWithAmount(simulatedCap), IFluidHandler.FluidAction.EXECUTE);
          }
        }
      });
    });
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
  public FluidComponent provideComponent() {
    return new FluidComponent(this.getTank(), ioType);
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    this.ioType = IOType.getByString(compound.getString("ioType"));
    this.hatchSize = FluidHatchSize.value(compound.getString("size"));
    FluidHandler newTank = hatchSize.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    CompoundTag tankTag = compound.getCompound("tank");
    newTank.readNBT(tankTag, provider);
    this.tank = newTank;
    this.capabilityInventory.deserialize(compound.getCompound("capInventory"), provider);
    this.filterInventory.deserialize(compound.getCompound("filterInventory"), provider);
    if (filterInventory.getItem(0).isEmpty()) {
      filterInventory.setItem(0, Items.BUCKET.getDefaultInstance());
    }
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_fluid" + ioType.getSerializedName() + "hatch_" + hatchSize.getSerializedName());

    this.baseTexture = compound.contains("baseTexture") ? ResourceLocation.parse(compound.getString("baseTexture")) : defaultBaseTexture;
    this.overlayTexture = compound.contains("overlayTexture") ? ResourceLocation.parse(compound.getString("overlayTexture")) : defaultOverlayTexture;

    this.config.deserialize(compound.getCompound("config"));

    this.tank.setListener((slot, value) -> {
      if(!getLevel().isClientSide()) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
            new SUpdateFluidComponentPacket(slot, value, getBlockPos()));
      }
      getControllerPosSet().forEach(p -> {
        if (getLevel() == null) return;
        if (getLevel().isClientSide()) return;
        if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
          controller.getProcessor().setMachineInventoryChanged();
        }
      });
    });
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    if (ioType == null) {
      ioType = this instanceof FluidInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    compound.putString("ioType", ioType.getSerializedName());
    compound.putString("size", this.hatchSize.getSerializedName());
    compound.put("tank", this.tank.writeNBT(provider));
    compound.put("capInventory", this.capabilityInventory.writeNBT(provider));
    compound.put("filterInventory", this.filterInventory.writeNBT(provider));
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      compound.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      compound.putString("overlayTexture", overlayTexture.toString());
    compound.put("config", this.config.serialize());
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
  public ResourceLocation getMachineBaseTexture() {
    return baseTexture;
  }

  @Override
  public ResourceLocation getMachineOverlayTexture() {
    return overlayTexture;
  }

  @Override
  public void setMachineBaseTexture(ResourceLocation newTexture) {
    setChanged();
    this.baseTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineTexturePacket(baseTexture, true, getBlockPos()));
    }
  }

  @Override
  public void setMachineOverlayTexture(ResourceLocation newTexture) {
    setChanged();
    this.overlayTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
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
      case INPUT -> (switch (hatchSize) {
        case TINY -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_LUDICROUS;
        case VACUUM -> MachineHatchTypeRegistration.FLUID_INPUT_HATCH_VACUUM;
      }).get();
      case OUTPUT -> (switch(hatchSize) {
        case TINY -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_TINY;
        case SMALL -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_BIG;
        case HUGE -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_LUDICROUS;
        case VACUUM -> MachineHatchTypeRegistration.FLUID_OUTPUT_HATCH_VACUUM;
      }).get();
      default -> null;
    };
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IOSideConfigSyncable.create(this::getConfig, this.config::set));
  }
}
