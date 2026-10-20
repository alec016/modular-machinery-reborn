package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.machine.MachineHatchType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MachineHatchTypeRegistration {

  public static final DeferredRegister<MachineHatchType> MACHINE_COMPONENTS =
      DeferredRegister.create(MachineHatchType.REGISTRY_KEY, ModularMachineryReborn.MODID);
  public static final Registry<MachineHatchType> MachineHatchType_REGISTRY = MACHINE_COMPONENTS.makeRegistry(builder -> {
  });

  public static final Supplier<MachineHatchType> BIOME_READER =
      MACHINE_COMPONENTS.register(rootLC("BIOME_READER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> CHUNKLOADER =
      MACHINE_COMPONENTS.register(rootLC("CHUNKLOADER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> DIMENSIONAL_DETECTOR =
      MACHINE_COMPONENTS.register(rootLC("DIMENSIONAL_DETECTOR".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> TIME_COUNTER =
      MACHINE_COMPONENTS.register(rootLC("TIME_COUNTER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> WEATHER_SENSOR =
      MACHINE_COMPONENTS.register(rootLC("WEATHER_SENSOR".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> HEIGHT_METER =
      MACHINE_COMPONENTS.register(rootLC("HEIGHT_METER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> PARALLEL_HATCH_BASIC =
      MACHINE_COMPONENTS.register(rootLC("PARALLEL_HATCH_BASIC".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> PARALLEL_HATCH_ADVANCED =
      MACHINE_COMPONENTS.register(rootLC("PARALLEL_HATCH_ADVANCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> PARALLEL_HATCH_MEDIUM =
      MACHINE_COMPONENTS.register(rootLC("PARALLEL_HATCH_MEDIUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> PARALLEL_HATCH_ULTIMATE =
      MACHINE_COMPONENTS.register(rootLC("PARALLEL_HATCH_ULTIMATE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> PARALLEL_HATCH_MAX =
      MACHINE_COMPONENTS.register(rootLC("PARALLEL_HATCH_MAX".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_INPUT_HATCH_ULTIMATE =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_INPUT_HATCH_ULTIMATE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENERGY_OUTPUT_HATCH_ULTIMATE =
      MACHINE_COMPONENTS.register(rootLC("ENERGY_OUTPUT_HATCH_ULTIMATE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_INPUT_HATCH_VACUUM =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_INPUT_HATCH_VACUUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EXPERIENCE_OUTPUT_HATCH_VACUUM =
      MACHINE_COMPONENTS.register(rootLC("EXPERIENCE_OUTPUT_HATCH_VACUUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_INPUT_HATCH_VACUUM =
      MACHINE_COMPONENTS.register(rootLC("FLUID_INPUT_HATCH_VACUUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_HUGE =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FLUID_OUTPUT_HATCH_VACUUM =
      MACHINE_COMPONENTS.register(rootLC("FLUID_OUTPUT_HATCH_VACUUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_TINY =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_SMALL =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_BIG =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_HUGE =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_INPUT_BUS_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("ITEM_INPUT_BUS_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_TINY =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_SMALL =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_BIG =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_HUGE =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ITEM_OUTPUT_BUS_LUDICROUS =
      MACHINE_COMPONENTS.register(rootLC("ITEM_OUTPUT_BUS_LUDICROUS".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> DURABILITY_HATCH_TINY =
      MACHINE_COMPONENTS.register(rootLC("DURABILITY_HATCH_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> DURABILITY_HATCH_SMALL =
      MACHINE_COMPONENTS.register(rootLC("DURABILITY_HATCH_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> DURABILITY_HATCH_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("DURABILITY_HATCH_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> DURABILITY_HATCH_BIG =
      MACHINE_COMPONENTS.register(rootLC("DURABILITY_HATCH_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> FUEL_TANK_TINY =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_TINY".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FUEL_TANK_SMALL =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FUEL_TANK_NORMAL =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_NORMAL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FUEL_TANK_REINFORCED =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_REINFORCED".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FUEL_TANK_BIG =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> FUEL_TANK_HUGE =
      MACHINE_COMPONENTS.register(rootLC("FUEL_TANK_HUGE".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> EFFECT_DISPENSER_SMALL =
      MACHINE_COMPONENTS.register(rootLC("EFFECT_DISPENSER_SMALL".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EFFECT_DISPENSER_MEDIUM =
      MACHINE_COMPONENTS.register(rootLC("EFFECT_DISPENSER_MEDIUM".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> EFFECT_DISPENSER_BIG =
      MACHINE_COMPONENTS.register(rootLC("EFFECT_DISPENSER_BIG".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> ENTITY_DETECTOR =
      MACHINE_COMPONENTS.register(rootLC("ENTITY_DETECTOR".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENTITY_SPAWNER =
      MACHINE_COMPONENTS.register(rootLC("ENTITY_SPAWNER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENTITY_KILLER =
      MACHINE_COMPONENTS.register(rootLC("ENTITY_KILLER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENTITY_HEALER =
      MACHINE_COMPONENTS.register(rootLC("ENTITY_HEALER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);
  public static final Supplier<MachineHatchType> ENTITY_DAMAGER =
      MACHINE_COMPONENTS.register(rootLC("ENTITY_DAMAGER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> STRUCTURE_CHECKER =
      MACHINE_COMPONENTS.register(rootLC("STRUCTURE_CHECKER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> REDSTONE_PORT =
      MACHINE_COMPONENTS.register(rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static final Supplier<MachineHatchType> COMMAND_EXECUTIONER =
      MACHINE_COMPONENTS.register(rootLC("COMMAND_EXECUTIONER".toLowerCase(Locale.ENGLISH)),
      MachineHatchType::create);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
