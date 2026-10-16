package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BlockDynamicColor;
import es.degrassi.mmr.common.entity.BiomeReaderEntity;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import es.degrassi.mmr.common.entity.CommandExecutionerEntity;
import es.degrassi.mmr.common.entity.DimensionalDetectorEntity;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmr.common.entity.EnergyOutputHatchEntity;
import es.degrassi.mmr.common.entity.EntityDetectorEntity;
import es.degrassi.mmr.common.entity.EntitySpawnerEntity;
import es.degrassi.mmr.common.entity.EntityKillerEntity;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
import es.degrassi.mmr.common.entity.FluidInputHatchEntity;
import es.degrassi.mmr.common.entity.FluidOutputHatchEntity;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.entity.EntityHealerEntity;
import es.degrassi.mmr.common.entity.HeightMeterEntity;
import es.degrassi.mmr.common.entity.EntityDamagerEntity;
import es.degrassi.mmr.common.entity.ItemInputBusEntity;
import es.degrassi.mmr.common.entity.ItemOutputBusEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import es.degrassi.mmr.common.entity.TimeCounterEntity;
import es.degrassi.mmr.common.entity.WeatherSensorEntity;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

public class EntityRegistration {
  private EntityRegistration() {}
  public static final DeferredRegister<BlockEntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<BlockEntityType<ColorableMachineComponentEntity>> COLORABLE_MACHINE = ENTITY_TYPE.register(
      rootLC("colorable_entity"),
      () -> new BlockEntityType<>(
          ColorableMachineComponentEntity::new,
          Set.of(
              BlockRegistration.BLOCKS.getEntries().stream().filter(holder -> holder.get() instanceof BlockDynamicColor).map(DeferredHolder::get).toArray(Block[]::new)
          ))
  );

  public static final Supplier<BlockEntityType<MachineControllerEntity>> CONTROLLER = ENTITY_TYPE.register(
      rootLC("controller"),
      () -> new BlockEntityType<>(
          MachineControllerEntity::new,
          validMachineBlocks())
  );

  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_INPUT_HATCH = ENTITY_TYPE.register(
      rootLC("energy_hatch_input"),
      () -> new BlockEntityType<>(
          EnergyInputHatchEntity::new,
          Set.of(
              BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get()
          ))
  );
  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_OUTPUT_HATCH = ENTITY_TYPE.register(
      rootLC("energy_hatch_output"),
      () -> new BlockEntityType<>(
          EnergyOutputHatchEntity::new,
          Set.of(
              BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get()
          ))
  );
  public static final Supplier<BlockEntityType<ItemInputBusEntity>> ITEM_INPUT_BUS = ENTITY_TYPE.register(
      rootLC("item_input_bus"),
      () -> new BlockEntityType<>(
          ItemInputBusEntity::new,
          Set.of(
              BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
              BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
              BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
              BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
              BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
              BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
              BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get()
          ))
  );
  public static final Supplier<BlockEntityType<ItemOutputBusEntity>> ITEM_OUTPUT_BUS = ENTITY_TYPE.register(
      rootLC("item_output_bus"),
      () -> new BlockEntityType<>(
          ItemOutputBusEntity::new,
          Set.of(
              BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
              BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get()
          ))
  );
  public static final Supplier<BlockEntityType<DurabilityHatchEntity>> ITEM_DURABILITY_HATCH =
      ENTITY_TYPE.register(
      rootLC("item_durability_hatch"),
      () -> new BlockEntityType<>(
          DurabilityHatchEntity::new,
          Set.of(
              BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get(),
              BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get(),
              BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get(),
              BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get()
          ))
  );
  public static final Supplier<BlockEntityType<FluidInputHatchEntity>> FLUID_INPUT_HATCH = ENTITY_TYPE.register(
      rootLC("fluid_hatch_input"),
      () -> new BlockEntityType<>(
          FluidInputHatchEntity::new,
          Set.of(
              BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
              BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
              BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
              BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
              BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
              BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
              BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get()
          ))
  );
  public static final Supplier<BlockEntityType<FluidOutputHatchEntity>> FLUID_OUTPUT_HATCH = ENTITY_TYPE.register(
      rootLC("fluid_hatch_output"),
      () -> new BlockEntityType<>(
          FluidOutputHatchEntity::new,
          Set.of(
              BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get()
          ))
  );
  public static final Supplier<BlockEntityType<ExperienceInputHatchEntity>> EXPERIENCE_INPUT_HATCH =
      ENTITY_TYPE.register(
          rootLC("experience_hatch_input"),
      () -> new BlockEntityType<>(
          ExperienceInputHatchEntity::new,
          Set.of(
              BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get()
          ))
  );
  public static final Supplier<BlockEntityType<ExperienceOutputHatchEntity>> EXPERIENCE_OUTPUT_HATCH =
      ENTITY_TYPE.register(
          rootLC("experience_hatch_output"),
      () -> new BlockEntityType<>(
          ExperienceOutputHatchEntity::new,
          Set.of(
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get(),
              BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get()
          ))
  );

