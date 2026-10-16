package es.degrassi.mmr.api.handler;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface FilterRendererFactory {
  void render(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y);
}
