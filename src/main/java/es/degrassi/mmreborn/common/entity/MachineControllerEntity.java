package es.degrassi.mmreborn.common.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.client.machine.SoundManagerEntity;
import es.degrassi.mmreborn.api.controller.ComponentMapper;
import es.degrassi.mmreborn.api.controller.CorePopup;
import es.degrassi.mmreborn.api.controller.IMultiblockController;
import es.degrassi.mmreborn.api.controller.MMRWorldSavedData;
import es.degrassi.mmreborn.api.crafting.ComponentNotFoundException;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.ComponentSyncable;
import es.degrassi.mmreborn.api.network.syncable.CorePopupSyncable;
import es.degrassi.mmreborn.api.network.syncable.EnumSyncable;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.NbtSyncable;
import es.degrassi.mmreborn.api.network.syncable.ResourceLocationSyncable;
import es.degrassi.mmreborn.client.integration.athena.model.controller.ControllerBakedModel;
import es.degrassi.mmreborn.client.integration.athena.model.controller.ControllerData;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import es.degrassi.mmreborn.common.entity.base.IClientTickEntity;
import es.degrassi.mmreborn.common.entity.base.IServerTickEntity;
import es.degrassi.mmreborn.common.entity.base.TextureableMachineEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.ComponentManager;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessor;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmreborn.common.manager.crafting.MachineStatus;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import es.degrassi.mmreborn.common.util.Utils;
import es.degrassi.mmreborn.common.util.sound.AmbientSound;
import es.degrassi.mmreborn.common.util.sound.SoundManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static es.degrassi.mmreborn.ModularMachineryReborn.CONTROLLERS;

