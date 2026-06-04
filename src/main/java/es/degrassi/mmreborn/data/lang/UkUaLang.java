package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags.Blocks;
import es.degrassi.mmreborn.data.MMRTags.Items;


final class UkUaLang extends Lang {
  @Override
  protected void addControllerTexts() {
    add(mm("controller.tooltip.0"), "Натисніть кресленням по встановленому контролеру,");
    add(mm("controller.tooltip.1"), "щоб побачити структуру навколо нього");
    add(mm("controller.no_machine"), "Машину не вказано");
    add(mmr("controller.exactly"), " (Рівно %s)");
    add(mmr("controller.max"), " (Макс: %s)");
    add(mmr("controller.min"), " (Мін: %s)");
    add(mmr("controller.min_max"), " (Мін: %s, Макс: %s)");
    add(mm("controller.shift"), "[SHIFT]");
    add(mm("controller.control"), "[CTRL]");
    add(mm("controller.alt"), "[ALT]");
    add(mm("controller.shift.blocks"), "щоб показати необхідні блоки");
    add(mm("controller.control.modifier"), "щоб показати блоки-модифікатори");
    add(mm("controller.alt.minmax"), "щоб показати мін/макс характеристики");
    add(mm("controller.required"), "Необхідно:");
    add(mm("controller.required.item"), "%s %s");
    add(mm("controller.required.tag"), "%s");
    add(mm("controller.required.block"), "%s");
    add(mm("controller.required.block.key"), "Натисніть [%s], щоб показати повну інформацію");
    add(mm("controller.required.shift"), "SHIFT");
    add(mm("controller.required.control"), "CTRL");
    add(mm("controller.modifier"), "Модифікатори:");
    add(mmr("place.non_air"), "Спроба встановити %s у %s провалилася: не знайдено відповідного структурного блоку");
    add(mmr("place.no_item"), "Спроба встановити %s у %s провалилася: не знайдено предмет %s в інвентарі гравця");
    add(mmr("place.replace"), "Руйнування %s на %s для встановлення нового блоку...");
    add(mmr("damagesource.kill"), "%s було вбито %s");
    add(mmr("structure.error.exact"), "Очікувалося %s, але знайдено %s (%s)");
    add(mmr("structure.error.max"), "Очікувалося щонайбільше %s, але знайдено %s (%s)");
    add(mmr("structure.error.min"), "Очікувалося щонайменше %s, але знайдено %s (%s)");
    add(mmr("structure.error.between"), "Очікувалося між %s та %s, але знайдено %s (%s)");
    add(mmr("config.tooltip.info"), "Відносно %s");
    add(mmr("north"), "Північ");
    add(mmr("south"), "Південь");
    add(mmr("east"), "Схід");
    add(mmr("west"), "Захід");
    add(mmr("up"), "Вгору");
    add(mmr("down"), "Вниз");
  }