  public static final Supplier<BlockEntityType<DimensionalDetectorEntity>> DIMENSIONAL_DETECTOR = ENTITY_TYPE.register(
      rootLC("dimensional_detector"),
      () -> new BlockEntityType<>(
          DimensionalDetectorEntity::new,
          Set.of(
              BlockRegistration.DIMENSIONAL_DETECTOR.get()
          ))
  );

  public static final Supplier<BlockEntityType<BiomeReaderEntity>> BIOME_READER = ENTITY_TYPE.register(
      rootLC("biome_reader"),
      () -> new BlockEntityType<>(
          BiomeReaderEntity::new,
          Set.of(
              BlockRegistration.BIOME_READER.get()
          ))
  );

  public static final Supplier<BlockEntityType<WeatherSensorEntity>> WEATHER_SENSOR = ENTITY_TYPE.register(
      rootLC("weather_sensor"),
      () -> new BlockEntityType<>(
          WeatherSensorEntity::new,
          Set.of(
              BlockRegistration.WEATHER_SENSOR.get()
          ))
  );

  public static final Supplier<BlockEntityType<TimeCounterEntity>> TIME_COUNTER = ENTITY_TYPE.register(
      rootLC("time_counter"),
      () -> new BlockEntityType<>(
          TimeCounterEntity::new,
          Set.of(
              BlockRegistration.TIME_COUNTER.get()
          ))
  );

  public static final Supplier<BlockEntityType<HeightMeterEntity>> HEIGHT_METER = ENTITY_TYPE.register(
      rootLC("height_meter"),
      () -> new BlockEntityType<>(
          HeightMeterEntity::new,
          Set.of(
              BlockRegistration.HEIGHT_METER.get()
          ))
  );

  public static final Supplier<BlockEntityType<ChunkloaderEntity>> CHUNKLOADER = ENTITY_TYPE.register(
      rootLC("chunkloader"),
      () -> new BlockEntityType<>(
          ChunkloaderEntity::new,
          Set.of(
              BlockRegistration.CHUNKLOADER.get()
          ))
  );

  public static final Supplier<BlockEntityType<ParallelHatchEntity>> PARALLEL_HATCH = ENTITY_TYPE.register(
      rootLC("parallel_hatch"),
      () -> new BlockEntityType<>(
          ParallelHatchEntity::new,
          Set.of(
              BlockRegistration.PARALLEL_HATCH_BASIC.get(),
              BlockRegistration.PARALLEL_HATCH_MEDIUM.get(),
              BlockRegistration.PARALLEL_HATCH_ADVANCED.get(),
              BlockRegistration.PARALLEL_HATCH_ULTIMATE.get(),
              BlockRegistration.PARALLEL_HATCH_MAX.get()
          ))
  );

