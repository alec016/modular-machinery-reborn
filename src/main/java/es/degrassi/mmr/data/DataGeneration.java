package es.degrassi.mmr.data;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.data.advancements.AdvancementGenerator;
import es.degrassi.mmr.data.damagetypes.MMRDamageTypeProvider;
import es.degrassi.mmr.data.event.TranslationLangEvent;
import es.degrassi.mmr.data.kubejs.recipeschema.MMRRecipeSchemaProvider;
import es.degrassi.mmr.data.lang.Lang;
import es.degrassi.mmr.data.loot.MMRLootTableProvider;
import es.degrassi.mmr.data.models.MMRBlockStateProvider;
import es.degrassi.mmr.data.recipes.MMRRecipeProvider;
import es.degrassi.mmr.data.tags.MMRBlockTagProvider;
import es.degrassi.mmr.data.tags.MMRItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID)
public class DataGeneration {
  private DataGeneration() {}

  @SubscribeEvent
  public static void gatherData(GatherDataEvent.Client event) {
    DataGenerator generator = event.getGenerator();
    var registries = RegistryPatchGenerator.createLookup(event.getLookupProvider(), createDatapackEntriesBuilder())
        .thenApply(RegistrySetBuilder.PatchedRegistries::full);

    var localizations = Lang.getAll(generator);
    var pack = generator.getVanillaPack(true);

    //var engLang = localizations.stream().filter(lang -> lang.getLang().equals(Lang.en_us)).findFirst().orElse(null);

    // Loot
    pack.addProvider(out -> new MMRLootTableProvider(out, registries));

    // Tags
    var blockTagProvider = pack.addProvider(out -> new MMRBlockTagProvider(out, registries));
    pack.addProvider(out -> new MMRItemTagProvider(out, registries, blockTagProvider.contentsGetter()));
    // Damage Types
    pack.addProvider(MMRDamageTypeProvider::new);

    // Models
    // Blockstates
    pack.addProvider(MMRBlockStateProvider::new);

    // Misc
    pack.addProvider(out -> new AdvancementProvider(out, registries, List.of(
        new AdvancementGenerator()
    )));

    // Recipes
    pack.addProvider(bindRegistries(MMRRecipeProvider.Runner::new, registries));

    // KubeJS Recipe Schema Provider
    pack.addProvider(_ -> new MMRRecipeSchemaProvider(event));

    var translationEvent = new TranslationLangEvent();
    ModLoader.postEventWrapContainerInModOrder(translationEvent);
    // Must run last
    for (var local : localizations) {
      pack.addProvider(_ -> local.withTranslations(translationEvent.get(local.getLang())));
    }
  }

  private static RegistrySetBuilder createDatapackEntriesBuilder() {
    return new RegistrySetBuilder();
  }

  private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
      BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory,
      CompletableFuture<HolderLookup.Provider> factories) {
    return packOutput -> factory.apply(packOutput, factories);
  }
}
