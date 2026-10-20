package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.definition.BlockDefinition;
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
import es.degrassi.mmr.common.block.BlockEnergyInputHatch;
import es.degrassi.mmr.common.block.BlockEnergyOutputHatch;
import es.degrassi.mmr.common.block.BlockEntityDamager;
import es.degrassi.mmr.common.block.BlockEntityDetector;
import es.degrassi.mmr.common.block.BlockEntityHealer;
import es.degrassi.mmr.common.block.BlockEntityKiller;
import es.degrassi.mmr.common.block.BlockEntitySpawner;
import es.degrassi.mmr.common.block.BlockExperienceInputHatch;
import es.degrassi.mmr.common.block.BlockExperienceOutputHatch;
import es.degrassi.mmr.common.block.BlockFluidInputHatch;
import es.degrassi.mmr.common.block.BlockFluidOutputHatch;
import es.degrassi.mmr.common.block.BlockFuelTank;
import es.degrassi.mmr.common.block.BlockHeightMeter;
import es.degrassi.mmr.common.block.BlockInputBus;
import es.degrassi.mmr.common.block.BlockOutputBus;
import es.degrassi.mmr.common.block.BlockRedstonePort;
import es.degrassi.mmr.common.block.BlockStructureChecker;
import es.degrassi.mmr.common.block.BlockTimeCounter;
import es.degrassi.mmr.common.block.BlockWeatherSensor;
import es.degrassi.mmr.common.block.ParallelHatchBlock;
import es.degrassi.mmr.common.block.base.BlockEnergyHatch;
import es.degrassi.mmr.common.block.base.BlockExperienceHatch;
import es.degrassi.mmr.common.block.base.BlockFluidHatch;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.item.BiomeReaderItem;
import es.degrassi.mmr.common.item.CasingItem;
import es.degrassi.mmr.common.item.ChunkloaderItem;
import es.degrassi.mmr.common.item.CommandExecutionerItem;
import es.degrassi.mmr.common.item.ControllerItem;
import es.degrassi.mmr.common.item.DimensionalDetectorItem;
import es.degrassi.mmr.common.item.DurabilityHatchItem;
import es.degrassi.mmr.common.item.EffectDispenserItem;
import es.degrassi.mmr.common.item.EnergyHatchItem;
import es.degrassi.mmr.common.item.EntityItem;
import es.degrassi.mmr.common.item.ExperienceHatchItem;
import es.degrassi.mmr.common.item.FluidHatchItem;
import es.degrassi.mmr.common.item.FuelTankItem;
import es.degrassi.mmr.common.item.HeightMeterItem;
import es.degrassi.mmr.common.item.InputBusItem;
import es.degrassi.mmr.common.item.OutputBusItem;
import es.degrassi.mmr.common.item.ParallelHatchItem;
import es.degrassi.mmr.common.item.RedstonePortItem;
import es.degrassi.mmr.common.item.StructureCheckerItem;
import es.degrassi.mmr.common.item.TimeCounterItem;
import es.degrassi.mmr.common.item.WeatherSensorItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockRegistration {

  public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
  public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(ModularMachineryReborn.MODID);

  public static final BlockDefinition<BlockCasing> CASING_PLAIN = blockWithItem(
      "Plain Casing",
      rootLC("casing_" + CasingType.PLAIN.getSerializedName()),
      p -> new BlockCasing(p, CasingType.PLAIN),
      CasingItem::new
  );
  public static final BlockDefinition<BlockCasing> CASING_VENT = blockWithItem(
      "Vent Casing",
      rootLC("casing_" + CasingType.VENT.getSerializedName()),
     id -> new BlockCasing(id, CasingType.VENT),
      CasingItem::new
  );
  public static final BlockDefinition<BlockCasing> CASING_FIREBOX = blockWithItem(
      "Firebox Casing",
      rootLC("casing_" + CasingType.FIREBOX.getSerializedName()),
      id -> new BlockCasing(id, CasingType.FIREBOX),
      CasingItem::new
  );
  public static final BlockDefinition<BlockCasing> CASING_GEARBOX = blockWithItem(
      "Gearbox Casing",
      rootLC("casing_" + CasingType.GEARBOX.getSerializedName()),
      id -> new BlockCasing(id, CasingType.GEARBOX),
      CasingItem::new
  );
  public static final BlockDefinition<BlockCasing> CASING_REINFORCED = blockWithItem(
      "Reinforced Casing",
      rootLC("casing_" + CasingType.REINFORCED.getSerializedName()),
      id -> new BlockCasing(id, CasingType.REINFORCED),
      CasingItem::new
  );
  public static final BlockDefinition<BlockCasing> CASING_CIRCUITRY = blockWithItem(
      "Circuitry Casing",
      rootLC("casing_" + CasingType.CIRCUITRY.getSerializedName()),
      id -> new BlockCasing(id, CasingType.CIRCUITRY),
      CasingItem::new
  );

  public static final BlockDefinition<BlockController> CONTROLLER = blockWithItem(
      "Machine Controller",
      rootLC("controller"), 
      BlockController::new,
      ControllerItem::new
  );

  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_TINY = blockWithItem(
      "Tiny Energy Input Hatch",
      rootLC("energyinputhatch_" + EnergyHatchSize.TINY.getSerializedName()), 
      props -> new BlockEnergyInputHatch(props, EnergyHatchSize.TINY),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_SMALL = blockWithItem(
      "Small Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.SMALL),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_NORMAL = blockWithItem(
      "Normal Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.NORMAL),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.REINFORCED),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_BIG = blockWithItem(
      "Big Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.BIG),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_HUGE = blockWithItem(
      "Huge Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.HUGE),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.LUDICROUS),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_INPUT_HATCH_ULTIMATE = blockWithItem(
      "Ultimate Energy Input Hatch", 
      rootLC("energyinputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
      id -> new BlockEnergyInputHatch(id, EnergyHatchSize.ULTIMATE),
      EnergyHatchItem::new
  );

  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_TINY = blockWithItem(
      "Tiny Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.TINY.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.TINY),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_SMALL = blockWithItem(
      "Small Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.SMALL),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_NORMAL = blockWithItem(
      "Normal Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.NORMAL),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.REINFORCED),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_BIG = blockWithItem(
      "Big Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.BIG),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_HUGE = blockWithItem(
      "Huge Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.HUGE),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.LUDICROUS),
      EnergyHatchItem::new
  );
  public static final BlockDefinition<BlockEnergyHatch> ENERGY_OUTPUT_HATCH_ULTIMATE = blockWithItem(
      "Ultimate Energy Output Hatch",
      rootLC("energyoutputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
      id -> new BlockEnergyOutputHatch(id, EnergyHatchSize.ULTIMATE),
      EnergyHatchItem::new
  );

  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_TINY = blockWithItem(
      "Tiny Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.TINY.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.TINY),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_SMALL = blockWithItem(
      "Small Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.SMALL.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.SMALL),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_NORMAL = blockWithItem(
      "Normal Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.NORMAL.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.NORMAL),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_REINFORCED = blockWithItem(
      "Reinforced Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.REINFORCED),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_BIG = blockWithItem(
      "Big Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.BIG.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.BIG),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_HUGE = blockWithItem(
      "Huge Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.HUGE.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.HUGE),
      InputBusItem::new
  );
  public static final BlockDefinition<BlockInputBus> ITEM_INPUT_BUS_LUDICROUS = blockWithItem(
      "Ludicrous Item Input Bus",
      rootLC("inputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
      id -> new BlockInputBus(id, ItemBusSize.LUDICROUS),
      InputBusItem::new
  );

  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_TINY = blockWithItem(
      "Tiny Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.TINY.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.TINY),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_SMALL = blockWithItem(
      "Small Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.SMALL.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.SMALL),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_NORMAL = blockWithItem(
      "Normal Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.NORMAL.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.NORMAL),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_REINFORCED = blockWithItem(
      "Reinforced Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.REINFORCED),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_BIG = blockWithItem(
      "Big Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.BIG.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.BIG),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_HUGE = blockWithItem(
      "Huge Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.HUGE.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.HUGE),
      OutputBusItem::new
  );
  public static final BlockDefinition<BlockOutputBus> ITEM_OUTPUT_BUS_LUDICROUS = blockWithItem(
      "Ludicrous Item Output Bus",
      rootLC("outputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
      id -> new BlockOutputBus(id, ItemBusSize.LUDICROUS),
      OutputBusItem::new
  );

  public static final BlockDefinition<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_TINY = blockWithItem(
      "Tiny Durability Hatch",
      rootLC("durabilityhatch_" + ItemDurabilityHatchSize.TINY.getSerializedName()),
      id -> new BlockDurabilityHatch(id, ItemDurabilityHatchSize.TINY),
      DurabilityHatchItem::new
  );
  public static final BlockDefinition<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_SMALL = blockWithItem(
      "Small Durability Hatch",
      rootLC("durabilityhatch_" + ItemDurabilityHatchSize.SMALL.getSerializedName()),
      id -> new BlockDurabilityHatch(id, ItemDurabilityHatchSize.SMALL),
      DurabilityHatchItem::new
  );
  public static final BlockDefinition<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_NORMAL = blockWithItem(
      "Normal Durability Hatch",
      rootLC("durabilityhatch_" + ItemDurabilityHatchSize.NORMAL.getSerializedName()),
      id -> new BlockDurabilityHatch(id, ItemDurabilityHatchSize.NORMAL),
      DurabilityHatchItem::new
  );
  public static final BlockDefinition<BlockDurabilityHatch> ITEM_DURABILITY_HATCH_BIG = blockWithItem(
      "Big Durability Hatch",
      rootLC("durabilityhatch_" + ItemDurabilityHatchSize.BIG.getSerializedName()),
      id -> new BlockDurabilityHatch(id, ItemDurabilityHatchSize.BIG),
      DurabilityHatchItem::new
  );

  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_TINY = blockWithItem(
      "Tiny Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.TINY.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.TINY),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_SMALL = blockWithItem(
      "Small Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.SMALL),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_NORMAL = blockWithItem(
      "Normal Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.NORMAL),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.REINFORCED),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_BIG = blockWithItem(
      "Big Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.BIG.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.BIG),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_HUGE = blockWithItem(
      "Huge Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.HUGE),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.LUDICROUS),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidInputHatch> FLUID_INPUT_HATCH_VACUUM = blockWithItem(
      "Vacuum Fluid Input Hatch",
      rootLC("fluidinputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
      id -> new BlockFluidInputHatch(id, FluidHatchSize.VACUUM),
      FluidHatchItem::new
  );

  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_TINY = blockWithItem(
      "Tiny Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.TINY.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.TINY),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_SMALL = blockWithItem(
      "Small Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.SMALL),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_NORMAL = blockWithItem(
      "Normal Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.NORMAL),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.REINFORCED),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_BIG = blockWithItem(
      "Big Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.BIG.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.BIG),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_HUGE = blockWithItem(
      "Huge Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.HUGE),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidOutputHatch> FLUID_OUTPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.LUDICROUS),
      FluidHatchItem::new
  );
  public static final BlockDefinition<BlockFluidHatch> FLUID_OUTPUT_HATCH_VACUUM = blockWithItem(
      "Vacuum Fluid Output Hatch",
      rootLC("fluidoutputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
      id -> new BlockFluidOutputHatch(id, FluidHatchSize.VACUUM),
      FluidHatchItem::new
  );

  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_TINY = blockWithItem(
      "Tiny Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.TINY),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_SMALL = blockWithItem(
      "Small Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.SMALL),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_NORMAL = blockWithItem(
      "Normal Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.NORMAL),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.REINFORCED),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_BIG = blockWithItem(
      "Big Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.BIG),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_HUGE = blockWithItem(
      "Huge Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.HUGE),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.LUDICROUS),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_INPUT_HATCH_VACUUM = blockWithItem(
      "Vacuum Experience Input Hatch",
      rootLC("experienceinputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
      id -> new BlockExperienceInputHatch(id, ExperienceHatchSize.VACUUM),
      ExperienceHatchItem::new
  );

  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_TINY = blockWithItem(
      "Tiny Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.TINY),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_SMALL = blockWithItem(
      "Small Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.SMALL),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_NORMAL = blockWithItem(
      "Normal Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.NORMAL),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_REINFORCED = blockWithItem(
      "Reinforced Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.REINFORCED),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_BIG = blockWithItem(
      "Big Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.BIG),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_HUGE = blockWithItem(
      "Huge Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.HUGE),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_LUDICROUS = blockWithItem(
      "Ludicrous Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.LUDICROUS),
      ExperienceHatchItem::new
  );
  public static final BlockDefinition<BlockExperienceHatch> EXPERIENCE_OUTPUT_HATCH_VACUUM = blockWithItem(
      "Vacuum Experience Output Hatch",
      rootLC("experienceoutputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
      id -> new BlockExperienceOutputHatch(id, ExperienceHatchSize.VACUUM),
      ExperienceHatchItem::new
  );

  public static final BlockDefinition<BlockDimensionDetector> DIMENSIONAL_DETECTOR = blockWithItem(
      "Dimensional Detector",
      rootLC("dimensional_detector"),
      BlockDimensionDetector::new,
      DimensionalDetectorItem::new
  );
  public static final BlockDefinition<BlockBiomeReader> BIOME_READER = blockWithItem(
      "Biome Reader",
      rootLC("biome_reader"),
      BlockBiomeReader::new,
      BiomeReaderItem::new
  );
  public static final BlockDefinition<BlockWeatherSensor> WEATHER_SENSOR = blockWithItem(
      "Weather Sensor",
      rootLC("weather_sensor"),
      BlockWeatherSensor::new,
      WeatherSensorItem::new
  );
  public static final BlockDefinition<BlockTimeCounter> TIME_COUNTER = blockWithItem(
      "Time Counter",
      rootLC("time_counter"),
      BlockTimeCounter::new,
      TimeCounterItem::new
  );
  public static final BlockDefinition<BlockHeightMeter> HEIGHT_METER = blockWithItem(
      "Height Meter",
      rootLC("height_meter"),
      BlockHeightMeter::new,
      HeightMeterItem::new
  );
  public static final BlockDefinition<BlockChunkloader> CHUNKLOADER = blockWithItem(
      "Chunkloader",
      rootLC("chunkloader"),
      BlockChunkloader::new,
      ChunkloaderItem::new
  );

  public static final BlockDefinition<ParallelHatchBlock> PARALLEL_HATCH_BASIC = blockWithItem(
      "Basic Parallel Hatch",
      rootLC("parallel_hatch_" + ParallelHatchSize.BASIC.getSerializedName()),
      id -> new ParallelHatchBlock(id, ParallelHatchSize.BASIC),
      ParallelHatchItem::new
  );
  public static final BlockDefinition<ParallelHatchBlock> PARALLEL_HATCH_MEDIUM = blockWithItem(
      "Medium Parallel Hatch",
      rootLC("parallel_hatch_" + ParallelHatchSize.MEDIUM.getSerializedName()),
      id -> new ParallelHatchBlock(id, ParallelHatchSize.MEDIUM),
      ParallelHatchItem::new
  );
  public static final BlockDefinition<ParallelHatchBlock> PARALLEL_HATCH_ADVANCED = blockWithItem(
      "Advanced Parallel Hatch",
      rootLC("parallel_hatch_" + ParallelHatchSize.ADVANCED.getSerializedName()),
      id -> new ParallelHatchBlock(id, ParallelHatchSize.ADVANCED),
      ParallelHatchItem::new
  );
  public static final BlockDefinition<ParallelHatchBlock> PARALLEL_HATCH_ULTIMATE = blockWithItem(
      "Ultimate Parallel Hatch",
      rootLC("parallel_hatch_" + ParallelHatchSize.ULTIMATE.getSerializedName()),
      id -> new ParallelHatchBlock(id, ParallelHatchSize.ULTIMATE),
      ParallelHatchItem::new
  );
  public static final BlockDefinition<ParallelHatchBlock> PARALLEL_HATCH_MAX = blockWithItem(
      "Max Parallel Hatch",
      rootLC("parallel_hatch_" + ParallelHatchSize.MAX.getSerializedName()),
      id -> new ParallelHatchBlock(id, ParallelHatchSize.MAX),
      ParallelHatchItem::new
  );

  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_TINY = blockWithItem(
      "Tiny Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.TINY.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.TINY),
      FuelTankItem::new
  );
  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_SMALL = blockWithItem(
      "Small Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.SMALL.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.SMALL),
      FuelTankItem::new
  );
  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_NORMAL = blockWithItem(
      "Normal Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.NORMAL.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.NORMAL),
      FuelTankItem::new
  );
  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_REINFORCED = blockWithItem(
      "Reinforced Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.REINFORCED.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.REINFORCED),
      FuelTankItem::new
  );
  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_BIG = blockWithItem(
      "Big Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.BIG.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.BIG),
      FuelTankItem::new
  );
  public static final BlockDefinition<BlockFuelTank> FUEL_TANK_HUGE = blockWithItem(
      "Huge Fuel Tank",
      rootLC("fuel_tank_" + FuelTankSize.HUGE.getSerializedName()),
      id -> new BlockFuelTank(id, FuelTankSize.HUGE),
      FuelTankItem::new
  );

  public static final BlockDefinition<BlockEffectDispenser> EFFECT_DISPENSER_SMALL = blockWithItem(
      "Small Effect Dispenser",
      rootLC("effect_dispenser_" + EffectDispenserSize.SMALL.getSerializedName()),
      id -> new BlockEffectDispenser(id, EffectDispenserSize.SMALL),
      EffectDispenserItem::new
  );
  public static final BlockDefinition<BlockEffectDispenser> EFFECT_DISPENSER_MEDIUM = blockWithItem(
      "Medium Effect Dispenser",
      rootLC("effect_dispenser_" + EffectDispenserSize.MEDIUM.getSerializedName()),
      id -> new BlockEffectDispenser(id, EffectDispenserSize.MEDIUM),
      EffectDispenserItem::new
  );
  public static final BlockDefinition<BlockEffectDispenser> EFFECT_DISPENSER_BIG = blockWithItem(
      "Big Effect Dispenser",
      rootLC("effect_dispenser_" + EffectDispenserSize.BIG.getSerializedName()),
      id -> new BlockEffectDispenser(id, EffectDispenserSize.BIG),
      EffectDispenserItem::new
  );

  public static final BlockDefinition<BlockEntityDetector> ENTITY_DETECTOR = blockWithItem(
      "Entity Detector",
      rootLC("entity_detector"),
      BlockEntityDetector::new,
      EntityItem::new
  );
  public static final BlockDefinition<BlockEntityDamager> ENTITY_DAMAGER = blockWithItem(
      "Entity Damager",
      rootLC("entity_damager"),
      BlockEntityDamager::new,
      EntityItem::new
  );
  public static final BlockDefinition<BlockEntityHealer> ENTITY_HEALER = blockWithItem(
      "Entity Healer",
      rootLC("entity_healer"),
      BlockEntityHealer::new,
      EntityItem::new
  );
  public static final BlockDefinition<BlockEntitySpawner> ENTITY_SPAWNER = blockWithItem(
      "Entity Spawner",
      rootLC("entity_spawner"),
      BlockEntitySpawner::new,
      EntityItem::new
  );
  public static final BlockDefinition<BlockEntityKiller> ENTITY_KILLER = blockWithItem(
      "Entity Killer",
      rootLC("entity_killer"),
      BlockEntityKiller::new,
      EntityItem::new
  );

  public static final BlockDefinition<BlockStructureChecker> STRUCTURE_CHECKER = blockWithItem(
      "Structure Checker",
      rootLC("structure_checker"),
      BlockStructureChecker::new,
      StructureCheckerItem::new
  );
  public static final BlockDefinition<BlockRedstonePort> REDSTONE_PORT = blockWithItem(
      "Redstone Port",
      rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      BlockRedstonePort::new,
      RedstonePortItem::new
  );
  public static final BlockDefinition<BlockCommandExecutioner> COMMAND_EXECUTIONER = blockWithItem(
      "Command Executioner",
      rootLC("command_executioner"),
      BlockCommandExecutioner::new,
      CommandExecutionerItem::new
  );
  
  private static <T extends Block> BlockDefinition<T> blockWithItem(
      String englishName,
      String id,
      Function<BlockBehaviour.Properties, T> blockDefinition,
      BiFunction<T, Item.Properties, BlockItem> itemDefinition
  ) {
    var block = DR.registerBlock(id, blockDefinition);
    var definition = new BlockDefinition<>(
        englishName,
        block,
        ItemRegistration.item(
            englishName,
            id,
            p -> itemDefinition.apply(block.get(), p)
        )
    );
    BLOCKS.add(definition);
    return definition;
  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }
}
