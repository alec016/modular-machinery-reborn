package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags;

final class PtBrLang extends Lang {

  @Override
  protected void addKeys() {
    add("key.categories.modular_machinery_reborn", "Modular Machinery Reborn");
    add("key." + mm("structure_mode_change"), "Modo do Criador de Estrutura");
  }

  @Override
  protected void addControllerTexts() {
    add(mm("controller.tooltip.0"), "Clique no controlador posicionado com um projeto");
    add(mm("controller.tooltip.1"), "para mostrar a estrutura ao redor do controlador.");
    add(mm("controller.no_machine"), "Nenhuma máquina fornecida");
    add(mmr("controller.exactly"), " (Exactly %s)");
    add(mmr("controller.max"), " (Max: %s)");
    add(mmr("controller.min"), " (Min: %s)");
    add(mmr("controller.min_max"), " (Min: %s, Max: %s)");
    add(mm("controller.shift"), "[SHIFT]");
    add(mm("controller.control"), "[CTRL]");
    add(mm("controller.alt"), "[ALT]");
    add(mm("controller.alt.minmax"), "to show the min-max specifications");
    add(mm("controller.shift.blocks"), "para mostrar os blocos necessários");
    add(mm("controller.control.modifier"), "para mostrar os blocos modificadores");
    add(mm("controller.required"), "Necessário:");
    add(mm("controller.required.item"), "%s %s");
    add(mm("controller.required.tag"), "%s");
    add(mm("controller.required.block"), "%s");
    add(mm("controller.required.block.key"), "Pressione [%s] para mostrar informações completas");
    add(mm("controller.required.shift"), "SHIFT");
    add(mm("controller.required.control"), "CTRL");
    add(mm("controller.modifier"), "Modificadores:");
    add(mmr("place.non_air"), "Tentou posicionar %s em %s mas não encontrou um bloco de estrutura válido");
    add(mmr("place.no_item"), "Tentou posicionar %s em %s mas não conseguiu encontrar o item %s no inventário do jogador");
    add(mmr("place.replace"), "Quebrando %s em %s para posicionar novo bloco...");
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
    add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "Controlador Modular Machinery Reborn");
    add("config.jade.plugin_modular_machinery_reborn.hatch_component_provider", "Modular Machinery Reborn Hatch");
  }

  @Override
  protected void addCommands() {
    add(mm("command.reload.machines"), "Máquinas da Modular Machinery recarregadas com sucesso!");
    add(mm("command.reload.recipes"), "Receitas da Modular Machinery recarregadas com sucesso!");
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
    coreInfo0.addProperty("text", "Executando: ");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("index", 0);
    coreInfo0.addProperty("color", "aqua");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", " núcleos");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    add(mmr("waila.cores"), coreInfo);
  }

  @Override
  protected void addRecipeModifiers() {
    add(recipeModifier("speed.ADDITION"), " (%s a velocidade de processamento da receita)");
    add(recipeModifier("speed.MULTIPLICATION"), " (x%s a velocidade de processamento da receita)");
    add(recipeModifier("item.ADDITION"), " (%s item(ns) %s para a receita, chance: %s)");
    add(recipeModifier("item.MULTIPLICATION"), " (x%s item %s da receita, chance: %s)");
    add(recipeModifier("durability.ADDITION"), " (%s durabilidade de item(ns) %s para a receita)");
    add(recipeModifier("durability.MULTIPLICATION"), " (x%s durabilidade de item %s da receita)");
    add(recipeModifier("fluid.ADDITION"), " (%s mB de fluido(s) %s para a receita, chance: %s)");
    add(recipeModifier("fluid.MULTIPLICATION"), " (x%s fluido %s da receita, chance: %s)");
    add(recipeModifier("energy.ADDITION"), " (%s energia %s para a receita, chance: %s)");
    add(recipeModifier("energy.MULTIPLICATION"), " (x%s energia %s da receita, chance: %s)");
    add(recipeModifier("loot_table.ADDITION"), " (%s sorte de tabela de loot para a receita)");
    add(recipeModifier("loot_table.MULTIPLICATION"), " (x%s sorte de tabela de loot da receita)");
    add(recipeModifier("experience.ADDITION"), " (%s experiência %s para a receita, chance: %s)");
    add(recipeModifier("experience.MULTIPLICATION"), " (x%s experiência %s da receita, chance: %s)");
    add(recipeModifier("fuel.ADDITION"), " (%s burntime %s to recipe, chance: %s)");
    add(recipeModifier("fuel.MULTIPLICATION"), " (x%s burntime %s of recipe, chance: %s)");
  }

  @Override
  protected void addIngredients() {
    add(mm(ingredient("perTick")), "por tick");
    add(mm(ingredient("chance.input")), "Chance de ser consumido: %s%s");
    add(mm(ingredient("chance")), "%s%s");
    add(mm(ingredient("chance.output")), "Chance de ser produzido: %s%s");
    add(mm(ingredient("chance.not_consumed")), "Não Consumido");
    add(mm(ingredient("chance.nc")), "NC");
    add(jeiIngredient("long"), "%s");
    add(jeiIngredient("int"), "%s");
    add(jeiIngredient("energy.input"), "Requer: %s RF");
    add(jeiIngredient("energy.output"), "Produz: %s RF");
    add(jeiIngredient("energy.total.input"), "Requer: %s RF @ %s RF/t");
    add(jeiIngredient("energy.total.output"), "Produz: %s RF @ %s RF/t");
    add(jeiIngredient("fluid.input"), "Requer %s %s mB");
    add(jeiIngredient("fluid.output"), "Produz %s %s mB");
    add(jeiIngredient("experience.input"), "Requer %s XP");
    add(jeiIngredient("experience.output"), "Produz %s XP");
    add(jeiIngredient("duration"), "Tempo de processo: %s ticks");
    add(jeiIngredient("item.input"), "Requer");
    add(jeiIngredient("item.output"), "Produz");
    add(jeiIngredient("dimension.true"), "Lista de dimensões proibidas: %s");
    add(jeiIngredient("dimension.false"), "Lista de dimensões permitidas: %s");
    add(jeiIngredient("biome.true"), "Lista de biomas proibidos: %s");
    add(jeiIngredient("biome.false"), "Lista de biomas permitidos: %s");
    add(jeiIngredient("weather"), "Tempo climático: %s");
    add(jeiIngredient("time"), "Tempo: %s");
    add(jeiIngredient("height"), "Altura: %s");
    add(jeiIngredient("chunkload"), "Raio de carregamento de chunk: %s");
    add(mm(ingredient("durability.consume")), "Consumir %s de durabilidade");
    add(mm(ingredient("durability.repair")), "Reparar %s de durabilidade");
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
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "Criador de Estrutura (Single)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "Criador de Estrutura (Box)");
    addItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM, "Structure Template (Incomplete)");
    addItem(ItemRegistration.BLUEPRINT, "Projeto de Máquina");
    addItem(ItemRegistration.MODULARIUM, "Barra de Modularium");
    addItem(ItemRegistration.WRENCH, "Wrench");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.CONTROLLER, "Controlador de Máquina");

    addBlock(BlockRegistration.CASING_PLAIN, "Carcaça de Máquina");
    addBlock(BlockRegistration.CASING_VENT, "Ventilação de Máquina");
    addBlock(BlockRegistration.CASING_FIREBOX, "Carcaça de Fornalha");
    addBlock(BlockRegistration.CASING_GEARBOX, "Caixa de Engrenagens de Máquina");
    addBlock(BlockRegistration.CASING_REINFORCED, "Carcaça de Máquina Reforçada");
    addBlock(BlockRegistration.CASING_CIRCUITRY, "Circuitaria de Máquina");

    addBlock(BlockRegistration.ITEM_INPUT_BUS_TINY, "Escotilha de Entrada de Itens Mínimo");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_SMALL, "Escotilha de Entrada de Itens Pequeno");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_NORMAL, "Escotilha de Entrada de Itens Normal");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_REINFORCED, "Escotilha de Entrada de Itens Reforçado");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_BIG, "Escotilha de Entrada de Itens Grande");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_HUGE, "Escotilha de Entrada de Itens Enorme");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS, "Escotilha de Entrada de Itens Ludicrous");

    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_TINY, "Escotilha de Saída de Itens Mínimo");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_SMALL, "Escotilha de Saída de Itens Pequeno");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL, "Escotilha de Saída de Itens Normal");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED, "Escotilha de Saída de Itens Reforçado");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_BIG, "Escotilha de Saída de Itens Grande");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_HUGE, "Escotilha de Saída de Itens Enorme");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS, "Escotilha de Saída de Itens Ludicrous");

    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_TINY, "Escotilha de Durabilidade Mínima");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL, "Escotilha de Durabilidade Pequena");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL, "Escotilha de Durabilidade Normal");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_BIG, "Escotilha de Durabilidade Grande");

    addBlock(BlockRegistration.FLUID_INPUT_HATCH_TINY, "Escotilha de Entrada de Fluido Mínima");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_SMALL, "Escotilha de Entrada de Fluido Pequena");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_NORMAL, "Escotilha de Entrada de Fluido Normal");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED, "Escotilha de Entrada de Fluido Reforçada");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_BIG, "Escotilha de Entrada de Fluido Grande");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_HUGE, "Escotilha de Entrada de Fluido Enorme");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS, "Escotilha de Entrada de Fluido Ludicrous");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_VACUUM, "Escotilha de Entrada de Fluido a Vácuo");

    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_TINY, "Escotilha de Saída de Fluido Mínima");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL, "Escotilha de Saída de Fluido Pequena");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL, "Escotilha de Saída de Fluido Normal");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED, "Escotilha de Saída de Fluido Reforçada");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_BIG, "Escotilha de Saída de Fluido Grande");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE, "Escotilha de Saída de Fluido Enorme");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS, "Escotilha de Saída de Fluido Ludicrous");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM, "Escotilha de Saída de Fluido a Vácuo");

    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY, "Escotilha de Entrada de Experiência Mínima");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL, "Escotilha de Entrada de Experiência Pequena");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL, "Escotilha de Entrada de Experiência Normal");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED, "Escotilha de Entrada de Experiência Reforçada");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG, "Escotilha de Entrada de Experiência Grande");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE, "Escotilha de Entrada de Experiência Enorme");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS, "Escotilha de Entrada de Experiência Ludicrous");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM, "Escotilha de Entrada de Experiência Vácuo");

    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY, "Escotilha de Saída de Experiência Mínima");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL, "Escotilha de Saída de Experiência Pequena");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL, "Escotilha de Saída de Experiência Normal");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED, "Escotilha de Saída de Experiência Reforçada");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG, "Escotilha de Saída de Experiência Grande");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE, "Escotilha de Saída de Experiência Enorme");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS, "Escotilha de Saída de Experiência Ludicrous");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM, "Escotilha de Saída de Experiência Vácuo");

    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_TINY, "Escotilha de Entrada de Energia Mínima");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_SMALL, "Escotilha de Entrada de Energia Pequena");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL, "Escotilha de Entrada de Energia Normal");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED, "Escotilha de Entrada de Energia Reforçada");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_BIG, "Escotilha de Entrada de Energia Grande");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_HUGE, "Escotilha de Entrada de Energia Enorme");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "Escotilha de Entrada de Energia Ludicrous");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "Escotilha de Entrada de Energia Suprema");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "Escotilha de Saída de Energia Mínima");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "Escotilha de Saída de Energia Pequena");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "Escotilha de Saída de Energia Normal");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "Escotilha de Saída de Energia Reforçada");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "Escotilha de Saída de Energia Grande");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "Escotilha de Saída de Energia Enorme");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "Escotilha de Saída de Energia Ludicrous");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE, "Escotilha de Saída de Energia Suprema");

    addBlock(BlockRegistration.PARALLEL_HATCH_BASIC, "Escotilha Paralela Básica");
    addBlock(BlockRegistration.PARALLEL_HATCH_MEDIUM, "Escotilha Paralela Média");
    addBlock(BlockRegistration.PARALLEL_HATCH_ADVANCED, "Escotilha Paralela Avançada");
    addBlock(BlockRegistration.PARALLEL_HATCH_ULTIMATE, "Escotilha Paralela Suprema");
    addBlock(BlockRegistration.PARALLEL_HATCH_MAX, "Escotilha Paralela Máxima");

    addBlock(BlockRegistration.DIMENSIONAL_DETECTOR, "Detector Dimensional");
    addBlock(BlockRegistration.BIOME_READER, "Leitor de Bioma");
    addBlock(BlockRegistration.WEATHER_SENSOR, "Sensor de Clima");
    addBlock(BlockRegistration.TIME_COUNTER, "Contador de Tempo");
    addBlock(BlockRegistration.HEIGHT_METER, "Medidor de Altura");
    addBlock(BlockRegistration.CHUNKLOADER, "Carregador de Chunks");

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
    add(tooltip("machinery.empty"), "Vazio");
    add(tooltip("machinery.fuel"), "Aceita qualquer combustível");
    add(tooltip("machinery.fuel.item"), "Tempo de Queima: %s");
    add(tooltip("machinery.fuel.in"), "Tempo Total de Queima de Combustível Necessário:");
    add(tooltip("machinery.fuel.in.total"), "%s ticks.");

    add(tooltip("machinery.duration"), "Tempo de Processamento: %s ticks");
    add(tooltip("machinery.chance.in"), "Chance de Consumo: %s");
    add(tooltip("machinery.chance.out"), "Chance de Produção: %s");
    add(tooltip("machinery.chance.in.never"), "Não é consumido.");
    add(tooltip("machinery.chance.out.never"), "Nunca é produzido.");

    add(tooltip("machinery.energy.in"), "Energia Necessária:");
    add(tooltip("machinery.energy.in.tick"), "Por Tick: %s %s/t");
    add(tooltip("machinery.energy.in.total"), "Total: %s %s");
    add(tooltip("machinery.energy.out"), "Energia Produzida:");
    add(tooltip("machinery.energy.out.tick"), "Por Tick: %s %s/t");
    add(tooltip("machinery.energy.out.total"), "Total: %s %s");

    add(tooltip("experiencehatch.empty"), "Vazio");
    add(tooltip("experiencehatch.tank.points"), "%s / %s XP");
    add(tooltip("experiencehatch.tank.levels"), "%s / %s níveis");
    add(tooltip("experiencehatch.tank.info"), "Pode conter %s pontos de XP");

    add(tooltip("energy.type.fe"), "FE");
    add(tooltip("energy.type.ic2_eu"), "EU");
    add(tooltip("energy.type.gt_eu"), "EU");

    add(tooltip("constructtool.creative"), "Ferramenta Criativa de Estrutura para JSON");

    add(tooltip("itembus.slot"), "1 Slot");
    add(tooltip("itembus.slots"), "%s Slots");

    add(tooltip("fluidhatch.empty"), "Vazio");
    add(tooltip("fluidhatch.fluid"), "[Fluido]");
    add(tooltip("fluidhatch.gas"), "[Gás]");
    add(tooltip("fluidhatch.tank"), "%smB / %smB");
    add(tooltip("fluidhatch.tank.gas"), "%s / %s");
    add(tooltip("fluidhatch.tank.info"), "Pode conter %s mB");

    add(tooltip("energyhatch.charge"), "%s / %s %s");
    add(tooltip("energyhatch.storage"), "Armazena %s FE");
    add(tooltip("energyhatch.out.transfer"), "Transfere %s FE por tick");
    add(tooltip("energyhatch.in.accept"), "Aceita %s FE por tick");

    add(tooltip("parallelhatch.size"), "Pode fazer o multiblock executar até %s receitas");
    add(tooltip("blueprint"), "Clique em qualquer controlador de máquina para mostrar a estrutura");

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
    add(missingComponent("energy.output"), "Nenhuma Escotilha de Saída de Energia encontrada!");
    add(missingComponent("energy.input"), "Nenhuma Escotilha de Entrada de Energia encontrada!");
    add(missingComponent("experience.output"), "Nenhuma Escotilha de Saída de Experiência encontrada!");
    add(missingComponent("experience.input"), "Nenhuma Escotilha de Entrada de Experiência encontrada!");
    add(missingComponent("fluid.output"), "Nenhuma Escotilha de Saída de Fluido encontrada!");
    add(missingComponent("fluid.input"), "Nenhuma Escotilha de Entrada de Fluido encontrada!");
    add(missingComponent("item.output"), "Nenhum Escotilha de Saída de Itens encontrado!");
    add(missingComponent("item.input"), "Nenhum Escotilha de Entrada de Itens encontrado!");
    add(missingComponent("dimension"), "Nenhum Detector Dimensional encontrado!");
    add(missingComponent("weather"), "Nenhum Sensor de Clima encontrado!");
    add(missingComponent("biome"), "Nenhum Leitor de Bioma encontrado!");
    add(missingComponent("time"), "Nenhum Contador de Tempo encontrado!");
    add(missingComponent("height"), "Nenhum Medidor de Altura encontrado!");
    add(missingComponent("chunkload"), "Nenhum Carregador de Chunks encontrado!");
    add(missingComponent("function"), "Nenhum Componente de Função (Controlador) encontrado!");
    add(missingComponent("durability.input"), "Nenhuma Escotilha de Durabilidade encontrada!");
    add(missingComponent("durability.output"), "Nenhuma Escotilha de Durabilidade encontrada!");
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
    add(craftCheck("item.input"), "Faltando item de entrada! Necessário: %sx %s");
    add(craftCheck("item.output.space"), "Espaço insuficiente no inventário para a(s) saída(s) de itens!");
    add(craftCheck("durability.input"), "Durabilidade insuficiente, %s necessário(s), mas %s encontrado(s)!");
    add(craftCheck("durability.output"), "Não é possível reparar o item, tentando reparar %s de durabilidade, mas faltam %s!");
    add(craftCheck("fluid.input"), "Faltando fluido de entrada! Necessário %s mB de %s, mas encontrado %s mB de %s");
    add(craftCheck("fluid.output.space"), "Espaço insuficiente no tanque para a(s) saída(s) de fluido! Necessário: %s mB, mas encontrado %s mB");
    add(craftCheck("fluid.output.fluid"), "O fluido no tanque não corresponde, necessário: %s, mas encontrado %s!");
    add(craftCheck("energy.input"), "Energia insuficiente! Necessário: %s FE, mas encontrado %s FE");
    add(craftCheck("energy.output"), "Espaço insuficiente para a saída de energia! Necessário: %s FE, mas encontrado espaço para %s FE");
    add(craftCheck("experience.input"), "Experiência insuficiente! Necessário: %s XP, mas encontrado %s XP");
    add(craftCheck("experience.output"), "Espaço insuficiente para a saída de experiência! Necessário: %s XP, mas encontrado espaço para %s XP");
    add(craftCheck("dimension.false"), "Não está na dimensão correta, esperado: %s, encontrado: %s");
    add(craftCheck("dimension.true"), "Não está na dimensão correta, esperado não estar em: %s, encontrado: %s");
    add(craftCheck("biome.false"), "Não está no bioma correto, esperado: %s, encontrado: %s");
    add(craftCheck("biome.true"), "Não está no bioma correto, esperado não estar em: %s, encontrado: %s");
    add(craftCheck("weather"), "Clima inválido, esperado: %s");
    add(craftCheck("time"), "Altura inválida, esperada: %s, mas encontrada: %s");
    add(craftCheck("height"), "Invalid height, expected: %s but found: %s");
    add(craftCheck("parallel.loot_table"), "Não é possível processar várias Tabelas de Loot ao mesmo tempo, remova a Escotilha Paralela para processar esta receita.");
    add(craftCheck("function"), "Não foi possível executar o requisito de função, verifique os logs para erros!");
    add(craftCheck("function.no_listener"), "O requisito de função com id: %s não tem um evento KubeJS associado");
    add(craftCheck("function.interrupt"), "Parado pelo evento KubeJS");
    add(craftCheck("fuel"), "Not enough burntime, needed: %s but found %s");
    add(craftCheck("entity.amount"), "Not enough entities nearby !");
    add(craftCheck("entity.health"), "Can't collect %s health points with nearby entities !");
    add(craftCheck("structure"), "Invalid structure!");
    add(craftCheck("redstone"), "Required redstone power: %s but found %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "Controlador");
    add(mm(gui("title.energy_hatch")), "Escotilha de Energia");
    add(mm(gui("title.fluid_hatch")), "Escotilha de Fluido");
    add(mm(gui("title.item_bus")), "Escotilha de Itens");
    add(mm(gui("title.parallel_hatch")), "Escotilha Paralela");
    add(mm(gui("title.fuel_tank")), "Fuel Tank");
    add(mm(gui("title.redstone_port")), "Redstone Port");
    add(mmr(gui("button.back")), "Voltar");
    add(mmr(gui("button.close")), "Fechar");
    add(mmr(gui("button.page.next")), "Próxima Página");
    add(mmr(gui("button.page.prev")), "Página Anterior");
    add(mm(gui("core_button")), "Mostrar informações sobre os núcleos");
    add(mmr(gui("element.experience.tooltip")), "%s / %s");
    add(mmr(gui("element.experience.tooltip.input")), "Requer: %s / %s");
    add(mmr(gui("element.experience.tooltip.output")), "Produz: %s / %s");
    add(mmr(gui("element.experience.level")), "%s nível(is)");
    add(mmr(gui(tooltip("experience.button.extract_1"))), "Extrair 1 Nível");
    add(mmr(gui(tooltip("experience.button.extract_10"))), "Extrair 10 Níveis");
    add(mmr(gui(tooltip("experience.button.extract_all"))), "Extrair Todos os Níveis");
    add(mmr(gui(tooltip("experience.button.insert_1"))), "Inserir 1 Nível");
    add(mmr(gui(tooltip("experience.button.insert_10"))), "Inserir 10 Níveis");
    add(mmr(gui(tooltip("experience.button.insert_all"))), "Inserir Todos os Níveis");
    add(mm(gui("structure_placer_button")), "Clique para tentar posicionar a estrutura");
    add(mm(gui("structure_breaker_button")), "Clique para quebrar a estrutura atual");
    add(mmr(gui("structure.break")), "Você tem certeza de que deseja quebrar a estrutura atual?");
    add(mmr(gui("structure.place.modifier")), "Você quer posicionar a estrutura com ou sem modificadores?");
    add(mmr(gui("structure.place.modifier.true")), "Você tem certeza de que deseja posicionar a estrutura com modificadores?");
    add(mmr(gui("structure.place.modifier.false")), "Você tem certeza de que deseja posicionar a estrutura sem modificadores?");
    add(mmr(gui("structure.place.confirm.modifier")), "Com modificadores");
    add(mmr(gui("structure.place.cancel.modifier")), "Sem modificadores");
    add(mmr(gui("popup.confirm")), "Confirmar");
    add(mmr(gui("popup.cancel")), "Cancelar");
    add("emi." + tooltip("show.recipes"), "Mostrar Receitas");
    add(mmr("emi.no_items"), "Nenhum item na receita");
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
    add(mm("structure_creator.mode.change.tooltip"), "Pressione [%s] com o item na mão (SEM GUI) para mudar de modo");
    add(mm("structure_creator.mode.change"), "Modo alterado de %s para %s");
    add(mm("structure_creator.mode.single"), "Único");
    add(mm("structure_creator.mode.box"), "Caixa");
    add(mm("structure_creator.mode.box.first"), "Selecione o primeiro canto");
    add(mm("structure_creator.mode.box.second"), "Selecione o segundo canto");
    add(mm("structure_creator.mode"), "Modo Atual: %s");
    add(mm("structure_creator.no_blocks"), "Nenhum bloco selecionado");
    add(mm("structure_creator.amount"), "%s blocos selecionados");
    add(mm("structure_creator.select"), "Clique com o botão direito em um bloco para adicioná-lo ou removê-lo. Se o bloco tiver uma GUI, clique com Shift para adicioná-lo ou removê-lo.");
    add(mm("structure_creator.finish"), "Clique com Shift em qualquer controlador de máquina para obter a estrutura");
    add(mm("structure_creator.message"), "Estrutura gerada, clique para copiar: %s %s %s");
    add(mm("structure_creator.reset"), "Clique com o botão direito no ar enquanto agachado para resetar");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "Projeto Encontrado: %s");
    add(gui("controller.blueprint.none"), "Nenhum");
    add(gui("controller.structure"), "Estrutura Encontrada: %s");
    add(gui("controller.structure.none"), "Nenhum");
    add(gui("controller.status"), "Status: ");
    add(gui("controller.error.info"), "Error Info: ");
    add(gui("controller.status.redstone_stopped"), "Máquina parada por sinal de redstone");
    add(gui("controller.status.paused"), "Pausado");
    add(gui("controller.status.missing_structure"), "Faltando estrutura");
    add(gui("controller.status.no_recipe"), "Nenhuma receita correspondente encontrada");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "Processando...");
    add(gui("controller.status.crafting.progress"), "Progresso: %s");
    add(mmr("core.active.true"), "O núcleo pode executar receitas");
    add(mmr("core.active.false"), "O núcleo não pode executar receitas");
    add(mmr("core.number"), "Número do núcleo: %s");
    add(mmr(gui("core.button")), "Clique para mostrar informações sobre o núcleo número %s");
    add(mmr(gui(tooltip("core.action.button.single.add"))), "Adicionar 1 núcleo ativo");
    add(mmr(gui(tooltip("core.action.button.single.remove"))), "Remover 1 núcleo ativo");
    add(mmr(gui(tooltip("core.action.button.shift.add"))), "Adicionar 10 núcleos ativos");
    add(mmr(gui(tooltip("core.action.button.shift.remove"))), "Remover 10 núcleos ativos");
    add(mmr(gui(tooltip("core.action.button.control.add"))), "Adicionar todos os núcleos ativos");
    add(mmr(gui(tooltip("core.action.button.control.remove"))), "Remover todos os núcleos ativos menos 1");
  }
}
