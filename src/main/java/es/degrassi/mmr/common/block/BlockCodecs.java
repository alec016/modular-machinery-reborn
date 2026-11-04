package es.degrassi.mmr.common.block;

import es.degrassi.experiencelib.api.codec.EnumCodec;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.experiencelib.api.codec.NamedRecordCodec;
import es.degrassi.mmr.common.block.base.SizedBlockHatch;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

interface BlockCodecs {
  NamedMapCodec<BlockBiomeReader> BIOME_READER = simpleCodec(BlockBiomeReader::new, "Biome Reader");
  NamedMapCodec<BlockChunkloader> CHUNKLOADER = simpleCodec(BlockChunkloader::new, "Chunkloader");
  NamedMapCodec<BlockCommandExecutioner> COMMAND_EXECUTIONER = simpleCodec(BlockCommandExecutioner::new, "Command Executioner");
  NamedMapCodec<BlockController> CONTROLLER = simpleCodec(BlockController::new, "Controller");
  NamedMapCodec<BlockDimensionDetector> DIMENSION_DETECTOR = simpleCodec(BlockDimensionDetector::new, "Dimension Detector");
  NamedMapCodec<BlockHeightMeter> HEIGHT_METER = simpleCodec(BlockHeightMeter::new, "Height Meter");
  NamedMapCodec<BlockRedstonePort> REDSTONE = simpleCodec(BlockRedstonePort::new, "Redstone Port");
  NamedMapCodec<BlockStructureChecker> STRUCTURE_CHECKER = simpleCodec(BlockStructureChecker::new, "Structure Checker");
  NamedMapCodec<BlockTimeCounter> TIME_COUNTER = simpleCodec(BlockTimeCounter::new, "Time Counter");
  NamedMapCodec<BlockWeatherSensor> WEATHER_SENSOR = simpleCodec(BlockWeatherSensor::new, "Weather Sensor");

  NamedMapCodec<BlockEntityDamager> ENTITY_DAMAGER = simpleCodec(BlockEntityDamager::new, "Entity Damager");
  NamedMapCodec<BlockEntityDetector> ENTITY_DETECTOR = simpleCodec(BlockEntityDetector::new, "Entity Detector");
  NamedMapCodec<BlockEntityHealer> ENTITY_HEALER = simpleCodec(BlockEntityHealer::new, "Entity Healer");
  NamedMapCodec<BlockEntityKiller> ENTITY_KILLER = simpleCodec(BlockEntityKiller::new, "Entity Killer");
  NamedMapCodec<BlockEntitySpawner> ENTITY_SPAWNER = simpleCodec(BlockEntitySpawner::new, "Entity Spawner");

  NamedMapCodec<BlockCasing> CASING = sizedCodec(BlockCasing::new, BlockCasing.CasingType.class, "Casing");
  NamedMapCodec<BlockInputBus> ITEM_INPUT_BUS = sizedCodec(BlockInputBus::new, ItemBusSize.class, "Item Input Bus");
  NamedMapCodec<BlockOutputBus> ITEM_OUTPUT_BUS = sizedCodec(BlockOutputBus::new, ItemBusSize.class, "Item Output Bus");
  NamedMapCodec<BlockEnergyInputHatch> ENERGY_INPUT = sizedCodec(BlockEnergyInputHatch::new, EnergyHatchSize.class, "Energy Input Hatch");
  NamedMapCodec<BlockEnergyOutputHatch> ENERGY_OUTPUT = sizedCodec(BlockEnergyOutputHatch::new, EnergyHatchSize.class, "Energy Output Hatch");
  NamedMapCodec<BlockExperienceInputHatch> XP_INPUT = sizedCodec(BlockExperienceInputHatch::new, ExperienceHatchSize.class, "Experience Input Hatch");
  NamedMapCodec<BlockExperienceOutputHatch> XP_OUTPUT = sizedCodec(BlockExperienceOutputHatch::new, ExperienceHatchSize.class, "Experience Output Hatch");
  NamedMapCodec<BlockFluidInputHatch> FLUID_INPUT = sizedCodec(BlockFluidInputHatch::new, FluidHatchSize.class, "Fluid Input Hatch");
  NamedMapCodec<BlockFluidOutputHatch> FLUID_OUTPUT = sizedCodec(BlockFluidOutputHatch::new, FluidHatchSize.class, "Fluid Output Hatch");

  NamedMapCodec<BlockFuelTank> FUEL = sizedCodec(BlockFuelTank::new, FuelTankSize.class, "Fuel Tank Hatch");
  NamedMapCodec<BlockDurabilityHatch> DURABILITY = sizedCodec(BlockDurabilityHatch::new, ItemDurabilityHatchSize.class, "Durability Hatch");
  NamedMapCodec<BlockEffectDispenser> EFFECT_DISPENSER = sizedCodec(BlockEffectDispenser::new, EffectDispenserSize.class, "Effect Dispenser");
  NamedMapCodec<BlockParallelHatch> PARALLEL = sizedCodec(BlockParallelHatch::new, ParallelHatchSize.class, "Parallel Hatch");

  private static <B extends Block & SizedBlockHatch<E>, E extends Enum<E>> NamedMapCodec<B> sizedCodec(BiFunction<BlockBehaviour.Properties, E, B> factory, Class<E> enumClass, String name) {
    return NamedCodec.record(i -> i.group(propertiesCodec(), EnumCodec.of(enumClass).fieldOf("size").forGetter(SizedBlockHatch::getSize)).apply(i, factory), name);
  }

  private static <B extends Block> NamedMapCodec<B> simpleCodec(Function<BlockBehaviour.Properties, B> factory, String name) {
    return NamedCodec.record(i -> i.group(propertiesCodec()).apply(i, factory), name);
  }

  private static <B extends Block> NamedRecordCodec<B, BlockBehaviour.Properties> propertiesCodec() {
    return NamedCodec.of(BlockBehaviour.Properties.CODEC).fieldOf("properties").forGetter(BlockBehaviour::properties);
  }
}
