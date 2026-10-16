package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockBiomeReader;
import es.degrassi.mmr.common.block.BlockCasing;
import es.degrassi.mmr.common.block.BlockCasing.CasingType;
import es.degrassi.mmr.common.block.BlockChunkloader;
import es.degrassi.mmr.common.block.BlockCommandExecutioner;
import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.block.BlockDimensionDetector;
import es.degrassi.mmr.common.block.BlockDurabilityHatch;
import es.degrassi.mmr.common.block.BlockEffectDispenser;
import es.degrassi.mmr.common.block.base.BlockEnergyHatch;
import es.degrassi.mmr.common.block.BlockEnergyInputHatch;
import es.degrassi.mmr.common.block.BlockEnergyOutputHatch;
import es.degrassi.mmr.common.block.BlockEntityDetector;
import es.degrassi.mmr.common.block.BlockEntityKiller;
import es.degrassi.mmr.common.block.BlockEntitySpawner;
import es.degrassi.mmr.common.block.base.BlockExperienceHatch;
import es.degrassi.mmr.common.block.BlockExperienceInputHatch;
import es.degrassi.mmr.common.block.BlockExperienceOutputHatch;
import es.degrassi.mmr.common.block.base.BlockFluidHatch;
import es.degrassi.mmr.common.block.BlockFluidInputHatch;
import es.degrassi.mmr.common.block.BlockFluidOutputHatch;
import es.degrassi.mmr.common.block.BlockFuelTank;
import es.degrassi.mmr.common.block.BlockEntityHealer;
import es.degrassi.mmr.common.block.BlockHeightMeter;
import es.degrassi.mmr.common.block.BlockEntityDamager;
import es.degrassi.mmr.common.block.BlockInputBus;
import es.degrassi.mmr.common.block.BlockOutputBus;
import es.degrassi.mmr.common.block.BlockRedstonePort;
import es.degrassi.mmr.common.block.BlockStructureChecker;
import es.degrassi.mmr.common.block.BlockTimeCounter;
import es.degrassi.mmr.common.block.BlockWeatherSensor;
import es.degrassi.mmr.common.block.ParallelHatchBlock;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

