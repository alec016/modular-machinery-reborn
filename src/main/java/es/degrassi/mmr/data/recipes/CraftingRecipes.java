package es.degrassi.mmr.data.recipes;

import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class CraftingRecipes extends MMRRecipeProvider {
  public CraftingRecipes(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
  }

  @Override
  protected void buildRecipes() {
    shaped(RecipeCategory.MISC, ItemRegistration.MODULARIUM, 5)
        .pattern("gig")
        .pattern("iri")
        .pattern("ror")
        .define('r', Tags.Items.DUSTS_REDSTONE)
        .define('g', Tags.Items.INGOTS_GOLD)
        .define('i', Tags.Items.INGOTS_IRON)
        .define('o', Tags.Items.DUSTS_GLOWSTONE)
        .unlockedBy("has_dust/redstone", has(Tags.Items.DUSTS_REDSTONE))
        .unlockedBy("has_dust/glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
        .unlockedBy("has_ingot/iron", has(Tags.Items.INGOTS_IRON))
        .unlockedBy("has_ingot/gold", has(Tags.Items.INGOTS_GOLD))
        .save(output, idString("modularium_ingot"));

    shaped(RecipeCategory.MISC, BlockRegistration.CASING_PLAIN, 2)
        .pattern(" m ")
        .pattern("mrm")
        .pattern(" m ")
        .define('r', Tags.Items.DUSTS_REDSTONE)
        .define('m', ItemRegistration.MODULARIUM)
        .unlockedBy("has_ingot/modularium", has(ItemRegistration.MODULARIUM))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.CASING_FIREBOX, 2)
        .pattern("rfr")
        .pattern("c c")
        .pattern("rir")
        .define('r', Tags.Items.DUSTS_REDSTONE)
        .define('i', Tags.Items.INGOTS_IRON)
        .define('c', BlockRegistration.CASING_PLAIN)
        .define('f', Items.FLINT)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.CASING_REINFORCED, 4)
        .pattern("oco")
        .pattern("c c")
        .pattern("oco")
        .define('o', Tags.Items.OBSIDIANS)
        .define('c', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIANS))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ENERGY_INPUT_HATCH_TINY)
        .pattern(" d ")
        .pattern("dmd")
        .pattern("ere")
        .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
        .define('e', Items.REPEATER)
        .define('d', Tags.Items.DUSTS_REDSTONE)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ENERGY_INPUT_HATCH_SMALL)
        .pattern("ded")
        .pattern("rmr")
        .pattern("drd")
        .define('d', Tags.Items.DUSTS_REDSTONE)
        .define('e', Items.REPEATER)
        .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
        .define('m', BlockRegistration.ENERGY_INPUT_HATCH_TINY)
        .unlockedBy("has_energy_hatch/tiny", has(BlockRegistration.ENERGY_INPUT_HATCH_TINY))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ENERGY_OUTPUT_HATCH_TINY)
        .pattern("ere")
        .pattern("dmd")
        .pattern(" d ")
        .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
        .define('e', Items.REPEATER)
        .define('d', Tags.Items.DUSTS_REDSTONE)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ENERGY_OUTPUT_HATCH_SMALL)
        .pattern("drd")
        .pattern("rmr")
        .pattern("ded")
        .define('d', Tags.Items.DUSTS_REDSTONE)
        .define('e', Items.REPEATER)
        .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
        .define('m', BlockRegistration.ENERGY_OUTPUT_HATCH_TINY)
        .unlockedBy("has_energy_hatch/tiny", has(BlockRegistration.ENERGY_OUTPUT_HATCH_TINY))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.FLUID_INPUT_HATCH_TINY)
        .pattern("h")
        .pattern("m")
        .pattern("c")
        .define('c', Items.BUCKET)
        .define('h', Items.HOPPER)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.FLUID_OUTPUT_HATCH_TINY)
        .pattern("c")
        .pattern("m")
        .pattern("h")
        .define('c', Items.BUCKET)
        .define('h', Items.HOPPER)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.FLUID_INPUT_HATCH_SMALL)
        .pattern(" h ")
        .pattern("mbm")
        .pattern("cmc")
        .define('c', Items.BUCKET)
        .define('m', ItemRegistration.MODULARIUM)
        .define('h', Items.HOPPER)
        .define('b', BlockRegistration.FLUID_INPUT_HATCH_TINY)
        .unlockedBy("has_fluid_hatch/tiny", has(BlockRegistration.FLUID_INPUT_HATCH_TINY))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.FLUID_OUTPUT_HATCH_SMALL)
        .pattern("cmc")
        .pattern("mbm")
        .pattern(" h ")
        .define('c', Items.BUCKET)
        .define('m', ItemRegistration.MODULARIUM)
        .define('h', Items.HOPPER)
        .define('b', BlockRegistration.FLUID_OUTPUT_HATCH_TINY)
        .unlockedBy("has_fluid_hatch/tiny", has(BlockRegistration.FLUID_OUTPUT_HATCH_TINY))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ITEM_INPUT_BUS_TINY)
        .pattern("h")
        .pattern("m")
        .pattern("c")
        .define('c', Tags.Items.CHESTS)
        .define('h', Items.HOPPER)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ITEM_OUTPUT_BUS_TINY)
        .pattern("c")
        .pattern("m")
        .pattern("h")
        .define('c', Tags.Items.CHESTS)
        .define('h', Items.HOPPER)
        .define('m', BlockRegistration.CASING_PLAIN)
        .unlockedBy("has_casing/plain", has(BlockRegistration.CASING_PLAIN))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ITEM_INPUT_BUS_SMALL)
        .pattern(" h ")
        .pattern("mbm")
        .pattern("cmc")
        .define('c', Tags.Items.CHESTS)
        .define('h', Items.HOPPER)
        .define('m', ItemRegistration.MODULARIUM)
        .define('b', BlockRegistration.ITEM_INPUT_BUS_TINY)
        .unlockedBy("has_item_bus/tiny", has(BlockRegistration.ITEM_INPUT_BUS_TINY))
        .save(output);

    shaped(RecipeCategory.MISC, BlockRegistration.ITEM_OUTPUT_BUS_SMALL)
        .pattern("cmc")
        .pattern("mbm")
        .pattern(" h ")
        .define('c', Tags.Items.CHESTS)
        .define('h', Items.HOPPER)
        .define('m', ItemRegistration.MODULARIUM)
        .define('b', BlockRegistration.ITEM_OUTPUT_BUS_TINY)
        .unlockedBy("has_item_bus/tiny", has(BlockRegistration.ITEM_OUTPUT_BUS_TINY))
        .save(output);
  }
}
