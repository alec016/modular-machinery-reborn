package es.degrassi.mmreborn.api.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterFilterRendererEvent extends Event implements IModBusEvent {
  private final Map<Object, FilterRendererFactory> renderers = Maps.newHashMap();

  public <T> void register(T item, FilterRendererFactory rendererFactory) {
    this.renderers.put(item, rendererFactory);
  }

  public Map<Object, FilterRendererFactory> getRenderers() {
    return ImmutableMap.copyOf(renderers);
  }
}
