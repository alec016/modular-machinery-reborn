package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags;

final class EnUsLang extends Lang {

  @Override
  protected void addKeys() {
    add("key.categories.modular_machinery_reborn", "Modular Machinery Reborn");
    add("key." + mm("structure_mode_change"), "Structure Creator Mode");
  }

  @Override
  protected void addControllerTexts() {
    add(mm("controller.tooltip.0"), "Click in placed controller with blueprint");
    add(mm("controller.tooltip.1"), "to show the structure around the controller");
    add(mm("controller.no_machine"), "No machine provided");
    add(mmr("controller.exactly"), " (Exactly %s)");
    add(mmr("controller.max"), " (Max: %s)");
    add(mmr("controller.min"), " (Min: %s)");
    add(mmr("controller.min_max"), " (Min: %s, Max: %s)");
    add(mm("controller.shift"), "[SHIFT]");
    add(mm("controller.control"), "[CTRL]");
    add(mm("controller.alt"), "[ALT]");
    add(mm("controller.shift.blocks"), "to show the required blocks");
    add(mm("controller.control.modifier"), "to show the modifier blocks");
    add(mm("controller.alt.minmax"), "to show the min-max specifications");
    add(mm("controller.required"), "Required:");
    add(mm("controller.required.item"), "%s %s");
    add(mm("controller.required.tag"), "%s");
    add(mm("controller.required.block"), "%s");
    add(mm("controller.required.block.key"), "Press [%s] to show full info");
    add(mm("controller.required.shift"), "SHIFT");
    add(mm("controller.required.control"), "CTRL");
    add(mm("controller.modifier"), "Modifiers:");
    add(mmr("place.non_air"), "Tried to place %s in %s but found no valid structure block");
    add(mmr("place.no_item"), "Tried to place %s in %s but couldn't find the item %s in the player inventory");
    add(mmr("place.replace"), "Breaking %s at %s to place new block...");
    add(mmr("damagesource.kill"), "%s was killed by a %s");
    add(mmr("structure.error.exact"), "Expected %s, but found %s (%s)");
    add(mmr("structure.error.max"), "Expected at most %s, but found %s (%s)");
    add(mmr("structure.error.min"), "Expected at least %s, but found %s (%s)");
    add(mmr("structure.error.between"), "Expected to be between %s and %s, but found %s (%s)");
    add(mmr("config.tooltip.info"), "Relative to %s");
    add(mmr("north"), "North");
    add(mmr("south"), "South");
    add(mmr("east"), "East");
    add(mmr("west"), "West");
    add(mmr("up"), "Up");
    add(mmr("down"), "Down");
  }

