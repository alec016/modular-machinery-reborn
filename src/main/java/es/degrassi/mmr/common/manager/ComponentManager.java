package es.degrassi.mmr.common.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.controller.ControllerAttacheable;
import es.degrassi.mmr.api.crafting.ComponentNotFoundException;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.entity.ColorableMachineEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmr.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.machine.component.FunctionComponent;
import es.degrassi.mmr.common.machine.component.ItemComponent;
import es.degrassi.mmr.common.machine.component.ParallelComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ComponentManager implements ISyncableStuff, ValueIOSerializable {
  @Getter
  private final MachineControllerEntity controller;

  public static final LoadingCache<MachineControllerEntity, List<BlockPos>> cache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
    @Override
    public @NotNull List<BlockPos> load(MachineControllerEntity key) {
      BlockPos pos = key.getBlockPos();
      return key
          .getFoundMachine()
          .getPattern()
          .getBlocks(key.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
          .entrySet()
          .parallelStream()
          .filter(e -> !e.getValue().equals(BlockIngredient.MACHINE))
          .map(Map.Entry::getKey)
          .map(pos::offset)
          .toList();
    }
  });

  private final LoadingCache<BlockPos, Optional<MachineComponent<?>>> fC;
  private final LoadingCache<ComponentType<?>, Map<IOType, List<MachineComponent<?>>>> fCV;
  private final LoadingCache<BlockPos, List<ModifierReplacement>> fM;
  private final LoadingCache<RequirementType<?, ?, ?>, List<RecipeModifier<?, ?, ?>>> fMV;

  public ComponentManager(MachineControllerEntity entity) {
    this.controller = entity;
    this.fC = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
          @Override
          public @NotNull Optional<MachineComponent<?>> load(BlockPos key) {
            if (controller.getLevel() == null) return Optional.empty();
            if (key.equals(controller.getBlockPos())) return Optional.of(new FunctionComponent(key));
            if (controller.getLevel().getBlockEntity(key) instanceof MachineComponentEntity<?> e) {
              if (e instanceof ControllerAccessible ca && ca.getControllerPos() == null) {
                ca.setControllerPos(controller.getBlockPos());
              } else if (e instanceof ControllerAccessible ca && !ca.getControllerPos().equals(controller.getBlockPos())) {
                return Optional.empty();
              }
              return Optional.ofNullable(e.provideComponent());
            }
            return Optional.empty();
          }
        });
    this.fCV = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
          @Override
          public @NotNull Map<IOType, List<MachineComponent<?>>> load(ComponentType<?> key) {
            Map<IOType, List<MachineComponent<?>>> foundComponentsValues = Maps.newEnumMap(IOType.class);
            for (var value : IOType.values()) {
              foundComponentsValues.computeIfAbsent(value, io -> Lists.newArrayList());
            }
            for (MachineComponent<?> comp :
                getFoundComponentsList()
                    .parallelStream()
                    .filter(c -> c.getComponentType().equals(key))
                    .toList()) {
                foundComponentsValues.computeIfAbsent(comp.getIOType(), io -> Lists.newArrayList()).add(comp);
            }
            return foundComponentsValues;
          }
        });
    this.fM = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
          @Override
          public @NotNull List<ModifierReplacement> load(BlockPos key) {
            if (controller.getLevel() == null) return Collections.emptyList();
            return controller.getFoundMachine()
                .getPattern()
                .getPattern()
                .getModifiers(controller.getFacing())
                .get(key)
                .parallelStream()
                .filter(modifier -> modifier.test(new BlockInWorld(
                    controller.getLevel(),
                    controller.getBlockPos().offset(key),
                    false
                )))
                .toList();
          }
        });
    this.fMV = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
          @Override
          public @NotNull List<RecipeModifier<?, ?, ?>> load(RequirementType<?, ?, ?> key) {
            return getFoundModifiersList()
                .parallelStream()
                .map(ModifierReplacement::getModifiers)
                .flatMap(List::stream)
                .filter(r -> r.getRequirementType().equals(key))
                .toList();
          }
        });
  }

  public final void reset() {
    fC.invalidateAll();
    fM.invalidateAll();
    fCV.invalidateAll();
    fMV.invalidateAll();
  }

  public final void resetWithColor() {
    try {
      for (BlockPos current : cache.get(controller)) {
        if (Objects.requireNonNull(controller.getLevel()).getBlockEntity(current) instanceof ControllerAttacheable entity) {
          entity.getControllerPosSet().remove(controller.getBlockPos());
          if (entity instanceof ColorableMachineEntity cEntity)
            cEntity.setMachineColor(Config.machineColor);
          if (entity instanceof TextureableMachineEntity e) e.resetTextures();
        }
      }
    } catch (ExecutionException | NullPointerException ignored) {}
    reset();
  }

  public final void updateComponents() {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY) return;
    Level level = controller.getLevel();
    if (level == null) return;
    resetWithColor();
    var controllerPos = controller.getBlockPos();
    cache.refresh(controller);
    if (controller.hasCustomModel()) {
      level.setBlockAndUpdate(controllerPos, controller.getBlockState().setValue(BlockMachineComponent.CONNECT_TEXTURES, false));
    } else if(!controller.getBlockState().getValue(BlockMachineComponent.CONNECT_TEXTURES)) {
      level.setBlockAndUpdate(controllerPos, controller.getBlockState().setValue(BlockMachineComponent.CONNECT_TEXTURES, true));
    }
    try {
      Set<ComponentType<?>> toRefreshComponent = Sets.newHashSet();
      Set<RequirementType<?, ?, ?>> toRefreshRequirement = Sets.newHashSet();
      cache.get(controller).forEach(pos -> {
        var oldState = level.getBlockState(pos);
        var entity = level.getBlockEntity(pos);
        if (entity instanceof ControllerAttacheable ce) {
          ce.getControllerPosSet().add(controllerPos);
        }
        try {
          fC.refresh(pos);
          fC.get(pos)
              .map(MachineComponent::getComponentType)
              .ifPresent(toRefreshComponent::add);
          var isModifierToo = controller.getFoundMachine()
              .getPattern()
              .getPattern()
              .getModifiers()
              .parallelStream()
              .map(ModifierReplacement::getPosition)
              .map(controllerPos::offset)
              .anyMatch(pos::equals);
          if (!isModifierToo) return;
          var key = pos.offset(-controllerPos.getX(), -controllerPos.getY(), -controllerPos.getZ());
          fM.refresh(key);
          fM.get(key)
              .parallelStream()
              .map(ModifierReplacement::getModifiers)
              .flatMap(List::stream)
              .map(RecipeModifier::getRequirementType)
              .forEach(toRefreshRequirement::add);
        } catch (ExecutionException ignored) {}
        if (!(entity instanceof TextureableMachineEntity textureable)) return;
        var state = oldState.setValue(BlockMachineComponent.CONNECT_TEXTURES, Optional.ofNullable(controller.getFoundMachine().getFormedTextures().get(textureable.getHatchType())).isEmpty());
        level.setBlockAndUpdate(pos, state);
      });
      toRefreshComponent.forEach(fCV::refresh);
      toRefreshRequirement.forEach(fMV::refresh);
      controller.setChanged();
    } catch(ExecutionException ignored) {}
  }

  @SuppressWarnings("unchecked")
  public final List<MachineComponent<?>> getFoundComponentsList() {
    return (List<MachineComponent<?>>) (Object) fC.asMap()
        .values()
        .parallelStream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public List<ModifierReplacement> getFoundModifiersList() {
    return fM.asMap()
        .values()
        .parallelStream()
        .flatMap(List::stream)
        .toList();
  }

  public final Map<BlockPos, Optional<MachineComponent<?>>> getFoundComponentsMap() {
    return fC.asMap();
  }

  public final Map<BlockPos, List<ModifierReplacement>> getFoundModifiersMap() {
    return fM.asMap();
  }

  @SuppressWarnings("unchecked")
  public <R extends IRequirement<C, T>, C extends MachineComponent<T>, T> List<RecipeModifier<R, C, T>> getModifiers(RequirementType<R, C, T> type) {
    try {
      return (List<RecipeModifier<R, C, T>>) (Object) fMV.get(type);
    } catch (ExecutionException e) {
      return List.of();
    }
  }

  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<T>, T> Optional<C> getComponent(IRequirement<C, T> requirement, ICraftingContext context) {
    try {
      if (requirement.getType().equals(RequirementTypeRegistration.DURABILITY.get())) {
        return getComponent(requirement.getComponentType(), IOType.INPUT);
      } else if (requirement.getType().equals(RequirementTypeRegistration.FUNCTION.get())) {
        return (Optional<C>) fC.get(controller.getBlockPos());
      }
      try {
        var components = fCV.get(requirement.getComponentType())
            .get(requirement.getMode())
            .parallelStream()
            .filter(Objects::nonNull)
            .map(c -> (C) c)
            .filter(c -> requirement.isComponentValid(c, context) && requirement.test(c, context))
            .sorted()
            .toList();
        if (components.isEmpty()) return Optional.empty();
        var merged = components.getFirst();
        for (var next : components.subList(1, components.size()))
          if (merged.canMerge(next))
            merged = merged.merge(next);
        return Optional.of(merged);
      } catch (ExecutionException ignored) {
        throw new ComponentNotFoundException(controller.getFoundMachine(), requirement.getComponentType());
      }
    } catch(Exception e) {
      return Optional.empty();
    }
  }

  public Optional<ParallelComponent> getParallel() {
    try {
      return getComponent(ComponentRegistration.COMPONENT_PARALLEL.get(), IOType.NONE);
    } catch(Exception e) {
      return Optional.empty();
    }
  }

  public Optional<ItemComponent> getItemComponent(IOType mode) {
    try {
      return getComponent(ComponentRegistration.COMPONENT_ITEM.get(), mode);
    } catch(Exception e) {
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<T>, T> Optional<C> getComponent(ComponentType<T> type, IOType mode) {
    try {
      var components = fCV.get(type)
          .get(mode)
          .parallelStream()
          .map(c -> (C) c)
          .filter(Objects::nonNull)
          .sorted()
          .toList();
      if (components.isEmpty()) return Optional.empty();
      var merged = components.getFirst();
      for (var next : components.subList(1, components.size()))
        if (merged.canMerge(next))
          merged = merged.merge(next);
      return Optional.of(merged);
    } catch (ExecutionException ignored) {
      throw new ComponentNotFoundException(controller.getFoundMachine(), type);
    }
  }

  public void serialize(ValueOutput output) {
    var componentsByType = output.child("components");

    fCV.asMap().forEach((type, map) -> {
      var listByMode = componentsByType.child(type.getId().toString());
      map.forEach((mode, list) -> {
        var components = listByMode.childrenList(mode.getSerializedName());

        list.forEach(component -> {
          var componentOutput = components.addChild();
          component.asTag(componentOutput);
        });
      });
    });
    var modifiers = output.childrenList("modifiers");
    fM.asMap().forEach((pos, list) -> {
      var mod = modifiers.addChild();
      var mods = mod.childrenList("modifiers");
      list.forEach(rep -> rep.asTag(mods.addChild()));
      var position = mod.child("position");
      position.putInt("x", pos.getX());
      position.putInt("y", pos.getY());
      position.putInt("z", pos.getZ());
    });
  }

  public void deserialize(ValueInput input) {
    updateComponents();
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    getFoundComponentsList().stream()
        .filter(c -> c instanceof ISyncableStuff)
        .map(c -> (ISyncableStuff) c)
        .forEach(c -> c.getStuffToSync(container));
  }
}
