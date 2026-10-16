package es.degrassi.mmr.common.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;

public abstract class CustomJsonReloadListener extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

  private static final Gson GSON = new GsonBuilder().create();
  private static final int PATH_SUFFIX_LENGTH = ".json".length();
  private static final Logger LOGGER = MMRLogger.INSTANCE;
  private final String directory;

  protected CustomJsonReloadListener(String string) {
    this.directory = string;
  }

  @Override
  protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
    LOGGER.info("Parsing all .json files in {} folder.", this.directory);
    Map<Identifier, JsonElement> map = Maps.newHashMap();
    int i = this.directory.length() + 1;

    for(Map.Entry<Identifier, Resource> entry : manager.listResources(this.directory, loc -> loc.getPath().endsWith(".json")).entrySet()) {
      Identifier loc = entry.getKey();
      String path = loc.getPath();
      Identifier id = Identifier.fromNamespaceAndPath(loc.getNamespace(), path.substring(i, path.length() - PATH_SUFFIX_LENGTH));

      try(Reader reader = entry.getValue().openAsReader()) {
        JsonElement jsonElement = GsonHelper.fromNullableJson(GSON, reader, TypeToken.get(JsonElement.class));
        if(jsonElement != null) {
          JsonElement replaced = map.put(id, jsonElement);
          if(replaced != null)
            throw new IllegalStateException("Duplicate data file ignored with ID " + id);
        } else
          LOGGER.error("Couldn't load data file {} from {} as it's null or empty", id, loc);
      } catch (IllegalArgumentException | IOException | JsonParseException e) {
        LOGGER.error("Couldn't parse data file {} from {}\n{}", id, loc, e);
      }
    }
    LOGGER.info("Finished, {} .json files successfully parsed in {} folder.", map.size(), this.directory);
    return map;
  }
}
