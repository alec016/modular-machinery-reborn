package es.degrassi.mmreborn.data.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.data.MMRTags;

final class ZhCnLang extends Lang {

  @Override
  protected void addKeys() {
    add("key.categories.modular_machinery_reborn", "模块化机械:重置");
    add("key." + mm("structure_mode_change"), "结构创建器模式");
  }

  @Override
  protected void addControllerTexts() {
    add(mm("controller.tooltip.0"), "使用蓝图右键已放置的控制器");
    add(mm("controller.tooltip.1"), "来显示关于这个控制器的结构");
    add(mm("controller.no_machine"), "未找到机器");
    add(mmr("controller.exactly"), " (Exactly %s)");
    add(mmr("controller.max"), " (Max: %s)");
    add(mmr("controller.min"), " (Min: %s)");
    add(mmr("controller.min_max"), " (Min: %s, Max: %s)");
    add(mm("controller.shift"), "[SHIFT]");
    add(mm("controller.control"), "[CTRL]");
    add(mm("controller.alt"), "[ALT]");
    add(mm("controller.alt.minmax"), "to show the min-max specifications");
    add(mm("controller.shift.blocks"), "显示需求的方块");
    add(mm("controller.control.modifier"), "显示调节器方块");
    add(mm("controller.required"), "需要:");
    add(mm("controller.required.item"), "%s %s");
    add(mm("controller.required.tag"), "%s");
    add(mm("controller.required.block"), "%s");
    add(mm("controller.required.block.key"), "按下[%s]来显示完整信息");
    add(mm("controller.required.shift"), "SHIFT");
    add(mm("controller.required.control"), "CTRL");
    add(mm("controller.modifier"), "机器调节:");
    add(mmr("place.non_air"), "尝试放置 %s 在 %s 但是找到了不属于当前结构的方块");
    add(mmr("place.no_item"), "尝试放置 %s 在 %s 但是在玩家库存中未找到 %s ");
    add(mmr("place.replace"), "正在破坏 %s 位于 %s 以放置新方块");
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
    // add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "模块化机械:重生");
    add("config.jade.plugin_modular_machinery_reborn.machine_component_provider", "Modular Machinery Reborn Controller");
    add("config.jade.plugin_modular_machinery_reborn.hatch_component_provider", "Modular Machinery Reborn Hatch");
  }

  @Override
  protected void addCommands() {
    add(mm("command.reload.machines"), "成功重载模块化机械！");
    add(mm("command.reload.recipes"), "成功重载模块化机械的配方");
  }

  @Override
  protected void addTags() {
    MMRTags.getAllTags().forEach(tag -> add(tag.getFirst(), tag.getSecond()));
  }

  @Override
  protected void addItemGroups() {
    add("itemgroup." + mm("group"), "模块化机械:重生");
  }

  @Override
  protected void addJsonProps() {
    addCores();
  }

  private void addCores() {
    JsonArray coreInfo = new JsonArray();
    JsonObject coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", "正在使用: ");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("index", 0);
    coreInfo0.addProperty("color", "aqua");
    coreInfo.add(coreInfo0);
    coreInfo0 = new JsonObject();
    coreInfo0.addProperty("text", " 核心");
    coreInfo0.addProperty("color", "gray");
    coreInfo.add(coreInfo0);
    add(mmr("waila.cores"), coreInfo);
  }