  public static final Supplier<BlockEntityType<FuelTankEntity>> FUEL_TANK = ENTITY_TYPE.register(
      rootLC("fuel_tank"),
      () -> new BlockEntityType<>(
          FuelTankEntity::new,
          Set.of(
              BlockRegistration.FUEL_TANK_TINY.get(),
              BlockRegistration.FUEL_TANK_SMALL.get(),
              BlockRegistration.FUEL_TANK_NORMAL.get(),
              BlockRegistration.FUEL_TANK_REINFORCED.get(),
              BlockRegistration.FUEL_TANK_BIG.get(),
              BlockRegistration.FUEL_TANK_HUGE.get()
          ))
  );

  public static final Supplier<BlockEntityType<EffectDispenserEntity>> EFFECT_DISPENSER = ENTITY_TYPE.register(
      rootLC("effect_dispenser"),
      () -> new BlockEntityType<>(
          EffectDispenserEntity::new,
          Set.of(
              BlockRegistration.EFFECT_DISPENSER_SMALL.get(),
              BlockRegistration.EFFECT_DISPENSER_MEDIUM.get(),
              BlockRegistration.EFFECT_DISPENSER_BIG.get()
          ))
  );

  public static final Supplier<BlockEntityType<EntityDetectorEntity>> ENTITY_DETECTOR = ENTITY_TYPE.register(
     rootLC("entity_detector"),
      () -> new BlockEntityType<>(
          EntityDetectorEntity::new,
          Set.of(
              BlockRegistration.ENTITY_DETECTOR.get()
          ))
  );

  public static final Supplier<BlockEntityType<EntitySpawnerEntity>> ENTITY_SPAWNER = ENTITY_TYPE.register(
      rootLC("entity_spawner"),
      () -> new BlockEntityType<>(
          EntitySpawnerEntity::new,
          Set.of(
              BlockRegistration.ENTITY_SPAWNER.get()
          ))
  );

  public static final Supplier<BlockEntityType<EntityDamagerEntity>> ENTITY_DAMAGER = ENTITY_TYPE.register(
      rootLC("entity_damager"),
      () -> new BlockEntityType<>(
          EntityDamagerEntity::new,
          Set.of(
              BlockRegistration.ENTITY_DAMAGER.get()
          ))
  );

  public static final Supplier<BlockEntityType<EntityHealerEntity>> ENTITY_HEALER = ENTITY_TYPE.register(
      rootLC("entity_healer"),
      () -> new BlockEntityType<>(
          EntityHealerEntity::new,
          Set.of(
              BlockRegistration.ENTITY_HEALER.get()
          ))
  );

  public static final Supplier<BlockEntityType<EntityKillerEntity>> ENTITY_KILLER = ENTITY_TYPE.register(
      rootLC("entity_killer"),
      () -> new BlockEntityType<>(
          EntityKillerEntity::new,
          Set.of(
              BlockRegistration.ENTITY_KILLER.get()
          ))
  );

  public static final Supplier<BlockEntityType<StructureCheckerEntity>> STRUCTURE_CHECKER = ENTITY_TYPE.register(
      rootLC("structure_checker"),
      () -> new BlockEntityType<>(
          StructureCheckerEntity::new,
          Set.of(
              BlockRegistration.STRUCTURE_CHECKER.get()
          ))
  );

  public static final Supplier<BlockEntityType<RedstonePortEntity>> REDSTONE_PORT = ENTITY_TYPE.register(
      rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      () -> new BlockEntityType<>(
          RedstonePortEntity::new,
          Set.of(
              BlockRegistration.REDSTONE_PORT.get()
          ))
  );

  public static final Supplier<BlockEntityType<CommandExecutionerEntity>> COMMAND_EXECUTIONER = ENTITY_TYPE.register(
      rootLC("command_executioner"),
      () -> new BlockEntityType<>(
          CommandExecutionerEntity::new,
          Set.of(
              BlockRegistration.COMMAND_EXECUTIONER.get()
          ))
  );

  public static void register(final IEventBus bus) {
    ENTITY_TYPE.register(bus);
  }

  private static Set<Block> validMachineBlocks() {
    Set<Block> validBlocks = new HashSet<>();
    validBlocks.add(BlockRegistration.CONTROLLER.get());
    validBlocks.addAll(ModularMachineryReborn.MACHINES_BLOCK.values());
    return validBlocks;
  }
}
