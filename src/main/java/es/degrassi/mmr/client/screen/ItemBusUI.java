package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.network.chat.Component;

/**
 * LDLib2 replacement for the old {@code ItemBusContainer} + {@code ItemBusScreen} pair.
 *
 * <p>Binds the machine item handler directly, so slot syncing is done by LDLib2 instead of the old
 * {@code ContainerBase#stuffToSync} machinery. Buses bigger than 36 slots scroll: the pane shows the
 * first visible rows with the legacy small scroller on the right and the auto-IO tab just right of
 * it with an AE2-like padding. Buses within the limit show all rows without a scroller widget, but
 * keep the same panel outline the scrolled look produces (the reserved bar/tab space is padding).
 */
public final class ItemBusUI {

  /** Buses larger than this many slots scroll; 36 slots stay exactly as big as the legacy grid. */
  private static final int SCROLL_THRESHOLD = 36;
  /** The legacy {@code ItemBusScreen.MAX_VISIBLE_ROWS}. */
  private static final int MAX_VISIBLE_ROWS = 5;
  /** The 4px gap between the grid and the scroller bar (the legacy GuiSlotScroll track offset). */
  private static final int GRID_BAR_GAP = 4;
  /** The 6px AE2-like padding between the scroller bar and the auto-IO tab. */
  private static final int BAR_TAB_PAD = 6;
  /** The auto-IO tab sits at {@code width - 7}, hugging the panel's right edge. */
  static final int TAB_INSET = 7;
  /** Small 9-slice borders of the {@code small_scroller(_disabled)} sprites (L3 T4 R3 B4). */
  private static final int SCROLL_BORDER_LR = 3;
  private static final int SCROLL_BORDER_TB = 4;