  @Override
  protected void addRecipeModifiers() {
    add(recipeModifier("speed.ADDITION"), " (%s 配方处理的速度)");
    add(recipeModifier("speed.MULTIPLICATION"), " (x%s 倍配方处理的速度)");
    add(recipeModifier("item.ADDITION"), " (为配方增加 %s 物品 %s , 是否影响概率: %s)");
    add(recipeModifier("item.MULTIPLICATION"), " (为配方提供 %s 倍物品 %s, 是否影响概率: %s)");
    add(recipeModifier("durability.ADDITION"), " (为配方增加 %s 物品耐久值 %s)");
    add(recipeModifier("durability.MULTIPLICATION"), " (为配方增加 %s 倍品耐久值 %s)");
    add(recipeModifier("fluid.ADDITION"), " (为配方增加 %s 流体 %s, 是否影响概率: %s)");
    add(recipeModifier("fluid.MULTIPLICATION"), " (为配方提供 %s 倍流体 %s, 是否影响概率: %s)");
    add(recipeModifier("energy.ADDITION"), " (为配方增加 %s 能源 %s, 是否影响概率: %s)");
    add(recipeModifier("energy.MULTIPLICATION"), " (为配方提供 %s 倍能源 %s, 是否影响概率: %s)");
    add(recipeModifier("loot_table.ADDITION"), " (为配方增加 %s 战利品幸运值)");
    add(recipeModifier("loot_table.MULTIPLICATION"), " (为配方提供 %s 倍战利品幸运值)");
    add(recipeModifier("experience.ADDITION"), " (为配方增加 %s 经验 %s, 是否影响概率: %s)");
    add(recipeModifier("experience.MULTIPLICATION"), " (为配方提供%s 倍经验 %s, 是否影响概率: %s)");
    add(recipeModifier("fuel.ADDITION"), " (%s burntime %s to recipe, chance: %s)");
    add(recipeModifier("fuel.MULTIPLICATION"), " (x%s burntime %s of recipe, chance: %s)");
  }

  @Override
  protected void addIngredients() {
    add(mm(ingredient("perTick")), "per tick");
    add(mm(ingredient("chance.input")), "消耗概率: %s%s");
    add(mm(ingredient("chance")), "%s%s");
    add(mm(ingredient("chance.output")), "产出概率: %s%s");
    add(mm(ingredient("chance.not_consumed")), "不被消耗");
    add(mm(ingredient("chance.nc")), "NC");
    add(jeiIngredient("long"), "%s");
    add(jeiIngredient("int"), "%s");
    add(jeiIngredient("energy.input"), "需要: %s RF");
    add(jeiIngredient("energy.output"), "产出: %s RF");
    add(jeiIngredient("energy.total.input"), "需要: %s RF @ %s RF/t");
    add(jeiIngredient("energy.total.output"), "产出: %s RF @ %s RF/t");
    add(jeiIngredient("fluid.input"), "需要 %s %s mB");
    add(jeiIngredient("fluid.output"), "产出 %s %s mB");
    add(jeiIngredient("experience.input"), "需要 %s XP");
    add(jeiIngredient("experience.output"), "产出 %s XP");
    add(jeiIngredient("duration"), "加工时间: %s ticks");
    add(jeiIngredient("item.input"), "配方输入");
    add(jeiIngredient("item.output"), "配方产出");
    add(jeiIngredient("dimension.true"), "维度黑名单: %s");
    add(jeiIngredient("dimension.false"), "维度白名单: %s");
    add(jeiIngredient("biome.true"), "群系黑名单: %s");
    add(jeiIngredient("biome.false"), "群系白名单: %s");
    add(jeiIngredient("weather"), "天气时间: %s");
    add(jeiIngredient("time"), "时间: %s");
    add(jeiIngredient("height"), "高度: %s");
    add(jeiIngredient("chunkload"), "区块加载器半径: %s");
    add(mm(ingredient("durability.consume")), "消耗 %s 耐久值");
    add(mm(ingredient("durability.repair")), "修复 %s 耐久值");
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
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE, "结构创建器 (Single)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX, "结构创建器 (Box)");
    addItem(ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN, "Structure Creator (Proximity (Vein))");
    addItem(ItemRegistration.STRUCTURE_TEMPLATE_ITEM, "Structure Template (Incomplete)");
    addItem(ItemRegistration.BLUEPRINT, "机械蓝图");
    addItem(ItemRegistration.MODULARIUM, "模块化合金");
    addItem(ItemRegistration.WRENCH, "Wrench");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.CONTROLLER, "模块化机械控制器");

