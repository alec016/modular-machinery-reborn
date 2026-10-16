package es.degrassi.mmr.data.lang;

import com.google.gson.JsonObject;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class MMRLangProvider extends LanguageProvider {
  private final String locale;
  private final PackOutput output;
  private final Lang executioner;
  public MMRLangProvider(PackOutput output, String locale) {
    super(output, ModularMachineryReborn.MODID, locale);
    this.locale = locale;
    this.output = output;
    this.executioner = Lang.fromLocale(locale);
  }

  public CompletableFuture<?> run(CachedOutput cache) {
    if (this.executioner != null) {
      this.executioner.init();
      return this.save(
          cache,
          this.output
              .getOutputFolder(PackOutput.Target.RESOURCE_PACK)
              .resolve(this.executioner.modId())
              .resolve("lang")
              .resolve(this.locale + ".json")
      );
    }
    return CompletableFuture.allOf();
  }

  public String getName() {
    return "Languages: " + this.locale + " for mod: " + this.executioner.modId();
  }

  private CompletableFuture<?> save(CachedOutput cache, Path target) {
    JsonObject json = new JsonObject();
    this.executioner.getData().forEach(json::addProperty);
    this.executioner.getJsonData().forEach(json::add);
    if (json.keySet().isEmpty()) return CompletableFuture.allOf();
    return DataProvider.saveStable(cache, json, target);
  }


  @Override
  protected void addTranslations() {}
}
