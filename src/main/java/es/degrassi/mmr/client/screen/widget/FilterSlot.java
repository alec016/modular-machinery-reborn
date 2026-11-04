package es.degrassi.mmr.client.screen.widget;

import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import es.degrassi.mmr.api.handler.FilterRendererRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FilterSlot<T, E> extends ItemSlot {
  private static final int PLACEHOLDER_VEIL = 0x50FFFFFF;
  private final FiltereableEntity<T, E> entity;

  public FilterSlot(FiltereableEntity<T, E> entity) {
    this.entity = entity;
  }

  @Override
  protected void drawItemStack(IGUIContext context, ItemStack itemStack) {
    if (!(context instanceof GUIContext guiContext)) {
      return;
    }
    var filter = entity.toFilterRender();
    if (FilterRendererRegistry.hasRenderer(filter)) {
      try {
        FilterRendererRegistry.render(filter, guiContext, 0, 0);
        drawPlaceholderVeil(guiContext);
        return;
      } catch (Exception ignored) {}
    }
    DrawerHelperClient.drawItemStack(guiContext, itemStack, 0, 0, 0);
    drawPlaceholderVeil(guiContext);
  }

  private void drawPlaceholderVeil(GUIContext guiContext) {
    if (isHover()) return;
    DrawerHelperClient.drawSolidRect(guiContext, 0F, 0F, 16F, 16F, PLACEHOLDER_VEIL);
  }

  @Override
  protected void drawSlotOverlay(IGUIContext context) {
    if (context instanceof GUIContext cx) drawPlaceholderVeil(cx);
  }

  @Override
  protected void onHoverTooltips(UIEvent event) {
    Component filter = entity.getFilterComponent();
    if (filter == null) {
      super.onHoverTooltips(event);
      return;
    }
    event.hoverTooltips = HoverTooltips.create(filter);
  }
}
