package es.degrassi.mmr.data.tags;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MMRItemTagProvider extends BlockTagCopyingItemTagProvider {
  public MMRItemTagProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
    super(arg, completableFuture, blockTagsProvider, ModularMachineryReborn.MODID);
  }

  @Override
  public void addTags(HolderLookup.@NotNull Provider provider) {
    tag(MMRTags.Items.CHECK_NBT)
        .add(BlockRegistration.CONTROLLER.asItem());
    tag(MMRTags.Items.WRENCH)
        .add(
            ItemRegistration.WRENCH.asItem()
        );
    tag(MMRTags.Items.ENERGY_INPUT)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.asItem()
        );

    tag(MMRTags.Items.ENERGY_OUTPUT)
        .add(
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.asItem()
        );

    tag(MMRTags.Items.FLUID_INPUT)
        .add(
            BlockRegistration.FLUID_INPUT_HATCH_TINY.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.asItem()
        );

    tag(MMRTags.Items.FLUID_OUTPUT)
        .add(
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.asItem()
        );

    tag(MMRTags.Items.EXPERIENCE_INPUT)
        .add(
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.asItem()
        );

    tag(MMRTags.Items.EXPERIENCE_OUTPUT)
        .add(
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.asItem()
        );

    tag(MMRTags.Items.INPUT_BUS)
        .add(
            BlockRegistration.ITEM_INPUT_BUS_TINY.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.asItem(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.asItem()
        );

    tag(MMRTags.Items.OUTPUT_BUS)
        .add(
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.asItem()
        );

    tag(MMRTags.Items.DURABILITY)
        .add(
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.asItem()
        );

    tag(MMRTags.Items.FUEL_TANK)
        .add(
            BlockRegistration.FUEL_TANK_TINY.asItem(),
            BlockRegistration.FUEL_TANK_SMALL.asItem(),
            BlockRegistration.FUEL_TANK_NORMAL.asItem(),
            BlockRegistration.FUEL_TANK_REINFORCED.asItem(),
            BlockRegistration.FUEL_TANK_BIG.asItem(),
            BlockRegistration.FUEL_TANK_HUGE.asItem()
        );

    tag(MMRTags.Items.ITEM)
        .addTag(MMRTags.Items.INPUT_BUS)
        .addTag(MMRTags.Items.OUTPUT_BUS);

    tag(MMRTags.Items.ENERGY)
        .addTag(MMRTags.Items.ENERGY_INPUT)
        .addTag(MMRTags.Items.ENERGY_OUTPUT);

    tag(MMRTags.Items.FLUID)
        .addTag(MMRTags.Items.FLUID_INPUT)
        .addTag(MMRTags.Items.FLUID_OUTPUT);

    tag(MMRTags.Items.EXPERIENCE)
        .addTag(MMRTags.Items.EXPERIENCE_INPUT)
        .addTag(MMRTags.Items.EXPERIENCE_OUTPUT);

    tag(MMRTags.Items.PARALLEL)
        .add(
            BlockRegistration.PARALLEL_HATCH_BASIC.asItem(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.asItem(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.asItem(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.asItem(),
            BlockRegistration.PARALLEL_HATCH_MAX.asItem()
        );

    tag(MMRTags.Items.EFFECT_DISPENSER)
        .add(
            BlockRegistration.EFFECT_DISPENSER_SMALL.asItem(),
            BlockRegistration.EFFECT_DISPENSER_MEDIUM.asItem(),
            BlockRegistration.EFFECT_DISPENSER_BIG.asItem()
        );

    tag(MMRTags.Items.ENTITY)
        .add(
            BlockRegistration.ENTITY_DETECTOR.asItem(),
            BlockRegistration.ENTITY_SPAWNER.asItem(),
            BlockRegistration.ENTITY_KILLER.asItem(),
            BlockRegistration.ENTITY_DAMAGER.asItem(),
            BlockRegistration.ENTITY_HEALER.asItem()
        );

    tag(MMRTags.Items.CASINGS)
        .add(
            BlockRegistration.CASING_PLAIN.asItem(),
            BlockRegistration.CASING_VENT.asItem(),
            BlockRegistration.CASING_FIREBOX.asItem(),
            BlockRegistration.CASING_GEARBOX.asItem(),
            BlockRegistration.CASING_REINFORCED.asItem(),
            BlockRegistration.CASING_CIRCUITRY.asItem()
        );

    tag(MMRTags.Items.HATCHES)
        .addTag(MMRTags.Items.ENERGY)
        .addTag(MMRTags.Items.ITEM)
        .addTag(MMRTags.Items.FLUID)
        .addTag(MMRTags.Items.EXPERIENCE)
        .addTag(MMRTags.Items.PARALLEL)
        .addTag(MMRTags.Items.DURABILITY)
        .addTag(MMRTags.Items.FUEL_TANK)
        .addTag(MMRTags.Items.EFFECT_DISPENSER)
        .addTag(MMRTags.Items.ENTITY)
        .add(
            BlockRegistration.BIOME_READER.asItem(),
            BlockRegistration.DIMENSIONAL_DETECTOR.asItem(),
            BlockRegistration.WEATHER_SENSOR.asItem(),
            BlockRegistration.TIME_COUNTER.asItem(),
            BlockRegistration.CHUNKLOADER.asItem(),
            BlockRegistration.HEIGHT_METER.asItem(),
            BlockRegistration.STRUCTURE_CHECKER.asItem(),
            BlockRegistration.REDSTONE_PORT.asItem(),
            BlockRegistration.COMMAND_EXECUTIONER.asItem()
        );

    tag(MMRTags.Items.ALL_CASINGS)
        .addTag(MMRTags.Items.CASINGS)
        .addTag(MMRTags.Items.HATCHES);
  }
}
