package es.degrassi.mmreborn.api.integration.emi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.emi.emi.api.stack.EmiStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterEmiFilterDragDropEvent extends Event implements IModBusEvent {
  private final Map<Class<? extends EmiStack>, EmiFilterDragDropFactory<? extends EmiStack>> map = Maps.newHashMap();

  /**
   * Registers a conversion between a certain type and its compatible format with mmr filter system
   * @param key type class to be registered
   * @param value minecraft packet constructor with position
   * @param <K> generic type por type-safe in constructing factory that might extends {@link EmiStack}
   */
  public <K extends EmiStack> void register(Class<K> key, EmiFilterDragDropFactory<K> value) {
    if (map.containsKey(key)) {
      throw new IllegalArgumentException("Duplicate key " + key.getName() );
    }
    map.put(key, value);
  }

  public Map<Class<? extends EmiStack>, EmiFilterDragDropFactory<? extends EmiStack>> getMap() {
    return ImmutableMap.copyOf(this.map);
  }}
