package es.degrassi.mmr.api.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface TooltipRender {
  void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY);
}
