package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags;

final class EsEsLang extends Lang {
  @Override
  protected void addItemGroups() {
    add("itemgroup." + mm("group"), "Modular Machinery Reborn");
  }

  @Override
  protected void addTags() {
    MMRTags.getAllTags().forEach(tag -> add(tag.getFirst(), tag.getSecond()));
  }

  @Override
  protected void addCommands() {
    add(mm("command.reload.machines"), "Maquinaria de Modular Machinery recargada satisfactoriamente!");
    add(mm("command.reload.recipes"), "Recetas de Modular Machinery recargadas satisfactoriamente!");
  }

  @Override
  protected void addKeys() {
    add("key.categories.modular_machinery_reborn", "Modular Machinery Reborn");
    add("key." + mm("structure_mode_change"), "Structure Creator Mode");
  }

  @Override
  protected void addJade() {
    add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "Modular Machinery Reborn Controller");
    add("config.jade.plugin_modular_machinery_reborn.hatch_component_provider", "Modular Machinery Reborn hatch");
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
    add(mm("controller.alt.minmax"), "to show the min-max specifications");
    add(mm("controller.shift.blocks"), "to show the required blocks");
    add(mm("controller.control.modifier"), "to show the modifier blocks");
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
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "Creador de estructura (Single)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN, "Creador de estructura (Proximity (Vein))");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "Creador de estructura (Box)");
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
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "Experience Energy Input Hatch");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "Ultimate Energy Input Hatch");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "Tiny Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "Small Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "Normal Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "Reinforced Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "Big Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "Huge Energy Output Hatch");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "Experience Energy Output Hatch");
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

    add(mmr("side.enabled"), "Activado");
    add(mmr("side.disabled"), "Desactivado");
    add(mmr("side.top"), "Arriba");
    add(mmr("side.front"), "Frontal");
    add(mmr("side.back"), "Detrás");
    add(mmr("side.left"), "Izquierda");
    add(mmr("side.right"), "Derecha");
    add(mmr("side.bottom"), "Abajo");
    add(mmr(tooltip("open_side_config")), "Abrir Configuración de Lados");
    add(mmr("wrench.side_mode.change"), "Changed side: %s from %s to %s");
  }

  @Override
  protected void addComponents() {
    add(missingComponent("energy.output"), "No se ha encontrado Energy Output Hatch!");
    add(missingComponent("energy.input"), "No se ha encontrado Energy Input Hatch!");
    add(missingComponent("experience.output"), "No se ha Experience Output Hatch!");
    add(missingComponent("experience.input"), "No se ha Experience Input Hatch!");
    add(missingComponent("fluid.output"), "No se ha Fluid Output Hatch!");
    add(missingComponent("fluid.input"), "No se ha Fluid Input Hatch!");
    add(missingComponent("item.output"), "No se ha Item Output Bus!");
    add(missingComponent("item.input"), "No se ha Item Input Bus!");
    add(missingComponent("dimension"), "No se ha Dimensional Detector!");
    add(missingComponent("weather"), "No se ha Weather Sensor!");
    add(missingComponent("biome"), "No se ha Biome Reader!");
    add(missingComponent("time"), "No se ha Time Counter!");
    add(missingComponent("height"), "No se ha Height Meter!");
    add(missingComponent("chunkload"), "No se ha Chunkloader!");
    add(missingComponent("function"), "No se ha Function Component(Controller)!");
    add(missingComponent("durability.input"), "No se ha Durability Hatch!");
    add(missingComponent("durability.output"), "No se ha Durability Hatch!");
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
    add(craftCheck("item.input"), "Sin item de entrada!, necesario: %sx %s");
    add(craftCheck("item.output.space"), "No hay suficiente espacio en el inventario para la salida de item(s)!");
    add(craftCheck("durability.input"), "No hay suficiente durabilidad, %s necesario pero se encontró %s!");
    add(craftCheck("durability.output"), "No se puede reparar el item, tratando de reparar %s durabilidad pero falta(n) %s!");
    add(craftCheck("fluid.input"), "Sin líquido de entrada! necesario %s mB %s pero se encontró %s mB %s");
    add(craftCheck("fluid.output.space"), "No hay suficiente espacio en el tanque para salida de líquido(s)!, necesario: %s mB pero se encontró %s mB");
    add(craftCheck("fluid.output.fluid"), "El líquido del tanque no coincide, necesario: %s pero se encontró %s!");
    add(craftCheck("energy.input"), "No hay suficiente energía! Se necesita %s FE pero se encontró %s FE");
    add(craftCheck("energy.output"), "No hay suficiente espacio para la salida de energía!, Se necesita %s FE, pero se encontró %s FE de espacio");
    add(craftCheck("experience.input"), "No hay suficiente experiencia!, %s XP pero se encontró %s XP");
    add(craftCheck("experience.output"), "No hay suficiente espacio para la salida de experiencia!, necesario: %s XP, pero se encontró %s XP space");
    add(craftCheck("dimension.false"), "No está en la dimensión correcta, se esperaba estar en: %s, estando en: %s");
    add(craftCheck("dimension.true"), "No está en la dimensión correcta, se esperaba no estar en: %s, estando en: %s");
    add(craftCheck("biome.false"), "No está en la bioma correcto, se esperaba estar en: %s, estando en: %s");
    add(craftCheck("biome.true"), "No está en la bioma correcto, se esperaba no estar en: %s, estando en: %s");
    add(craftCheck("weather"), "Tiempo atmosférico inválido, se esperaba: %s");
    add(craftCheck("time"), "Tiempo del día inválido, se esperaba: %s se ecnontró: %s");
    add(craftCheck("height"), "Altura del controlador inválida, se esperaba: %s se ecnontró: %s");
    add(craftCheck("parallel.loot_table"), "No se pueden procesar varias Loot Tables al mismo tiempo, elimina el Parallel Hatch para procesar esta receta.");
    add(craftCheck("function"), "No se puede ejecutar la función requerida, mira los logs para más información!");
    add(craftCheck("function.no_listener"), "Función requerida con id: %s no tiene asociado ningún KubeJS event");
    add(craftCheck("function.interrupt"), "Parado por KubeJS event");
    add(craftCheck("fuel"), "Not enough burntime, needed: %s but found %s");
    add(craftCheck("entity.amount"), "Not enough entities nearby !");
    add(craftCheck("entity.health"), "Can't collect %s health points with nearby entities !");
    add(craftCheck("structure"), "Invalid structure!");
    add(craftCheck("redstone"), "Required redstone power: %s but found %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "Controlador");
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
    add(mmr(gui("missing_structure")), "No disponible hasta que la estructura no esté formada");
    add(mmr(gui("paused")), "No disponible mientras el multibloque está pausado");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "Blueprint Found: %s");
    add(gui("controller.blueprint.none"), "Ninguno");
    add(gui("controller.structure"), "Estrcutura encontrada: %s");
    add(gui("controller.structure.none"), "Ninguna");
    add(gui("controller.status"), "Estado: ");
    add(gui("controller.error.info"), "Error Info: ");
    add(gui("controller.status.redstone_stopped"), "Máquina parada por una señal de redstone recibida.");
    add(gui("controller.status.paused"), "Pausado");
    add(gui("controller.status.missing_structure"), "Sin estructura");
    add(gui("controller.status.no_recipe"), "Sin receta encontrada");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "Procesando...");
    add(gui("controller.status.crafting.progress"), "Progreso: %s");
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

  @Override
  protected void addStructureCreator() {
    add(mmr("no_paper_template"), "No se ha encontrad papel o template de estructura en el inventario");
    add(mm("structure_creator.mode.change.tooltip"), "Presiona [%s] con el item en la mano(NO GUI) para cambiar el modo");
    add(mm("structure_creator.mode.change"), "Cambiado el modo de %s a %s");
    add(mm("structure_creator.mode.single"), "Único");
    add(mm("structure_creator.mode.box"), "Caja");
    add(mm("structure_creator.mode.vein"), "Proximity (Vein)");
    add(mm("structure_creator.mode.box.first"), "Selecciona primera esquina");
    add(mm("structure_creator.mode.box.second"), "Selecciona segunda esquina");
    add(mm("structure_creator.mode"), "Modo Actual: %s");
    add(mm("structure_creator.no_blocks"), "Sin bloques selecionados");
    add(mm("structure_creator.amount"), "%s bloques seleccionados");
    add(mm("structure_creator.select"), "Click derecho en un bloque para añadir o removerlo. Si el bloque tiene una GUI shift click para añadir o removerlo.");
    add(mm("structure_creator.finish"), "Shift click en cualquier controlador para obtener la estructura");
    add(mm("structure_creator.message"), "Estructura generada, click para copiar: %s %s %s %s");
    add(mm("structure_creator.reset"), "Click derecho en el aire mientras te agachas para resetear");
    add(mm("structure_creator.vein.select"), "Selecciona cualquier bloque que no sea aire o el controlador.");
    add(mm("structure_creator.vein.max"), "Max %s bloques");
  }
}
