package es.degrassi.mmr.common.entity.base;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.CapabilityInventoryEntity;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.api.handler.FilterConverterRegistry;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.FluidInputHatchEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.FluidComponent;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.UpdateValueListener;
import es.degrassi.mmr.common.network.server.SUpdateFilterInvPacket;
import es.degrassi.mmr.common.network.server.SUpdateFluidFilterPacket;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateFluidComponentPacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public abstract class FluidTankEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<FluidComponent>, ControllerAccessible,
    TextureableMachineEntity, CapabilityInventoryEntity<ResourceHandler<FluidResource>>, ITickEntity, IServerTickEntity,
    ISyncableStuff, IAutoEntity<ResourceHandler<FluidResource>>, ISideConfigComponent<IOSideMode>,
    FiltereableEntity<FluidResource, Fluid> {
  private FluidHandler tank;
  private IOType ioType;
  private FluidHatchSize hatchSize;
  @Nullable
  private BlockPos controllerPos;
  private Identifier baseTexture;
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  @Getter
  private final ItemHandler capabilityInventory;
  @Getter
  private final ItemHandler filterInventory;

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;
  private final Map<Direction, BlockCapabilityCache<ResourceHandler<FluidResource>, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  @Getter
  private final IOSideConfig config;

  @Nullable
  private FluidResource filter = null;

  private final UpdateValueListener<FluidStack> listener;
  private final UpdateValueListener<ItemStack> filterListener;

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

    this.listener = (slot, value) -> {
      if(!getLevel().isClientSide()) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), ChunkPos.containing(getBlockPos()),
            new SUpdateFluidComponentPacket(slot, tank.getStack(slot), getBlockPos()));
      }
      getControllerPosSet().forEach(p -> {
        if (getLevel() == null) return;
        if (getLevel().isClientSide()) return;
        if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
          controller.getProcessor().setMachineInventoryChanged();
        }
      });
    };

    this.filterListener = (slot, stack) -> {
      stack = filterInventory.getStack(slot);
      if (!getLevel().isClientSide()) {
        if (stack.isEmpty()) {
          setFilter(FluidResource.EMPTY);
          return;
        }
        var fluidCap = stack.getCapability(getCapability(), null);
        if (fluidCap != null) {
          var fluidCandidate = fluidCap.getResource(0).toStack(1);
          if (fluidCandidate.isEmpty()) {
            setFilter(FluidResource.EMPTY);
            filterInventory.setStack(0, Items.BUCKET.getDefaultInstance());
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), ChunkPos.containing(getBlockPos()),
                new SUpdateFilterInvPacket(getBlockPos(), Items.BUCKET.getDefaultInstance()));
            return;
          }
          if (FilterConverterRegistry.isConvertible(fluidCandidate)) {
            stack = FilterConverterRegistry.convertBack(fluidCandidate);
            filterInventory.setStack(slot, stack);
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), ChunkPos.containing(getBlockPos()), new SUpdateFilterInvPacket(getBlockPos(), stack));
            this.setFilter(FluidResource.of(fluidCandidate));
            return;
          }
        }
        var hasConverter = FilterConverterRegistry.hasConverter(stack);
        var value = FilterConverterRegistry.convert(stack);
        if (hasConverter && value instanceof FluidStack fs) {
          setFilter(FluidResource.of(fs));
        }
      }
    };

    this.tank.setListener(listener);
    filterInventory.setListener(filterListener);
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (filterInventory.getResource(0).isEmpty()) {
      setFilter(null);
      filterInventory.setStack(0, Items.BUCKET.getDefaultInstance());
    }
  }

  public FluidResource getFilter() {
    return Optional.ofNullable(this.filter).orElse(FluidResource.EMPTY);
  }

  @Override
  public void setFilter(@Nullable FluidResource filter) {
    this.filter = filter == null || filter.isEmpty() ? null : filter;
    this.tank.setFilter(s -> this.filter == null || this.filter == FluidResource.EMPTY || (
        this.filter.is(s.getFluid()) && s.getComponents().stream().allMatch(type -> this.filter.has(type.type()))
    ));
    if (getLevel() != null && !getLevel().isClientSide())
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), ChunkPos.containing(getBlockPos()),
        new SUpdateFluidFilterPacket(getBlockPos(), Optional.ofNullable(filter).orElse(FluidResource.EMPTY)));
  }

  @Override
  public CompoundTag serializeFilter(HolderLookup.Provider provider) {
    return (CompoundTag) FluidResource.OPTIONAL_CODEC.encodeStart(
        provider.createSerializationContext(NbtOps.INSTANCE),
        getFilter()
    )
    .result()
    .orElse(new CompoundTag());
  }

  @Override
  public void deserializeFilter(CompoundTag tag, HolderLookup.Provider provider) {
    var possibleFilter = FluidResource.OPTIONAL_CODEC.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag)
        .map(Pair::getFirst)
        .result()
        .orElse(FluidResource.EMPTY);
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
  public ItemCapability<ResourceHandler<FluidResource>, ItemAccess> getCapability() {
    return Capabilities.Fluid.ITEM;
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    tickInventory();
  }

  @Override
  public void tickInventory() {
    if (!shouldTickInventory()) return;
    capabilityInventory.forEach((slot, index) -> {
      if (slot.isEmpty()) return;
      Optional.ofNullable(slot.getCapability(getCapability(), ItemAccess.forStack(slot))).ifPresent(cap -> {
        if (ioType == IOType.NONE) return;
        if (ioType.isInput()) {
          if (getTank().isFull()) return;
          if (slot.getItem() instanceof BucketItem bucket) {
            try (var tx = Transaction.open(null)) {
              if (bucket.content.isSame(Fluids.EMPTY)) return;
              int simulatedInsert = getTank().insert(FluidResource.of(bucket.content), FluidType.BUCKET_VOLUME, tx);
              if (simulatedInsert < FluidType.BUCKET_VOLUME) return;
              capabilityInventory.setStack(index, new ItemStack(Items.BUCKET));
              tx.commit();
            }
          } else {
            try (var tx = Transaction.open(null)) {
              ResourceHandlerUtil.moveStacking(cap, getTank(),
                  resource -> getFilter().isEmpty() || resource.equals(getFilter()), Integer.MAX_VALUE, tx);
              tx.commit();
            }
          }
        } else if (ioType.isOutput()) {
          if (this.getTank().isEmpty()) return;
          if (slot.getItem() instanceof BucketItem bucket) {
            try (var tx = Transaction.open(null)) {
              if (!bucket.content.isSame(Fluids.EMPTY)) return;
              var stack = ResourceHandlerUtil.extractFirst(getTank(), _ -> true, FluidType.BUCKET_VOLUME, tx);
              if (stack == null || stack.isEmpty() || stack.resource().isEmpty() ||stack.amount() < FluidType.BUCKET_VOLUME) return;
              ItemStack fluidStack = BuiltInRegistries.ITEM.stream()
                  .filter(item -> item instanceof BucketItem b && b.content.isSame(stack.resource().getFluid()))
                  .findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
              capabilityInventory.setStack(index, fluidStack);
              tx.commit();
            }
          } else {
            try (var tx = Transaction.open(null)) {
              ResourceHandlerUtil.moveStacking(getTank(), cap, _ -> true, Integer.MAX_VALUE, tx);
              tx.commit();
            }
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
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.ioType = IOType.getByString(input.getString("ioType").orElseThrow());
    this.hatchSize = FluidHatchSize.value(input.getString("size").orElseThrow());
    input.readChild("tank", tank);
    input.readChild("capInventory", capabilityInventory);
    input.readChild("filterInventory", filterInventory);
    if (filterInventory.getStack(0).isEmpty()) {
      filterInventory.setStack(0, Items.BUCKET.getDefaultInstance());
    }
    input.getLong("controllerPos").ifPresent(v -> this.controllerPos = BlockPos.of(v));
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_fluid" + ioType.getSerializedName() + "hatch_" + hatchSize.getSerializedName());

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);


    input.readChild("config", config);

    this.tank.setListener(listener);
    this.filterInventory.setListener(filterListener);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    if (ioType == null) {
      ioType = this instanceof FluidInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    output.putString("ioType", ioType.getSerializedName());
    output.putString("size", this.hatchSize.getSerializedName());
    output.putChild("tank", this.tank);
    output.putChild("capInventory", this.capabilityInventory);
    output.putChild("filterInventory", this.filterInventory);
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
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
  public void getStuffToSync(SyncableList container) {
    container.sync(IOSideConfig.class, this::getConfig, this.config::set);
  }
}
