package es.degrassi.mmreborn.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.prop.EffectDispenserSize;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.block.prop.FuelTankSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
import es.degrassi.mmreborn.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MMRBlockStateProvider extends BaseMMRBlockStateProvider {

  public MMRBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, ModularMachineryReborn.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    addDefaultModels();
    itemModels().basicItem(ItemRegistration.MODULARIUM.asItem());
    itemModels().basicItem(ItemRegistration.BLUEPRINT.asItem());
    itemModels().basicItem(ItemRegistration.WRENCH.asItem());
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX.asItem(), mcLoc("item/stick"));
    basicItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM.asItem(), modLoc("item/template"));

    addController(BlockRegistration.CONTROLLER.get(), false, modLoc("block/overlay_controller"));

    addCasing(BlockRegistration.CASING_PLAIN.get(), false, null, modLoc("block/overlay_transparent"));
    addCasing(BlockRegistration.CASING_REINFORCED.get(), true, null, modLoc("block/overlay_reinforced"));
    addCasing(BlockRegistration.CASING_CIRCUITRY.get(), false, modLoc("block/overlay_circuitry"), modLoc("block/overlay_circuitry"));
    addCasing(BlockRegistration.CASING_GEARBOX.get(), false, modLoc("block/overlay_gearbox"), modLoc("block/overlay_gearbox"));
    addCasing(BlockRegistration.CASING_VENT.get(), false, modLoc("block/overlay_vent"), modLoc("block/overlay_vent"));
    addCasing(BlockRegistration.CASING_FIREBOX.get(), false, modLoc("block/overlay_firebox"), modLoc("block/overlay_firebox"));

    addHatch(BlockRegistration.BIOME_READER.get(), false, modLoc("block/overlay_biome_reader"), false);
    addHatch(BlockRegistration.CHUNKLOADER.get(), false, modLoc("block/overlay_chunkloader"), false);
    addHatch(BlockRegistration.DIMENSIONAL_DETECTOR.get(), false, modLoc("block/overlay_dimensional_detector"), false);
    addHatch(BlockRegistration.HEIGHT_METER.get(), false, modLoc("block/overlay_height_meter"), true);
    addHatch(BlockRegistration.TIME_COUNTER.get(), false, modLoc("block/overlay_time_counter"), false);
    addHatch(BlockRegistration.WEATHER_SENSOR.get(), false, modLoc("block/overlay_weather_sensor"), false);

    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get(), false, modLoc("block/overlay_durabilityhatch_tiny"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get(), false, modLoc("block/overlay_durabilityhatch_small"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get(), false, modLoc("block/overlay_durabilityhatch_normal"), false);
    addHatch(BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get(), true, modLoc("block/overlay_durabilityhatch_big"), false);

    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(), false, energy(true, EnergyHatchSize.TINY), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(), false, energy(true, EnergyHatchSize.SMALL), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(), false, energy(true, EnergyHatchSize.NORMAL), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(), true, energy(true, EnergyHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(), true, energy(true, EnergyHatchSize.BIG), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(), true, energy(true, EnergyHatchSize.HUGE), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(), true, energy(true, EnergyHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(), true, energy(true, EnergyHatchSize.ULTIMATE), false);

    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(), false, energy(false, EnergyHatchSize.TINY), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(), false, energy(false, EnergyHatchSize.SMALL), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(), false, energy(false, EnergyHatchSize.NORMAL), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(), true, energy(false, EnergyHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(), true, energy(false, EnergyHatchSize.BIG), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(), true, energy(false, EnergyHatchSize.HUGE), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(), true, energy(false, EnergyHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(), true, energy(false, EnergyHatchSize.ULTIMATE), false);

    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get(), false, experience(true, ExperienceHatchSize.TINY), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get(), false, experience(true, ExperienceHatchSize.SMALL), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get(), false, experience(true, ExperienceHatchSize.NORMAL), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get(), true, experience(true, ExperienceHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get(), true, experience(true, ExperienceHatchSize.BIG), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get(), true, experience(true, ExperienceHatchSize.HUGE), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get(), true, experience(true, ExperienceHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get(), true, experience(true, ExperienceHatchSize.VACUUM), false);

    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get(), false, experience(false, ExperienceHatchSize.TINY), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get(), false, experience(false, ExperienceHatchSize.SMALL), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get(), false, experience(false, ExperienceHatchSize.NORMAL), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get(), true, experience(false, ExperienceHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get(), true, experience(false, ExperienceHatchSize.BIG), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get(), true, experience(false, ExperienceHatchSize.HUGE), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get(), true, experience(false, ExperienceHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get(), true, experience(false, ExperienceHatchSize.VACUUM), false);

    addHatch(BlockRegistration.FLUID_INPUT_HATCH_TINY.get(), false, fluid(true, FluidHatchSize.TINY), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(), false, fluid(true, FluidHatchSize.SMALL), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(), false, fluid(true, FluidHatchSize.NORMAL), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(), true, fluid(true, FluidHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_BIG.get(), true, fluid(true, FluidHatchSize.BIG), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(), true, fluid(true, FluidHatchSize.HUGE), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(), true, fluid(true, FluidHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get(), true, fluid(true, FluidHatchSize.VACUUM), false);

    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(), false, fluid(false, FluidHatchSize.TINY), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(), false, fluid(false, FluidHatchSize.SMALL), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(), false, fluid(false, FluidHatchSize.NORMAL), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(), true, fluid(false, FluidHatchSize.REINFORCED), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(), true, fluid(false, FluidHatchSize.BIG), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(), true, fluid(false, FluidHatchSize.HUGE), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(), true, fluid(false, FluidHatchSize.LUDICROUS), false);
    addHatch(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(), true, fluid(false, FluidHatchSize.VACUUM), false);

    addHatch(BlockRegistration.ITEM_INPUT_BUS_TINY.get(), false, item(true, ItemBusSize.TINY), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_SMALL.get(), false, item(true, ItemBusSize.SMALL), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(), false, item(true, ItemBusSize.NORMAL), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(), true, item(true, ItemBusSize.REINFORCED), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_BIG.get(), true, item(true, ItemBusSize.BIG), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_HUGE.get(), true, item(true, ItemBusSize.HUGE), false);
    addHatch(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get(), true, item(true, ItemBusSize.LUDICROUS), false);

    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(), false, item(false, ItemBusSize.TINY), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(), false, item(false, ItemBusSize.SMALL), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(), false, item(false, ItemBusSize.NORMAL), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(), true, item(false, ItemBusSize.REINFORCED), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(), true, item(false, ItemBusSize.BIG), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(), true, item(false, ItemBusSize.HUGE), false);
    addHatch(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(), true, item(false, ItemBusSize.LUDICROUS), false);

    addHatch(BlockRegistration.PARALLEL_HATCH_BASIC.get(), false, parallel(ParallelHatchSize.BASIC), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_MEDIUM.get(), false, parallel(ParallelHatchSize.MEDIUM), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_ADVANCED.get(), false, parallel(ParallelHatchSize.ADVANCED), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_ULTIMATE.get(), false, parallel(ParallelHatchSize.ULTIMATE), true);
    addHatch(BlockRegistration.PARALLEL_HATCH_MAX.get(), false, parallel(ParallelHatchSize.MAX), true);

    addHatch(BlockRegistration.FUEL_TANK_TINY.get(), false, fuel(FuelTankSize.TINY), false);
    addHatch(BlockRegistration.FUEL_TANK_SMALL.get(), false, fuel(FuelTankSize.SMALL), false);
    addHatch(BlockRegistration.FUEL_TANK_NORMAL.get(), false, fuel(FuelTankSize.NORMAL), false);
    addHatch(BlockRegistration.FUEL_TANK_REINFORCED.get(), true, fuel(FuelTankSize.REINFORCED), false);
    addHatch(BlockRegistration.FUEL_TANK_BIG.get(), true, fuel(FuelTankSize.BIG), false);
    addHatch(BlockRegistration.FUEL_TANK_HUGE.get(), true, fuel(FuelTankSize.HUGE), false);

    addHatch(BlockRegistration.EFFECT_DISPENSER_SMALL.get(), false, effect(EffectDispenserSize.SMALL), false);
    addHatch(BlockRegistration.EFFECT_DISPENSER_MEDIUM.get(), false, effect(EffectDispenserSize.MEDIUM), false);
    addHatch(BlockRegistration.EFFECT_DISPENSER_BIG.get(), true, effect(EffectDispenserSize.BIG), false);

    addHatch(BlockRegistration.ENTITY_DETECTOR.get(), false, entity(RequirementEntity.Action.CHECK_AMOUNT), false);
    addHatch(BlockRegistration.ENTITY_KILLER.get(), true, entity(RequirementEntity.Action.KILL), false);
    addHatch(BlockRegistration.ENTITY_SPAWNER.get(), true, entity(RequirementEntity.Action.SPAWN), false);
    addHatch(BlockRegistration.ENTITY_HEALER.get(), false, entity(RequirementEntity.Action.ADD_HEALTH), false);
    addHatch(BlockRegistration.ENTITY_DAMAGER.get(), false, entity(RequirementEntity.Action.CONSUME_HEALTH), false);

    addHatch(BlockRegistration.STRUCTURE_CHECKER.get(), true, modLoc("block/overlay_structure_checker"), false);
    addHatch(BlockRegistration.REDSTONE_PORT.get(), false, modLoc("block/overlay_redstone_port"), false);
    addHatch(BlockRegistration.COMMAND_EXECUTIONER.get(), false, modLoc("block/overlay_command_executioner"), false);
  }

  private void addDefaultModels() {
    ResourceLocation blockModelOverlayOrientableAll = modLoc("block/blockmodel_overlay_orientable_all");
    ResourceLocation blockModelOverlayAll = modLoc("block/blockmodel_overlay_all");
    ResourceLocation blockModelOverlay = modLoc("block/blockmodel_overlay");

    var blockModelOverlayOrientableAllModel = models()
        .getBuilder(blockModelOverlayOrientableAll.getPath())
        .parent(new ModelFile.UncheckedModelFile(blockModelOverlay))
        .transforms()
          .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
          .rotation(0, 135, 0)
          .translation(0, 0, 0)
          .scale(0.4f)
          .end()
        .end()
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
        .texture("ov_r_west", "#ov_r_all");
    block(blockModelOverlayOrientableAll, blockModelOverlayOrientableAllModel);

    var blockModelOverlayAllModel = models()
        .getBuilder(blockModelOverlayAll.getPath())
        .parent(new ModelFile.UncheckedModelFile(blockModelOverlay))
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
        .texture("ov_r_west", "#ov_r_all");
    block(blockModelOverlayAll, blockModelOverlayAllModel);

    var blockModelOverlayModel = models()
        .getBuilder(blockModelOverlay.getPath())
        .parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
        .renderType("cutout")
        .texture("ov_r_all", modLoc("block/overlay_transparent"))
        // background
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .allFaces((dir, builder) -> builder.tintindex(4)
            .cullface(dir)
            .texture("#bg_" + dir.getName())
            .end()
        )
        .end()
        // reinforced overlay
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .allFaces((dir, builder) -> builder
            .cullface(dir)
            .texture("#ov_r_" + dir.getName())
            .end()
        )
        .end()
        // overlay
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .allFaces((dir, builder) -> builder
            .cullface(dir)
            .texture("#ov_" + dir.getName())
            .end()
        )
        .end();
    block(blockModelOverlay, blockModelOverlayModel);

    ResourceLocation hatch_all = modLoc("hatch_all");
    var hatch_all_model = models()
        .getBuilder(hatch_all.withPrefix("default/").getPath())
        .parent(new ModelFile.UncheckedModelFile(blockModelOverlayAll));
    defaultModel(hatch_all, hatch_all_model);

    ResourceLocation hatch_orientable = modLoc("hatch_orientable");
    var hatch_orientable_model = models()
        .getBuilder(hatch_orientable.withPrefix("default/").getPath())
        .parent(new ModelFile.UncheckedModelFile(blockModelOverlayOrientableAll));
    defaultModel(hatch_orientable, hatch_orientable_model);

    models().cubeAll("nope", mcLoc("item/barrier"));
  }
  
  private ResourceLocation experience(boolean input, ExperienceHatchSize size) {
    return modLoc("block/overlay_experience" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private ResourceLocation energy(boolean input, EnergyHatchSize size) {
    return modLoc("block/overlay_energy" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private ResourceLocation fluid(boolean input, FluidHatchSize size) {
    return modLoc("block/overlay_fluid" + (input ? "input" : "output") + "hatch_" + size.getSerializedName());
  }
  private ResourceLocation item(boolean input, ItemBusSize size) {
    return modLoc("block/overlay_" + (input ? "input" : "output") + "bus_" + size.getSerializedName());
  }
  private ResourceLocation parallel(ParallelHatchSize size) {
    return modLoc("block/overlay_parallel_hatch_" + size.getSerializedName());
  }
  private ResourceLocation fuel(FuelTankSize size) {
    return modLoc("block/overlay_fueltank_" + size.getSerializedName());
  }
  private ResourceLocation effect(EffectDispenserSize size) {
    return modLoc("block/overlay_effectdispenser_" + size.getSerializedName());
  }
  private ResourceLocation entity(RequirementEntity.Action mode) {
    return modLoc("block/overlay_entity" + switch(mode) {
      case CHECK_AMOUNT, CHECK_HEALTH -> "detector";
      case KILL -> "killer";
      case SPAWN -> "spawner";
      case CONSUME_HEALTH -> "damager";
      case ADD_HEALTH -> "healer";
    });
  }
}