  @Override
  protected void addJade() {
    add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "Контролер Modular Machinery Reborn");
    add("config.jade.plugin_modular_machinery_reborn.hatch_component_provider", "Люк Modular Machinery Reborn");
  }

  @Override
  protected void addCommands() {
    add(mm("command.reload.machines"), "Машини Modular Machinery успішно перезавантажені!");
    add(mm("command.reload.recipes"), "Рецепти Modular Machinery успішно перезавантажені!");
  }

  @Override
  protected void addTags() {
    add(Blocks.ALL_CASINGS, "Всі корпуси");
    add(Blocks.CASINGS, "Корпуси");
    add(Blocks.DURABILITY, "Люки міцності");
    add(Blocks.EFFECT_DISPENSER, "Роздавачі ефектів");
    add(Blocks.ENERGY, "Енергетичні люки");
    add(Blocks.ENERGY_INPUT, "Люки входу енергії");
    add(Blocks.ENERGY_OUTPUT, "Люки виходу енергії");
    add(Blocks.ENTITY, "Люки сутностей");
    add(Blocks.EXPERIENCE, "Люки досвіду");
    add(Blocks.EXPERIENCE_INPUT, "Люки входу досвіду");
    add(Blocks.EXPERIENCE_OUTPUT, "Люки виходу досвіду");
    add(Blocks.FLUID, "Рідинні люки");
    add(Blocks.FLUID_INPUT, "Люки входу рідини");
    add(Blocks.FLUID_OUTPUT, "Люки виходу рідини");
    add(Blocks.FUEL_TANK, "Паливні баки");
    add(Blocks.HATCHES, "Люки");
    add(Blocks.INPUT_BUS, "Шини входу предметів");
    add(Blocks.ITEM, "Шини предметів");
    add(Blocks.OUTPUT_BUS, "Шини виходу предметів");
    add(Blocks.PARALLEL, "Люки паралельності");
    add(Blocks.PLAIN_CONNECTABLE, "Звичайні з'єднувані");
    add(Blocks.PLAIN_HATCHES, "Звичайні люки");
    add(Blocks.REINFORCED_CONNECTABLE, "Укріплені з'єднувані");
    add(Blocks.REINFORCED_HATCHES, "Укріплені люки");
    add(Blocks.REPLACEABLE, "Замінні");

    add(Items.ALL_CASINGS, "Всі корпуси");
    add(Items.CASINGS, "Корпуси");
    add(Items.DURABILITY, "Люки міцності");
    add(Items.EFFECT_DISPENSER, "Роздавачі ефектів");
    add(Items.ENERGY, "Енергетичні люки");
    add(Items.ENERGY_INPUT, "Люки входу енергії");
    add(Items.ENERGY_OUTPUT, "Люки виходу енергії");
    add(Items.ENTITY, "Люки сутностей");
    add(Items.EXPERIENCE, "Люки досвіду");
    add(Items.EXPERIENCE_INPUT, "Люки входу досвіду");
    add(Items.EXPERIENCE_OUTPUT, "Люки виходу досвіду");
    add(Items.FLUID, "Рідинні люки");
    add(Items.FLUID_INPUT, "Люки входу рідини");
    add(Items.FLUID_OUTPUT, "Люки виходу рідини");
    add(Items.FUEL_TANK, "Паливні баки");
    add(Items.HATCHES, "Люки");
    add(Items.INPUT_BUS, "Шини входу предметів");
    add(Items.ITEM, "Шини предметів");
    add(Items.OUTPUT_BUS, "Шини виходу предметів");
    add(Items.PARALLEL, "Люки паралельності");
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
    coreInfo0.addProperty("text", "Працює: ");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("index", 0);
    coreInfo0.addProperty("color", "aqua");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", " ядер");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    add(mmr("waila.cores"), coreInfo);
  }

  @Override
  protected void addRecipeModifiers() {
    add(recipeModifier("speed.ADDITION"), " (%s до швидкості обробки рецепта)");
    add(recipeModifier("speed.MULTIPLICATION"), " (x%s до швидкості обробки рецепта)");
    add(recipeModifier("item.ADDITION"), " (%s предметів %s до рецепта, шанс: %s)");
    add(recipeModifier("item.MULTIPLICATION"), " (x%s предметів %s у рецепті, шанс: %s)");
    add(recipeModifier("durability.ADDITION"), " (%s до міцності предметів %s у рецепті)");
    add(recipeModifier("durability.MULTIPLICATION"), " (x%s до міцності предметів %s у рецепті)");
    add(recipeModifier("fluid.ADDITION"), " (%s мБ рідини %s до рецепта, шанс: %s)");
    add(recipeModifier("fluid.MULTIPLICATION"), " (x%s рідини %s у рецепті, шанс: %s)");
    add(recipeModifier("energy.ADDITION"), " (%s енергії %s до рецепта, шанс: %s)");
    add(recipeModifier("energy.MULTIPLICATION"), " (x%s енергії %s у рецепті, шанс: %s)");
    add(recipeModifier("loot_table.ADDITION"), " (%s до удачі таблиці луту в рецепті)");
    add(recipeModifier("loot_table.MULTIPLICATION"), " (x%s до удачі таблиці луту в рецепті)");
    add(recipeModifier("experience.ADDITION"), " (%s досвіду %s до рецепта, шанс: %s)");
    add(recipeModifier("experience.MULTIPLICATION"), " (x%s досвіду %s у рецепті, шанс: %s)");
    add(recipeModifier("fuel.ADDITION"), " (%s часу горіння %s до рецепта, шанс: %s)");
    add(recipeModifier("fuel.MULTIPLICATION"), " (x%s часу горіння %s у рецепті, шанс: %s)");
  }

  @Override
  protected void addIngredients() {
    add(mm(ingredient("perTick")), "за такт");
    add(mm(ingredient("chance.input")), "Шанс споживання: %s%s");
    add(mm(ingredient("chance")), "%s%s");
    add(mm(ingredient("chance.output")), "Шанс виробництва: %s%s");
    add(mm(ingredient("chance.not_consumed")), "Не споживається");
    add(mm(ingredient("chance.nc")), "НС");
    add(mm("requirement.mode.input"), "Вхід");
    add(mm("requirement.mode.output"), "Вихід");
    add(mm("requirement.mode.none"), "Немає");
    add(jeiIngredient("long"), "%s");
    add(jeiIngredient("int"), "%s");
    add(jeiIngredient("energy.input"), "Потрібно: %s RF");
    add(jeiIngredient("energy.output"), "Виробляє: %s RF");
    add(jeiIngredient("energy.total.input"), "Потрібно: %s RF @ %s RF/т");
    add(jeiIngredient("energy.total.output"), "Виробляє: %s RF @ %s RF/т");
    add(jeiIngredient("fluid.input"), "Потрібно %s %s мБ");
    add(jeiIngredient("fluid.output"), "Виробляє %s %s мБ");
    add(jeiIngredient("experience.input"), "Потрібно %s XP");
    add(jeiIngredient("experience.output"), "Виробляє %s XP");
    add(jeiIngredient("duration"), "Час обробки: %s такт");
    add(jeiIngredient("item.input"), "Потрібно");
    add(jeiIngredient("item.output"), "Виробляє");
    add(jeiIngredient("dimension.true"), "Чорний список вимірів: %s");
    add(jeiIngredient("dimension.false"), "Білий список вимірів: %s");
    add(jeiIngredient("biome.true"), "Чорний список біомів: %s");
    add(jeiIngredient("biome.false"), "Білий список біомів: %s");
    add(jeiIngredient("weather"), "Час погоди: %s");
    add(jeiIngredient("time"), "Час: %s");
    add(jeiIngredient("height"), "Висота: %s");
    add(jeiIngredient("chunkload"), "Радіус завантаження чанків: %s");
    add(mm(ingredient("durability.consume")), "Споживає %s міцності");
    add(mm(ingredient("durability.repair")), "Відновлює %s міцності");
    add(jeiIngredient("fuel"), "Потрібно %s часу горіння");
    add(jeiIngredient("effect.info.tick"), "Надає %s %s на %s такт кожен такт");
    add(jeiIngredient("effect.info.whitelist"), "Білий список сутностей:");
    add(jeiIngredient("entity.whitelist"), "Білий список сутностей:");
    add(jeiIngredient("entity.blacklist"), "Чорний список сутностей:");
    add(jeiIngredient("entity.kill.info"), "Вбиває %s сутностей у радіусі %s блоків");
    add(jeiIngredient("entity.spawn.info"), "Спавнить %s сутностей у радіусі %s блоків");
    add(jeiIngredient("entity.check_health.info"), "Потребує %s HP сутностей у радіусі %s блоків (не споживається)");
    add(jeiIngredient("entity.check_amount.info"), "Потребує %s сутностей у радіусі %s блоків");
    add(jeiIngredient("entity.consume_health.info"), "Потребує та споживає %s HP сутностей у радіусі %s блоків");
    add(jeiIngredient("entity.add_health.info"), "Потребує поранених та лікує %s HP сутностей у радіусі %s блоків");
    add(jeiIngredient("structure.info"), "Потребує структуру");
    add(jeiIngredient("structure.click"), "Натисніть, щоб переглянути необхідну структуру");
    add(jeiIngredient("structure.shift"), "Натисніть [Ctrl], щоб переглянути список блоків");
    add(jeiIngredient("structure.list"), "  %sx %s");
    add(jeiIngredient("structure.break"), "Зруйнує структуру");
    add(jeiIngredient("structure.destroy"), "Знищить структуру");
    add(jeiIngredient("structure.place"), "Встановить структуру");
    add(jeiIngredient("structure.not"), "НЕ %s");
    add(jeiIngredient("structure.or"), " або ");
    add(jeiIngredient("redstone.input"), "Потребує подачі %s потужності редстоуну");
    add(jeiIngredient("redstone.output"), "Випромінює %s потужності редстоуну");
    add(jeiIngredient("command.info"), "Запустити %s на %s");
  }

  @Override
  protected void addItems() {
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "Творець структур (Одиничний)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "Творець структур (Область)");
    addItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM, "Шаблон структури (Незавершений)");
    addItem(ItemRegistration.BLUEPRINT, "Креслення машини");
    addItem(ItemRegistration.MODULARIUM, "Модуляріум");
    addItem(ItemRegistration.WRENCH, "Гайковий ключ");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.CONTROLLER, "Контролер машини");

    addBlock(BlockRegistration.CASING_PLAIN, "Машинний корпус");
    addBlock(BlockRegistration.CASING_VENT, "Машинна вентиляція");
    addBlock(BlockRegistration.CASING_FIREBOX, "Корпус плавки");
    addBlock(BlockRegistration.CASING_REINFORCED, "Укріплений машинний корпус");
    addBlock(BlockRegistration.CASING_CIRCUITRY, "Машинні схеми");
    addBlock(BlockRegistration.CASING_GEARBOX, "Машинна коробка передач");

    addBlock(BlockRegistration.ITEM_INPUT_BUS_TINY, "Крихітний люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_SMALL, "Малий люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_NORMAL, "Звичайний люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_REINFORCED, "Укріплений люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_BIG, "Великий люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_HUGE, "Величезний люк входу предметів");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS, "Безглуздо великий люк входу предметів");

    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_TINY, "Крихітний люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_SMALL, "Малий люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL, "Звичайний люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED, "Укріплений люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_BIG, "Великий люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_HUGE, "Величезний люк виходу предметів");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS, "Безглуздо великий люк виходу предметів");

    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_TINY, "Крихітний люк міцності");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL, "Малий люк міцності");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL, "Звичайний люк міцності");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_BIG, "Великий люк міцності");

    addBlock(BlockRegistration.FLUID_INPUT_HATCH_TINY, "Крихітний люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_SMALL, "Малий люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_NORMAL, "Звичайний люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED, "Укріплений люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_BIG, "Великий люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_HUGE, "Величезний люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS, "Безглуздо великий люк входу рідини");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_VACUUM, "Вакуумний люк входу рідини");

    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_TINY, "Крихітний люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL, "Малий люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL, "Звичайний люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED, "Укріплений люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_BIG, "Великий люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE, "Величезний люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS, "Безглуздо великий люк виходу рідини");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM, "Вакуумний люк виходу рідини");

    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY, "Крихітний люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL, "Малий люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL, "Звичайний люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED, "Укріплений люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG, "Великий люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE, "Величезний люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS, "Безглуздо великий люк входу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM, "Вакуумний люк входу досвіду");

    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY, "Крихітний люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL, "Малий люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL, "Звичайний люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED, "Укріплений люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG, "Великий люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE, "Величезний люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS, "Безглуздо великий люк виходу досвіду");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM, "Вакуумний люк виходу досвіду");

    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_TINY, "Крихітний люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_SMALL, "Малий люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL, "Звичайний люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED, "Укріплений люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_BIG, "Великий люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_HUGE, "Величезний люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "Безглуздо великий люк входу енергії");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "Досконалий люк входу енергії");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "Крихітний люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "Малий люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "Звичайний люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "Укріплений люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "Великий люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "Величезний люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "Безглуздо великий люк виходу енергії");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE, "Досконалий люк виходу енергії");

    addBlock(BlockRegistration.PARALLEL_HATCH_BASIC, "Базовий люк паралельності");
    addBlock(BlockRegistration.PARALLEL_HATCH_MEDIUM, "Середній люк паралельності");
    addBlock(BlockRegistration.PARALLEL_HATCH_ADVANCED, "Покращений люк паралельності");
    addBlock(BlockRegistration.PARALLEL_HATCH_ULTIMATE, "Досконалий люк паралельності");
    addBlock(BlockRegistration.PARALLEL_HATCH_MAX, "Максимальний люк паралельності");

    addBlock(BlockRegistration.DIMENSIONAL_DETECTOR, "Зчитувач вимірів");
    addBlock(BlockRegistration.BIOME_READER, "Зчитувач біомів");
    addBlock(BlockRegistration.WEATHER_SENSOR, "Зчитувач погоди");
    addBlock(BlockRegistration.TIME_COUNTER, "Таймер");
    addBlock(BlockRegistration.HEIGHT_METER, "Вимірювач висоти");
    addBlock(BlockRegistration.CHUNKLOADER, "Провантажувач чанків");

    addBlock(BlockRegistration.FUEL_TANK_TINY, "Крихітний паливний бак");
    addBlock(BlockRegistration.FUEL_TANK_SMALL, "Малий паливний бак");
    addBlock(BlockRegistration.FUEL_TANK_NORMAL, "Звичайний паливний бак");
    addBlock(BlockRegistration.FUEL_TANK_REINFORCED, "Укріплений паливний бак");
    addBlock(BlockRegistration.FUEL_TANK_BIG, "Великий паливний бак");
    addBlock(BlockRegistration.FUEL_TANK_HUGE, "Величезний паливний бак");

    addBlock(BlockRegistration.EFFECT_DISPENSER_SMALL, "Малий роздавач ефектів");
    addBlock(BlockRegistration.EFFECT_DISPENSER_MEDIUM, "Середній роздавач ефектів");
    addBlock(BlockRegistration.EFFECT_DISPENSER_BIG, "Великий роздавач ефектів");

    addBlock(BlockRegistration.ENTITY_DETECTOR, "Зчитувач сутностей");
    addBlock(BlockRegistration.ENTITY_KILLER, "Вбивця сутностей");
    addBlock(BlockRegistration.ENTITY_SPAWNER, "Спавнер сутностей");
    addBlock(BlockRegistration.ENTITY_HEALER, "Цілитель сутностей");
    addBlock(BlockRegistration.ENTITY_DAMAGER, "Пристрій завдання шкоди сутностям");

    addBlock(BlockRegistration.STRUCTURE_CHECKER, "Перевірик структури");
    addBlock(BlockRegistration.REDSTONE_PORT, "Порт редстоуну");
    addBlock(BlockRegistration.COMMAND_EXECUTIONER, "Виконувач команд");
  }

  @Override
  protected void addTooltips() {
    add(tooltip("machinery.empty"), "Порожньо");
    add(tooltip("machinery.fuel"), "Приймає будь-яке паливо");
    add(tooltip("machinery.fuel.item"), "Час горіння: %s");
    add(tooltip("machinery.fuel.in"), "Необхідний час горіння:");
    add(tooltip("machinery.fuel.in.total"), "%s тактів.");

    add(tooltip("machinery.duration"), "Час обробки: %s тактів");
    add(tooltip("machinery.chance.in"), "Шанс споживання: %s");
    add(tooltip("machinery.chance.out"), "Шанс виробництва: %s");
    add(tooltip("machinery.chance.in.never"), "Не споживається.");
    add(tooltip("machinery.chance.out.never"), "Ніколи не виробляється.");

    add(tooltip("machinery.energy.in"), "Необхідна енергія:");
    add(tooltip("machinery.energy.in.tick"), "За такт: %s %s/т");
    add(tooltip("machinery.energy.in.total"), "Всього: %s %s");
    add(tooltip("machinery.energy.out"), "Вироблена енергія:");
    add(tooltip("machinery.energy.out.tick"), "За такт: %s %s/т");
    add(tooltip("machinery.energy.out.total"), "Всього: %s %s");

    add(tooltip("experiencehatch.empty"), "Порожньо");
    add(tooltip("experiencehatch.tank.points"), "%s / %s XP");
    add(tooltip("experiencehatch.tank.levels"), "%s / %s рівнів");
    add(tooltip("experiencehatch.tank.info"), "Може містити %s XP");

    add(tooltip("energy.type.fe"), "FE");
    add(tooltip("energy.type.ic2_eu"), "EU");
    add(tooltip("energy.type.gt_eu"), "EU");

    add(tooltip("constructtool.creative"), "Креативний інструмент 'Структура в JSON'");

    add(tooltip("itembus.slot"), "1 слот");
    add(tooltip("itembus.slots"), "Слотів: %s");

    add(tooltip("fluidhatch.empty"), "Порожньо");
    add(tooltip("fluidhatch.fluid"), "[Рідина]");
    add(tooltip("fluidhatch.gas"), "[Газ]");
    add(tooltip("fluidhatch.tank"), "%smB / %smB");
    add(tooltip("fluidhatch.tank.gas"), "%s / %s");
    add(tooltip("fluidhatch.tank.info"), "Може містити %s мБ");

    add(tooltip("energyhatch.charge"), "%s / %s %s");
    add(tooltip("energyhatch.storage"), "Зберігає %s FE");
    add(tooltip("energyhatch.out.transfer"), "Передає %s FE за такт");
    add(tooltip("energyhatch.in.accept"), "Приймає %s FE за такт");

    add(tooltip("parallelhatch.size"), "Дозволяє машині виконувати до %s рецептів одночасно");
    add(tooltip("blueprint"), "Натисніть по будь-якому контролеру, щоб переглянути структуру");

    add(tooltip("fueltank.storage"), "Може зберігати %s часу горіння (тактів)");
    add(tooltip("fuel_tank.tank"), "%s / %s тактів");

    add(tooltip("effectdispenser.interdimensional"), "Надає ефекти між вимірами");
    add(tooltip("effectdispenser.radius"), "Надає ефекти в радіусі %s блоків навколо");

    add(mmr("side.enabled"), "Увімкнено");
    add(mmr("side.disabled"), "Вимкнено");
    add(mmr("side.top"), "Зверху");
    add(mmr("side.front"), "Спереду");
    add(mmr("side.back"), "Ззаду");
    add(mmr("side.left"), "Зліва");
    add(mmr("side.right"), "Справа");
    add(mmr("side.bottom"), "Знизу");
    add(mmr(tooltip("open_side_config")), "Відкрити конфігурацію сторін");
    add(mmr("wrench.side_mode.change"), "Змінено сторону: %s з %s на %s");
  }

  @Override
  protected void addComponents() {
    add(missingComponent("energy.output"), "Люк виходу енергії не знайдено!");
    add(missingComponent("energy.input"), "Люк входу енергії не знайдено!");
    add(missingComponent("experience.output"), "Люк виходу досвіду не знайдено!");
    add(missingComponent("experience.input"), "Люк входу досвіду не знайдено!");
    add(missingComponent("fluid.output"), "Люк виходу рідини не знайдено!");
    add(missingComponent("fluid.input"), "Люк входу рідини не знайдено!");
    add(missingComponent("item.output"), "Люк виходу предметів не знайдено!");
    add(missingComponent("item.input"), "Люк входу предметів не знайдено!");
    add(missingComponent("dimension"), "Зчитувач вимірів не знайдено!");
    add(missingComponent("weather"), "Зчитувач погоди не знайдено!");
    add(missingComponent("biome"), "Зчитувач біомів не знайдено!");
    add(missingComponent("time"), "Таймер не знайдено!");
    add(missingComponent("height"), "Вимірювач висоти не знайдено!");
    add(missingComponent("chunkload"), "Провантажувач чанків не знайдено!");
    add(missingComponent("function"), "Компонент функцій (контролер) не знайдено!");
    add(missingComponent("durability.input"), "Люк міцності не знайдено!");
    add(missingComponent("durability.output"), "Люк міцності не знайдено!");
    add(missingComponent("fuel"), "Паливний бак не знайдено!");
    add(missingComponent("effect"), "Роздавач ефектів не знайдено!");
    add(missingComponent("entity.detector"), "Зчитувач сутностей не знайдено!");
    add(missingComponent("entity.healer"), "Цілитель сутностей не знайдено!");
    add(missingComponent("entity.damager"), "Пристрій шкоди не знайдено!");
    add(missingComponent("entity.killer"), "Вбивця сутностей не знайдено!");
    add(missingComponent("entity.spawner"), "Спавнер сутностей не знайдено!");
    add(missingComponent("structure"), "Перевірка структури не знайдено!");
    add(missingComponent("redstone"), "Порт редстоуну не знайдено!");
    add(missingComponent("command"), "Виконувач команд не знайдено!");
  }

  @Override
  protected void addCraftcheck() {
    add(craftCheck("item.input"), "Відсутній вхідний предмет! Потрібно: %sx %s");
    add(craftCheck("item.output.space"), "Недостатньо місця в інвентарі для виходу предметів!");
    add(craftCheck("durability.input"), "Недостатньо міцності, потрібно %s, але знайдено %s!");
    add(craftCheck("durability.output"), "Неможливо полагодити предмет, спроба відновити %s міцності, але не вистачає %s!");
    add(craftCheck("fluid.input"), "Відсутня рідина! Потрібно %s мБ %s, знайдено %s мБ %s");
    add(craftCheck("fluid.output.space"), "Недостатньо місця в баку для виводу рідини! Потрібно: %s мБ, вільно %s мБ");
    add(craftCheck("fluid.output.fluid"), "Рідина в баку не збігається, потрібно: %s, знайдено %s!");
    add(craftCheck("energy.input"), "Недостатньо енергії! Потрібно: %s FE, знайдено %s FE");
    add(craftCheck("energy.output"), "Недостатньо місця для виводу енергії! Потрібно: %s FE, вільно %s FE");
    add(craftCheck("experience.input"), "Недостатньо досвіду! Потрібно %s XP, знайдено %s XP");
    add(craftCheck("experience.output"), "Недостатньо місця для виводу досвіду! Потрібно: %s XP, вільно %s XP");
    add(craftCheck("dimension.false"), "Неправильний вимір, очікується: %s, знайдено: %s");
    add(craftCheck("dimension.true"), "Неправильний вимір, очікується НЕ: %s, знайдено: %s");
    add(craftCheck("biome.false"), "Неправильний біом, очікується: %s, знайдено: %s");
    add(craftCheck("biome.true"), "Неправильний біом, очікується НЕ: %s, знайдено: %s");
    add(craftCheck("weather"), "Неправильний час погоди, очікується: %s");
    add(craftCheck("time"), "Неправильний час, очікується: %s, знайдено: %s");
    add(craftCheck("height"), "Неправильний висота, очікується: %s, знайдено: %s");
    add(craftCheck("parallel.loot_table"), "Неможливо обробляти кілька таблиць луту одночасно, зніміть люк паралельності.");
    add(craftCheck("function"), "Неможливо виконати функцію, перевірте логи на наявність помилок!");
    add(craftCheck("function.no_listener"), "Вимога функції з ID %s не має пов'язаної події KubeJS");
    add(craftCheck("function.interrupt"), "Зупинено подією KubeJS");
    add(craftCheck("fuel"), "Недостатньо часу горіння, потрібно: %s, знайдено %s");
    add(craftCheck("entity.amount"), "Недостатньо сутностей поруч!");
    add(craftCheck("entity.health"), "Неможливо зібрати %s очок здоров'я з навколишніх сутностей!");
    add(craftCheck("structure"), "Неправильний структура!");
    add(craftCheck("redstone"), "Необхідна потужність редстоуну: %s, знайдено %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "Контролер");
    add(mm(gui("title.energy_hatch")), "Енергетичний люк");
    add(mm(gui("title.fluid_hatch")), "Рідинний люк");
    add(mm(gui("title.item_bus")), "Люк предметів");
    add(mm(gui("title.parallel_hatch")), "Люк паралельності");
    add(mm(gui("title.fuel_tank")), "Паливний бак");
    add(mm(gui("title.redstone_port")), "Порт редстоуну");
    add(mmr(gui("button.back")), "Назад");
    add(mmr(gui("button.close")), "Закрити");
    add(mmr(gui("button.page.next")), "Наступна сторінка");
    add(mmr(gui("button.page.prev")), "Попередня сторінка");
    add(mm(gui("core_button")), "Показати інформацію про ядра");
    add(mmr(gui("element.experience.tooltip")), "%s / %s");
    add(mmr(gui("element.experience.tooltip.input")), "Потрібно: %s / %s");
    add(mmr(gui("element.experience.tooltip.output")), "Виробляє: %s / %s");
    add(mmr(gui("element.experience.level")), "%s рівень(ів)");
    add(mmr(gui(tooltip("experience.button.extract_1"))), "Витягти 1 рівень");
    add(mmr(gui(tooltip("experience.button.extract_10"))), "Витягти 10 рівнів");
    add(mmr(gui(tooltip("experience.button.extract_all"))), "Витягти всі рівні");
    add(mmr(gui(tooltip("experience.button.insert_1"))), "Вставити 1 рівень");
    add(mmr(gui(tooltip("experience.button.insert_10"))), "Вставити 10 рівнів");
    add(mmr(gui(tooltip("experience.button.insert_all"))), "Вставити всі рівні");
    add(mm(gui("structure_placer_button")), "Натисніть, щоб спробувати встановити структуру");
    add(mm(gui("structure_breaker_button")), "Натисніть, щоб розібрати поточну структуру");
    add(mmr(gui("structure.break")), "Ви впевнені, що хочете розібрати поточну структуру?");
    add(mmr(gui("structure.place.modifier")), "Бажаєте встановити структуру з модифікаторами чи без?");
    add(mmr(gui("structure.place.modifier.true")), "Ви впевнені, що хочете встановити структуру з модифікаторами?");
    add(mmr(gui("structure.place.modifier.false")), "Ви впевнені, що хочете встановити структуру без модифікаторів?");
    add(mmr(gui("structure.place.confirm.modifier")), "З модифікаторами");
    add(mmr(gui("structure.place.cancel.modifier")), "Без модифікаторів");
    add(mmr(gui("popup.confirm")), "Підтвердити");
    add(mmr(gui("popup.cancel")), "Скасувати");
    add("emi." + tooltip("show.recipes"), "Показати рецепти");
    add(mmr("emi.no_items"), "Немає предметів у рецепті");
    add(mmr(gui(tooltip("redstone.button.mode.input"))), "Вхід");
    add(mmr(gui(tooltip("redstone.button.mode.output"))), "Вихід");
    add(mmr(gui(tooltip("redstone.button.mode.none"))), "Немає");
    add(mmr(gui(tooltip("button.enum.cycle"))), "Наступний: %s");
    add(mmr(gui(tooltip("auto_output.change"))), "Поточний режим: %s, змінити на: %s");
    add(mmr(gui(tooltip("auto_output"))), "Авто-вихід");
    add(mmr(gui(tooltip("auto_input.change"))), "Поточний режим: %s, змінити на: %s");
    add(mmr(gui(tooltip("auto_input"))), "Авто-вхід");
    add(mmr(tooltip("auto_output")), "Авто-вихід: %s");
    add(mmr(tooltip("auto_input")), "Авто-вхід: %s");
    add(mmr(gui(tooltip("enabled.true"))), "Увімкнено");
    add(mmr(gui(tooltip("enabled.false"))), "Вимкнено");
    add(mmr(tooltip("effect")), "Накладання ефекту в радіусі %s блоків");
    add(mmr(tooltip("effect.interdimensional")), "Накладання ефекту між вимірами");
    add(mmr(tooltip("redstone.emit")), "Випромінює %s");
    add(mmr(tooltip("redstone.receive")), "Отримує %s");
    add(mmr(gui("missing_structure")), "Not Available until structure formed");
  }

  @Override
  protected void addStructureCreator() {
    add(mmr("no_paper_template"), "No paper or structure template found in inventory");
    add(mm("structure_creator.mode.change.tooltip"), "Натисніть [%s] з предметом у руці (поза інтерфейсом), щоб змінити режим");
    add(mm("structure_creator.mode.change"), "Змінено режим з %s на %s");
    add(mm("structure_creator.mode.single"), "Одиничний");
    add(mm("structure_creator.mode.box"), "Область");
    add(mm("structure_creator.mode.box.first"), "Виберіть перший кут");
    add(mm("structure_creator.mode.box.second"), "Виберіть другий кут");
    add(mm("structure_creator.mode"), "Поточний режим: %s");
    add(mm("structure_creator.no_blocks"), "Блоки не вибрані");
    add(mm("structure_creator.amount"), "Вибрано блоків: %s");
    add(mm("structure_creator.select"), "ПКМ по блоку, щоб додати/видалити його. Якщо блок має інтерфейс, використовуйте Shift+ПКМ.");
    add(mm("structure_creator.finish"), "Натисніть Shift+ПКМ по будь-якому контролеру, щоб отримати структуру");
    add(mm("structure_creator.message"), "Структуру згенеровано, натисніть для копіювання: %s %s %s");
    add(mm("structure_creator.reset"), "ПКМ у повітря під час присідання, щоб скинути вибір");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "Знайдено креслення: %s");
    add(gui("controller.blueprint.none"), "Немає");
    add(gui("controller.structure"), "Знайдено структуру: %s");
    add(gui("controller.structure.none"), "Немає");
    add(gui("controller.status"), "Статус: ");
    add(gui("controller.error.info"), "Помилка: ");
    add(gui("controller.status.redstone_stopped"), "Машину зупинено сигналом редстоуну.");
    add(gui("controller.status.paused"), "Пауза");
    add(gui("controller.status.missing_structure"), "Відсутня структура");
    add(gui("controller.status.no_recipe"), "Відповідного рецепту не знайдено");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "Обробка...");
    add(gui("controller.status.crafting.progress"), "Прогрес: %s");
    add(mmr("core.active.true"), "Ядро може виконувати рецепти");
    add(mmr("core.active.false"), "Ядро не може виконувати рецепти");
    add(mmr("core.number"), "Номер ядра: %s");
    add(mmr(gui("core.button")), "Натисніть, щоб показати інформацію про ядро №%s");
    add(mmr(gui(tooltip("core.action.button.single.add"))), "Додати 1 активне ядро");
    add(mmr(gui(tooltip("core.action.button.single.remove"))), "Видалити 1 активне ядро");
    add(mmr(gui(tooltip("core.action.button.shift.add"))), "Додати 10 активних ядер");
    add(mmr(gui(tooltip("core.action.button.shift.remove"))), "Видалити 10 активних ядер");
    add(mmr(gui(tooltip("core.action.button.control.add"))), "Додати всі активні ядра");
    add(mmr(gui(tooltip("core.action.button.control.remove"))), "Видалити всі активні ядра");
  }
}