  private ItemBusUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, TileItemBus bus) {
    var handler = bus.getInventory();
    int slots = Math.min(bus.getSize().slots, handler.size());
    int cols = Math.max(1, bus.getSize().cols);
    int rows = (slots + cols - 1) / cols;
    boolean scrolling = slots > SCROLL_THRESHOLD;
    // The visible pane shows 36/cols rows (exactly 36 cells) when scrolling, everything otherwise.
    int visibleRows = scrolling
        ? Math.clamp(SCROLL_THRESHOLD / cols, 2, MAX_VISIBLE_ROWS)
        : rows;

    int gridX = ItemBusConfig.get().itemSlotXOffset.get();
    int gridY = ItemBusConfig.get().itemSlotYOffset.get();
    // Both cases use the scrolled outline: the no-scroll panel keeps the exact same width the
    // scrolled look produces (grid + 4 gap + 7 bar + 6 tab pad + 7 tab inset), only without the
    // scroller widget itself, so switching between the two never shifts the panel or the tab.
    int width = scrolledWidth(gridX, cols);
    int playerY = gridY + visibleRows * MMRMachineUI.SLOT + 10;
    int hotbarY = playerY + 57;
    int height = hotbarY + MMRMachineUI.SLOT + 7;

    UIElement panel = MMRMachineUI.panelNineSlice(MMRMachineUI.TEX_BACKGROUND, width - 7, height + 15 + 12);
    if (scrolling) {
      addScrolledGrid(panel, handler, slots, cols, visibleRows, gridX, gridY + 15);
    } else {
      addPlainGrid(panel, handler, slots, cols, gridX, gridY + 15);
    }

    // Legacy tab spot; with the scrollbar lane it lands just right of the scroller, AE2-like.
    new AutoIoTab<>(panel, bus, width - TAB_INSET, 0, bus.getIoType().isInput());
    // Operator-only fill tab on the left edge (gamemaster/custom permission), input buses only.
    if (bus.getIoType().isInput()) {
      FillTab.fillItemTab(panel, holder.player, bus, FillTab.LEFT_TAB_X, 0, bus::getInventory, null);
    }

    // Vanilla-style title fills the header above the gauges, the legacy (8, 6) caption spot.
    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.item_bus"), 8, 6);

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, playerY + 15);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, playerY + 15 + 12);
    return MMRMachineUI.menu(panel, holder.player);
  }

  /** The legacy unscrolled grid: every slot visible, frames starting at {@code (gridX-1, gridY-1)}. */
  static void addPlainGrid(
      UIElement panel, ItemHandler handler, int slots, int cols, int gridX, int gridY) {
    for (int i = 0; i < slots; i++) {
      MMRMachineUI.add(panel, styledSlot(handler, i),
          gridX - 1 + (i % cols) * MMRMachineUI.SLOT,
          gridY - 1 + (i / cols) * MMRMachineUI.SLOT);
    }
  }

  /**
   * The scrolling slot pane: the grid lives in the scroller's content, clipped by the viewport; the
   * bar uses the legacy small scroller sprites and leaves {@link #BAR_TAB_PAD} pixels for the
   * auto-IO tab on its right.
   */
  static void addScrolledGrid(
      UIElement panel, ItemHandler handler, int slots, int cols, int visibleRows,
      int gridX, int gridY) {
    ScrollerView scroller = new ScrollerView();
    // The element starts 1px up-left so the slot frames (their 18x18 boxes) stay fully inside the
    // clipped viewport; the port then spans exactly cols*18 x visibleRows*18.
    scroller.lss("width", cols * MMRMachineUI.SLOT + GRID_BAR_GAP + 7)
        .lss("height", visibleRows * MMRMachineUI.SLOT);
    MMRMachineUI.add(panel, scroller, gridX - 1, gridY - 1);
    scroller.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO);
    // No default viewport chrome; the MMR small scroller sprites replace the theme ones.
    scroller.viewPort.layout(layout -> layout.paddingAll(0));
    scroller.viewPort.style(style -> style.background(IGuiTexture.EMPTY));
    scroller.verticalScroller.lss("margin-left", GRID_BAR_GAP).lss("width", 7);
    scroller.verticalScroller.scrollContainer.style(style ->
        style.background(MMRMachineUI.spriteNineSlice(
            MMRMachineUI.TEX_SMALL_SCROLLER_DISABLED, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
            SCROLL_BORDER_LR, SCROLL_BORDER_TB)));
    scroller.verticalScroller.scrollBar.buttonStyle(style -> {
      var thumb = MMRMachineUI.spriteNineSlice(
          MMRMachineUI.TEX_SMALL_SCROLLER, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
          SCROLL_BORDER_LR, SCROLL_BORDER_TB);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    for (int i = 0; i < slots; i++) {
      scroller.viewContainer.addChild(MMRMachineUI.at(styledSlot(handler, i),
          (i % cols) * MMRMachineUI.SLOT,
          (i / cols) * MMRMachineUI.SLOT));
    }
  }

  /**
   * {@code scrolledWidth}: panel edge so the auto-IO tab ({@code width - 7}) sits
   * {@link #BAR_TAB_PAD} right of the scroller bar, just right of the scroll area, AE2-like.
   */
  static int scrolledWidth(int gridX, int cols) {
    return gridX - 1 // scroller origin
        + cols * MMRMachineUI.SLOT // scroll port
        + GRID_BAR_GAP + 7 // scroller bar
        + BAR_TAB_PAD + TAB_INSET;
  }

  /**
   * One bus slot with the same 18x18 legacy frame the panel slots use, parented by the caller.
   *
   * <p>Binds through {@link MMRBusSlot} so the manual actions keep the legacy semantics (always
   * takeable from input buses, placeable into output buses) instead of the handler's auto-IO
   * throughput limits, and so slot writes are direct replacements rather than re-inserts.
   */
  static ItemSlot styledSlot(ItemHandler handler, int index) {
    ItemSlot slot = new ItemSlot().bind(new MMRBusSlot(handler, index));
    slot.layout(layout -> {
      layout.width(MMRMachineUI.SLOT);
      layout.height(MMRMachineUI.SLOT);
      layout.paddingAll(1);
      layout.marginAll(0);
    });
    slot.style(style -> style.background(MMRMachineUI.slotTexture()));
    slot.slotStyle(s -> s.hoverOverlay(MMRMachineUI.hoverOverlayTexture()));
    return slot;
  }
}
