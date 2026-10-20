package es.degrassi.mmr.data.tags;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.registration.BlockRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MMRBlockTagProvider extends BlockTagsProvider {
  public MMRBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, ModularMachineryReborn.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    tag(MMRTags.Blocks.ENERGY_INPUT)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.block()
        );

    tag(MMRTags.Blocks.ENERGY_OUTPUT)
        .add(
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.block()
        );

    tag(MMRTags.Blocks.FLUID_INPUT)
        .add(
            BlockRegistration.FLUID_INPUT_HATCH_TINY.block(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.block(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.block(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.block(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.block()
        );

    tag(MMRTags.Blocks.FLUID_OUTPUT)
        .add(
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.block()
        );

    tag(MMRTags.Blocks.EXPERIENCE_INPUT)
        .add(
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.block()
        );

    tag(MMRTags.Blocks.EXPERIENCE_OUTPUT)
        .add(
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.block()
        );

    tag(MMRTags.Blocks.INPUT_BUS)
        .add(
            BlockRegistration.ITEM_INPUT_BUS_TINY.block(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.block(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.block(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.block(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.block(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.block(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.block()
        );

    tag(MMRTags.Blocks.OUTPUT_BUS)
        .add(
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.block()
        );

    tag(MMRTags.Blocks.DURABILITY)
        .add(
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.block()
        );

    tag(MMRTags.Blocks.FUEL_TANK)
        .add(
            BlockRegistration.FUEL_TANK_TINY.block(),
            BlockRegistration.FUEL_TANK_SMALL.block(),
            BlockRegistration.FUEL_TANK_NORMAL.block(),
            BlockRegistration.FUEL_TANK_REINFORCED.block(),
            BlockRegistration.FUEL_TANK_BIG.block(),
            BlockRegistration.FUEL_TANK_HUGE.block()
        );

    tag(MMRTags.Blocks.ITEM)
        .addTag(MMRTags.Blocks.INPUT_BUS)
        .addTag(MMRTags.Blocks.OUTPUT_BUS);

    tag(MMRTags.Blocks.ENERGY)
        .addTag(MMRTags.Blocks.ENERGY_INPUT)
        .addTag(MMRTags.Blocks.ENERGY_OUTPUT);

    tag(MMRTags.Blocks.FLUID)
        .addTag(MMRTags.Blocks.FLUID_INPUT)
        .addTag(MMRTags.Blocks.FLUID_OUTPUT);

    tag(MMRTags.Blocks.EXPERIENCE)
        .addTag(MMRTags.Blocks.EXPERIENCE_INPUT)
        .addTag(MMRTags.Blocks.EXPERIENCE_OUTPUT);

    tag(MMRTags.Blocks.PARALLEL)
        .add(
            BlockRegistration.PARALLEL_HATCH_BASIC.block(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.block(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.block(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.block(),
            BlockRegistration.PARALLEL_HATCH_MAX.block()
        );

    tag(MMRTags.Blocks.EFFECT_DISPENSER)
        .add(
          BlockRegistration.EFFECT_DISPENSER_SMALL.block(),
          BlockRegistration.EFFECT_DISPENSER_MEDIUM.block(),
          BlockRegistration.EFFECT_DISPENSER_BIG.block()
        );

    tag(MMRTags.Blocks.ENTITY)
        .add(
          BlockRegistration.ENTITY_DETECTOR.block(),
          BlockRegistration.ENTITY_SPAWNER.block(),
          BlockRegistration.ENTITY_KILLER.block(),
          BlockRegistration.ENTITY_DAMAGER.block(),
          BlockRegistration.ENTITY_HEALER.block()
        );

    tag(MMRTags.Blocks.CASINGS)
        .add(
            BlockRegistration.CASING_PLAIN.block(),
            BlockRegistration.CASING_VENT.block(),
            BlockRegistration.CASING_FIREBOX.block(),
            BlockRegistration.CASING_GEARBOX.block(),
            BlockRegistration.CASING_REINFORCED.block(),
            BlockRegistration.CASING_CIRCUITRY.block()
        );

    tag(MMRTags.Blocks.HATCHES)
        .addTag(MMRTags.Blocks.ENERGY)
        .addTag(MMRTags.Blocks.ITEM)
        .addTag(MMRTags.Blocks.FLUID)
        .addTag(MMRTags.Blocks.EXPERIENCE)
        .addTag(MMRTags.Blocks.PARALLEL)
        .addTag(MMRTags.Blocks.DURABILITY)
        .addTag(MMRTags.Blocks.FUEL_TANK)
        .addTag(MMRTags.Blocks.EFFECT_DISPENSER)
        .addTag(MMRTags.Blocks.ENTITY)
        .add(
            BlockRegistration.BIOME_READER.block(),
            BlockRegistration.DIMENSIONAL_DETECTOR.block(),
            BlockRegistration.WEATHER_SENSOR.block(),
            BlockRegistration.TIME_COUNTER.block(),
            BlockRegistration.CHUNKLOADER.block(),
            BlockRegistration.HEIGHT_METER.block(),
            BlockRegistration.STRUCTURE_CHECKER.block(),
            BlockRegistration.REDSTONE_PORT.block(),
            BlockRegistration.COMMAND_EXECUTIONER.block()
        );

    tag(MMRTags.Blocks.ALL_CASINGS)
        .addTag(MMRTags.Blocks.CASINGS)
        .addTag(MMRTags.Blocks.HATCHES);

    tag(MMRTags.Blocks.REPLACEABLE)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.block());

    tag(MMRTags.Blocks.PLAIN_HATCHES)
        .add(
            BlockRegistration.BIOME_READER.block(),
            BlockRegistration.CHUNKLOADER.block(),
            BlockRegistration.DIMENSIONAL_DETECTOR.block(),
            BlockRegistration.WEATHER_SENSOR.block(),
            BlockRegistration.TIME_COUNTER.block(),
            BlockRegistration.HEIGHT_METER.block(),
            BlockRegistration.PARALLEL_HATCH_BASIC.block(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.block(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.block(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.block(),
            BlockRegistration.PARALLEL_HATCH_MAX.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.block(),
            BlockRegistration.ITEM_INPUT_BUS_TINY.block(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.block(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.FLUID_INPUT_HATCH_TINY.block(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.block(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.block(),
            BlockRegistration.FUEL_TANK_TINY.block(),
            BlockRegistration.FUEL_TANK_SMALL.block(),
            BlockRegistration.FUEL_TANK_NORMAL.block(),
            BlockRegistration.EFFECT_DISPENSER_SMALL.block(),
            BlockRegistration.EFFECT_DISPENSER_MEDIUM.block(),
            BlockRegistration.ENTITY_DETECTOR.block(),
            BlockRegistration.ENTITY_DAMAGER.block(),
            BlockRegistration.ENTITY_HEALER.block(),
            BlockRegistration.REDSTONE_PORT.block(),
            BlockRegistration.COMMAND_EXECUTIONER.block()
        );

    tag(MMRTags.Blocks.PLAIN_CONNECTABLE)
        .add(
            BlockRegistration.CASING_PLAIN.block(),
            BlockRegistration.CASING_VENT.block(),
            BlockRegistration.CASING_FIREBOX.block(),
            BlockRegistration.CASING_GEARBOX.block(),
            BlockRegistration.CASING_CIRCUITRY.block()
        )
        .add(BlockRegistration.CONTROLLER.block())
        .addTag(MMRTags.Blocks.PLAIN_HATCHES)
    ;

    tag(MMRTags.Blocks.REINFORCED_HATCHES)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.block(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.block(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.block(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.block(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.block(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.block(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.block(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.block(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.block(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.block(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.block(),
            BlockRegistration.FUEL_TANK_REINFORCED.block(),
            BlockRegistration.FUEL_TANK_BIG.block(),
            BlockRegistration.FUEL_TANK_HUGE.block(),
            BlockRegistration.EFFECT_DISPENSER_BIG.block(),
            BlockRegistration.ENTITY_KILLER.block(),
            BlockRegistration.ENTITY_SPAWNER.block(),
            BlockRegistration.STRUCTURE_CHECKER.block()
        );

    tag(MMRTags.Blocks.REINFORCED_CONNECTABLE)
        .add(BlockRegistration.CASING_REINFORCED.block())
        .addTag(MMRTags.Blocks.REINFORCED_HATCHES);

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.block());

    tag(BlockTags.NEEDS_STONE_TOOL)
        .addTag(MMRTags.Blocks.ALL_CASINGS);
  }
}
