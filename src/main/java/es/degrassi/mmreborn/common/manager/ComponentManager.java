package es.degrassi.mmreborn.common.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.ParallelHatchEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.FunctionComponent;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.machine.component.ParallelComponent;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ComponentManager implements INBTSerializable<CompoundTag>, ISyncableStuff {
  @Getter
  private final MachineControllerEntity controller;

  private final Map<BlockPos, MachineComponent<?>> foundComponents = Maps.newHashMap();
  private final Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> foundComponentsValues = Maps.newHashMap();
  private final Map<BlockPos, List<ModifierReplacement>> foundModifiers = Maps.newHashMap();

  public ComponentManager(MachineControllerEntity entity) {
    this.controller = entity;
  }

  public final void reset() {
    foundComponents.clear();
    foundModifiers.clear();
    foundComponentsValues.clear();
  }

  public final void updateModifiers(boolean force) {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY) return;
    if (controller.getLevel() == null) return;
    if (force || controller.getLevel().getGameTime() % MMRConfig.get().checkStructureTicks.get() == 0) {
      foundModifiers.clear();
      foundModifiers.putAll(gatherModifiers());
    }
    controller.setChanged();
  }

  public final void updateComponents(boolean force) {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY) return;
    if (controller.getLevel() == null) return;
    if (force || controller.getLevel().getGameTime() % 20 == 0) {
      reset();
      foundComponents.putAll(gatherComponents());
      foundComponentsValues.putAll(filter());
      updateModifiers(force);
      controller.getProcessor().setMachineInventoryChanged();
    }
    controller.setChanged();
  }

  private Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> filter() {
    Map<ComponentType, Map<IOType, List<MachineComponent<?>>>> foundComponentsValues = Maps.newHashMap();
    for (MachineComponent<?> comp : foundComponents.values()) {
      foundComponentsValues
          .computeIfAbsent(comp.getComponentType(), t -> Maps.newHashMap())
          .computeIfAbsent(comp.getIOType(), io -> Lists.newArrayList())
          .add(comp);
    }
    return foundComponentsValues;
  }

  public final List<MachineComponent<?>> getFoundComponentsList() {
    if (foundComponents.isEmpty()) updateComponents(true);
    return foundComponents.values()
        .stream()
        .toList();
  }

  public List<ModifierReplacement> getFoundModifiersList() {
    if (foundModifiers.isEmpty()) updateComponents(true);
    return foundModifiers.values().stream().flatMap(List::stream).toList();
  }

  public final Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    return foundComponents;
  }

  public final Map<BlockPos, List<ModifierReplacement>> getFoundModifiersMap() {
    return foundModifiers;
  }

  private Map<BlockPos, MachineComponent<?>> gatherComponents() {
    Map<BlockPos, MachineComponent<?>> map = Maps.newHashMap();
    Map<BlockPos, BlockIngredient> filteredMap = controller.getFoundMachine().getPattern().getBlocksFiltered(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    BlockPos controllerPos = controller.getBlockPos();
    Level level = controller.getLevel();
    if (level == null) return map;
    for (BlockPos potentialPosition : filteredMap.keySet()) {
      BlockPos realPos = controllerPos.offset(potentialPosition);
      BlockEntity te = level.getBlockEntity(realPos);
      if (te instanceof MachineComponentEntity<?> entity) {
        var component = entity.provideComponent();
        if (entity instanceof ControllerAccessible accessible) {
          if (accessible.getControllerPos() == null)
            accessible.setControllerPos(controllerPos.immutable());
          if (component != null && controllerPos.equals(accessible.getControllerPos()))
            map.put(realPos, component);
        } else {
          map.put(realPos, component);
        }
      }
    }
    map.put(controllerPos, new FunctionComponent(controllerPos));
    return map;
  }

  private Map<BlockPos, List<ModifierReplacement>> gatherModifiers() {
    Map<BlockPos, List<ModifierReplacement>> map = Maps.newHashMap();
    if (controller.getLevel() == null) return map;
    controller.getFoundMachine()
        .getPattern()
        .getPattern()
        .getModifiers(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
        .forEach((potentialPosition, modifiers) -> {
          BlockPos realPos = controller.getBlockPos().offset(potentialPosition);
          BlockInWorld biw = new BlockInWorld(controller.getLevel(), realPos, false);
          if (modifiers.stream().anyMatch(modifier -> modifier.getIngredient().getAll().stream().anyMatch(state -> state.test(biw))))
            map.put(realPos, modifiers);
        });
    return map;
  }

  public List<RecipeModifier> getModifiers(RequirementType<?> type) {
    if (foundModifiers.isEmpty()) {
      if (!getController().getFoundMachine().getModifiers().isEmpty())
        updateModifiers(false);
    }
    return foundModifiers.values()
        .stream()
        .flatMap(List::stream)
        .map(ModifierReplacement::getModifiers)
        .flatMap(List::stream)
        .filter(mod -> mod.getRequirementType().equals(type))
        .toList();
  }

  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> Optional<C> getComponent(IRequirement<C> requirement, ICraftingContext context) {
    if (foundComponentsValues.isEmpty()) updateComponents(true);
    AtomicReference<C> merged = new AtomicReference<>(null);
    Optional.ofNullable(foundComponentsValues.get(requirement.getComponentType()))
        .map(m -> m.get(requirement.getMode()))
        .stream()
        .flatMap(List::stream)
        .map(m -> (C) m)
        .filter(Objects::nonNull)
        .filter(m -> requirement.test(m, context) || requirement.isComponentValid(m, context))
        .sorted()
        .forEach(c -> {
          if (merged.get() == null)
            merged.set(c);
          if (merged.get().canMerge(c))
            merged.set(merged.get().merge(c));
        });
    return Optional.ofNullable(merged.get());
  }

  public Optional<ParallelComponent> getParallel() {
    Map<BlockPos, BlockIngredient> filteredMap = controller.getFoundMachine()
        .getPattern()
        .getBlocksFiltered(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    BlockPos controllerPos = controller.getBlockPos();
    Level level = controller.getLevel();
    if (level == null) return Optional.empty();
    for (BlockPos potentialPosition : filteredMap.keySet()) {
      BlockPos realPos = controllerPos.offset(potentialPosition);
      try {
        if (level.getBlockEntity(realPos) instanceof ParallelHatchEntity entity) {
          ParallelComponent component = entity.provideComponent();
          if (component == null)
            continue;
          return Optional.of(component);
        }
      } catch (Exception ignored) {
      }
    }
    return Optional.empty();
  }

  public Optional<ItemComponent> getItemComponent(IOType mode) {
    Map<BlockPos, BlockIngredient> filteredMap = controller.getFoundMachine()
        .getPattern()
        .getBlocksFiltered(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    BlockPos controllerPos = controller.getBlockPos();
    Level level = controller.getLevel();
    if (level == null) return Optional.empty();
    List<ItemComponent> components = Lists.newArrayList();
    for (BlockPos potentialPosition : filteredMap.keySet()) {
      BlockPos realPos = controllerPos.offset(potentialPosition);
      try {
        if (level.getBlockEntity(realPos) instanceof TileItemBus entity) {
          if (!entity.getIoType().equals(mode))
            continue;
          ItemComponent component = entity.provideComponent();
          if (component == null)
            continue;
          components.add(component);
        }
      } catch (Exception ignored) {
      }
    }
    if (components.isEmpty())
      return Optional.empty();
    else {
      AtomicReference<ItemComponent> merged = new AtomicReference<>(null);
      components.stream()
          .filter(Objects::nonNull)
          .sorted()
          .forEach(c -> {
            if (merged.get() == null)
              merged.set(c);
            else if (merged.get().canMerge(c)) {
              merged.set(merged.get().merge(c));
            }
          });
      return Optional.ofNullable(merged.get());
    }
  }

  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> Optional<C> getComponent(ComponentType type, IOType mode) {
    if (foundComponentsValues.isEmpty()) updateComponents(true);
    AtomicReference<C> merged = new AtomicReference<>(null);
    Optional.ofNullable(foundComponentsValues.get(type))
        .map(m -> m.get(mode))
        .stream()
        .flatMap(List::stream)
        .map(m -> (C) m)
        .filter(Objects::nonNull)
        .sorted()
        .forEach(c -> {
          if (merged.get() == null)
            merged.set(c);
          else if (merged.get().canMerge(c))
            merged.set(merged.get().merge(c));
        });
    return Optional.ofNullable(merged.get());
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag nbt = new CompoundTag();
    CompoundTag componentsByType = new CompoundTag();
    foundComponentsValues.forEach((type, map) -> {
      CompoundTag listByMode = new CompoundTag();
      map.forEach((mode, list) -> listByMode.put(
          mode.getSerializedName(),
          getComponent(type, mode).map(component -> component.asTag(provider)).orElse(new CompoundTag())
      ));
      componentsByType.put(type.getId().toString(), listByMode);
    });
    nbt.put("components", componentsByType);
    ListTag modifiers = new ListTag();
    foundModifiers
        .forEach((pos, list) -> {
          ListTag mods = list.stream().map(ModifierReplacement::asTag).collect(ListTag::new, ListTag::add, ListTag::add);
          CompoundTag mod = new CompoundTag();
          CompoundTag position = new CompoundTag();
          position.putInt("x", pos.getX());
          position.putInt("y", pos.getY());
          position.putInt("z", pos.getZ());
          mod.put("position", position);
          mod.put("modifiers", mods);
          modifiers.add(mod);
        });
    nbt.put("modifiers", modifiers);
    return nbt;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
    updateComponents(true);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    getFoundComponentsList().stream()
        .filter(c -> c instanceof ISyncableStuff)
        .map(c -> (ISyncableStuff) c)
        .forEach(c -> c.getStuffToSync(container));
  }
}
