package es.degrassi.mmr.data.recipes;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class MMRRecipeProvider extends RecipeProvider {
  protected final HolderGetter<Item> items;
  protected MMRRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
    this.items = registries.lookupOrThrow(Registries.ITEM);
  }

  protected final Identifier id(String path) {
    return ModularMachineryReborn.rl(path);
  }

  protected final String idString(String path) {
    return id(path).toString();
  }

  protected final ResourceKey<Recipe<?>> key(String path) {
    return ResourceKey.create(Registries.RECIPE, id(path));
  }

  @FunctionalInterface
  public interface RecipeProviderFactory {
    MMRRecipeProvider create(HolderLookup.Provider registries, RecipeOutput output);
  }

  public static final class Runner extends RecipeProvider.Runner {
    private static final List<RecipeProviderFactory> PROVIDERS = List.of(
      CraftingRecipes::new
    );

    public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
      super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
      return new RecipeProvider(provider, recipeOutput) {
        @Override
        protected void buildRecipes() {
          for (var provider : PROVIDERS) {
            provider.create(registries, output).buildRecipes();
          }
        }
      };
    }

    @Override
    public String getName() {
      return "MMR Recipes";
    }
  }
}