@Getter
@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineControllerEntity extends BlockEntityRestrictedTick implements ComponentMapper, ISyncableStuff, IServerTickEntity, IClientTickEntity,
    SoundManagerEntity, IMultiblockController {
  @Setter
  private CraftingStatus craftingStatus = CraftingStatus.MISSING_STRUCTURE;
  private boolean isPaused = false;
  private boolean formed = false;
  private ResourceLocation id = DynamicMachine.DUMMY.getRegistryName();
  private MachineStatus status = MachineStatus.IDLE;
  private Component errorMessage = Component.empty();
  private final ComponentList errorInfo = new ComponentList();
  private final ComponentManager componentManager;
  private final MachineProcessor processor;
  private int lastFocus;
  private SoundManager soundManager;
  private final Int2ObjectMap<List<MachineProcessorCore>> pages = new Int2ObjectArrayMap<>();

  private final long tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
  private long lastCheckTick;

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
    this.componentManager = new ComponentManager(this);
    this.processor = new MachineProcessor(this);
  }

  @Override
  public boolean shouldAddRender() {
    return false;
  }

  public void updateCorePages() {
    this.pages.clear();
    int maxCores = getProcessor().getMaxCores();
    int pagesN = maxCores / 50;
    int rest = maxCores % 50;
    pagesN += rest > 0 ? 1 : 0;
    for (int i = 0; i < pagesN; i++) {
      List<MachineProcessorCore> cores = Lists.newArrayList();
      for (int j = i * 50; j < Math.min((i + 1) * 50, maxCores); j++)
        cores.add(getProcessor().cores().get(j));
      pages.put(i + 1, cores);
    }
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    components.set(DataComponentRegistration.MACHINE_DATA, getId());
  }

  public void setStatus(MachineStatus status, Component message) {
    if (this.status != status) {
      this.componentManager.getFoundComponentsList().forEach(component -> component.onStatusChanged(this.status, status, message));
      this.status = status;
      this.errorMessage = message;
      clearInfoErrors();
      setCraftingStatus(craftingByMachine(status));
      setChanged();
      if (this.getLevel() instanceof ServerLevel sl) {
        BlockPos pos = this.getBlockPos();
        sl.updateNeighborsAt(pos, this.getBlockState().getBlock());
        PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(pos), new SUpdateCraftingStatusPacket(this.status, pos));
      }
    }
  }

  @Override
  public void setChanged() {
    if(this.level != null)
      this.level.blockEntityChanged(this.worldPosition);
  }

  private CraftingStatus craftingByMachine(MachineStatus status) {
    return switch (status) {
      case IDLE -> CraftingStatus.NO_RECIPE;
      case PAUSED -> craftingStatus;
      case ERRORED -> CraftingStatus.failure(errorMessage);
      case RUNNING -> CraftingStatus.working();
      case MISSING_STRUCTURE -> CraftingStatus.MISSING_STRUCTURE;
    };
  }

  public void setStatus(MachineStatus status) {
    this.setStatus(status, Component.empty());
  }

  public void setStatus(CraftingStatus status) {
    switch(status.getStatus()) {
      case MISSING_STRUCTURE -> this.status = MachineStatus.MISSING_STRUCTURE;
      case NO_RECIPE -> this.status = MachineStatus.IDLE;
      case CRAFTING -> this.status = MachineStatus.RUNNING;
      case FAILURE -> this.status = MachineStatus.ERRORED;
    }
    if (isPaused()) {
      this.status = MachineStatus.PAUSED;
    }
  }

  @Override
  public ModelData getModelData() {
    return ModelData.builder()
        .with(ControllerBakedModel.DATA, new ControllerData(getFoundMachine(), null, getStatus()))
        .build();
  }

  public DynamicMachine getFoundMachine() {
    return ModularMachineryReborn.MACHINES.getOrDefault(id, DynamicMachine.DUMMY);
  }

  public boolean hasCustomModel() {
    return getFoundMachine().getControllerModels()
        .values()
        .stream()
        .anyMatch(Objects::nonNull);
  }

  public void tryPause() {
    setPaused(RedstoneHelper.getReceivingRedstone(this) > 0);
  }

  public void setPaused(boolean paused) {
    if (paused) setStatus(MachineStatus.PAUSED);
    assert getLevel() != null;
    if (!getLevel().isClientSide && paused != isPaused) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
          new SSyncPauseStatePacket(paused, getBlockPos()));
    }
    this.isPaused = paused;
  }

  @Override
  public void doClientTick() {
    if (componentManager == null || processor == null) return;
    if (soundManager == null)
      soundManager = new SoundManager(getBlockPos());
    AmbientSound sound = getFoundMachine().getAmbientSound(status);

    if (!soundManager.isCurrentlyPlaying(sound)) {
      if (sound.isDefault()) {
        soundManager.setSound(null);
      } else {
        soundManager.setSound(sound);
      }
    }

    if (!soundManager.isPlaying())
      soundManager.play();
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    assert level != null;
    if (!isFormed()) {
      var mwsd = MMRWorldSavedData.getOrCreate((ServerLevel) level);
      if (!mwsd.containsAsyncLogicOrMapping(this)) {
        mwsd.addAsyncLogic(this);
      }
      if (!getStatus().isMissingStructure()) setStatus(MachineStatus.MISSING_STRUCTURE);
      return;
    }


    tryPause();
    if (isPaused()) return;
    level.getProfiler().push("Crafting Manager tick");
    try {
      processor.tick();
    } catch (ComponentNotFoundException e) {
      ModularMachineryReborn.LOGGER.error(e.getMessage());
    }
    level.getProfiler().pop();
  }

  @Override
  public void setRemoved() {
    if (this.level != null && this.level.isClientSide() && this.soundManager != null)
      this.soundManager.stop();
    if (getLevel() instanceof ServerLevel serverLevel && !getFoundMachine().isDummy()) {
      MMRWorldSavedData.getOrCreate(serverLevel).removeAsyncLogic(this);
      MMRWorldSavedData.getOrCreate(serverLevel).removeMapping(this);
    }
    super.setRemoved();
  }

  public void setMachine(ResourceLocation machine) {
    this.id = machine;
    this.formed = false;
    tryColorize(getBlockPos());
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SMachineUpdatePacket(machine, getBlockPos()));
      CONTROLLERS.add(this);
      try {
        ComponentManager.cache.get(this);
      } catch (ExecutionException ignored) {}
      var mwsd = MMRWorldSavedData.getOrCreate(l);
      mwsd.removeMapping(this);
      mwsd.removeAsyncLogic(this);
      mwsd.addAsyncLogic(this);
    }
    refreshClientData();
    setChanged();
  }

  @Override
  public int getMachineColor() {
    return getFoundMachine().getMachineColor();
  }

  private void tryColorize(BlockPos pos) {
    if (getLevel() == null) return;
    BlockEntity te = this.getLevel().getBlockEntity(pos);
    AtomicBoolean shouldColor = new AtomicBoolean(true);
    if (te instanceof TextureableMachineEntity entity) {
      entity.resetTextures();
      Optional.ofNullable(getFoundMachine().getFormedTextures().get(entity.getHatchType())).ifPresent(p -> {
        shouldColor.set(p.getFirst());
        p.mapSecond(pair -> {
          pair.getFirst().ifPresent(entity::setMachineBaseTexture);
          pair.getSecond().ifPresent(entity::setMachineOverlayTexture);
          return null;
        });
      });
    }

    if (te instanceof ColorableMachineEntity entity) {
      entity.setMachineColor(shouldColor.get() ? getMachineColor() : 0xffffffff);
    }
  }

  @Override
  @SneakyThrows
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);
    this.craftingStatus = CraftingStatus.deserialize(compound.getCompound("status"), pRegistries);
    this.processor.deserialize(compound.getCompound("craftingManager"));
    this.isPaused = compound.getBoolean("isPaused");
    this.id = ResourceLocation.parse(compound.getString("machine"));
    setMachine(id);
    setStatus(craftingStatus);
    if (getLevel() instanceof ServerLevel) {
      onBlockStateChanged(getBlockPos(), getBlockState());
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.put("status", this.craftingStatus.serializeNBT(pRegistries));
    compound.putString("machine", id.toString());
    compound.put("craftingManager", processor.serialize());
    compound.put("componentManager", componentManager.serializeNBT(pRegistries));
    compound.putBoolean("isPaused", isPaused);
  }

  public void refreshClientData() {
    requestModelDataUpdate();
  }

  public List<MachineComponent<?>> getFoundComponents() {
    return componentManager.getFoundComponentsList();
  }

  @Override
  public Map<BlockPos, Optional<MachineComponent<?>>> getFoundComponentsMap() {
    return componentManager.getFoundComponentsMap();
  }

  public List<ModifierReplacement> getFoundModifiers() {
    return componentManager.getFoundModifiersList();
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    if (this.getLevel() == null)
      return;
    getErrorInfo().getStuffToSync(container);
    processor.getStuffToSync(container);
    componentManager.getStuffToSync(container);
    RegistryAccess registries = this.getLevel().registryAccess();
    container.accept(ResourceLocationSyncable.create(() -> id, s -> id = s));
    container.accept(IntegerSyncable.create(() -> lastFocus, i -> lastFocus = i));
    container.accept(NbtSyncable.create(() -> craftingStatus.serializeNBT(registries), s -> craftingStatus = CraftingStatus.deserialize(s, registries)));
    container.accept(EnumSyncable.create(this::getStatus, this::setStatus));
    container.accept(ComponentSyncable.create(() -> this.errorMessage, err -> this.errorMessage = err));
    container.accept(CorePopupSyncable.create(() -> {
      int maxCores = getProcessor().getMaxCores();
      Map<String, List<CompoundTag>> pages = new HashMap<>();
      this.pages.forEach((page, list) -> pages.put(page.toString(),
          list.stream().map(MachineProcessorCore::serialize).toList()));
      return new CorePopup(maxCores, pages);
    }, corePopup -> {
      corePopup.pages().forEach((page, cores) -> {
        var list = cores.stream().map(tag -> {
          var core = getProcessor().cores().get(tag.getInt("core") - 1);
          core.deserialize(tag);
          return core;
        }).toList();
        this.pages.put(Integer.valueOf(page).intValue(), list);
      });
    }));
  }

  public SoundType getInteractionSound() {
    return getFoundMachine().getInteractionSound(status);
  }

  @Override
  public boolean isPosInCache(BlockPos pos) {
    try {
      return ComponentManager.cache.get(this).contains(pos);
    } catch(ExecutionException e) {
      return false;
    }
  }

  @Override
  public void onBlockStateChanged(BlockPos pos, BlockState newState) {
    if (level instanceof ServerLevel) {
      if (pos.equals(getBlockPos())) {
        onStructureUnformed();
      } else {
        if (getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getFacing())) {
          onStructureFormed();
        } else {
          onStructureUnformed();
        }
      }
    }
  }

  public Direction getFacing() {
    return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
  }

  public void onStructureFormed() {
      formed = true;
      setStatus(MachineStatus.IDLE);
      componentManager.updateComponents();
      try {
        ComponentManager.cache.get(this).forEach(this::tryColorize);
      } catch (ExecutionException ignored) {}
  }

  private List<Component> getStructureErrors() {
    return getFoundMachine().getPattern().getMinBlocksPredicate().getErrors();
  }

  public Component formatStructureErrors() {
    return getStructureErrors().stream().reduce(Component.empty(), MutableComponent::append, MutableComponent::append);
  }

  public void onStructureUnformed() {
    if (getLevel() instanceof ServerLevel sl) {
      this.formed = false;
      setStatus(MachineStatus.MISSING_STRUCTURE);
      addErrorInfo(formatStructureErrors());
      componentManager.resetWithColor();
      processor.reset();
      var mwsd = MMRWorldSavedData.getOrCreate(sl);
      mwsd.removeMapping(this);
      mwsd.removeAsyncLogic(this);
      mwsd.addAsyncLogic(this);
    }
  }

  @Getter
  private final Lock patternLock = new ReentrantLock();

  @Override
  public void asyncCheckPattern(long periodID) {
    if (isRemoved()) return;
    if (!formed && Utils.shouldRunPeriodicCheck(false, periodID, lastCheckTick, tickOffset, MMRConfig.get().checkStructureTicks.get())) {
      lastCheckTick = periodID;
      if (getLevel() instanceof ServerLevel sl) {
        sl.getServer().execute(() -> {
          patternLock.lock();
          if (getFoundMachine().getPattern().match(getLevel(), getBlockPos(), getFacing())) {
            onStructureFormed();
            var mwsd = MMRWorldSavedData.getOrCreate(sl);
            mwsd.addMapping(this);
            mwsd.removeAsyncLogic(this);
          } else {
            onStructureUnformed();
          }
          patternLock.unlock();
        });
      }
    }
  }

  public void addErrorInfo(Component translatable) {
    errorInfo.add(translatable);
  }

  public void clearInfoErrors() {
    errorInfo.clear();
  }

  public static class ComponentList implements ISyncableStuff {
    private final Set<Component> components;
    private Component unified;
    protected ComponentList() {
      this.components = Sets.newHashSet();
    }

    @Override
    public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
      var component = components
          .stream()
          .collect(Component::empty, MutableComponent::append, MutableComponent::append);
      if (components.isEmpty()) {
        component = null;
      }
      var finalComponent = Optional.ofNullable(component).orElse(Component.empty());
      container.accept(ComponentSyncable.create(
          () -> finalComponent,
          c -> unified = Objects.equals(c, Component.empty()) ? null : c
      ));
    }

    public void add(Component component) {
      this.components.add(component);
    }

    public void clear() {
      this.components.clear();
      this.unified = null;
    }

    public Stream<Component> stream() {
      return components.stream();
    }

    public boolean isEmpty() {
      return unified == null && components.isEmpty();
    }

    public Component get() {
      return Optional.ofNullable(unified).orElse(Component.empty());
    }
  }
}