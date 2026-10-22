package es.degrassi.mmr.client.screen.widget.gauges;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class Gauge extends UIElement {

  public static final int TANK_WIDTH = 20;
  public static final int TANK_HEIGHT = 61;
  public static final int TANK_BACKING_WIDTH = 22;
  public static final int TANK_BACKING_HEIGHT = 63;

  protected Gauge() {
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      List<Component> lines = tooltipLines();
      if (!lines.isEmpty()) {
        event.hoverTooltips = HoverTooltips.create(lines.toArray());
      }
    });
  }

  protected abstract List<Component> tooltipLines();

  protected static GUIContext clientContext(IGUIContext context) {
    return context instanceof GUIContext guiContext ? guiContext : null;
  }
}
