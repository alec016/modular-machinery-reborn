package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags;

final class RuRuLang extends Lang {

  @Override
  protected void addKeys() {
    add("key.categories.modular_machinery_reborn", "Modular Machinery Reborn");
    add("key." + mm("structure_mode_change"), "Structure Creator Mode");
  }

  @Override
  protected void addControllerTexts() {
    add(mm("controller.tooltip.0"), "Нажмите по контроллеру с чертежом,");
    add(mm("controller.tooltip.1"), "Чтобы показать мульти-структуру.");
    add(mm("controller.no_machine"), "Не существует мульти-структуры");
    add(mmr("controller.exactly"), " (Exactly %s)");
    add(mmr("controller.max"), " (Max: %s)");
    add(mmr("controller.min"), " (Min: %s)");
    add(mmr("controller.min_max"), " (Min: %s, Max: %s)");
    add(mm("controller.shift"), "[Shift]");
    add(mm("controller.control"), "[Ctrl]");
    add(mm("controller.alt"), "[ALT]");
    add(mm("controller.alt.minmax"), "to show the min-max specifications");
    add(mm("controller.shift.blocks"), "чтобы отобразить список блоков");
    add(mm("controller.control.modifier"), "чтобы отобразить список модифицируемых блоков");
    add(mm("controller.required"), "Требуется:");
    add(mm("controller.required.item"), "%s %s");
    add(mm("controller.required.tag"), "%s");
    add(mm("controller.required.block"), "%s");
    add(mm("controller.required.block.key"), "Нажмите [%s], чтобы отобразить полную информацию");
    add(mm("controller.required.shift"), "Shift");
    add(mm("controller.required.control"), "Ctrl");
    add(mm("controller.modifier"), "Модификаторы:");
    add(mmr("place.non_air"), "Не удалось разместить блок %s в %s, отсутствует структурный  блок");
    add(mmr("place.no_item"), "Не удалось разместить блок %s в %s, отсутствует предмет в инвентаре");
    add(mmr("place.replace"), "Убирается блок %s в %s, чтобы разместить другой блок");
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
    add(mm("command.reload.machines"), "Успешно перезагружены мульти-структуры!");
    add(mm("command.reload.recipes"), "Successfully reloaded modular machinery recipes !");
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
    coreInfo0.addProperty("text", "Выполняется: ");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("index", 0);
    coreInfo0.addProperty("color", "aqua");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", " потоки");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    add(mmr("waila.cores"), coreInfo);
  }

  @Override
  protected void addRecipeModifiers() {
    add(recipeModifier("speed.ADDITION"), " (%s скорость обработки рецепта)");
    add(recipeModifier("speed.MULTIPLICATION"), " (x%s скорость обработки рецепта)");
    add(recipeModifier("item.ADDITION"), " (%s предмет для %s рецепта, шанс: %s)");
    add(recipeModifier("item.MULTIPLICATION"), " (x%s предмет %s рецепта, шанс: %s)");
    add(recipeModifier("durability.ADDITION"), " (%s предмет(ов) прочность %s по рецепту)");
    add(recipeModifier("durability.MULTIPLICATION"), " (x%s долговечность предмета %s рецепта)");
    add(recipeModifier("fluid.ADDITION"), " (%s жидких mb %s к рецепту, шанс: %s)");
    add(recipeModifier("fluid.MULTIPLICATION"), " (x%s жидкость %s рецепта, шанс: %s)");
    add(recipeModifier("energy.ADDITION"), " (%s энергия %s для рецепта, шанс: %s)");
    add(recipeModifier("energy.MULTIPLICATION"), " (x%s энергия %s рецепта, шанс: %s)");
    add(recipeModifier("loot_table.ADDITION"), " (%s лут к рецепту)");
    add(recipeModifier("loot_table.MULTIPLICATION"), " (%s лут для рецепта)");
    add(recipeModifier("experience.ADDITION"), " (%s опыт %s к рецепту, шанс: %s)");
    add(recipeModifier("experience.MULTIPLICATION"), " (x%s опыт %s рецепта, шанс: %s)");
    add(recipeModifier("fuel.ADDITION"), " (%s burntime %s to recipe, chance: %s)");
    add(recipeModifier("fuel.MULTIPLICATION"), " (x%s burntime %s of recipe, chance: %s)");
  }

