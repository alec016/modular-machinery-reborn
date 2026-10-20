package es.degrassi.mmr.data.models;

import com.google.gson.JsonObject;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class MMRBlockStateProvider extends BaseMMRBlockStateProvider {

  public MMRBlockStateProvider(PackOutput output) {
    super(output, ModularMachineryReborn.MODID);
  }

  @Override
  protected void registerStatesAndModels() {
    addDefaultModels();
    basicTintedItem(ItemRegistration.MODULARIUM.asItem());
    basicItem(ItemRegistration.BLUEPRINT.asItem());
    basicItem(ItemRegistration.WRENCH.asItem());
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM.asItem(), modLoc("item/template"));

    addController(BlockRegistration.CONTROLLER.block(), false, modLoc("block/overlay_controller"));

    addCasing(BlockRegistration.CASING_PLAIN.block(), false, null, modLoc("block/overlay_transparent"));
    addCasing(BlockRegistration.CASING_REINFORCED.block(), true, null, modLoc("block/overlay_reinforced"));
    addCasing(BlockRegistration.CASING_CIRCUITRY.block(), false, modLoc("block/overlay_circuitry"), modLoc("block/overlay_circuitry"));
    addCasing(BlockRegistration.CASING_GEARBOX.block(), false, modLoc("block/overlay_gearbox"), modLoc("block/overlay_gearbox"));
    addCasing(BlockRegistration.CASING_VENT.block(), false, modLoc("block/overlay_vent"), modLoc("block/overlay_vent"));
    addCasing(BlockRegistration.CASING_FIREBOX.block(), false, modLoc("block/overlay_firebox"), modLoc("block/overlay_firebox"));

    addHatch(BlockRegistration.BIOME_READER.block(), false, modLoc("block/overlay_biome_reader"), false);
    addHatch(BlockRegistration.CHUNKLOADER.block(), false, modLoc("block/overlay_chunkloader"), false);
    addHatch(BlockRegistration.DIMENSIONAL_DETECTOR.block(), false, modLoc("block/overlay_dimensional_detector"), false);
    addHatch(BlockRegistration.HEIGHT_METER.block(), false, modLoc("block/overlay_height_meter"), true);
    addHatch(BlockRegistration.TIME_COUNTER.block(), false, modLoc("block/overlay_time_counter"), false);
    addHatch(BlockRegistration.WEATHER_SENSOR.block(), false, modLoc("block/overlay_weather_sensor"), false);

    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_TINY.block(), false, modLoc("block/overlay_durabilityhatch_tiny"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.block(), false, modLoc("block/overlay_durabilityhatch_small"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.block(), false, modLoc("block/overlay_durabilityhatch_normal"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_BIG.block(), true, modLoc("block/overlay_durabilityhatch_big"), false);

    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_TINY.block(), false, energy(true, EnergyHatchSize.TINY), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_SMALL.block(), false, energy(true, EnergyHatchSize.SMALL), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.block(), false, energy(true, EnergyHatchSize.NORMAL), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.block(), true, energy(true, EnergyHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_BIG.block(), true, energy(true, EnergyHatchSize.BIG), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_HUGE.block(), true, energy(true, EnergyHatchSize.HUGE), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.block(), true, energy(true, EnergyHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.block(), true, energy(true, EnergyHatchSize.ULTIMATE), false);

    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.block(), false, energy(false, EnergyHatchSize.TINY), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.block(), false, energy(false, EnergyHatchSize.SMALL), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.block(), false, energy(false, EnergyHatchSize.NORMAL), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.block(), true, energy(false, EnergyHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.block(), true, energy(false, EnergyHatchSize.BIG), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.block(), true, energy(false, EnergyHatchSize.HUGE), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.block(), true, energy(false, EnergyHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.block(), true, energy(false, EnergyHatchSize.ULTIMATE), false);

    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.block(), false, experience(true, ExperienceHatchSize.TINY), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.block(), false, experience(true, ExperienceHatchSize.SMALL), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.block(), false, experience(true, ExperienceHatchSize.NORMAL), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.block(), true, experience(true, ExperienceHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.block(), true, experience(true, ExperienceHatchSize.BIG), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.block(), true, experience(true, ExperienceHatchSize.HUGE), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.block(), true, experience(true, ExperienceHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.block(), true, experience(true, ExperienceHatchSize.VACUUM), false);

    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.block(), false, experience(false, ExperienceHatchSize.TINY), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.block(), false, experience(false, ExperienceHatchSize.SMALL), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.block(), false, experience(false, ExperienceHatchSize.NORMAL), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.block(), true, experience(false, ExperienceHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.block(), true, experience(false, ExperienceHatchSize.BIG), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.block(), true, experience(false, ExperienceHatchSize.HUGE), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.block(), true, experience(false, ExperienceHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.block(), true, experience(false, ExperienceHatchSize.VACUUM), false);

    addHatch(BlockRegistration.FLUID_INPUT_HATCH_TINY.block(), false, fluid(true, FluidHatchSize.TINY), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_SMALL.block(), false, fluid(true, FluidHatchSize.SMALL), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_NORMAL.block(), false, fluid(true, FluidHatchSize.NORMAL), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.block(), true, fluid(true, FluidHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_BIG.block(), true, fluid(true, FluidHatchSize.BIG), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_HUGE.block(), true, fluid(true, FluidHatchSize.HUGE), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.block(), true, fluid(true, FluidHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_VACUUM.block(), true, fluid(true, FluidHatchSize.VACUUM), false);

    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_TINY.block(), false, fluid(false, FluidHatchSize.TINY), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.block(), false, fluid(false, FluidHatchSize.SMALL), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.block(), false, fluid(false, FluidHatchSize.NORMAL), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.block(), true, fluid(false, FluidHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_BIG.block(), true, fluid(false, FluidHatchSize.BIG), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.block(), true, fluid(false, FluidHatchSize.HUGE), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.block(), true, fluid(false, FluidHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.block(), true, fluid(false, FluidHatchSize.VACUUM), false);

    addHatch(BlockRegistration.ITEM_INPUT_BUS_TINY.block(), false, item(true, ItemBusSize.TINY), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_SMALL.block(), false, item(true, ItemBusSize.SMALL), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_NORMAL.block(), false, item(true, ItemBusSize.NORMAL), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_REINFORCED.block(), true, item(true, ItemBusSize.REINFORCED), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_BIG.block(), true, item(true, ItemBusSize.BIG), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_HUGE.block(), true, item(true, ItemBusSize.HUGE), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.block(), true, item(true, ItemBusSize.LUDICROUS), false);

    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_TINY.block(), false, item(false, ItemBusSize.TINY), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_SMALL.block(), false, item(false, ItemBusSize.SMALL), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.block(), false, item(false, ItemBusSize.NORMAL), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.block(), true, item(false, ItemBusSize.REINFORCED), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_BIG.block(), true, item(false, ItemBusSize.BIG), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_HUGE.block(), true, item(false, ItemBusSize.HUGE), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.block(), true, item(false, ItemBusSize.LUDICROUS), false);

    addHatch(BlockRegistration.PARALLEL_HATCH_BASIC.block(), false, parallel(ParallelHatchSize.BASIC), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_MEDIUM.block(), false, parallel(ParallelHatchSize.MEDIUM), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_ADVANCED.block(), false, parallel(ParallelHatchSize.ADVANCED), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_ULTIMATE.block(), false, parallel(ParallelHatchSize.ULTIMATE), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_MAX.block(), false, parallel(ParallelHatchSize.MAX), true);

    addHatch(BlockRegistration.FUEL_TANK_TINY.block(), false, fuel(FuelTankSize.TINY), false);
    addHatch(BlockRegistration.FUEL_TANK_SMALL.block(), false, fuel(FuelTankSize.SMALL), false);
    addHatch(BlockRegistration.FUEL_TANK_NORMAL.block(), false, fuel(FuelTankSize.NORMAL), false);
    addHatch(BlockRegistration.FUEL_TANK_REINFORCED.block(), true, fuel(FuelTankSize.REINFORCED), false);
    addHatch(BlockRegistration.FUEL_TANK_BIG.block(), true, fuel(FuelTankSize.BIG), false);
    addHatch(BlockRegistration.FUEL_TANK_HUGE.block(), true, fuel(FuelTankSize.HUGE), false);

    addHatch(BlockRegistration.EFFECT_DISPENSER_SMALL.block(), false, effect(EffectDispenserSize.SMALL), false);
    addHatch(BlockRegistration.EFFECT_DISPENSER_MEDIUM.block(), false, effect(EffectDispenserSize.MEDIUM), false);
    addHatch(BlockRegistration.EFFECT_DISPENSER_BIG.block(), true, effect(EffectDispenserSize.BIG), false);

    addHatch(BlockRegistration.ENTITY_DETECTOR.block(), false, entity(RequirementEntity.Action.CHECK_AMOUNT), false);
    addHatch(BlockRegistration.ENTITY_KILLER.block(), true, entity(RequirementEntity.Action.KILL), false);
    addHatch(BlockRegistration.ENTITY_SPAWNER.block(), true, entity(RequirementEntity.Action.SPAWN), false);
    addHatch(BlockRegistration.ENTITY_HEALER.block(), false, entity(RequirementEntity.Action.ADD_HEALTH), false);
    addHatch(BlockRegistration.ENTITY_DAMAGER.block(), false, entity(RequirementEntity.Action.CONSUME_HEALTH), false);

    addHatch(BlockRegistration.STRUCTURE_CHECKER.block(), true, modLoc("block/overlay_structure_checker"), false);
    addHatch(BlockRegistration.REDSTONE_PORT.block(), false, modLoc("block/overlay_redstone_port"), false);
    addHatch(BlockRegistration.COMMAND_EXECUTIONER.block(), false, modLoc("block/overlay_command_executioner"), false);
  }

  private void addDefaultModels() {
    Identifier blockModelOverlayOrientableAll = modLoc("block/blockmodel_overlay_orientable_all");
    Identifier blockModelOverlayAll = modLoc("block/blockmodel_overlay_all");
    Identifier blockModelOverlay = modLoc("block/blockmodel_overlay");

    var blockModelOverlayOrientableAllModel = modelBuilder(blockModelOverlayOrientableAll.getPath())
        .parent(blockModelOverlay)
        .texture("particle", "#bg_all")
        .texture("bg_down", "#bg_all")
        .texture("bg_up", "#bg_all")
        .texture("bg_north", "#bg_all")
        .texture("bg_east", "#bg_all")
        .texture("bg_south", "#bg_all")
        .texture("bg_west", "#bg_all")
        .texture("ov_down", "#ov_top")
        .texture("ov_up", "#ov_top")
        .texture("ov_north", "#ov_front")
        .texture("ov_east", "#ov_side")
        .texture("ov_south", "#ov_side")
        .texture("ov_west", "#ov_side")
        .texture("ov_r_down", "#ov_r_all")
        .texture("ov_r_up", "#ov_r_all")
        .texture("ov_r_north", "#ov_r_all")
        .texture("ov_r_east", "#ov_r_all")
        .texture("ov_r_south", "#ov_r_all")
        .texture("ov_r_west", "#ov_r_all")
        .build();
    block(blockModelOverlayOrientableAll, blockModelOverlayOrientableAllModel);

    var blockModelOverlayAllModel = modelBuilder(blockModelOverlayAll.getPath())
        .parent(blockModelOverlay)
        .texture("particle", "#bg_all")
        .texture("bg_down", "#bg_all")
        .texture("bg_up", "#bg_all")
        .texture("bg_north", "#bg_all")
        .texture("bg_east", "#bg_all")
        .texture("bg_south", "#bg_all")
        .texture("bg_west", "#bg_all")
        .texture("ov_down", "#ov_all")
        .texture("ov_up", "#ov_all")
        .texture("ov_north", "#ov_all")
        .texture("ov_east", "#ov_all")
        .texture("ov_south", "#ov_all")
        .texture("ov_west", "#ov_all")
        .texture("ov_r_down", "#ov_r_all")
        .texture("ov_r_up", "#ov_r_all")
        .texture("ov_r_north", "#ov_r_all")
        .texture("ov_r_east", "#ov_r_all")
        .texture("ov_r_south", "#ov_r_all")
        .texture("ov_r_west", "#ov_r_all")
        .build();
    block(blockModelOverlayAll, blockModelOverlayAllModel);

    JsonObject blockModelOverlayModel = modelBuilder(blockModelOverlay.getPath())
        .parent(mcLoc("block/block"))
        .renderType("cutout")
        .texture("ov_r_all", modLoc("block/overlay_transparent"))
        // Legacy parity: the overlay root carried the full three-layer cube geometry (base cube
        // tinted with the machine-color tintindex 4, reinforced overlay, then the normal overlay)
        // which every leaf inheriting the chain baked into its quads.
        .elements(bgOverlayCubeElements())
        .build();
    block(blockModelOverlay, blockModelOverlayModel);

    // 1.21.1 parity: structure ghost/predictor fallback model (StructureRenderer NOPE_MODEL_KEY).
    // 26.1 rejects block models whose sprites resolve to another atlas, so the vanilla item/barrier
    // texture is shipped as a plain block texture instead of referencing it cross-atlas.
    this.block(modLoc("block/nope"), modelBuilder(modLoc("block/nope").getPath())
        .parent(mcLoc("block/cube_all"))
        .texture("all", modLoc("block/nope"))
        .build());

    Identifier hatch_all = modLoc("hatch_all");
    var hatch_all_model = modelBuilder(hatch_all.withPrefix("default/").getPath())
        .parent(blockModelOverlayAll)
        .texture("bg_all", modLoc("block/casing_plain"))
        .texture("ov_all", modLoc("block/overlay_transparent"))
        .build();
    defaultModel(hatch_all, hatch_all_model);

    Identifier hatch_orientable = modLoc("hatch_orientable");
    var hatch_orientable_model = modelBuilder(hatch_orientable.withPrefix("default/").getPath())
        .parent(blockModelOverlayOrientableAll)
        .texture("bg_all", modLoc("block/casing_plain"))
        .texture("ov_front", modLoc("block/overlay_transparent"))
        .texture("ov_side", modLoc("block/overlay_transparent"))
        .texture("ov_top", modLoc("block/overlay_transparent"))
        .build();
    defaultModel(hatch_orientable, hatch_orientable_model);
  }
  
  private Identifier experience(boolean input, ExperienceHatchSize size) {
    return modLoc("block/overlay_experience" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private Identifier energy(boolean input, EnergyHatchSize size) {
    return modLoc("block/overlay_energy" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private Identifier fluid(boolean input, FluidHatchSize size) {
    return modLoc("block/overlay_fluid" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private Identifier item(boolean input, ItemBusSize size) {
    return modLoc("block/overlay_" + (input ? "input" : "output") + "bus_" + size.getSerializedName());
  }
  private Identifier parallel(ParallelHatchSize size) {
    return modLoc("block/overlay_parallel_hatch_" + size.getSerializedName());
  }
  private Identifier fuel(FuelTankSize size) {
    return modLoc("block/overlay_fueltank_" + size.getSerializedName());
  }
  private Identifier effect(EffectDispenserSize size) {
    return modLoc("block/overlay_effectdispenser_" + size.getSerializedName());
  }
  private Identifier entity(RequirementEntity.Action mode) {
    return modLoc("block/overlay_entity" + switch(mode) {
      case CHECK_AMOUNT, CHECK_HEALTH -> "detector";
      case KILL -> "killer";
      case SPAWN -> "spawner";
      case CONSUME_HEALTH -> "damager";
      case ADD_HEALTH -> "healer";
    });
  }
}