public class BlockRegistration {
  private BlockRegistration() {}
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModularMachineryReborn.MODID);

  public static final DeferredBlock<BlockCasing> CASING_PLAIN =
      BLOCKS.register(rootLC("casing_" + CasingType.PLAIN.getSerializedName()),
          () -> new BlockCasing(CasingType.PLAIN));
  public static final DeferredBlock<BlockCasing> CASING_VENT =
      BLOCKS.register(rootLC("casing_" + CasingType.VENT.getSerializedName()),
          () -> new BlockCasing(CasingType.VENT));
  public static final DeferredBlock<BlockCasing> CASING_FIREBOX =
      BLOCKS.register(rootLC("casing_" + CasingType.FIREBOX.getSerializedName()),
          () -> new BlockCasing(CasingType.FIREBOX));
  public static final DeferredBlock<BlockCasing> CASING_GEARBOX =
      BLOCKS.register(rootLC("casing_" + CasingType.GEARBOX.getSerializedName()),
        () -> new BlockCasing(CasingType.GEARBOX));
  public static final DeferredBlock<BlockCasing> CASING_REINFORCED =
      BLOCKS.register(rootLC("casing_" + CasingType.REINFORCED.getSerializedName()),
          () -> new BlockCasing(CasingType.REINFORCED));
  public static final DeferredBlock<BlockCasing> CASING_CIRCUITRY =
      BLOCKS.register(rootLC("casing_" + CasingType.CIRCUITRY.getSerializedName()),
          () -> new BlockCasing(CasingType.CIRCUITRY));

  public static final DeferredBlock<BlockController> CONTROLLER = BLOCKS.register(rootLC("controller"), BlockController::new);

  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_TINY = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.TINY.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.TINY));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_SMALL = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.SMALL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_NORMAL = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.NORMAL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.REINFORCED));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_BIG = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.BIG));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_HUGE = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.HUGE));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_INPUT_HATCH_ULTIMATE = BLOCKS.register(rootLC("energyinputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
      () -> new BlockEnergyInputHatch(EnergyHatchSize.ULTIMATE));

  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_TINY = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.TINY.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.TINY));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_SMALL = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.SMALL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_NORMAL = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.NORMAL));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.REINFORCED));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_BIG = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.BIG));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_HUGE = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.HUGE));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_ULTIMATE = BLOCKS.register(rootLC("energyoutputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
      () -> new BlockEnergyOutputHatch(EnergyHatchSize.ULTIMATE));

  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_TINY = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.TINY.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.TINY));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_SMALL = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.SMALL.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.SMALL));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_NORMAL = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.NORMAL.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.NORMAL));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_REINFORCED = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.REINFORCED));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_BIG = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.BIG.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.BIG));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_HUGE = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.HUGE.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.HUGE));
  public static final DeferredBlock<BlockInputBus> ITEM_INPUT_BUS_LUDICROUS = BLOCKS.register(rootLC("inputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
      () -> new BlockInputBus(ItemBusSize.LUDICROUS));

  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_TINY = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.TINY.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.TINY));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_SMALL = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.SMALL.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.SMALL));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_NORMAL = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.NORMAL.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.NORMAL));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_REINFORCED = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.REINFORCED));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_BIG = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.BIG.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.BIG));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_HUGE = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.HUGE.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.HUGE));
  public static final DeferredBlock<BlockOutputBus> ITEM_OUTPUT_BUS_LUDICROUS = BLOCKS.register(rootLC("outputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
      () -> new BlockOutputBus(ItemBusSize.LUDICROUS));

  public static final DeferredBlock<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_TINY = BLOCKS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.TINY.getSerializedName()),
      () -> new BlockDurabilityHatch(ItemDurabilityHatchSize.TINY));
  public static final DeferredBlock<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_SMALL = BLOCKS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.SMALL.getSerializedName()),
      () -> new BlockDurabilityHatch(ItemDurabilityHatchSize.SMALL));
  public static final DeferredBlock<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_NORMAL = BLOCKS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.NORMAL.getSerializedName()),
      () -> new BlockDurabilityHatch(ItemDurabilityHatchSize.NORMAL));
  public static final DeferredBlock<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_BIG = BLOCKS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.BIG.getSerializedName()),
      () -> new BlockDurabilityHatch(ItemDurabilityHatchSize.BIG));

  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_TINY = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.TINY.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.TINY));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_SMALL = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.SMALL));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_NORMAL = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.NORMAL));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.REINFORCED));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_BIG = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.BIG.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.BIG));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_HUGE = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.HUGE));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockFluidInputHatch> FLUID_INPUT_HATCH_VACUUM = BLOCKS.register(rootLC("fluidinputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
      () -> new BlockFluidInputHatch(FluidHatchSize.VACUUM));

  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_TINY = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.TINY.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.TINY));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_SMALL = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.SMALL));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_NORMAL = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.NORMAL));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.REINFORCED));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_BIG = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.BIG.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.BIG));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_HUGE = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.HUGE));
  public static final DeferredBlock<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockFluidHatch> FLUID_OUTPUT_HATCH_VACUUM = BLOCKS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
      () -> new BlockFluidOutputHatch(FluidHatchSize.VACUUM));

  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_TINY = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.TINY));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_SMALL = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.SMALL));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_NORMAL = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.NORMAL));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.REINFORCED));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_BIG = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.BIG));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_HUGE = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.HUGE));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_VACUUM = BLOCKS.register(rootLC("experienceinputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
      () -> new BlockExperienceInputHatch(ExperienceHatchSize.VACUUM));

  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_TINY = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.TINY));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_SMALL = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.SMALL));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_NORMAL = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.NORMAL));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_REINFORCED = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.REINFORCED));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_BIG = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.BIG));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_HUGE = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.HUGE));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_LUDICROUS = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.LUDICROUS));
  public static final DeferredBlock<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_VACUUM = BLOCKS.register(rootLC("experienceoutputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
      () -> new BlockExperienceOutputHatch(ExperienceHatchSize.VACUUM));

  public static final DeferredBlock<BlockDimensionDetector> DIMENSIONAL_DETECTOR = BLOCKS.register(rootLC("dimensional_detector"), BlockDimensionDetector::new);
  public static final DeferredBlock<BlockBiomeReader> BIOME_READER = BLOCKS.register(rootLC("biome_reader"), BlockBiomeReader::new);
  public static final DeferredBlock<BlockWeatherSensor> WEATHER_SENSOR = BLOCKS.register(rootLC("weather_sensor"), BlockWeatherSensor::new);
  public static final DeferredBlock<BlockTimeCounter> TIME_COUNTER = BLOCKS.register(rootLC("time_counter"), BlockTimeCounter::new);
  public static final DeferredBlock<BlockHeightMeter> HEIGHT_METER = BLOCKS.register(rootLC("height_meter"), BlockHeightMeter::new);
  public static final DeferredBlock<BlockChunkloader> CHUNKLOADER = BLOCKS.register(rootLC("chunkloader"), BlockChunkloader::new);

  public static final DeferredBlock<ParallelHatchBlock> PARALLEL_HATCH_BASIC =
      BLOCKS.register(rootLC("parallel_hatch_" + ParallelHatchSize.BASIC.getSerializedName()),
      () -> new ParallelHatchBlock(ParallelHatchSize.BASIC));
  public static final DeferredBlock<ParallelHatchBlock> PARALLEL_HATCH_MEDIUM =
      BLOCKS.register(rootLC("parallel_hatch_" + ParallelHatchSize.MEDIUM.getSerializedName()),
      () -> new ParallelHatchBlock(ParallelHatchSize.MEDIUM));
  public static final DeferredBlock<ParallelHatchBlock> PARALLEL_HATCH_ADVANCED =
      BLOCKS.register(rootLC("parallel_hatch_" + ParallelHatchSize.ADVANCED.getSerializedName()),
      () -> new ParallelHatchBlock(ParallelHatchSize.ADVANCED));
  public static final DeferredBlock<ParallelHatchBlock> PARALLEL_HATCH_ULTIMATE =
      BLOCKS.register(rootLC("parallel_hatch_" + ParallelHatchSize.ULTIMATE.getSerializedName()),
      () -> new ParallelHatchBlock(ParallelHatchSize.ULTIMATE));
  public static final DeferredBlock<ParallelHatchBlock> PARALLEL_HATCH_MAX =
      BLOCKS.register(rootLC("parallel_hatch_" + ParallelHatchSize.MAX.getSerializedName()),
      () -> new ParallelHatchBlock(ParallelHatchSize.MAX));

  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_TINY =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.TINY.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.TINY));
  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_SMALL =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.SMALL.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.SMALL));
  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_NORMAL =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.NORMAL.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.NORMAL));
  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_REINFORCED =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.REINFORCED.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.REINFORCED));
  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_BIG =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.BIG.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.BIG));
  public static final DeferredBlock<BlockFuelTank> FUEL_TANK_HUGE =
      BLOCKS.register(rootLC("fuel_tank_" + FuelTankSize.HUGE.getSerializedName()),
      () -> new BlockFuelTank(FuelTankSize.HUGE));

  public static final DeferredBlock<BlockEffectDispenser> EFFECT_DISPENSER_SMALL =
      BLOCKS.register(rootLC("effect_dispenser_" + EffectDispenserSize.SMALL.getSerializedName()),
      () -> new BlockEffectDispenser(EffectDispenserSize.SMALL));
  public static final DeferredBlock<BlockEffectDispenser> EFFECT_DISPENSER_MEDIUM =
      BLOCKS.register(rootLC("effect_dispenser_" + EffectDispenserSize.MEDIUM.getSerializedName()),
          () -> new BlockEffectDispenser(EffectDispenserSize.MEDIUM));
  public static final DeferredBlock<BlockEffectDispenser> EFFECT_DISPENSER_BIG =
      BLOCKS.register(rootLC("effect_dispenser_" + EffectDispenserSize.BIG.getSerializedName()),
          () -> new BlockEffectDispenser(EffectDispenserSize.BIG));

  public static final DeferredBlock<BlockEntityDetector> ENTITY_DETECTOR =
      BLOCKS.register(rootLC("entity_detector"),
      BlockEntityDetector::new);
  public static final DeferredBlock<BlockEntityDamager> ENTITY_DAMAGER =
      BLOCKS.register(rootLC("entity_damager"),
      BlockEntityDamager::new);
  public static final DeferredBlock<BlockEntityHealer> ENTITY_HEALER =
      BLOCKS.register(rootLC("entity_healer"),
      BlockEntityHealer::new);
  public static final DeferredBlock<BlockEntitySpawner> ENTITY_SPAWNER =
      BLOCKS.register(rootLC("entity_spawner"),
      BlockEntitySpawner::new);
  public static final DeferredBlock<BlockEntityKiller> ENTITY_KILLER =
      BLOCKS.register(rootLC("entity_killer"),
      BlockEntityKiller::new);

  public static final DeferredBlock<BlockStructureChecker> STRUCTURE_CHECKER =
      BLOCKS.register(rootLC("structure_checker"),
      BlockStructureChecker::new);

  public static final DeferredBlock<BlockRedstonePort> REDSTONE_PORT =
      BLOCKS.register(rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      BlockRedstonePort::new);

  public static final DeferredBlock<BlockCommandExecutioner> COMMAND_EXECUTIONER =
      BLOCKS.register(rootLC("command_executioner"),
      BlockCommandExecutioner::new);

  public static void register(final IEventBus bus) {
    BLOCKS.register(bus);
  }
}
