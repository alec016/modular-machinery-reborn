package es.degrassi.mmreborn.api.handler;

import net.minecraft.client.gui.GuiGraphics;

public interface FilterRendererFactory {
  void render(GuiGraphics guiGraphics, int x, int y);
}
