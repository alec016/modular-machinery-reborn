package es.degrassi.mmr.api.integration.jei;

import com.google.common.collect.Maps;
import es.degrassi.mmr.api.MMREvent;
import mezz.jei.api.ingredients.IIngredientType;

import java.util.Map;

public class RegisterJeiFilterDragDropEvent extends MMREvent<IIngredientType<?>, JeiFilterDragDropFactory<?>> {
  private final Map<IIngredientType<?>, JeiFilterDragDropFactory<?>> map = Maps.newHashMap();

  /**
   * Registers a conversion between a certain type and its compatible format with mmr filter system
   * @param type type to be registered
   * @param factory minecraft packet constructor with position
   * @param <T> generic type por type-safe in constructing factory
   */
  public <T> void register(IIngredientType<T> type, JeiFilterDragDropFactory<T> factory) {
    if (map.containsKey(type)) {
      throw new IllegalArgumentException("Duplicated type " + type);
    }
    map.put(type, factory);
  }
}