  @Override
  protected void addJade() {
    add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "Modular Machinery Reborn Controller");
    add("config.jade.plugin_modular_machinery_reborn.hatch_component_provider", "Modular Machinery Reborn Hatch");
  }

  @Override
  protected void addCommands() {
    add(mm("command.reload.machines"), "Successfully reloaded modular machinery machines!");
    add(mm("command.reload.recipes"), "Successfully reloaded modular machinery recipes!");
  }

  @Override
  protected void addTags() {
    MMRTags.getAllTags().forEach(tag -> add(tag.getFirst(), tag.getSecond()));
  }

  @Override
  protected void addItemGroups() {
    add("itemgroup." + mm("group"), "Modular Machinery Reborn");
  }

  @Override
  protected void addJsonProps() {
    addCores();
  }

  private void addCores() {
    JsonArray coreInfo = new JsonArray();
    JsonObject coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", "Running: ");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("index", 0);
    coreInfo0.addProperty("color", "aqua");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", " cores");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    add(mmr("waila.cores"), coreInfo);
  }

  @Override
  protected void addRecipeModifiers() {
    add(recipeModifier("speed.ADDITION"), " (%s the speed of processing the recipe)");
    add(recipeModifier("speed.MULTIPLICATION"), " (x%s the speed of processing the recipe)");
    add(recipeModifier("item.ADDITION"), " (%s item(s) %s to recipe, chance: %s)");
    add(recipeModifier("item.MULTIPLICATION"), " (x%s item %s of recipe, chance: %s)");
    add(recipeModifier("durability.ADDITION"), " (%s item(s) durability %s to recipe)");
    add(recipeModifier("durability.MULTIPLICATION"), " (x%s item durability %s of recipe)");
    add(recipeModifier("fluid.ADDITION"), " (%s fluid mB(s) %s to recipe, chance: %s)");
    add(recipeModifier("fluid.MULTIPLICATION"), " (x%s fluid %s of recipe, chance: %s)");
    add(recipeModifier("energy.ADDITION"), " (%s energy %s to recipe, chance: %s)");
    add(recipeModifier("energy.MULTIPLICATION"), " (x%s energy %s of recipe, chance: %s)");
    add(recipeModifier("loot_table.ADDITION"), " (%s loot table luck to recipe)");
    add(recipeModifier("loot_table.MULTIPLICATION"), " (x%s loot table luck of recipe)");
    add(recipeModifier("experience.ADDITION"), " (%s experience %s to recipe, chance: %s)");
    add(recipeModifier("experience.MULTIPLICATION"), " (x%s experience %s of recipe, chance: %s)");
    add(recipeModifier("fuel.ADDITION"), " (%s burntime %s to recipe, chance: %s)");
    add(recipeModifier("fuel.MULTIPLICATION"), " (x%s burntime %s of recipe, chance: %s)");
  }

  @Override
  protected void addIngredients() {
    add(mm(ingredient("perTick")), "per tick");
    add(mm(ingredient("chance.input")), "Chance to be consumed: %s%s");
    add(mm(ingredient("chance")), "%s%s");
    add(mm(ingredient("chance.output")), "Chance to be produced: %s%s");
    add(mm(ingredient("chance.not_consumed")), "Not Consumed");
    add(mm(ingredient("chance.nc")), "NC");
    add(mm("requirement.mode.input"), "Input");
    add(mm("requirement.mode.output"), "Output");
    add(mm("requirement.mode.none"), "None");
    add(jeiIngredient("long"), "%s");
    add(jeiIngredient("int"), "%s");
    add(jeiIngredient("energy.input"), "Require: %s RF");
    add(jeiIngredient("energy.output"), "Produce: %s RF");
    add(jeiIngredient("energy.total.input"), "Require: %s RF @ %s RF/t");
    add(jeiIngredient("energy.total.output"), "Produce: %s RF @ %s RF/t");
    add(jeiIngredient("fluid.input"), "Require %s %s mB");
    add(jeiIngredient("fluid.output"), "Produce %s %s mB");
    add(jeiIngredient("experience.input"), "Require %s XP");
    add(jeiIngredient("experience.output"), "Produce %s XP");
    add(jeiIngredient("duration"), "Process time: %s ticks");
    add(jeiIngredient("item.input"), "Require");
    add(jeiIngredient("item.output"), "Produce");
    add(jeiIngredient("dimension.true"), "Dimension blacklist: %s");
    add(jeiIngredient("dimension.false"), "Dimension whitelist: %s");
    add(jeiIngredient("biome.true"), "Biome blacklist: %s");
    add(jeiIngredient("biome.false"), "Biome whitelist: %s");
    add(jeiIngredient("weather"), "Weather time: %s");
    add(jeiIngredient("time"), "Time: %s");
    add(jeiIngredient("height"), "Height: %s");
    add(jeiIngredient("chunkload"), "Chunkload radius: %s");
    add(mm(ingredient("durability.consume")), "Consume %s durability");
    add(mm(ingredient("durability.repair")), "Repair %s durability");
    add(jeiIngredient("fuel"), "Require %s burntime");
    add(jeiIngredient("effect.info.tick"), "Give %s %s for %s ticks each tick");
    add(jeiIngredient("effect.info.whitelist"), "Entity Whitelist :");
    add(jeiIngredient("entity.whitelist"), "Entity whitelist:");
    add(jeiIngredient("entity.blacklist"), "Entity blacklist:");
    add(jeiIngredient("entity.kill.info"), "Kill %s entities in %s blocks radius");
    add(jeiIngredient("entity.spawn.info"), "Spawns %s entities in %s blocks radius");
    add(jeiIngredient("entity.check_health.info"), "Requires %s entity HP in a %s blocks radius (not consumed)");
    add(jeiIngredient("entity.check_amount.info"), "Requires %s entities in a %s blocks radius");
    add(jeiIngredient("entity.consume_health.info"), "Requires and consume %s entity HP in a %s blocks radius");
    add(jeiIngredient("entity.add_health.info"), "Requires missing and heals %s entity HP in a %s blocks radius");
    add(jeiIngredient("structure.info"), "Requires a structure");
    add(jeiIngredient("structure.click"), "Click to view the required structure");
    add(jeiIngredient("structure.shift"), "Press [Ctrl] to view blocks list");
    add(jeiIngredient("structure.list"), "  %sx %s");
    add(jeiIngredient("structure.break"), "Will break structure");
    add(jeiIngredient("structure.destroy"), "Will destroy structure");
    add(jeiIngredient("structure.place"), "Will place structure");
    add(jeiIngredient("structure.not"), "Not %s");
    add(jeiIngredient("structure.or"), " or ");
    add(jeiIngredient("redstone.input"), "Requires applying %s redstone power");
    add(jeiIngredient("redstone.output"), "Emits %s redstone power");
    add(jeiIngredient("command.info"), "Run %s on %s");
  }

  @Override
  protected void addItems() {
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "Structure Creator (Single)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "Structure Creator (Box)");
    addItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM, "Structure Template (Incomplete)");
    addItem(ItemRegistration.BLUEPRINT, "Machine Blueprint");
    addItem(ItemRegistration.MODULARIUM, "Modularium");
    addItem(ItemRegistration.WRENCH, "Wrench");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.CONTROLLER, "Machine Controller");

    addBlock(BlockRegistration.CASING_PLAIN, "Machine Casing");
    addBlock(BlockRegistration.CASING_VENT, "Machine Vent");
    addBlock(BlockRegistration.CASING_FIREBOX, "Firebox Casing");
    addBlock(BlockRegistration.CASING_REINFORCED, "Reinforced Machine Casing");
    addBlock(BlockRegistration.CASING_CIRCUITRY, "Machine Circuitry");
    addBlock(BlockRegistration.CASING_GEARBOX, "Machine Gearbox");

    addBlock(BlockRegistration.ITEM_INPUT_BUS_TINY, "Tiny Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_SMALL, "Small Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_NORMAL, "Normal Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_REINFORCED, "Reinforced Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_BIG, "Big Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_HUGE, "Huge Item Input Bus");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS, "Ludicrous Item Input Bus");

    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_TINY, "Tiny Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_SMALL, "Small Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL, "Normal Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED, "Reinforced Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_BIG, "Big Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_HUGE, "Huge Item Output Bus");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS, "Ludicrous Item Output Bus");

    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_TINY, "Tiny Durability Hatch");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL, "Small Durability Hatch");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL, "Normal Durability Hatch");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_BIG, "Big Durability Hatch");

    addBlock(BlockRegistration.FLUID_INPUT_HATCH_TINY, "Tiny Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_SMALL, "Small Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_NORMAL, "Normal Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED, "Reinforced Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_BIG, "Big Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_HUGE, "Huge Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS, "Ludicrous Fluid Input Hatch");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_VACUUM, "Vacuum Fluid Input Hatch");

    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_TINY, "Tiny Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL, "Small Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL, "Normal Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED, "Reinforced Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_BIG, "Big Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE, "Huge Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS, "Ludicrous Fluid Output Hatch");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM, "Vacuum Fluid Output Hatch");

    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY, "Tiny Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL, "Small Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL, "Normal Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED, "Reinforced Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG, "Big Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE, "Huge Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS, "Ludicrous Experience Input Hatch");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM, "Vacuum Experience Input Hatch");

    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY, "Tiny Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL, "Small Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL, "Normal Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED, "Reinforced Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG, "Big Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE, "Huge Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS, "Ludicrous Experience Output Hatch");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM, "Vacuum Experience Output Hatch");

    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_TINY, "Tiny Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_SMALL, "Small Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL, "Normal Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED, "Reinforced Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_BIG, "Big Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_HUGE, "Huge Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "Ludicrous Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "Ultimate Energy Input Hatch");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "Tiny Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "Small Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "Normal Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "Reinforced Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "Big Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "Huge Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "Ludicrous Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE, "Ultimate Energy Output Hatch");

    addBlock(BlockRegistration.PARALLEL_HATCH_BASIC, "Basic Parallel Hatch");
    addBlock(BlockRegistration.PARALLEL_HATCH_MEDIUM, "Medium Parallel Hatch");
    addBlock(BlockRegistration.PARALLEL_HATCH_ADVANCED, "Advanced Parallel Hatch");
    addBlock(BlockRegistration.PARALLEL_HATCH_ULTIMATE, "Ultimate Parallel Hatch");
    addBlock(BlockRegistration.PARALLEL_HATCH_MAX, "Max Parallel Hatch");

    addBlock(BlockRegistration.DIMENSIONAL_DETECTOR, "Dimensional Detector");
    addBlock(BlockRegistration.BIOME_READER, "Biome Reader");
    addBlock(BlockRegistration.WEATHER_SENSOR, "Weather Sensor");
    addBlock(BlockRegistration.TIME_COUNTER, "Time Counter");
    addBlock(BlockRegistration.HEIGHT_METER, "Height Meter");
    addBlock(BlockRegistration.CHUNKLOADER, "Chunkloader");

    addBlock(BlockRegistration.FUEL_TANK_TINY, "Tiny Fuel Tank");
    addBlock(BlockRegistration.FUEL_TANK_SMALL, "Small Fuel Tank");
    addBlock(BlockRegistration.FUEL_TANK_NORMAL, "Normal Fuel Tank");
    addBlock(BlockRegistration.FUEL_TANK_REINFORCED, "Reinforced Fuel Tank");
    addBlock(BlockRegistration.FUEL_TANK_BIG, "Big Fuel Tank");
    addBlock(BlockRegistration.FUEL_TANK_HUGE, "Huge Fuel Tank");

    addBlock(BlockRegistration.EFFECT_DISPENSER_SMALL, "Small Effect Dispenser");
    addBlock(BlockRegistration.EFFECT_DISPENSER_MEDIUM, "Medium Effect Dispenser");
    addBlock(BlockRegistration.EFFECT_DISPENSER_BIG, "Big Effect Dispenser");

    addBlock(BlockRegistration.ENTITY_DETECTOR, "Entity Detector");
    addBlock(BlockRegistration.ENTITY_KILLER, "Entity Killer");
    addBlock(BlockRegistration.ENTITY_SPAWNER, "Entity Spawner");
    addBlock(BlockRegistration.ENTITY_HEALER, "Entity Healer");
    addBlock(BlockRegistration.ENTITY_DAMAGER, "Entity Damager");

    addBlock(BlockRegistration.STRUCTURE_CHECKER, "Structure Checker");
    addBlock(BlockRegistration.REDSTONE_PORT, "Redstone Port");
    addBlock(BlockRegistration.COMMAND_EXECUTIONER, "Command Executioner");
  }

  @Override
  protected void addTooltips() {
    add(tooltip("machinery.empty"), "Empty");
    add(tooltip("machinery.fuel"), "Accepts any fuel");
    add(tooltip("machinery.fuel.item"), "BurnTime: %s");
    add(tooltip("machinery.fuel.in"), "Required total Fuel Burntime:");
    add(tooltip("machinery.fuel.in.total"), "%s ticks.");

    add(tooltip("machinery.duration"), "Processing Time: %s ticks");
    add(tooltip("machinery.chance.in"), "Consumption Chance: %s");
    add(tooltip("machinery.chance.out"), "Production Chance: %s");
    add(tooltip("machinery.chance.in.never"), "Doesn't get consumed.");
    add(tooltip("machinery.chance.out.never"), "Is never produced.");

    add(tooltip("machinery.energy.in"), "Required Energy:");
    add(tooltip("machinery.energy.in.tick"), "Per Tick: %s %s/t");
    add(tooltip("machinery.energy.in.total"), "Total: %s %s");
    add(tooltip("machinery.energy.out"), "Produced Energy:");
    add(tooltip("machinery.energy.out.tick"), "Per Tick: %s %s/t");
    add(tooltip("machinery.energy.out.total"), "Total: %s %s");

    add(tooltip("experiencehatch.empty"), "Empty");
    add(tooltip("experiencehatch.tank.points"), "%s / %s XP");
    add(tooltip("experiencehatch.tank.levels"), "%s / %s levels");
    add(tooltip("experiencehatch.tank.info"), "Can hold %s XP points");

    add(tooltip("energy.type.fe"), "FE");
    add(tooltip("energy.type.ic2_eu"), "EU");
    add(tooltip("energy.type.gt_eu"), "EU");

    add(tooltip("constructtool.creative"), "Creative Structure-To-JSON Tool");

    add(tooltip("itembus.slot"), "1 Slot");
    add(tooltip("itembus.slots"), "%s Slots");

    add(tooltip("fluidhatch.empty"), "Empty");
    add(tooltip("fluidhatch.fluid"), "[Fluid]");
    add(tooltip("fluidhatch.gas"), "[Gas]");
    add(tooltip("fluidhatch.tank"), "%smB / %smB");
    add(tooltip("fluidhatch.tank.gas"), "%s / %s");
    add(tooltip("fluidhatch.tank.info"), "Can hold %s mB");

    add(tooltip("energyhatch.charge"), "%s / %s %s");
    add(tooltip("energyhatch.storage"), "Stores %s FE");
    add(tooltip("energyhatch.out.transfer"), "Transfers %s FE per tick");
    add(tooltip("energyhatch.in.accept"), "Accepts %s FE per tick");

    add(tooltip("parallelhatch.size"), "Can make the multiblock run up to %s recipes");
    add(tooltip("blueprint"), "Click on any machine controller to show the structure preview");

    add(tooltip("fueltank.storage"), "Can store %s burntime (ticks)");
    add(tooltip("fuel_tank.tank"), "%s / %s ticks");

    add(tooltip("effectdispenser.interdimensional"), "Gives effects interdimensionally");
    add(tooltip("effectdispenser.radius"), "Gives effects in a radius of %s blocks around this block");

    add(mmr("side.enabled"), "Enabled");
    add(mmr("side.disabled"), "Disabled");
    add(mmr("side.top"), "Top");
    add(mmr("side.front"), "Front");
    add(mmr("side.back"), "Back");
    add(mmr("side.left"), "Left");
    add(mmr("side.right"), "Right");
    add(mmr("side.bottom"), "Bottom");
    add(mmr(tooltip("open_side_config")), "Open Side Config");
    add(mmr("wrench.side_mode.change"), "Changed side: %s from %s to %s");
  }

  @Override
  protected void addComponents() {
    add(missingComponent("energy.output"), "No Energy Output Hatch found!");
    add(missingComponent("energy.input"), "No Energy Input Hatch found!");
    add(missingComponent("experience.output"), "No Experience Output Hatch found!");
    add(missingComponent("experience.input"), "No Experience Input Hatch found!");
    add(missingComponent("fluid.output"), "No Fluid Output Hatch found!");
    add(missingComponent("fluid.input"), "No Fluid Input Hatch found!");
    add(missingComponent("item.output"), "No Item Output Bus found!");
    add(missingComponent("item.input"), "No Item Input Bus found!");
    add(missingComponent("dimension"), "No Dimensional Detector found!");
    add(missingComponent("weather"), "No Weather Sensor found!");
    add(missingComponent("biome"), "No Biome Reader found!");
    add(missingComponent("time"), "No Time Counter found!");
    add(missingComponent("height"), "No Height Meter found!");
    add(missingComponent("chunkload"), "No Chunkloader found!");
    add(missingComponent("function"), "No Function Component(Controller) found!");
    add(missingComponent("durability.input"), "No Durability Hatch found!");
    add(missingComponent("durability.output"), "No Durability Hatch found!");
    add(missingComponent("fuel"), "No Fuel Tank found!");
    add(missingComponent("effect"), "No Effect Dispenser found!");
    add(missingComponent("entity.detector"), "No Entity Detector found!");
    add(missingComponent("entity.healer"), "No Entity Healer found!");
    add(missingComponent("entity.damager"), "No Entity Damager found!");
    add(missingComponent("entity.killer"), "No Entity Killer found!");
    add(missingComponent("entity.spawner"), "No Entity Spawner found!");
    add(missingComponent("structure"), "No Structure Checker found!");
    add(missingComponent("redstone"), "No Redstone Port found!");
    add(missingComponent("command"), "No Command Executioner found!");
  }

  @Override
  protected void addCraftcheck() {
    add(craftCheck("item.input"), "Missing input item!, required: %sx %s");
    add(craftCheck("item.output.space"), "Not enough inventory space for item output(s)!");
    add(craftCheck("durability.input"), "Not enough durability, %s needed but %s found !");
    add(craftCheck("durability.output"), "Can't repair item, trying to repair %s durability but %s missing !");
    add(craftCheck("fluid.input"), "Missing fluid input! required %s mB %s but found %s mB %s");
    add(craftCheck("fluid.output.space"), "Not enough tank space for fluid output(s)!, required: %s mB but found %s mB");
    add(craftCheck("fluid.output.fluid"), "Fluid in tank doesn't match, required: %s but found %s!");
    add(craftCheck("energy.input"), "Not enough energy! Required: %s FE but found %s FE");
    add(craftCheck("energy.output"), "Not enough space for energy output!, needed: %s FE, but found %s FE space");
    add(craftCheck("experience.input"), "Not enough experience!, %s XP but found %s XP");
    add(craftCheck("experience.output"), "Not enough space for experience output!, needed: %s XP, but found %s XP space");
    add(craftCheck("dimension.false"), "Not in the correct dimension, expected to be: %s, found: %s");
    add(craftCheck("dimension.true"), "Not in the correct dimension, expected to not be: %s, found: %s");
    add(craftCheck("biome.false"), "Not in the correct biome, expected to be: %s, found: %s");
    add(craftCheck("biome.true"), "Not in the correct biome, expected to not be: %s, found: %s");
    add(craftCheck("weather"), "Invalid weather time, expected: %s");
    add(craftCheck("time"), "Invalid time, expected: %s but found: %s");
    add(craftCheck("height"), "Invalid height, expected: %s but found: %s");
    add(craftCheck("parallel.loot_table"), "Can't process multiple Loot Tables at the same time, remove the Parallel Hatch to process this recipe.");
    add(craftCheck("function"), "Can't execute function requirement, check logs for errors!");
    add(craftCheck("function.no_listener"), "Function requirement with id: %s has no associated KubeJS event");
    add(craftCheck("function.interrupt"), "Stopped by KubeJS event");
    add(craftCheck("fuel"), "Not enough burntime, needed: %s but found %s");
    add(craftCheck("entity.amount"), "Not enough entities nearby !");
    add(craftCheck("entity.health"), "Can't collect %s health points with nearby entities !");
    add(craftCheck("structure"), "Invalid structure!");
    add(craftCheck("redstone"), "Required redstone power: %s but found %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "Controller");
    add(mm(gui("title.energy_hatch")), "Energy Hatch");
    add(mm(gui("title.fluid_hatch")), "Fluid Hatch");
    add(mm(gui("title.item_bus")), "Item Bus");
    add(mm(gui("title.parallel_hatch")), "Parallel Hatch");
    add(mm(gui("title.fuel_tank")), "Fuel Tank");
    add(mm(gui("title.redstone_port")), "Redstone Port");
    add(mmr(gui("button.back")), "Back");
    add(mmr(gui("button.close")), "Close");
    add(mmr(gui("button.page.next")), "Next Page");
    add(mmr(gui("button.page.prev")), "Prev Page");
    add(mm(gui("core_button")), "Show info about cores");
    add(mmr(gui("element.experience.tooltip")), "%s / %s");
    add(mmr(gui("element.experience.tooltip.input")), "Require: %s / %s");
    add(mmr(gui("element.experience.tooltip.output")), "Produce: %s / %s");
    add(mmr(gui("element.experience.level")), "%s level(s)");
    add(mmr(gui(tooltip("experience.button.extract_1"))), "Extract 1 Level");
    add(mmr(gui(tooltip("experience.button.extract_10"))), "Extract 10 Levels");
    add(mmr(gui(tooltip("experience.button.extract_all"))), "Extract All Levels");
    add(mmr(gui(tooltip("experience.button.insert_1"))), "Insert 1 Level");
    add(mmr(gui(tooltip("experience.button.insert_10"))), "Insert 10 Levels");
    add(mmr(gui(tooltip("experience.button.insert_all"))), "Insert All Levels");
    add(mm(gui("structure_placer_button")), "Click to try place structure");
    add(mm(gui("structure_breaker_button")), "Click to break the current structure");
    add(mmr(gui("structure.break")), "Are you sure you want to break the current structure?");
    add(mmr(gui("structure.place.modifier")), "Do you want to place the structure with ot without modifiers?");
    add(mmr(gui("structure.place.modifier.true")), "Are you sure you want to place the structure with modifiers?");
    add(mmr(gui("structure.place.modifier.false")), "Are you sure you want to place the structure without modifiers?");
    add(mmr(gui("structure.place.confirm.modifier")), "With modifiers");
    add(mmr(gui("structure.place.cancel.modifier")), "Without modifiers");
    add(mmr(gui("popup.confirm")), "Confirm");
    add(mmr(gui("popup.cancel")), "Cancel");
    add("emi." + tooltip("show.recipes"), "Show Recipes");
    add(mmr("emi.no_items"), "No items in recipe");
    add(mmr(gui(tooltip("redstone.button.mode.input"))), "Input");
    add(mmr(gui(tooltip("redstone.button.mode.output"))), "Output");
    add(mmr(gui(tooltip("redstone.button.mode.none"))), "None");
    add(mmr(gui(tooltip("button.enum.cycle"))), "Next: %s");
    add(mmr(gui(tooltip("auto_output.change"))), "Actual Mode: %s, Change to: %s");
    add(mmr(gui(tooltip("auto_output"))), "Auto Output");
    add(mmr(gui(tooltip("auto_input.change"))), "Actual Mode: %s, Change to: %s");
    add(mmr(gui(tooltip("auto_input"))), "Auto Input");
    add(mmr(tooltip("auto_output")), "Auto Output: %s");
    add(mmr(tooltip("auto_input")), "Auto Input: %s");
    add(mmr(gui(tooltip("enabled.true"))), "Enabled");
    add(mmr(gui(tooltip("enabled.false"))), "Disabled");
    add(mmr(tooltip("effect")), "Giving effect in %s block(s) radius");
    add(mmr(tooltip("effect.interdimensional")), "Giving effect interdimensionally");
    add(mmr(tooltip("redstone.emit")), "Emitting %s");
    add(mmr(tooltip("redstone.receive")), "Receiving %s");
    add(mmr(gui("missing_structure")), "Not Available until structure formed");
  }

  @Override
  protected void addStructureCreator() {
    add(mmr("no_paper_template"), "No paper or structure template found in inventory");
    add(mm("structure_creator.mode.change.tooltip"), "Press [%s] with item in hand(NO GUI) to change mode");
    add(mm("structure_creator.mode.change"), "Changed mode from %s to %s");
    add(mm("structure_creator.mode.single"), "Single");
    add(mm("structure_creator.mode.box"), "Box");
    add(mm("structure_creator.mode.box.first"), "Select first corner");
    add(mm("structure_creator.mode.box.second"), "Select seconds corner");
    add(mm("structure_creator.mode"), "Current mode: %s");
    add(mm("structure_creator.no_blocks"), "No blocks are selected");
    add(mm("structure_creator.amount"), "%s blocks selected");
    add(mm("structure_creator.select"), "Right click a block to add or remove it. If block has GUI shift click to add or remove it.");
    add(mm("structure_creator.finish"), "Shift click in any machine controller to get the structure");
    add(mm("structure_creator.message"), "Structure generated, click to copy: %s %s %s");
    add(mm("structure_creator.reset"), "Right click in the air while crouching to reset");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "Blueprint Found: %s");
    add(gui("controller.blueprint.none"), "None");
    add(gui("controller.structure"), "Structure Found: %s");
    add(gui("controller.structure.none"), "None");
    add(gui("controller.status"), "Status: ");
    add(gui("controller.error.info"), "Error Info: ");
    add(gui("controller.status.redstone_stopped"), "Machine stopped by incoming redstone signal.");
    add(gui("controller.status.paused"), "Paused");
    add(gui("controller.status.missing_structure"), "Missing structure");
    add(gui("controller.status.no_recipe"), "No matching recipe found");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "Processing...");
    add(gui("controller.status.crafting.progress"), "Progress: %s");
    add(mmr("core.active.true"), "Core is able to run recipes");
    add(mmr("core.active.false"), "Core is not able to run recipes");
    add(mmr("core.number"), "Core number: %s");
    add(mmr(gui("core.button")), "Click to show info about core number %s");
    add(mmr(gui(tooltip("core.action.button.single.add"))), "Add 1 active core");
    add(mmr(gui(tooltip("core.action.button.single.remove"))), "Remove 1 active core");
    add(mmr(gui(tooltip("core.action.button.shift.add"))), "Add 10 active cores");
    add(mmr(gui(tooltip("core.action.button.shift.remove"))), "Remove 10 active cores");
    add(mmr(gui(tooltip("core.action.button.control.add"))), "Add all active cores");
    add(mmr(gui(tooltip("core.action.button.control.remove"))), "Remove all active cores");
  }
}
