package es.degrassi.mmr.data;

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
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get()
        );

    tag(MMRTags.Blocks.ENERGY_OUTPUT)
        .add(
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get()
        );

    tag(MMRTags.Blocks.FLUID_INPUT)
        .add(
            BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get()
        );

    tag(MMRTags.Blocks.FLUID_OUTPUT)
        .add(
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get()
        );

    tag(MMRTags.Blocks.EXPERIENCE_INPUT)
        .add(
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get()
        );

    tag(MMRTags.Blocks.EXPERIENCE_OUTPUT)
        .add(
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get()
        );

    tag(MMRTags.Blocks.INPUT_BUS)
        .add(
            BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get()
        );

    tag(MMRTags.Blocks.OUTPUT_BUS)
        .add(
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get()
        );

    tag(MMRTags.Blocks.DURABILITY)
        .add(
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get()
        );

    tag(MMRTags.Blocks.FUEL_TANK)
        .add(
            BlockRegistration.FUEL_TANK_TINY.get(),
            BlockRegistration.FUEL_TANK_SMALL.get(),
            BlockRegistration.FUEL_TANK_NORMAL.get(),
            BlockRegistration.FUEL_TANK_REINFORCED.get(),
            BlockRegistration.FUEL_TANK_BIG.get(),
            BlockRegistration.FUEL_TANK_HUGE.get()
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
            BlockRegistration.PARALLEL_HATCH_BASIC.get(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.get(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.get(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.get(),
            BlockRegistration.PARALLEL_HATCH_MAX.get()
        );

    tag(MMRTags.Blocks.EFFECT_DISPENSER)
        .add(
          BlockRegistration.EFFECT_DISPENSER_SMALL.get(),
          BlockRegistration.EFFECT_DISPENSER_MEDIUM.get(),
          BlockRegistration.EFFECT_DISPENSER_BIG.get()
        );

    tag(MMRTags.Blocks.ENTITY)
        .add(
          BlockRegistration.ENTITY_DETECTOR.get(),
          BlockRegistration.ENTITY_SPAWNER.get(),
          BlockRegistration.ENTITY_KILLER.get(),
          BlockRegistration.ENTITY_DAMAGER.get(),
          BlockRegistration.ENTITY_HEALER.get()
        );

    tag(MMRTags.Blocks.CASINGS)
        .add(
            BlockRegistration.CASING_PLAIN.get(),
            BlockRegistration.CASING_VENT.get(),
            BlockRegistration.CASING_FIREBOX.get(),
            BlockRegistration.CASING_GEARBOX.get(),
            BlockRegistration.CASING_REINFORCED.get(),
            BlockRegistration.CASING_CIRCUITRY.get()
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
            BlockRegistration.BIOME_READER.get(),
            BlockRegistration.DIMENSIONAL_DETECTOR.get(),
            BlockRegistration.WEATHER_SENSOR.get(),
            BlockRegistration.TIME_COUNTER.get(),
            BlockRegistration.CHUNKLOADER.get(),
            BlockRegistration.HEIGHT_METER.get(),
            BlockRegistration.STRUCTURE_CHECKER.get(),
            BlockRegistration.REDSTONE_PORT.get(),
            BlockRegistration.COMMAND_EXECUTIONER.get()
        );

    tag(MMRTags.Blocks.ALL_CASINGS)
        .addTag(MMRTags.Blocks.CASINGS)
        .addTag(MMRTags.Blocks.HATCHES);

    tag(MMRTags.Blocks.REPLACEABLE)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.get());

    tag(MMRTags.Blocks.PLAIN_HATCHES)
        .add(
            BlockRegistration.BIOME_READER.get(),
            BlockRegistration.CHUNKLOADER.get(),
            BlockRegistration.DIMENSIONAL_DETECTOR.get(),
            BlockRegistration.WEATHER_SENSOR.get(),
            BlockRegistration.TIME_COUNTER.get(),
            BlockRegistration.HEIGHT_METER.get(),
            BlockRegistration.PARALLEL_HATCH_BASIC.get(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.get(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.get(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.get(),
            BlockRegistration.PARALLEL_HATCH_MAX.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get(),
            BlockRegistration.ITEM_INPUT_BUS_TINY.get(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.get(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_TINY.get(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.get(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get(),
            BlockRegistration.FUEL_TANK_TINY.get(),
            BlockRegistration.FUEL_TANK_SMALL.get(),
            BlockRegistration.FUEL_TANK_NORMAL.get(),
            BlockRegistration.EFFECT_DISPENSER_SMALL.get(),
            BlockRegistration.EFFECT_DISPENSER_MEDIUM.get(),
            BlockRegistration.ENTITY_DETECTOR.get(),
            BlockRegistration.ENTITY_DAMAGER.get(),
            BlockRegistration.ENTITY_HEALER.get(),
            BlockRegistration.REDSTONE_PORT.get(),
            BlockRegistration.COMMAND_EXECUTIONER.get()
        );

    tag(MMRTags.Blocks.PLAIN_CONNECTABLE)
        .add(
            BlockRegistration.CASING_PLAIN.get(),
            BlockRegistration.CASING_VENT.get(),
            BlockRegistration.CASING_FIREBOX.get(),
            BlockRegistration.CASING_GEARBOX.get(),
            BlockRegistration.CASING_CIRCUITRY.get()
        )
        .add(BlockRegistration.CONTROLLER.get())
        .addTag(MMRTags.Blocks.PLAIN_HATCHES)
    ;

    tag(MMRTags.Blocks.REINFORCED_HATCHES)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get(),
            BlockRegistration.FUEL_TANK_REINFORCED.get(),
            BlockRegistration.FUEL_TANK_BIG.get(),
            BlockRegistration.FUEL_TANK_HUGE.get(),
            BlockRegistration.EFFECT_DISPENSER_BIG.get(),
            BlockRegistration.ENTITY_KILLER.get(),
            BlockRegistration.ENTITY_SPAWNER.get(),
            BlockRegistration.STRUCTURE_CHECKER.get()
        );

    tag(MMRTags.Blocks.REINFORCED_CONNECTABLE)
        .add(BlockRegistration.CASING_REINFORCED.get())
        .addTag(MMRTags.Blocks.REINFORCED_HATCHES);

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .addTag(MMRTags.Blocks.ALL_CASINGS)
        .add(BlockRegistration.CONTROLLER.get());

    tag(BlockTags.NEEDS_STONE_TOOL)
        .addTag(MMRTags.Blocks.ALL_CASINGS);
  }
}
