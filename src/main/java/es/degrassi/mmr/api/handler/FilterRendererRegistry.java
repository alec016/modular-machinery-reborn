package es.degrassi.mmr.api.handler;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class FilterRendererRegistry {
  private FilterRendererRegistry() {}

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

  public static <T> void render(T from, GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    getRenderer(from).render(GuiGraphicsExtractor, x, y);
  }
}