  @Override
  protected void addIngredients() {
    add(mm(ingredient("perTick")), "per tick");
    add(mm(ingredient("chance.input")), "Шанс использованным: %s%s");
    add(mm(ingredient("chance")), "%s%s");
    add(mm(ingredient("chance.output")), "Шанс получение: %s%s");
    add(mm(ingredient("chance.not_consumed")), "Не тратится");
    add(mm(ingredient("chance.nc")), "NC");
    add(jeiIngredient("long"), "%s");
    add(jeiIngredient("int"), "%s");
    add(jeiIngredient("energy.input"), "Требуется: %s RF");
    add(jeiIngredient("energy.output"), "Производить: %s RF");
    add(jeiIngredient("energy.total.input"), "Требуется: %s RF @ %s RF/t");
    add(jeiIngredient("energy.total.output"), "Производить: %s RF @ %s RF/t");
    add(jeiIngredient("fluid.input"), "Требуется %s %s mB");
    add(jeiIngredient("fluid.output"), "Производить %s %s mB");
    add(jeiIngredient("experience.input"), "Требуется %s XP");
    add(jeiIngredient("experience.output"), "Производить %s XP");
    add(jeiIngredient("duration"), "Время выполнения: %s тиков");
    add(jeiIngredient("item.input"), "Требуется");
    add(jeiIngredient("item.output"), "Производить");
    add(jeiIngredient("dimension.true"), "Мир в черном списке: %s");
    add(jeiIngredient("dimension.false"), "Миры в белом списке: %s");
    add(jeiIngredient("biome.true"), "Биом в черном списке: %s");
    add(jeiIngredient("biome.false"), "Биом в белом списке: %s");
    add(jeiIngredient("weather"), "Погода: %s");
    add(jeiIngredient("time"), "Время: %s");
    add(jeiIngredient("height"), "Высота: %s");
    add(jeiIngredient("chunkload"), "Радиус загрузки: %s");
    add(mm(ingredient("durability.consume")), "Тратится %s прочности");
    add(mm(ingredient("durability.repair")), "Ремонтируется %s прочности");
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
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "Создатель мульти-структуры (Single)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "Создатель мульти-структуры (Box)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN, "Structure Creator (Proximity (Vein))");
    addItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM, "Structure Template (Incomplete)");
    addItem(ItemRegistration.BLUEPRINT, "Мульти-структуры чертеж");
    addItem(ItemRegistration.MODULARIUM, "Модулариум");
    addItem(ItemRegistration.WRENCH, "Wrench");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.CONTROLLER, "мульти-структуры Контроллер");

    addBlock(BlockRegistration.CASING_PLAIN, "Мульти-структуры Корпус");
    addBlock(BlockRegistration.CASING_VENT, "Мульти-структуры Вентиляция");
    addBlock(BlockRegistration.CASING_FIREBOX, "Огнеупорный Корпус");
    addBlock(BlockRegistration.CASING_REINFORCED, "Укреплённый Мульти-структуры Корпус");
    addBlock(BlockRegistration.CASING_CIRCUITRY, "Мульти-структура Электроника");
    addBlock(BlockRegistration.CASING_GEARBOX, "Мултьи-структуры Шестерней");

    addBlock(BlockRegistration.ITEM_INPUT_BUS_TINY, "Крошечный Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_SMALL, "Малый Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_NORMAL, "Обычный Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_REINFORCED, "Укрепленный Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_BIG, "Большой Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_HUGE, "Огромный Предметный Вход");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS, "Сверх Огромный Предметный Вход");

    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_TINY, "Крошечный Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_SMALL, "Малый Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL, "Обычный Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED, "Укрепленный Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_BIG, "Большой Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_HUGE, "Огромный Предметный Выход");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS, "Сверх Огромный Предметный Выход");

    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_TINY, "Крошечный Инструментальный Люк");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL, "Малый Инструментальный Люк");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL, "Обычный Инструментальный Люк");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_BIG, "Большой Инструментальный Люк");

    addBlock(BlockRegistration.FLUID_INPUT_HATCH_TINY, "Крошечный Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_SMALL, "Малый Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_NORMAL, "Обычный Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED, "Укреплённый Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_BIG, "Большой Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_HUGE, "Огромный Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS, "Сверх Огромный Жидкостный Вход");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_VACUUM, "Вакуумный Жидкостный Вход");

    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_TINY, "Крошечный Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL, "Малый Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL, "Обычный Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED, "Укреплённый Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_BIG, "Большой Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE, "Огромный Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS, "Сверх Огромный Жидкостный Выход");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM, "Вакуумный Жидкостный Выход");

    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY, "Крошечный вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL, "Малый вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL, "Обычный вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED, "Укреплённый вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG, "Большой вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE, "Огромный вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS, "Сверх Огромный вход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM, "Вакуумный вход для опыта");

    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY, "Крошечный выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL, "Малый выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL, "Обычный выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED, "Укреплённый выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG, "Большой выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE, "Огромный выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS, "Сверх Огромный выход для опыта");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM, "Вакуумный выход для опыта");

    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_TINY, "Крошечный Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_SMALL, "Малый Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL, "Обычный Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED, "Укреплённый Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_BIG, "Большой Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_HUGE, "Огромный Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "Очень Огромный Энергетический Вход");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "Ультимативный Энергетический Вход");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "Крошечный Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "Малый Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "Обычный Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "Укреплённый Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "Большой Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "Огромный Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "Очень Огромный Энергетический Выход");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE, "Ультимативный Энергетический Выход");

    addBlock(BlockRegistration.PARALLEL_HATCH_BASIC, "Базовое Параллельное Ядро");
    addBlock(BlockRegistration.PARALLEL_HATCH_MEDIUM, "Улучшенное Параллельное Ядро");
    addBlock(BlockRegistration.PARALLEL_HATCH_ADVANCED, "Продвинутое Параллельное Ядро");
    addBlock(BlockRegistration.PARALLEL_HATCH_ULTIMATE, "Ультимативное Параллельное Ядро");
    addBlock(BlockRegistration.PARALLEL_HATCH_MAX, "Максимальное Параллельное Ядро");

    addBlock(BlockRegistration.DIMENSIONAL_DETECTOR, "Мировой Анализатор");
    addBlock(BlockRegistration.BIOME_READER, "Биомный Анализатор");
    addBlock(BlockRegistration.WEATHER_SENSOR, "Погодный Анализатор");
    addBlock(BlockRegistration.TIME_COUNTER, "Временной Анализатор");
    addBlock(BlockRegistration.HEIGHT_METER, "Высотный Анализатор");
    addBlock(BlockRegistration.CHUNKLOADER, "Загрузчик чанков");

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
    add(tooltip("machinery.empty"), "Пусто");
    add(tooltip("machinery.fuel"), "Принимает любое топливо.");
    add(tooltip("machinery.fuel.item"), "Время горение: %s");
    add(tooltip("machinery.fuel.in"), "Необходимое время для горения топлива:");
    add(tooltip("machinery.fuel.in.total"), "%s тиков.");

    add(tooltip("machinery.duration"), "Время обработки: %s тиков");
    add(tooltip("machinery.chance.in"), "Шанс потери: %s");
    add(tooltip("machinery.chance.out"), "Шанс получение: %s");
    add(tooltip("machinery.chance.in.never"), "Не используется.");
    add(tooltip("machinery.chance.out.never"), "Никогда не производится.");

    add(tooltip("machinery.energy.in"), "Требуется энергий:");
    add(tooltip("machinery.energy.in.tick"), "За тик: %s %s/t");
    add(tooltip("machinery.energy.in.total"), "Общий: %s %s");
    add(tooltip("machinery.energy.out"), "Вырабатывается энергий:");
    add(tooltip("machinery.energy.out.tick"), "За тик: %s %s/t");
    add(tooltip("machinery.energy.out.total"), "Общий: %s %s");

    add(tooltip("experiencehatch.empty"), "Пусто");
    add(tooltip("experiencehatch.tank.points"), "%s / %s XP");
    add(tooltip("experiencehatch.tank.levels"), "%s / %s уровней");
    add(tooltip("experiencehatch.tank.info"), "Может имеет %s очков опыта");

    add(tooltip("energy.type.fe"), "FE");
    add(tooltip("energy.type.ic2_eu"), "EU");
    add(tooltip("energy.type.gt_eu"), "EU");

    add(tooltip("constructtool.creative"), "Creative Structure-To-JSON Tool");

    add(tooltip("itembus.slot"), "1 Слот");
    add(tooltip("itembus.slots"), "%s Слотов");

    add(tooltip("fluidhatch.empty"), "Пусто");
    add(tooltip("fluidhatch.fluid"), "[Жидкость]");
    add(tooltip("fluidhatch.gas"), "[Газ]");
    add(tooltip("fluidhatch.tank"), "%smB / %smB");
    add(tooltip("fluidhatch.tank.gas"), "%s / %s");
    add(tooltip("fluidhatch.tank.info"), "Может содержать %s mB");

    add(tooltip("energyhatch.charge"), "%s / %s %s");
    add(tooltip("energyhatch.storage"), "Хранится %s FE");
    add(tooltip("energyhatch.out.transfer"), "Принимается %s FE за тик");
    add(tooltip("energyhatch.in.accept"), "Отдается %s FE за тик");

    add(tooltip("parallelhatch.size"), "Позволяет параллельно создавать до %s рецептов одновременно.");
    add(tooltip("blueprint"), "Нажмите по контролеру, чтобы показать мульти-структуру");

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
    add(missingComponent("energy.output"), "Не найдет энергетический выход!");
    add(missingComponent("energy.input"), "Не найдет энергетический вход!");
    add(missingComponent("experience.output"), "Не найдет выход для опыта!");
    add(missingComponent("experience.input"), "Не найдет вход для опыта!");
    add(missingComponent("fluid.output"), "Не найдет жидкостный выход!");
    add(missingComponent("fluid.input"), "Не найдет жидкостный вход!");
    add(missingComponent("item.output"), "Не найдет предметный выход!");
    add(missingComponent("item.input"), "Не найдет предметный вход!");
    add(missingComponent("dimension"), "Не найдет мировой анализатор!");
    add(missingComponent("weather"), "Не найдет погодный анализатор!");
    add(missingComponent("biome"), "Не найдет биомный анализатор!");
    add(missingComponent("time"), "Не найдет временной анализатор!");
    add(missingComponent("height"), "Hе найдет высотный анализатор!");
    add(missingComponent("chunkload"), "Не найдет загрузчик чанков!");
    add(missingComponent("function"), "Контроллер не найден!");
    add(missingComponent("durability.input"), "Не найден вход для увеличения срока службы инструментов!");
    add(missingComponent("durability.output"), "Не найден вход для уменьшения срока службы инструментов!");
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
    add(craftCheck("item.input"), "Отсутствует предмет на входе! Требуется: %sx %s");
    add(craftCheck("item.output.space"), "Отсутствует места на выходе, освободите его!");
    add(craftCheck("durability.input"), "Не хватает прочности, %s нужно, но %s найдено!");
    add(craftCheck("durability.output"), "Не могу починить предмет, пытаюсь восстановить %s его прочности, но %s его не хватает!");
    add(craftCheck("fluid.input"), "Отсутствует ввод жидкости! требуется %s mb %s, но найдено %s mb %s");
    add(craftCheck("fluid.output.space"), "Недостаточно места в баке для выпуска жидкости!, требуется: %s mb, но найдено %s mb");
    add(craftCheck("fluid.output.fluid"), "Жидкость в баке не соответствует, требуется: %s, но найдено%s!");
    add(craftCheck("energy.input"), "Недостаточно энергии! Требуется: %s FE, но найдено %s FE");
    add(craftCheck("energy.output"), "Недостаточно места для выработки энергии!, требуется: %s FE, но найдено место для %s FE");
    add(craftCheck("experience.input"), "Не хватает опыта!, %s XP, но нашел %s XP");
    add(craftCheck("experience.output"), "Недостаточно места для вывода опыта!, требуется: %s XP, но найдено место для %s XP");
    add(craftCheck("dimension.false"), "Не в том измерении, ожидалось, что будет: %s, найдено: %s");
    add(craftCheck("dimension.true"), "Не в нужном измерении, ожидалось, что его не будет: %s, найдено: %s");
    add(craftCheck("biome.false"), "Не в том биоме, ожидалось, что будет: %s, найдено: %s");
    add(craftCheck("biome.true"), "Не в нужном биоме, ожидалось, что его не будет: %s, найдено: %s");
    add(craftCheck("weather"), "Неверное время прогноза погоды, %s");
    add(craftCheck("time"), "Недопустимое время, ожидаемое: %с, но найденное: %s");
    add(craftCheck("height"), "Недопустимая высота, ожидаемая: %s, но найденная: %s");
    add(craftCheck("parallel.loot_table"), "Если вы не можете обрабатывать несколько таблиц с добычей одновременно, удалите параллельную штриховку, чтобы обработать этот рецепт.");
    add(craftCheck("function"), "Не удается выполнить функциональное требование, проверьте журналы на наличие ошибок!");
    add(craftCheck("function.no_listener"), "Функциональное требование с идентификатором: %s не имеет связанного события KubeJS");
    add(craftCheck("function.interrupt"), "Остановлен ивентом KubeJS!");
    add(craftCheck("fuel"), "Not enough burntime, needed: %s but found %s");
    add(craftCheck("entity.amount"), "Not enough entities nearby !");
    add(craftCheck("entity.health"), "Can't collect %s health points with nearby entities !");
    add(craftCheck("structure"), "Invalid structure!");
    add(craftCheck("redstone"), "Required redstone power: %s but found %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "Контроллер");
    add(mm(gui("title.energy_hatch")), "Энергетический Порт");
    add(mm(gui("title.fluid_hatch")), "Жидкостный Порт");
    add(mm(gui("title.item_bus")), "Предметный Люк");
    add(mm(gui("title.parallel_hatch")), "Параллельное Ядро");
    add(mm(gui("title.fuel_tank")), "Fuel Tank");
    add(mm(gui("title.redstone_port")), "Redstone Port");
    add(mmr(gui("button.back")), "Назад");
    add(mmr(gui("button.close")), "Закрыть");
    add(mmr(gui("button.page.next")), "Следующая страница");
    add(mmr(gui("button.page.prev")), "Предыдущая страница");
    add(mm(gui("core_button")), "Показать информацию о потоках");
    add(mmr(gui("element.experience.tooltip")), "%s / %s");
    add(mmr(gui("element.experience.tooltip.input")), "Требуется: %s / %s");
    add(mmr(gui("element.experience.tooltip.output")), "Производится: %s / %s");
    add(mmr(gui("element.experience.level")), "%s уровень(и)");
    add(mmr(gui(tooltip("experience.button.extract_1"))), "Забрать 1 уровень");
    add(mmr(gui(tooltip("experience.button.extract_10"))), "Забрать 10 уровень");
    add(mmr(gui(tooltip("experience.button.extract_all"))), "Забрать все");
    add(mmr(gui(tooltip("experience.button.insert_1"))), "Положить 1 уровень");
    add(mmr(gui(tooltip("experience.button.insert_10"))), "Положить 10 уровень");
    add(mmr(gui(tooltip("experience.button.insert_all"))), "Положить все");
    add(mm(gui("structure_placer_button")), "Нажмите, чтобы разместить мульти-структуру");
    add(mm(gui("structure_breaker_button")), "Нажмите, чтобы разобрать мульти-структуру");
    add(mmr(gui("structure.break")), "Вы хотите разрушить мульти-структуру?");
    add(mmr(gui("structure.place.modifier")), "Вы хотите разместить мульти-структуру?");
    add(mmr(gui("structure.place.modifier.true")), "Вы хотите разместить мульти-структуру с модификациями?");
    add(mmr(gui("structure.place.modifier.false")), "Вы хотите разместить мульти-структуру без модификаций?");
    add(mmr(gui("structure.place.confirm.modifier")), "С модификациями");
    add(mmr(gui("structure.place.cancel.modifier")), "Без модификаций");
    add(mmr(gui("popup.confirm")), "Подтвердить");
    add(mmr(gui("popup.cancel")), "Отменить");
    add("emi." + tooltip("show.recipes"), "Показать рецепты");
    add(mmr("emi.no_items"), "В рецепте нет предметов");
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
    add(mmr(gui("paused")), "Not available while multiblock is Paused");
  }

  @Override
  protected void addStructureCreator() {
    add(mmr("no_paper_template"), "No paper or structure template found in inventory");
    add(mm("structure_creator.mode.change.tooltip"), "Нажмите [%s] с предметом в руке (не в инвентаре), чтобы сменить режим работы");
    add(mm("structure_creator.mode.change"), "Сменить режим с %s на %s");
    add(mm("structure_creator.mode.single"), "Блочное выделение");
    add(mm("structure_creator.mode.box"), "Областное выделение");
    add(mm("structure_creator.mode.vein"), "Proximity (Vein)");
    add(mm("structure_creator.mode.box.first"), "Укажите начальный точку");
    add(mm("structure_creator.mode.box.second"), "Укажите конечную точку");
    add(mm("structure_creator.mode"), "Выбранный режим: %s");
    add(mm("structure_creator.no_blocks"), "Нет выбранных блоков");
    add(mm("structure_creator.amount"), "%s выделенных блоков");
    add(mm("structure_creator.select"), "ПКМ по блоку, чтобы добавить или удалить блок из выделения. Если блок имеет интерфейс, зажмите клавишу Shift и добавьте или удалите блок из выделения.");
    add(mm("structure_creator.finish"), "Shift + ПКМ по любому контроллеру, чтобы получить мульти-структуры");
    add(mm("structure_creator.message"), "Мульти-структура сгенерирована. Нажмите, чтобы скопировать: %s %s %s %s");
    add(mm("structure_creator.reset"), "ПКМ по воздуху, чтобы сбросить выделение");
    add(mm("structure_creator.vein.select"), "Select any block that is not air or the controller.");
    add(mm("structure_creator.vein.max"), "Max %s blocks");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "Чертеж найден: %s");
    add(gui("controller.blueprint.none"), "Ничего");
    add(gui("controller.structure"), "Мульти-структура найдена: %s");
    add(gui("controller.structure.none"), "Ничего");
    add(gui("controller.status"), "Статус: ");
    add(gui("controller.error.info"), "Error Info: ");
    add(gui("controller.status.redstone_stopped"), "Мульти-структура приостановлено из-за ред стоун сигнала");
    add(gui("controller.status.paused"), "Приостановлено");
    add(gui("controller.status.missing_structure"), "Мульти-структура не найдена");
    add(gui("controller.status.no_recipe"), "Рецепт не найден");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "Работает...");
    add(gui("controller.status.crafting.progress"), "Прогресс: %s");
    add(mmr("core.active.true"), "Поток может выполнить рецепты");
    add(mmr("core.active.false"), "Поток не может выполнить рецепты");
    add(mmr("core.number"), "Поток %s#");
    add(mmr(gui("core.button")), "Нажмите, чтобы показать информацию о потоке %s#");
    add(mmr(gui(tooltip("core.action.button.single.add"))), "Добавить 1 поток");
    add(mmr(gui(tooltip("core.action.button.single.remove"))), "Удалить 1 поток");
    add(mmr(gui(tooltip("core.action.button.shift.add"))), "Добавить 10 потоков");
    add(mmr(gui(tooltip("core.action.button.shift.remove"))), "Удалить 10 потоков");
    add(mmr(gui(tooltip("core.action.button.control.add"))), "Добавить все потоки");
    add(mmr(gui(tooltip("core.action.button.control.remove"))), "Удалить все потоки, кроме последнего");
  }
}
