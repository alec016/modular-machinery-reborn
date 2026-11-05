package es.degrassi.mmr.client.screen.widget.tabs.fill.pick;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface Pick {
  default void addLines(List<Component> lines) {

  }

  default void draw(GUIContext context, int x, int y) {}

  <FILTER> FILTER stack();
}
