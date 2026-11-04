package es.degrassi.mmr.client.screen.widget.tabs;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import es.degrassi.mmr.api.client.Icon;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

abstract class TabElement extends UIElement {
  protected final Supplier<ItemStack> icon;
  protected final Supplier<List<Object>> tooltips;
  @Nullable
  protected Icon spriteIcon;

  protected TabElement(Supplier<ItemStack> icon, Supplier<List<Object>> tooltips) {
    this.icon = icon;
    this.tooltips = tooltips;
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      List<?> lines = this.tooltips == null ? null : this.tooltips.get();
      if (lines != null && !lines.isEmpty()) {
        event.hoverTooltips = HoverTooltips.create(lines.toArray());
      }
    });
  }

  protected abstract void drawTab(GUIContext gui, int x, int y);

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext gui)) {
      return;
    }
    drawTab(gui, (int) getContentX(), (int) getContentY());
  }
}
