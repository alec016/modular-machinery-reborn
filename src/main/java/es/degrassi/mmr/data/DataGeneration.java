package es.degrassi.mmr.data;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.data.lang.MMRLangProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID)
public class DataGeneration {
  private DataGeneration() {}

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();

    event.addProvider(new MMRBlockTagProvider(generator.getPackOutput(), event.getLookupProvider()));
    event.addProvider(new MMRItemTagProvider(generator.getPackOutput(), event.getLookupProvider()));

    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "en_us"));
    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "es_es"));
    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "pt_br"));
    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "zh_cn"));
    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "ru_ru"));
    event.addProvider(new MMRLangProvider(generator.getPackOutput(), "uk_ua"));

    event.addProvider(new MMRBlockStateProvider(generator.getPackOutput()));
  }
}
