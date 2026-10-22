package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.api.capability.EffectHandler;
import es.degrassi.mmr.api.capability.EntityHandler;
import es.degrassi.mmr.api.capability.IFuelHandler;
import es.degrassi.mmr.api.crafting.requirement.WeatherType;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.SimpleEnergyHandler;
import es.degrassi.mmr.common.util.Chunkloader;
import es.degrassi.mmr.common.util.IntRange;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.List;
import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComponentRegistration {

  public static final DeferredRegister<ComponentType<?>> MACHINE_COMPONENTS =
      DeferredRegister.create(ComponentType.REGISTRY_KEY, ModularMachineryReborn.MODID);
  public static final Registry<ComponentType<?>> COMPONENTS_REGISTRY = MACHINE_COMPONENTS.makeRegistry(builder -> {});

  public static final Supplier<ComponentType<ItemHandler>> COMPONENT_ITEM = MACHINE_COMPONENTS.register(rootLC("item"),
      ComponentType::create);
  public static final Supplier<ComponentType<ItemHandler>> COMPONENT_DURABILITY= MACHINE_COMPONENTS.register(rootLC("durability"),
      ComponentType::create);
  public static final Supplier<ComponentType<Void>> COMPONENT_DURATION = MACHINE_COMPONENTS.register(rootLC("duration"),
      ComponentType::create);
  public static final Supplier<ComponentType<FluidHandler>> COMPONENT_FLUID = MACHINE_COMPONENTS.register(rootLC("fluid"),
      ComponentType::create);
  public static final Supplier<ComponentType<SimpleEnergyHandler>> COMPONENT_ENERGY = MACHINE_COMPONENTS.register(rootLC("energy"),
      ComponentType::create);
  public static final Supplier<ComponentType<Identifier>> COMPONENT_DIMENSION = MACHINE_COMPONENTS.register(rootLC("dimension"),
      ComponentType::create);
  public static final Supplier<ComponentType<List<Identifier>>> COMPONENT_BIOME = MACHINE_COMPONENTS.register(rootLC("biome"),
      ComponentType::create);
  public static final Supplier<ComponentType<WeatherType>> COMPONENT_WEATHER = MACHINE_COMPONENTS.register(rootLC("weather"),
      ComponentType::create);
  public static final Supplier<ComponentType<IntRange>> COMPONENT_TIME = MACHINE_COMPONENTS.register(rootLC("time"),
      ComponentType::create);
  public static final Supplier<ComponentType<IntRange>> COMPONENT_HEIGHT = MACHINE_COMPONENTS.register(rootLC("height"),
      ComponentType::create);
  public static final Supplier<ComponentType<Chunkloader>> COMPONENT_CHUNKLOAD = MACHINE_COMPONENTS.register(rootLC("chunkload"),
      ComponentType::create);
  public static final Supplier<ComponentType<IExperienceHandler>> COMPONENT_EXPERIENCE = MACHINE_COMPONENTS.register(rootLC("experience"),
      ComponentType::create);
  public static final Supplier<ComponentType<Integer>> COMPONENT_PARALLEL = MACHINE_COMPONENTS.register(rootLC("parallel"),
      ComponentType::create);
  public static final Supplier<ComponentType<Void>> COMPONENT_FUNCTION = MACHINE_COMPONENTS.register(rootLC("function"),
      ComponentType::create);
  public static final Supplier<ComponentType<IFuelHandler>> COMPONENT_FUEL = MACHINE_COMPONENTS.register(rootLC("fuel"),
      ComponentType::create);
  public static final Supplier<ComponentType<EffectHandler>> COMPONENT_EFFECT = MACHINE_COMPONENTS.register(rootLC("effect"),
      ComponentType::create);
  public static final Supplier<ComponentType<EntityHandler>> COMPONENT_ENTITY = MACHINE_COMPONENTS.register(rootLC("entity"),
      ComponentType::create);
  public static final Supplier<ComponentType<Structure>> COMPONENT_STRUCTURE = MACHINE_COMPONENTS.register(rootLC("structure"),
      ComponentType::create);
  public static final Supplier<ComponentType<Integer>> COMPONENT_REDSTONE = MACHINE_COMPONENTS.register(rootLC("redstone"),
      ComponentType::create);
  public static final Supplier<ComponentType<Void>> COMPONENT_COMMAND = MACHINE_COMPONENTS.register(rootLC("command"),
      ComponentType::create);

  public static final Supplier<ComponentType<Void>> COMPONENT_EMPTY = MACHINE_COMPONENTS.register(rootLC("empty"),
      ComponentType::create);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
