package es.degrassi.mmreborn.api.integration.jei;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import mezz.jei.api.ingredients.IIngredientType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterJeiFilterDragDropEvent extends Event implements IModBusEvent {
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

  public Map<IIngredientType<?>, JeiFilterDragDropFactory<?>> getMap() {
    return ImmutableMap.copyOf(map);
  }
}
