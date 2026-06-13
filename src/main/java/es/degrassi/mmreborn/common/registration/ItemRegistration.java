package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockCasing.CasingType;
import es.degrassi.mmreborn.common.block.prop.EffectDispenserSize;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.FuelTankSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
import es.degrassi.mmreborn.common.item.BiomeReaderItem;
import es.degrassi.mmreborn.common.item.CasingItem;
import es.degrassi.mmreborn.common.item.ChunkloaderItem;
import es.degrassi.mmreborn.common.item.CommandExecutionerItem;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.item.DimensionalDetectorItem;
import es.degrassi.mmreborn.common.item.DurabilityHatchItem;
import es.degrassi.mmreborn.common.item.EffectDispenserItem;
import es.degrassi.mmreborn.common.item.EnergyHatchItem;
import es.degrassi.mmreborn.common.item.EntityItem;
import es.degrassi.mmreborn.common.item.ExperienceHatchItem;
import es.degrassi.mmreborn.common.item.FluidHatchItem;
import es.degrassi.mmreborn.common.item.FuelTankItem;
import es.degrassi.mmreborn.common.item.HeightMeterItem;
import es.degrassi.mmreborn.common.item.InputBusItem;
import es.degrassi.mmreborn.common.item.ItemBlueprint;
import es.degrassi.mmreborn.common.item.ItemModularium;
import es.degrassi.mmreborn.common.item.OutputBusItem;
import es.degrassi.mmreborn.common.item.ParallelHatchItem;
import es.degrassi.mmreborn.common.item.RedstonePortItem;
import es.degrassi.mmreborn.common.item.StructureCheckerItem;
import es.degrassi.mmreborn.common.item.StructureCreatorItem;
import es.degrassi.mmreborn.common.item.StructureCreatorItemMode;
import es.degrassi.mmreborn.common.item.StructureTemplateItem;
import es.degrassi.mmreborn.common.item.TimeCounterItem;
import es.degrassi.mmreborn.common.item.WeatherSensorItem;
import es.degrassi.mmreborn.common.item.WrenchItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;

import static es.degrassi.mmreborn.ModularMachineryReborn.rootLC;

