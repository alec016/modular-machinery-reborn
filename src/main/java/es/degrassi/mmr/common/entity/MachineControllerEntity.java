package es.degrassi.mmr.common.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.machine.SoundManagerEntity;
import es.degrassi.mmr.api.controller.ComponentMapper;
import es.degrassi.mmr.api.controller.CorePopup;
import es.degrassi.mmr.api.controller.IMultiblockController;
import es.degrassi.mmr.api.controller.MMRWorldSavedData;
import es.degrassi.mmr.api.crafting.ComponentNotFoundException;
import es.degrassi.mmr.api.entity.ColorableMachineEntity;
import es.degrassi.mmr.api.entity.IClientTickEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerBakedModel;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerData;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.ComponentManager;
import es.degrassi.mmr.common.manager.crafting.MachineProcessor;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmr.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmr.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.util.RedstoneHelper;
import es.degrassi.mmr.common.util.Utils;
import es.degrassi.mmr.common.util.sound.AmbientSound;
import es.degrassi.mmr.common.util.sound.SoundManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.model.data.ModelData;
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
import java.util.stream.Stream;

import static es.degrassi.mmr.ModularMachineryReborn.CONTROLLERS;

@Getter
@Setter
@ParametersAreNonnullByDefault
public class MachineControllerEntity extends BlockEntityRestrictedTick implements ComponentMapper, ISyncableStuff, IServerTickEntity,
    IClientTickEntity, SoundManagerEntity, IMultiblockController, ISyncPersistRPCBlockEntity {
  private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
  /*@Persisted
  @DescSynced*/
  private boolean isPaused = false;
  /*@RequireRerender
  @DescSynced*/
  private boolean formed = false;
  /*@Persisted
  @DescSynced*/
  private Identifier id = DynamicMachine.DUMMY.getRegistryName();
  /*@Persisted
  @RequireRerender
  @DescSynced*/
  private MachineStatus status = MachineStatus.IDLE;
  /*@Persisted
  @DescSynced*/
  private Component errorMessage = Component.empty();
  /*@Persisted
  @DescSynced*/
  private final ComponentList errorInfo = new ComponentList();
  /*@Persisted
  @DescSynced*/
  private final ComponentManager componentManager;
  /*@Persisted
  @DescSynced*/
  private final MachineProcessor processor;
  private int lastFocus;
  private SoundManager soundManager;
  private final Int2ObjectMap<List<MachineProcessorCore>> pages = new Int2ObjectArrayMap<>();

  private final long tickOffset;
  private long lastCheckTick;

  public MachineControllerEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CONTROLLER.get(), pos, state);
    this.tickOffset = Utils.RAND.nextIntBetweenInclusive(0, Integer.MAX_VALUE - 1);
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
      if (status.isErrored() && !Component.empty().equals(message))
        this.errorMessage = message;
      else if (!status.isErrored()) this.errorMessage = message;
      clearInfoErrors();
      getErrorInfo().add(message);
      setChanged();
      if (this.getLevel() instanceof ServerLevel sl) {
        BlockPos pos = this.getBlockPos();
        sl.updateNeighborsAt(pos, this.getBlockState().getBlock());
        PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(pos), new SUpdateCraftingStatusPacket(this.status, pos));
      }
      broadcastChanges();
    }
  }

  @Override
  public void setChanged() {
    if(this.level != null)
      this.level.blockEntityChanged(this.worldPosition);
    broadcastChanges();
  }

  public void setStatus(MachineStatus status) {
    this.setStatus(status, Component.empty());
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
    if (!getLevel().isClientSide() && paused != isPaused) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), ChunkPos.containing(getBlockPos()),
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
    try {
      processor.tick();
    } catch (ComponentNotFoundException e) {
      ModularMachineryReborn.LOGGER.error(e.getMessage());
    }
    broadcastChanges();
  }

  @Override
  public void setRemoved() {
    if (this.level != null && this.level.isClientSide() && this.soundManager != null)
      this.soundManager.stop();
    if (getLevel() instanceof ServerLevel serverLevel && !getFoundMachine().isDummy()) {
      MMRWorldSavedData.getOrCreate(serverLevel).removeAsyncLogic(this);
      MMRWorldSavedData.getOrCreate(serverLevel).removeMapping(this);
    }
    if (getLevel() instanceof ServerLevel) {
      getStuffToSync().clear();
    }
    super.setRemoved();
  }

  public void setMachine(Identifier machine) {
    this.id = machine;
    this.formed = false;
    tryColorize(getBlockPos());
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()), new SMachineUpdatePacket(machine, getBlockPos()));
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
  protected void loadAdditional(ValueInput compound) {
    super.loadAdditional(compound);
    setStatus(compound.read("status", MachineStatus.CODEC.codec()).orElse(MachineStatus.MISSING_STRUCTURE));
    compound.readChild("craftingManager", this.processor);
    this.isPaused = compound.getBooleanOr("isPaused", false);
    this.id = Identifier.parse(compound.getStringOr("machine", ""));
    setMachine(id);
    if (getLevel() instanceof ServerLevel) {
      onBlockStateChanged(getBlockPos(), getBlockState());
    }
  }

  @Override
  protected void saveAdditional(ValueOutput compound) {
    super.saveAdditional(compound);
    compound.store("status", MachineStatus.CODEC.codec(), status);
    compound.putString("machine", id.toString());
    compound.putChild("craftingManager", processor);
    compound.putChild("componentManager", componentManager);
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
  public void getStuffToSync(SyncableList container) {
    if (this.getLevel() == null)
      return;
    getErrorInfo().getStuffToSync(container);
    processor.getStuffToSync(container);
    componentManager.getStuffToSync(container);
    container.syncId(() -> id, s -> id = s);
    container.syncInt(() -> lastFocus, i -> lastFocus = i);
    container.syncEnum(this::getStatus, this::setStatus);
    container.syncComponent(() -> Optional.ofNullable(this.errorMessage).orElseGet(Component::empty),
        err -> this.errorMessage = err);
    container.sync(CorePopup.class, () -> {
      int maxCores = getProcessor().getMaxCores();
      Map<String, List<CompoundTag>> pages = new HashMap<>();
      this.pages.forEach((page, list) -> pages.put(String.valueOf(page),
          list.stream().map(MachineProcessorCore::serialize).toList()));
      return new CorePopup(maxCores, pages);
    }, corePopup -> {
      corePopup.pages().forEach((page, cores) -> {
        List<MachineProcessorCore> list = cores.stream().map(tag -> {
          var core = getProcessor().cores().get(tag.getIntOr("core", 0) - 1);
          core.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, getLevel().registryAccess(), tag));
          return core;
        }).toList();
        this.pages.put(Integer.parseInt(page), list);
      });
    });
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
      broadcastChanges();
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

  public static class ComponentList implements ISyncableStuff, ValueIOSerializable {
    private final Set<Component> components;
    private Component unified = Component.empty();
    protected ComponentList() {
      this.components = Sets.newHashSet();
    }

    @Override
    public void getStuffToSync(SyncableList container) {
      container.syncComponent(
          this::get,
          this::set
      );
    }

    private void set(Component c) {
      this.unified = c;
    }

    private Component calcComponent() {
      return stream().collect(Component::empty, MutableComponent::append, MutableComponent::append);
    }

    public void add(Component component) {
      if (component.equals(Component.empty())) return;
      this.components.add(component);
      this.unified = unified.copy().append(component);
    }

    public void clear() {
      this.components.clear();
      this.unified = Component.empty();
    }

    public Stream<Component> stream() {
      return components.stream();
    }

    public boolean isEmpty() {
      return unified.equals(Component.empty()) && components.isEmpty();
    }

    public Component get() {
      if (unified.equals(Component.empty())) {
        this.unified = calcComponent();
      }
      return unified;
    }

    @Override
    public void serialize(ValueOutput output) {
      var components = this.components.toArray(Component[]::new);
      for (int i = 0; i < components.length; i++) {
        output.store("component%s".formatted(i), ComponentSerialization.CODEC, components[i]);
      }
    }

    @Override
    public void deserialize(ValueInput input) {
      var length = components.size();
      clear();
      for (int i = 0; i < length; i++) {
        add(input.read("component%s".formatted(i), ComponentSerialization.CODEC).orElse(Component.empty()));
      }
    }
  }
}