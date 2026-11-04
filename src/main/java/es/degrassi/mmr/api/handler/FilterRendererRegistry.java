package es.degrassi.mmr.api.handler;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.fml.ModLoader;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterRendererRegistry {
  private static Map<Object, FilterRendererFactory> renderers;

  public static void init() {
    RegisterFilterRendererEvent event = new RegisterFilterRendererEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    renderers = event.getMap();
  }

  public static <T> boolean hasRenderer(T item) {
    return renderers.containsKey(item);
  }

  private static <T> FilterRendererFactory getRenderer(T item) {
    return renderers.get(item);
  }

  public static <T> void render(T from, GUIContext context, float x, float y) {
    getRenderer(from).render(context, x, y);
  }
}
