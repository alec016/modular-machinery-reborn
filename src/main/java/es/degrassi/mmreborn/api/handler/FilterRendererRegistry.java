package es.degrassi.mmreborn.api.handler;

import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class FilterRendererRegistry {
  private FilterRendererRegistry() {}

  private static Map<Object, FilterRendererFactory> renderers;

  public static void init() {
    RegisterFilterRendererEvent event = new RegisterFilterRendererEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    renderers = event.getRenderers();
    MMRLogger.INSTANCE.debug("Registered {} filter renderers",
        renderers.keySet().stream().map(Object::toString).toList().toString());
  }

  public static <T> boolean hasRenderer(T item) {
    return renderers.containsKey(item);
  }

  private static <T> FilterRendererFactory getRenderer(T item) {
    return renderers.get(item);
  }

  public static <T> void render(T from, GuiGraphics guiGraphics, int x, int y) {
    getRenderer(from).render(guiGraphics, x, y);
  }
}
