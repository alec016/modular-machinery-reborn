package es.degrassi.mmreborn.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public abstract class MMREvent<KEY, VALUE> extends Event implements IModBusEvent {
  private final Map<KEY,VALUE> map = Maps.newHashMap();

  public <K extends KEY, V extends VALUE> void register(K key, V value) {
    if (map.containsKey(key)) {
      throw new IllegalArgumentException("Duplicate key " + key.toString());
    }
    map.put(key, value);
  }

  public Map<KEY, VALUE> getMap() {
    return ImmutableMap.copyOf(this.map);
  }
}
