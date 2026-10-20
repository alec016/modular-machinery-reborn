package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.definition.BlockDefinition;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BlockDynamicColor;
import es.degrassi.mmr.common.entity.BiomeReaderEntity;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import es.degrassi.mmr.common.entity.CommandExecutionerEntity;
import es.degrassi.mmr.common.entity.DimensionalDetectorEntity;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmr.common.entity.EnergyOutputHatchEntity;
import es.degrassi.mmr.common.entity.EntityDamagerEntity;
import es.degrassi.mmr.common.entity.EntityDetectorEntity;
import es.degrassi.mmr.common.entity.EntityHealerEntity;
import es.degrassi.mmr.common.entity.EntityKillerEntity;
import es.degrassi.mmr.common.entity.EntitySpawnerEntity;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
import es.degrassi.mmr.common.entity.FluidInputHatchEntity;
import es.degrassi.mmr.common.entity.FluidOutputHatchEntity;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.entity.HeightMeterEntity;
import es.degrassi.mmr.common.entity.ItemInputBusEntity;
import es.degrassi.mmr.common.entity.ItemOutputBusEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import es.degrassi.mmr.common.entity.TimeCounterEntity;
import es.degrassi.mmr.common.entity.WeatherSensorEntity;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityRegistration {
  public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<BlockEntityType<ColorableMachineComponentEntity>> COLORABLE_MACHINE = blockEntity(
      rootLC("colorable_entity"),
      ColorableMachineComponentEntity::new,
      () -> Set.of(BlockRegistration.BLOCKS.stream().filter(holder -> holder.block() instanceof BlockDynamicColor).map(BlockDefinition::block).toArray(Block[]::new))
  );

  public static final Supplier<BlockEntityType<MachineControllerEntity>> CONTROLLER = blockEntity(
      rootLC("controller"),
      MachineControllerEntity::new,
      EntityRegistration::validMachineBlocks
  );

  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_INPUT_HATCH = blockEntity(
      rootLC("energy_hatch_input"),
      EnergyInputHatchEntity::new,
      () -> Set.of(BlockRegistration.ENERGY_INPUT_HATCH_TINY.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_SMALL.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_BIG.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_HUGE.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.block())
  );
  public static final Supplier<BlockEntityType<EnergyHatchEntity>> ENERGY_OUTPUT_HATCH = blockEntity(
      rootLC("energy_hatch_output"),
      EnergyOutputHatchEntity::new,
      () -> Set.of(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.block())
  );
  public static final Supplier<BlockEntityType<ItemInputBusEntity>> ITEM_INPUT_BUS = blockEntity(
      rootLC("item_input_bus"),
      ItemInputBusEntity::new,
      () -> Set.of(BlockRegistration.ITEM_INPUT_BUS_TINY.block(),
      BlockRegistration.ITEM_INPUT_BUS_SMALL.block(),
      BlockRegistration.ITEM_INPUT_BUS_NORMAL.block(),
      BlockRegistration.ITEM_INPUT_BUS_REINFORCED.block(),
      BlockRegistration.ITEM_INPUT_BUS_BIG.block(),
      BlockRegistration.ITEM_INPUT_BUS_HUGE.block(),
      BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.block())
  );
  public static final Supplier<BlockEntityType<ItemOutputBusEntity>> ITEM_OUTPUT_BUS = blockEntity(
      rootLC("item_output_bus"),
      ItemOutputBusEntity::new,
      () -> Set.of(BlockRegistration.ITEM_OUTPUT_BUS_TINY.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_SMALL.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_BIG.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_HUGE.block(),
      BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.block())
  );
  public static final Supplier<BlockEntityType<DurabilityHatchEntity>> ITEM_DURABILITY_HATCH = blockEntity(
      rootLC("item_durability_hatch"),
      DurabilityHatchEntity::new,
      () -> Set.of(BlockRegistration.ITEM_DURABILITY_HATCH_TINY.block(),
      BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.block(),
      BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.block(),
      BlockRegistration.ITEM_DURABILITY_HATCH_BIG.block())
  );
  public static final Supplier<BlockEntityType<FluidInputHatchEntity>> FLUID_INPUT_HATCH = blockEntity(
      rootLC("fluid_hatch_input"),
      FluidInputHatchEntity::new,
      () -> Set.of(BlockRegistration.FLUID_INPUT_HATCH_TINY.block(),
      BlockRegistration.FLUID_INPUT_HATCH_SMALL.block(),
      BlockRegistration.FLUID_INPUT_HATCH_NORMAL.block(),
      BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.block(),
      BlockRegistration.FLUID_INPUT_HATCH_BIG.block(),
      BlockRegistration.FLUID_INPUT_HATCH_HUGE.block(),
      BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.FLUID_INPUT_HATCH_VACUUM.block())
  );
  public static final Supplier<BlockEntityType<FluidOutputHatchEntity>> FLUID_OUTPUT_HATCH = blockEntity(
      rootLC("fluid_hatch_output"),
      FluidOutputHatchEntity::new,
      () -> Set.of(BlockRegistration.FLUID_OUTPUT_HATCH_TINY.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_BIG.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.block())
  );
  public static final Supplier<BlockEntityType<ExperienceInputHatchEntity>> EXPERIENCE_INPUT_HATCH = blockEntity(
      rootLC("experience_hatch_input"),
      ExperienceInputHatchEntity::new,
      () -> Set.of(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.block())
  );
  public static final Supplier<BlockEntityType<ExperienceOutputHatchEntity>> EXPERIENCE_OUTPUT_HATCH = blockEntity(
      rootLC("experience_hatch_output"),
      ExperienceOutputHatchEntity::new,
      () -> Set.of(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.block(),
      BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.block())
  );

  public static final Supplier<BlockEntityType<DimensionalDetectorEntity>> DIMENSIONAL_DETECTOR = blockEntity(
      rootLC("dimensional_detector"),
      DimensionalDetectorEntity::new,
      () -> Set.of(BlockRegistration.DIMENSIONAL_DETECTOR.block())
  );

  public static final Supplier<BlockEntityType<BiomeReaderEntity>> BIOME_READER = blockEntity(
      rootLC("biome_reader"),
      BiomeReaderEntity::new,
      () -> Set.of(BlockRegistration.BIOME_READER.block())
  );

  public static final Supplier<BlockEntityType<WeatherSensorEntity>> WEATHER_SENSOR = blockEntity(
      rootLC("weather_sensor"),
      WeatherSensorEntity::new,
      () -> Set.of(BlockRegistration.WEATHER_SENSOR.block())
  );

  public static final Supplier<BlockEntityType<TimeCounterEntity>> TIME_COUNTER = blockEntity(
      rootLC("time_counter"),
      TimeCounterEntity::new,
      () -> Set.of(BlockRegistration.TIME_COUNTER.block())
  );

  public static final Supplier<BlockEntityType<HeightMeterEntity>> HEIGHT_METER = blockEntity(
      rootLC("height_meter"),
      HeightMeterEntity::new,
      () -> Set.of(BlockRegistration.HEIGHT_METER.block())
  );

  public static final Supplier<BlockEntityType<ChunkloaderEntity>> CHUNKLOADER = blockEntity(
      rootLC("chunkloader"),
      ChunkloaderEntity::new,
      () -> Set.of(BlockRegistration.CHUNKLOADER.block())
  );

  public static final Supplier<BlockEntityType<ParallelHatchEntity>> PARALLEL_HATCH = blockEntity(
      rootLC("parallel_hatch"),
      ParallelHatchEntity::new,
      () -> Set.of(BlockRegistration.PARALLEL_HATCH_BASIC.block(),
      BlockRegistration.PARALLEL_HATCH_MEDIUM.block(),
      BlockRegistration.PARALLEL_HATCH_ADVANCED.block(),
      BlockRegistration.PARALLEL_HATCH_ULTIMATE.block(),
      BlockRegistration.PARALLEL_HATCH_MAX.block())
  );

  public static final Supplier<BlockEntityType<FuelTankEntity>> FUEL_TANK = blockEntity(
      rootLC("fuel_tank"),
      FuelTankEntity::new,
      () -> Set.of(BlockRegistration.FUEL_TANK_TINY.block(),
      BlockRegistration.FUEL_TANK_SMALL.block(),
      BlockRegistration.FUEL_TANK_NORMAL.block(),
      BlockRegistration.FUEL_TANK_REINFORCED.block(),
      BlockRegistration.FUEL_TANK_BIG.block(),
      BlockRegistration.FUEL_TANK_HUGE.block())
  );

  public static final Supplier<BlockEntityType<EffectDispenserEntity>> EFFECT_DISPENSER = blockEntity(
      rootLC("effect_dispenser"),
      EffectDispenserEntity::new,
      () -> Set.of(BlockRegistration.EFFECT_DISPENSER_SMALL.block(),
      BlockRegistration.EFFECT_DISPENSER_MEDIUM.block(),
      BlockRegistration.EFFECT_DISPENSER_BIG.block())
  );

  public static final Supplier<BlockEntityType<EntityDetectorEntity>> ENTITY_DETECTOR = blockEntity(
      rootLC("entity_detector"),
      EntityDetectorEntity::new,
      () -> Set.of(BlockRegistration.ENTITY_DETECTOR.block())
  );

  public static final Supplier<BlockEntityType<EntitySpawnerEntity>> ENTITY_SPAWNER = blockEntity(
      rootLC("entity_spawner"),
      EntitySpawnerEntity::new,
      () -> Set.of(BlockRegistration.ENTITY_SPAWNER.block())
  );

  public static final Supplier<BlockEntityType<EntityDamagerEntity>> ENTITY_DAMAGER = blockEntity(
      rootLC("entity_damager"),
      EntityDamagerEntity::new,
      () -> Set.of(BlockRegistration.ENTITY_DAMAGER.block())
  );

  public static final Supplier<BlockEntityType<EntityHealerEntity>> ENTITY_HEALER = blockEntity(
      rootLC("entity_healer"),
      EntityHealerEntity::new,
      () -> Set.of(BlockRegistration.ENTITY_HEALER.block())
  );

  public static final Supplier<BlockEntityType<EntityKillerEntity>> ENTITY_KILLER = blockEntity(
      rootLC("entity_killer"),
      EntityKillerEntity::new,
      () -> Set.of(BlockRegistration.ENTITY_KILLER.block())
  );

  public static final Supplier<BlockEntityType<StructureCheckerEntity>> STRUCTURE_CHECKER = blockEntity(
      rootLC("structure_checker"),
      StructureCheckerEntity::new,
      () -> Set.of(BlockRegistration.STRUCTURE_CHECKER.block())
  );

  public static final Supplier<BlockEntityType<RedstonePortEntity>> REDSTONE_PORT = blockEntity(
      rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      RedstonePortEntity::new,
      () -> Set.of(BlockRegistration.REDSTONE_PORT.block())
  );

  public static final Supplier<BlockEntityType<CommandExecutionerEntity>> COMMAND_EXECUTIONER = blockEntity(
      rootLC("command_executioner"),
      CommandExecutionerEntity::new,
      () -> Set.of(BlockRegistration.COMMAND_EXECUTIONER.block())
  );

  static <T extends BlockEntity> Supplier<BlockEntityType<T>> blockEntity(
      String id,
      BlockEntityType.BlockEntitySupplier<T> factory,
      Supplier<Set<Block>> validBlocks
  ) {
    return DR.register(id, () -> new BlockEntityType<>(factory, validBlocks.get()));
  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }

  private static Set<Block> validMachineBlocks() {
    Set<Block> validBlocks = new HashSet<>();
    validBlocks.add(BlockRegistration.CONTROLLER.block());
    validBlocks.addAll(ModularMachineryReborn.MACHINES_BLOCK.values());
    return validBlocks;
  }
}