public class ItemRegistration {
  private ItemRegistration() {}
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModularMachineryReborn.MODID);

  public static final DeferredItem<ItemBlueprint> BLUEPRINT = ITEMS.register(rootLC("blueprint"), ItemBlueprint::new);
  public static final DeferredItem<ItemModularium> MODULARIUM = ITEMS.register(rootLC("modularium"), ItemModularium::new);
  public static final DeferredItem<WrenchItem> WRENCH = ITEMS.register(rootLC("wrench"), WrenchItem::new);
  public static final DeferredItem<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_SINGLE = ITEMS.register(rootLC("structure_creator_single"),
      () -> new StructureCreatorItem(StructureCreatorItemMode.SINGLE, new Item.Properties()));
  public static final DeferredItem<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_VEIN = ITEMS.register(rootLC("structure_creator_vein"),
      () -> new StructureCreatorItem(StructureCreatorItemMode.VEIN, new Item.Properties()));
  public static final DeferredItem<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_BOX = ITEMS.register(rootLC("structure_creator_box"),
      () -> new StructureCreatorItem(StructureCreatorItemMode.BOX, new Item.Properties().component(DataComponentRegistration.STRUCTURE_CREATOR_BOX_CURRENT, true)));
  public static final DeferredItem<StructureTemplateItem> STRUCTURE_TEMPLATE_ITEM = ITEMS.register(rootLC("structure_template_item"), StructureTemplateItem::new);
  public static final DeferredItem<CasingItem> CASING_PLAIN =
      ITEMS.register(rootLC("casing_" + CasingType.PLAIN.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_PLAIN.get()));
  public static final DeferredItem<CasingItem> CASING_VENT =
      ITEMS.register(rootLC("casing_" + CasingType.VENT.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_VENT.get()));
  public static final DeferredItem<CasingItem> CASING_FIREBOX =
      ITEMS.register(rootLC("casing_" + CasingType.FIREBOX.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_FIREBOX.get()));
  public static final DeferredItem<CasingItem> CASING_GEARBOX =
      ITEMS.register(rootLC("casing_" + CasingType.GEARBOX.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_GEARBOX.get()));
  public static final DeferredItem<CasingItem> CASING_REINFORCED =
      ITEMS.register(rootLC("casing_" + CasingType.REINFORCED.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_REINFORCED.get()));
  public static final DeferredItem<CasingItem> CASING_CIRCUITRY =
      ITEMS.register(rootLC("casing_" + CasingType.CIRCUITRY.getSerializedName()),
      () -> new CasingItem(BlockRegistration.CASING_CIRCUITRY.get()));

  public static final DeferredItem<BlockItem> CONTROLLER = ITEMS.register(rootLC("controller"), ControllerItem::new);

  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_TINY = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.TINY.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(), EnergyHatchSize.TINY));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_SMALL = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(), EnergyHatchSize.SMALL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_NORMAL = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(), EnergyHatchSize.NORMAL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_REINFORCED = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(), EnergyHatchSize.REINFORCED));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_BIG = ITEMS.register(rootLC("energyinputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(), EnergyHatchSize.BIG));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_HUGE = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(), EnergyHatchSize.HUGE));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_LUDICROUS = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(), EnergyHatchSize.LUDICROUS));
  public static final DeferredItem<EnergyHatchItem> ENERGY_INPUT_HATCH_ULTIMATE = ITEMS.register(rootLC(
      "energyinputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(), EnergyHatchSize.ULTIMATE));

  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_TINY = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.TINY.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(), EnergyHatchSize.TINY));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_SMALL = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.SMALL.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(), EnergyHatchSize.SMALL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_NORMAL = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.NORMAL.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(), EnergyHatchSize.NORMAL));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_REINFORCED = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.REINFORCED.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(), EnergyHatchSize.REINFORCED));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_BIG = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.BIG.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(), EnergyHatchSize.BIG));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_HUGE = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.HUGE.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(), EnergyHatchSize.HUGE));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_LUDICROUS = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.LUDICROUS.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(), EnergyHatchSize.LUDICROUS));
  public static final DeferredItem<EnergyHatchItem> ENERGY_OUTPUT_HATCH_ULTIMATE = ITEMS.register(rootLC(
      "energyoutputhatch_" + EnergyHatchSize.ULTIMATE.getSerializedName()),
    () -> new EnergyHatchItem(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(), EnergyHatchSize.ULTIMATE));

  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_TINY =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.TINY.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_TINY.get(), ItemBusSize.TINY));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_SMALL =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.SMALL.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_SMALL.get(), ItemBusSize.SMALL));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_NORMAL =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.NORMAL.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(), ItemBusSize.NORMAL));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_REINFORCED =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(), ItemBusSize.REINFORCED));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_BIG =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.BIG.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_BIG.get(), ItemBusSize.BIG));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_HUGE =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.HUGE.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_HUGE.get(), ItemBusSize.HUGE));
  public static final DeferredItem<InputBusItem> ITEM_INPUT_BUS_LUDICROUS =
      ITEMS.register(rootLC("inputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
    () -> new InputBusItem(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get(), ItemBusSize.LUDICROUS));

  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_TINY =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.TINY.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(), ItemBusSize.TINY));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_SMALL =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.SMALL.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(), ItemBusSize.SMALL));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_NORMAL =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.NORMAL.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(), ItemBusSize.NORMAL));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_REINFORCED =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.REINFORCED.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(), ItemBusSize.REINFORCED));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_BIG =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.BIG.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(), ItemBusSize.BIG));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_HUGE =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.HUGE.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(), ItemBusSize.HUGE));
  public static final DeferredItem<OutputBusItem> ITEM_OUTPUT_BUS_LUDICROUS =
      ITEMS.register(rootLC("outputbus_" + ItemBusSize.LUDICROUS.getSerializedName()),
    () -> new OutputBusItem(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(), ItemBusSize.LUDICROUS));

  public static final DeferredItem<DurabilityHatchItem> ITEM_DURABILITY_HATCH_TINY =
      ITEMS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.TINY.getSerializedName()),
          () -> new DurabilityHatchItem(BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get(), ItemDurabilityHatchSize.TINY));
  public static final DeferredItem<DurabilityHatchItem> ITEM_DURABILITY_HATCH_SMALL =
      ITEMS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.SMALL.getSerializedName()),
          () -> new DurabilityHatchItem(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get(), ItemDurabilityHatchSize.SMALL));
  public static final DeferredItem<DurabilityHatchItem> ITEM_DURABILITY_HATCH_NORMAL =
      ITEMS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.NORMAL.getSerializedName()),
          () -> new DurabilityHatchItem(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get(), ItemDurabilityHatchSize.NORMAL));
  public static final DeferredItem<DurabilityHatchItem> ITEM_DURABILITY_HATCH_BIG =
      ITEMS.register(rootLC("durabilityhatch_" + ItemDurabilityHatchSize.BIG.getSerializedName()),
          () -> new DurabilityHatchItem(BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get(), ItemDurabilityHatchSize.BIG));

  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_TINY =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.TINY.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_TINY.get(), FluidHatchSize.TINY));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_SMALL =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(), FluidHatchSize.SMALL));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_NORMAL =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(), FluidHatchSize.NORMAL));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_REINFORCED =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(), FluidHatchSize.REINFORCED));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_BIG =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.BIG.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_BIG.get(), FluidHatchSize.BIG));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_HUGE =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(), FluidHatchSize.HUGE));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_LUDICROUS =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(), FluidHatchSize.LUDICROUS));
  public static final DeferredItem<FluidHatchItem> FLUID_INPUT_HATCH_VACUUM =
      ITEMS.register(rootLC("fluidinputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get(), FluidHatchSize.VACUUM));

  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_TINY =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.TINY.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(), FluidHatchSize.TINY));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_SMALL =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.SMALL.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(), FluidHatchSize.SMALL));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_NORMAL =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.NORMAL.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(), FluidHatchSize.NORMAL));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_REINFORCED =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.REINFORCED.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(), FluidHatchSize.REINFORCED));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_BIG =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.BIG.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(), FluidHatchSize.BIG));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_HUGE =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.HUGE.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(), FluidHatchSize.HUGE));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_LUDICROUS =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.LUDICROUS.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(), FluidHatchSize.LUDICROUS));
  public static final DeferredItem<FluidHatchItem> FLUID_OUTPUT_HATCH_VACUUM =
      ITEMS.register(rootLC("fluidoutputhatch_" + FluidHatchSize.VACUUM.getSerializedName()),
    () -> new FluidHatchItem(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(), FluidHatchSize.VACUUM));

  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_TINY = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get(), ExperienceHatchSize.TINY));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_SMALL = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get(), ExperienceHatchSize.SMALL));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_NORMAL = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get(), ExperienceHatchSize.NORMAL));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_REINFORCED = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get(), ExperienceHatchSize.REINFORCED));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_BIG = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get(), ExperienceHatchSize.BIG));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_HUGE = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get(), ExperienceHatchSize.HUGE));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_LUDICROUS = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get(), ExperienceHatchSize.LUDICROUS));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_INPUT_HATCH_VACUUM = ITEMS.register(rootLC(
      "experienceinputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get(), ExperienceHatchSize.VACUUM));

  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_TINY = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.TINY.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get(), ExperienceHatchSize.TINY));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_SMALL = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.SMALL.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get(), ExperienceHatchSize.SMALL));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_NORMAL = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.NORMAL.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get(), ExperienceHatchSize.NORMAL));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_REINFORCED = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.REINFORCED.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get(), ExperienceHatchSize.REINFORCED));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_BIG = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.BIG.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get(), ExperienceHatchSize.BIG));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_HUGE = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.HUGE.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get(), ExperienceHatchSize.HUGE));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_LUDICROUS = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.LUDICROUS.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get(), ExperienceHatchSize.LUDICROUS));
  public static final DeferredItem<ExperienceHatchItem> EXPERIENCE_OUTPUT_HATCH_VACUUM = ITEMS.register(rootLC(
      "experienceoutputhatch_" + ExperienceHatchSize.VACUUM.getSerializedName()),
    () -> new ExperienceHatchItem(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get(), ExperienceHatchSize.VACUUM));

  public static final DeferredItem<DimensionalDetectorItem> DIMENSIONAL_DETECTOR = ITEMS.register(rootLC("dimensional_detector"),
    () -> new DimensionalDetectorItem(BlockRegistration.DIMENSIONAL_DETECTOR.get()));
  public static final DeferredItem<BiomeReaderItem> BIOME_READER = ITEMS.register(rootLC("biome_reader"),
    () -> new BiomeReaderItem(BlockRegistration.BIOME_READER.get()));
  public static final DeferredItem<WeatherSensorItem> WEATHER_SENSOR = ITEMS.register(rootLC("weather_sensor"),
    () -> new WeatherSensorItem(BlockRegistration.WEATHER_SENSOR.get()));
  public static final DeferredItem<TimeCounterItem> TIME_COUNTER = ITEMS.register(rootLC("time_counter"),
    () -> new TimeCounterItem(BlockRegistration.TIME_COUNTER.get()));
  public static final DeferredItem<HeightMeterItem> HEIGHT_METER = ITEMS.register(rootLC("height_meter"),
    () -> new HeightMeterItem(BlockRegistration.HEIGHT_METER.get()));
  public static final DeferredItem<ChunkloaderItem> CHUNKLOADER = ITEMS.register(rootLC("chunkloader"),
    () -> new ChunkloaderItem(BlockRegistration.CHUNKLOADER.get()));

  public static final DeferredItem<ParallelHatchItem> PARALLEL_HATCH_BASIC =
      ITEMS.register(rootLC("parallel_hatch_" + ParallelHatchSize.BASIC.getSerializedName()),
    () -> new ParallelHatchItem(BlockRegistration.PARALLEL_HATCH_BASIC.get(), ParallelHatchSize.BASIC));
  public static final DeferredItem<ParallelHatchItem> PARALLEL_HATCH_MEDIUM =
      ITEMS.register(rootLC("parallel_hatch_" + ParallelHatchSize.MEDIUM.getSerializedName()),
    () -> new ParallelHatchItem(BlockRegistration.PARALLEL_HATCH_MEDIUM.get(), ParallelHatchSize.MEDIUM));
  public static final DeferredItem<ParallelHatchItem> PARALLEL_HATCH_ADVANCED =
      ITEMS.register(rootLC("parallel_hatch_" + ParallelHatchSize.ADVANCED.getSerializedName()),
    () -> new ParallelHatchItem(BlockRegistration.PARALLEL_HATCH_ADVANCED.get(), ParallelHatchSize.ADVANCED));
  public static final DeferredItem<ParallelHatchItem> PARALLEL_HATCH_ULTIMATE =
      ITEMS.register(rootLC("parallel_hatch_" + ParallelHatchSize.ULTIMATE.getSerializedName()),
    () -> new ParallelHatchItem(BlockRegistration.PARALLEL_HATCH_ULTIMATE.get(), ParallelHatchSize.ULTIMATE));
  public static final DeferredItem<ParallelHatchItem> PARALLEL_HATCH_MAX =
      ITEMS.register(rootLC("parallel_hatch_" + ParallelHatchSize.MAX.getSerializedName()),
    () -> new ParallelHatchItem(BlockRegistration.PARALLEL_HATCH_MAX.get(), ParallelHatchSize.MAX));

  public static final DeferredItem<FuelTankItem> FUEL_TANK_TINY =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.TINY.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_TINY.get(), FuelTankSize.TINY));
  public static final DeferredItem<FuelTankItem> FUEL_TANK_SMALL =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.SMALL.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_SMALL.get(), FuelTankSize.SMALL));
  public static final DeferredItem<FuelTankItem> FUEL_TANK_NORMAL =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.NORMAL.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_NORMAL.get(), FuelTankSize.NORMAL));
  public static final DeferredItem<FuelTankItem> FUEL_TANK_REINFORCED =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.REINFORCED.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_REINFORCED.get(), FuelTankSize.REINFORCED));
  public static final DeferredItem<FuelTankItem> FUEL_TANK_BIG =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.BIG.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_BIG.get(), FuelTankSize.BIG));
  public static final DeferredItem<FuelTankItem> FUEL_TANK_HUGE =
      ITEMS.register(rootLC("fuel_tank_" + FuelTankSize.HUGE.getSerializedName()),
      () -> new FuelTankItem(BlockRegistration.FUEL_TANK_HUGE.get(), FuelTankSize.HUGE));

  public static final DeferredItem<EffectDispenserItem> EFFECT_DISPENSER_SMALL =
      ITEMS.register(rootLC("effect_dispenser_" + EffectDispenserSize.SMALL.getSerializedName()),
      () -> new EffectDispenserItem(BlockRegistration.EFFECT_DISPENSER_SMALL.get(), EffectDispenserSize.SMALL));
  public static final DeferredItem<EffectDispenserItem> EFFECT_DISPENSER_MEDIUM =
      ITEMS.register(rootLC("effect_dispenser_" + EffectDispenserSize.MEDIUM.getSerializedName()),
      () -> new EffectDispenserItem(BlockRegistration.EFFECT_DISPENSER_MEDIUM.get(), EffectDispenserSize.MEDIUM));
  public static final DeferredItem<EffectDispenserItem> EFFECT_DISPENSER_BIG =
      ITEMS.register(rootLC("effect_dispenser_" + EffectDispenserSize.BIG.getSerializedName()),
      () -> new EffectDispenserItem(BlockRegistration.EFFECT_DISPENSER_BIG.get(), EffectDispenserSize.BIG));

  public static final DeferredItem<EntityItem> ENTITY_DETECTOR =
      ITEMS.register(rootLC("entity_detector"),
      () -> new EntityItem(BlockRegistration.ENTITY_DETECTOR.get()));
  public static final DeferredItem<EntityItem> ENTITY_HEALER =
      ITEMS.register(rootLC("entity_healer"),
      () -> new EntityItem(BlockRegistration.ENTITY_HEALER.get()));
  public static final DeferredItem<EntityItem> ENTITY_DAMAGER =
      ITEMS.register(rootLC("entity_damager"),
      () -> new EntityItem(BlockRegistration.ENTITY_DAMAGER.get()));
  public static final DeferredItem<EntityItem> ENTITY_SPAWNER =
      ITEMS.register(rootLC("entity_spawner"),
      () -> new EntityItem(BlockRegistration.ENTITY_SPAWNER.get()));
  public static final DeferredItem<EntityItem> ENTITY_KILLER =
      ITEMS.register(rootLC("entity_killer"),
      () -> new EntityItem(BlockRegistration.ENTITY_KILLER.get()));

  public static final DeferredItem<StructureCheckerItem> STRUCTURE_CHECKER =
      ITEMS.register(rootLC("structure_checker"),
      () -> new StructureCheckerItem(BlockRegistration.STRUCTURE_CHECKER.get()));

  public static final DeferredItem<RedstonePortItem> REDSTONE_PORT =
      ITEMS.register(rootLC("REDSTONE_PORT".toLowerCase(Locale.ENGLISH)),
      () -> new RedstonePortItem(BlockRegistration.REDSTONE_PORT.get()));

  public static final DeferredItem<CommandExecutionerItem> COMMAND_EXECUTIONER =
      ITEMS.register(rootLC("command_executioner"),
      () -> new CommandExecutionerItem(BlockRegistration.COMMAND_EXECUTIONER.get()));

  public static void register(final IEventBus bus) {
    ITEMS.register(bus);
  }
}
