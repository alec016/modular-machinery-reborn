package es.degrassi.mmr.api.handler;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;

public interface FilterRendererFactory {
  /**
   * @param context ldlib2 graphics wrapper renderer
   * @param x position
   * @param y position
   */
  void render(GUIContext context, float x, float y);
}
