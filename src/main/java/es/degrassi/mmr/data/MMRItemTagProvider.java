package es.degrassi.mmr.data;

import es.degrassi.mmr.ModularMachineryReborn;
import java.util.concurrent.CompletableFuture;

import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

public class MMRItemTagProvider extends ItemTagsProvider {
  public MMRItemTagProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, completableFuture, ModularMachineryReborn.MODID);
  }

  @Override
  public void addTags(HolderLookup.@NotNull Provider provider) {
    tag(MMRTags.Items.WRENCH)
        .add(
            ItemRegistration.WRENCH.asItem()
        );
    tag(MMRTags.Items.ENERGY_INPUT)
        .add(
            BlockRegistration.ENERGY_INPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.ENERGY_INPUT_HATCH_ULTIMATE.get().asItem()
        );

    tag(MMRTags.Items.ENERGY_OUTPUT)
        .add(
            BlockRegistration.ENERGY_OUTPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get().asItem()
        );

    tag(MMRTags.Items.FLUID_INPUT)
        .add(
            BlockRegistration.FLUID_INPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM.get().asItem()
        );

    tag(MMRTags.Items.FLUID_OUTPUT)
        .add(
            BlockRegistration.FLUID_OUTPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM.get().asItem()
        );

    tag(MMRTags.Items.EXPERIENCE_INPUT)
        .add(
            BlockRegistration.EXPERIENCE_INPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.EXPERIENCE_INPUT_HATCH_VACUUM.get().asItem()
        );

    tag(MMRTags.Items.EXPERIENCE_OUTPUT)
        .add(
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get().asItem(),
            BlockRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get().asItem()
        );

    tag(MMRTags.Items.INPUT_BUS)
        .add(
            BlockRegistration.ITEM_INPUT_BUS_TINY.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_SMALL.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_NORMAL.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_REINFORCED.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_BIG.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_HUGE.get().asItem(),
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS.get().asItem()
        );

    tag(MMRTags.Items.OUTPUT_BUS)
        .add(
            BlockRegistration.ITEM_OUTPUT_BUS_TINY.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_SMALL.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_NORMAL.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_BIG.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_HUGE.get().asItem(),
            BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS.get().asItem()
        );

    tag(MMRTags.Items.DURABILITY)
        .add(
            BlockRegistration.ITEM_DURABILITY_HATCH_TINY.get().asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_SMALL.get().asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_NORMAL.get().asItem(),
            BlockRegistration.ITEM_DURABILITY_HATCH_BIG.get().asItem()
        );

    tag(MMRTags.Items.FUEL_TANK)
        .add(
            BlockRegistration.FUEL_TANK_TINY.get().asItem(),
            BlockRegistration.FUEL_TANK_SMALL.get().asItem(),
            BlockRegistration.FUEL_TANK_NORMAL.get().asItem(),
            BlockRegistration.FUEL_TANK_REINFORCED.get().asItem(),
            BlockRegistration.FUEL_TANK_BIG.get().asItem(),
            BlockRegistration.FUEL_TANK_HUGE.get().asItem()
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
            BlockRegistration.PARALLEL_HATCH_BASIC.get().asItem(),
            BlockRegistration.PARALLEL_HATCH_MEDIUM.get().asItem(),
            BlockRegistration.PARALLEL_HATCH_ADVANCED.get().asItem(),
            BlockRegistration.PARALLEL_HATCH_ULTIMATE.get().asItem(),
            BlockRegistration.PARALLEL_HATCH_MAX.get().asItem()
        );

    tag(MMRTags.Items.EFFECT_DISPENSER)
        .add(
            BlockRegistration.EFFECT_DISPENSER_SMALL.get().asItem(),
            BlockRegistration.EFFECT_DISPENSER_MEDIUM.get().asItem(),
            BlockRegistration.EFFECT_DISPENSER_BIG.get().asItem()
        );

    tag(MMRTags.Items.ENTITY)
        .add(
            BlockRegistration.ENTITY_DETECTOR.get().asItem(),
            BlockRegistration.ENTITY_SPAWNER.get().asItem(),
            BlockRegistration.ENTITY_KILLER.get().asItem(),
            BlockRegistration.ENTITY_DAMAGER.get().asItem(),
            BlockRegistration.ENTITY_HEALER.get().asItem()
        );

    tag(MMRTags.Items.CASINGS)
        .add(
            BlockRegistration.CASING_PLAIN.get().asItem(),
            BlockRegistration.CASING_VENT.get().asItem(),
            BlockRegistration.CASING_FIREBOX.get().asItem(),
            BlockRegistration.CASING_GEARBOX.get().asItem(),
            BlockRegistration.CASING_REINFORCED.get().asItem(),
            BlockRegistration.CASING_CIRCUITRY.get().asItem()
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
            BlockRegistration.BIOME_READER.get().asItem(),
            BlockRegistration.DIMENSIONAL_DETECTOR.get().asItem(),
            BlockRegistration.WEATHER_SENSOR.get().asItem(),
            BlockRegistration.TIME_COUNTER.get().asItem(),
            BlockRegistration.CHUNKLOADER.get().asItem(),
            BlockRegistration.HEIGHT_METER.get().asItem(),
            BlockRegistration.STRUCTURE_CHECKER.get().asItem(),
            BlockRegistration.REDSTONE_PORT.get().asItem(),
            BlockRegistration.COMMAND_EXECUTIONER.get().asItem()
        );

    tag(MMRTags.Items.ALL_CASINGS)
        .addTag(MMRTags.Items.CASINGS)
        .addTag(MMRTags.Items.HATCHES);
  }
}