    addBlock(BlockRegistration.CASING_PLAIN, "机械外壳");
    addBlock(BlockRegistration.CASING_VENT, "机械通风口");
    addBlock(BlockRegistration.CASING_FIREBOX, "燃烧室外壳");
    addBlock(BlockRegistration.CASING_REINFORCED, "强化机械外壳");
    addBlock(BlockRegistration.CASING_CIRCUITRY, "机械电路板");
    addBlock(BlockRegistration.CASING_GEARBOX, "机械齿轮箱");

    addBlock(BlockRegistration.ITEM_INPUT_BUS_TINY, "微型物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_SMALL, "小型物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_NORMAL, "中型物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_REINFORCED, "强化物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_BIG, "大型物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_HUGE, "巨型物品输入仓");
    addBlock(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS, "超级物品输入仓");

    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_TINY, "微型物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_SMALL, "小型物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL, "中型物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED, "强化物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_BIG, "大型物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_HUGE, "巨型物品输出仓");
    addBlock(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS, "超级物品输出仓");

    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_TINY, "微型耐久处理仓");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_SMALL, "小型耐久处理仓");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL, "中型耐久处理仓");
    addBlock(BlockRegistration.ITEM_DURABILITY_HATCH_BIG, "大型耐久处理仓");

    addBlock(BlockRegistration.FLUID_INPUT_HATCH_TINY, "微型流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_SMALL, "小型流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_NORMAL, "中型流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED, "强化流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_BIG, "大型流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_HUGE, "巨型流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS, "超级流体输入仓");
    addBlock(BlockRegistration.FLUID_INPUT_HATCH_VACUUM, "真空流体输入仓");

    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_TINY, "微型流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL, "小型流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL, "中型流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED, "强化流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_BIG, "大型流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE, "巨型流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS, "超级流体输出仓");
    addBlock(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM, "真空流体输出仓");

    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY, "微型经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL, "小型经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL, "中型经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED, "强化经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG, "大型经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE, "巨型经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS, "超级经验输入仓");
    addBlock(BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM, "真空经验输入仓");

    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY, "微型经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL, "小型经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL, "中型经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED, "强化经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG, "大型经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE, "巨型经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS, "超级经验输出仓");
    addBlock(BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM, "真空经验输出仓");

    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_TINY, "微型能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_SMALL, "小型能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_NORMAL, "中型能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED, "强化能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_BIG, "大型能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_HUGE, "巨型能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS, "超级能源输入仓");
    addBlock(BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE, "终极能源输入仓");

    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY, "微型能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL, "小型能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL, "中型能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED, "强化能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_BIG, "大型能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE, "巨型能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS, "超级能源输出仓");
    addBlock(BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE, "终极能源输出仓");

    addBlock(BlockRegistration.PARALLEL_HATCH_BASIC, "基础并行处理仓");
    addBlock(BlockRegistration.PARALLEL_HATCH_MEDIUM, "中级并行处理仓");
    addBlock(BlockRegistration.PARALLEL_HATCH_ADVANCED, "高级并行处理仓");
    addBlock(BlockRegistration.PARALLEL_HATCH_ULTIMATE, "终极并行处理仓");
    addBlock(BlockRegistration.PARALLEL_HATCH_MAX, "极限并行处理仓");

    addBlock(BlockRegistration.DIMENSIONAL_DETECTOR, "维度检测器");
    addBlock(BlockRegistration.BIOME_READER, "群系读取器");
    addBlock(BlockRegistration.WEATHER_SENSOR, "天气传感器");
    addBlock(BlockRegistration.TIME_COUNTER, "时间计数器");
    addBlock(BlockRegistration.HEIGHT_METER, "高度计");
    addBlock(BlockRegistration.CHUNKLOADER, "区块加载器");

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
    add(tooltip("machinery.empty"), "空");
    add(tooltip("machinery.fuel"), "接受任意燃料。");
    add(tooltip("machinery.fuel.item"), "燃烧时间: %s");
    add(tooltip("machinery.fuel.in"), "合计需要的燃料燃烧时间：");
    add(tooltip("machinery.fuel.in.total"), "%s tick。");

    add(tooltip("machinery.duration"), "加工时间：%s ticks");
    add(tooltip("machinery.chance.in"), "有 %s 概率被消耗");
    add(tooltip("machinery.chance.out"), "有 %s 概率产出");
    add(tooltip("machinery.chance.in.never"), "不被消耗");
    add(tooltip("machinery.chance.out.never"), "不产出");

    add(tooltip("machinery.energy.in"), "能量消耗:");
    add(tooltip("machinery.energy.in.tick"), "每Tick消耗: %s %s/t");
    add(tooltip("machinery.energy.in.total"), "总计: %s %s");
    add(tooltip("machinery.energy.out"), "能量产出:");
    add(tooltip("machinery.energy.out.tick"), "每Tick产出: %s %s/t");
    add(tooltip("machinery.energy.out.total"), "总计: %s %s");

    add(tooltip("experiencehatch.empty"), "空");
    add(tooltip("experiencehatch.tank.points"), "%s / %s XP");
    add(tooltip("experiencehatch.tank.levels"), "%s / %s levels");
    add(tooltip("experiencehatch.tank.info"), "可以存储 %s 经验点数");

    add(tooltip("energy.type.fe"), "FE");
    add(tooltip("energy.type.ic2_eu"), "EU");
    add(tooltip("energy.type.gt_eu"), "EU");

    add(tooltip("constructtool.creative"), "创造模式用机械结构到 JSON 转换工具");

    add(tooltip("itembus.slot"), "1 格");
    add(tooltip("itembus.slots"), "%s 格");

    add(tooltip("fluidhatch.empty"), "空");
    add(tooltip("fluidhatch.fluid"), "[流体]");
    add(tooltip("fluidhatch.gas"), "[气体]");
    add(tooltip("fluidhatch.tank"), "%smB / %smB");
    add(tooltip("fluidhatch.tank.gas"), "%s / %s");
    add(tooltip("fluidhatch.tank.info"), "容积 %s mB");

    add(tooltip("energyhatch.charge"), "%s / %s %s");
    add(tooltip("energyhatch.storage"), "最大存储 %s FE");
    add(tooltip("energyhatch.out.transfer"), "最大输出 %s FE/t");
    add(tooltip("energyhatch.in.accept"), "最大输入 %s FE/t");

    add(tooltip("parallelhatch.size"), "使多方快结构最多同时处理 %s 个配方");
    add(tooltip("blueprint"), "右键任意机器控制器以显示结构");

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
    add(missingComponent("energy.output"), "未找到能量输出仓！");
    add(missingComponent("energy.input"), "未找到能量输入仓！");
    add(missingComponent("experience.output"), "未找到经验输出仓！");
    add(missingComponent("experience.input"), "未找到经验输入仓！");
    add(missingComponent("fluid.output"), "未找到流体输出仓！");
    add(missingComponent("fluid.input"), "未找到流体输入仓！");
    add(missingComponent("item.output"), "未找到物品输出仓！");
    add(missingComponent("item.input"), "未找到物品输入仓！");
    add(missingComponent("dimension"), "未找到维度检测器！");
    add(missingComponent("weather"), "未找到天气传感器！");
    add(missingComponent("biome"), "未找到群系读取器！");
    add(missingComponent("time"), "未找到时间计数器！");
    add(missingComponent("height"), "未找到高度计！");
    add(missingComponent("chunkload"), "未找到区块加载器！");
    add(missingComponent("function"), "未找到函数组件（控制器集成）！");
    add(missingComponent("durability.input"), "未找到耐久值损耗仓！");
    add(missingComponent("durability.output"), "未找到耐久值修复仓！");
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
    add(craftCheck("item.input"), "缺少物品输入, 需要 %sx %s！");
    add(craftCheck("item.output.space"), "没有足够的库存空间用于物品输出！");
    add(craftCheck("durability.input"), "耐久值不足, 需要 %s 但已找到 %s！");
    add(craftCheck("durability.output"), "未能修复物品, 尝试修复 %s 耐久值但是 %s 空余！");
    add(craftCheck("fluid.input"), "缺少液体输入, 需要 %s mB %s 但找到 %s mB %s！");
    add(craftCheck("fluid.output.space"), "没有足够的库存空间用于流体输出, 需要 %s mB 但找到 %s mB 空余！");
    add(craftCheck("fluid.output.fluid"), "与容器中已有流体不匹配, 需要 %s 但找到 %s!");
    add(craftCheck("energy.input"), "缺少能量输入: 需要 %s FE 但找到 %s FE！");
    add(craftCheck("energy.output"), "能源仓已满: 需要 %s FE 但找到 %s FE 空余！");
    add(craftCheck("experience.input"), "缺少经验输入: 需要 %s XP 但找到 %s XP！");
    add(craftCheck("experience.output"), "没有足够的库存空间用于经验输出, 需要 %s XP ,但找到 %s XP 空余！");
    add(craftCheck("dimension.false"), "未在指定的维度中, 应为: %s, 当前为: %s！");
    add(craftCheck("dimension.true"), "未在指定的维度中, 不能为: %s, 当前为: %s！");
    add(craftCheck("biome.false"), "未在指定的群系中, 应为: %s, 当前为: %s！");
    add(craftCheck("biome.true"), "未在指定的群系中, 不能为: %s, 当前为: %s！");
    add(craftCheck("weather"), "错误的天气, 应为: %s！");
    add(craftCheck("time"), "错误的时间, 应为: %s, 但找到: %s！");
    add(craftCheck("height"), "错误的高度, 应为: %s 但找到: %s！");
    add(craftCheck("parallel.loot_table"), "无法同时处理多个战利品表，请移除并行处理仓后再来处理此配方。");
    add(craftCheck("function"), "条件函数未能执行, 查看日志以获得错误信息!");
    add(craftCheck("function.no_listener"), "id 为 %s 的条件函数没有关联的 KubeJS 事件");
    add(craftCheck("function.interrupt"), "被 KubeJS 事件停止");
    add(craftCheck("fuel"), "Not enough burntime, needed: %s but found %s");
    add(craftCheck("entity.amount"), "Not enough entities nearby !");
    add(craftCheck("entity.health"), "Can't collect %s health points with nearby entities !");
    add(craftCheck("structure"), "Invalid structure!");
    add(craftCheck("redstone"), "Required redstone power: %s but found %s");
  }

  @Override
  protected void addGuiTitles() {
    add(mm(gui("title.controller")), "控制器");
    add(mm(gui("title.energy_hatch")), "能量仓");
    add(mm(gui("title.fluid_hatch")), "流体仓");
    add(mm(gui("title.item_bus")), "物品仓");
    add(mm(gui("title.parallel_hatch")), "并行处理仓");
    add(mm(gui("title.fuel_tank")), "Fuel Tank");
    add(mm(gui("title.redstone_port")), "Redstone Port");
    add(mmr(gui("button.back")), "返回");
    add(mmr(gui("button.close")), "关闭");
    add(mmr(gui("button.page.next")), "下一页");
    add(mmr(gui("button.page.prev")), "上一页");
    add(mm(gui("core_button")), "显示核心信息");
    add(mmr(gui("element.experience.tooltip")), "%s / %s");
    add(mmr(gui("element.experience.tooltip.input")), "需要: %s / %s");
    add(mmr(gui("element.experience.tooltip.output")), "产出: %s / %s");
    add(mmr(gui("element.experience.level")), "%s 级");
    add(mmr(gui(tooltip("experience.button.extract_1"))), "取出 1 级");
    add(mmr(gui(tooltip("experience.button.extract_10"))), "取出 10 级");
    add(mmr(gui(tooltip("experience.button.extract_all"))), "取出所有");
    add(mmr(gui(tooltip("experience.button.insert_1"))), "存入 1 级");
    add(mmr(gui(tooltip("experience.button.insert_10"))), "存入 10 级");
    add(mmr(gui(tooltip("experience.button.insert_all"))), "存入所有");
    add(mm(gui("structure_placer_button")), "点击以尝试放置结构");
    add(mm(gui("structure_breaker_button")), "点击以破坏当前结构");
    add(mmr(gui("structure.break")), "你确定要破坏当前的结构吗？");
    add(mmr(gui("structure.place.modifier")), "调节器是否与结构一同放置？");
    add(mmr(gui("structure.place.modifier.true")), "你确定放置结构和调节器吗？");
    add(mmr(gui("structure.place.modifier.false")), "你确定仅放置结构吗？");
    add(mmr(gui("structure.place.confirm.modifier")), "与结构一同放置");
    add(mmr(gui("structure.place.cancel.modifier")), "仅放置结构");
    add(mmr(gui("popup.confirm")), "确认");
    add(mmr(gui("popup.cancel")), "取消");
    add("emi." + tooltip("show.recipes"), "显示配方");
    add(mmr("emi.no_items"), "配方中没有物品");
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
    add(mm("structure_creator.mode.change.tooltip"), "当手持时,按下[%s]来切换模式");
    add(mm("structure_creator.mode.change"), "模式从 %s 变更到 %s");
    add(mm("structure_creator.mode.single"), "单个");
    add(mm("structure_creator.mode.box"), "范围");
    add(mm("structure_creator.mode.vein"), "Proximity (Vein)");
    add(mm("structure_creator.mode.box.first"), "请选择第一个角");
    add(mm("structure_creator.mode.box.second"), "请选择第二个角");
    add(mm("structure_creator.mode"), "当前模式: %s");
    add(mm("structure_creator.no_blocks"), "没有方块被选择");
    add(mm("structure_creator.amount"), "%s 个方块被选择");
    add(mm("structure_creator.select"), "右键方块来进行添加或者移除。如果被选择的方块拥有GUI界面则需要Shift+右键");
    add(mm("structure_creator.finish"), "Shift+右键任何模块化机械控制器来获取结构");
    add(mm("structure_creator.message"), "结构已生成。点击以复制 : %s %s %s %s");
    add(mm("structure_creator.reset"), "右键空气以清空被选择的方块");
    add(mm("structure_creator.vein.select"), "Select any block that is not air or the controller.");
    add(mm("structure_creator.vein.max"), "Max %s blocks");
  }

  @Override
  protected void addGuiController() {
    add(gui("controller.blueprint"), "已找到蓝图: %s");
    add(gui("controller.blueprint.none"), "无");
    add(gui("controller.structure"), "已找到结构: %s");
    add(gui("controller.structure.none"), "无");
    add(gui("controller.status"), "当前状态: ");
    add(gui("controller.error.info"), "Error Info: ");
    add(gui("controller.status.redstone_stopped"), "机器因红石信号停止工作");
    add(gui("controller.status.paused"), "暂停");
    add(gui("controller.status.missing_structure"), "结构不完整");
    add(gui("controller.status.no_recipe"), "没有相匹配的配方");
    add(gui("controller.status.failure"), "");
    add(gui("controller.status.crafting"), "工作中...");
    add(gui("controller.status.crafting.progress"), "处理中: %s");
    add(mmr("core.active.true"), "当前核心能为配方工作");
    add(mmr("core.active.false"), "当前核心不能能为配方工作");
    add(mmr("core.number"), "核心编号: %s");
    add(mmr(gui("core.button")), "单击显示 %s 核心的信息");
    add(mmr(gui(tooltip("core.action.button.single.add"))), "启用 1 个可用核心");
    add(mmr(gui(tooltip("core.action.button.single.remove"))), "禁用 1 个可用核心");
    add(mmr(gui(tooltip("core.action.button.shift.add"))), "启用 10 个可用核心");
    add(mmr(gui(tooltip("core.action.button.shift.remove"))), "禁用 10 个可用核心");
    add(mmr(gui(tooltip("core.action.button.control.add"))), "启用所有可用核心");
    add(mmr(gui(tooltip("core.action.button.control.remove"))), "禁用可用核心数至 1");
  }
}
