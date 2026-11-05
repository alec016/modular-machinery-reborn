package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.api.uistate.UIStates;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.GridPaneUI;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRows;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRowsButton;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.AbstractFillTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.FillTabs;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.network.client.CUpdateUIStatePacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Optional;

public final class ItemBusUI {
  private ItemBusUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, TileItemBus bus) {
    var handler = bus.getInventory();
    int slots = Math.min(bus.getSize().slots, handler.size());
    int cols = Math.max(1, bus.getSize().cols);
    int gridX = ItemBusConfig.get().itemSlotXOffset.get();
    int gridY = ItemBusConfig.get().itemSlotYOffset.get();
    UIElement panel = MMRMachineUI.panelNineSlice(MMRMachineUI.TEX_BACKGROUND, 1, 1);
    placePane(panel, holder, bus, handler, slots, cols, gridX, gridY,
        GridPaneUI.rowsStateOf(holder));
    return MMRMachineUI.menu(panel, holder.player);
  }

  private static void placePane(
      UIElement panel, BlockUIMenuType.BlockUIHolder holder, TileItemBus bus,
      ItemHandler handler, int slots, int cols, int gridX, int gridY,
      IBlockUIState<VisibleRows> rowsState) {
    boolean scrolling = slots > GridPaneUI.SCROLL_THRESHOLD;
    var screenMax = GridPaneUI.screenMax(gridY);
    int defaultRows = GridPaneUI.defaultRows(slots, cols);
    var currentRows = UIStates.get(holder.player, holder.pos, rowsState);
    int visibleRows = UIStates.has(holder.player, holder.pos, rowsState) && scrolling
        ? currentRows.resolve(screenMax)
        : defaultRows;

    int width = GridPaneUI.scrolledWidth(gridX, cols);
    panel.lss("width", width - GridPaneUI.TAB_INSET)
        .lss("height", GridPaneUI.panelHeightAt(gridY, visibleRows));

    GridPaneUI.ScrolledGrid grid = null;
    Label inventoryLabel;
    InventorySlots playerInventory;
    if (scrolling) {
      grid = GridPaneUI.addScrolledGrid(panel, handler, slots, cols, visibleRows,
          gridX, gridY + 15, holder.pos, holder.player);
    } else {
      GridPaneUI.addPlainGrid(panel, handler, slots, cols, gridX, gridY + 15);
    }

    new AutoIoTab<>(panel, bus, width - GridPaneUI.TAB_INSET, 0, bus.getIoType().isInput());
    if (bus.getIoType().isInput()) {
      FillTabs.fillItemTab(
          panel,
          holder.player,
          bus,
          AbstractFillTab.LEFT_TAB_X,
          Optional.ofNullable(grid).map(_ -> VisibleRowsButton.TAB_SPOT_BOTTOM).orElse(0),
          bus::getInventory,
          null
      );
    }

    MMRMachineUI.captionLabel(panel, Component.translatable("modular_machinery_reborn.gui.title.item_bus"), 8, 6);

    int invLabelY = GridPaneUI.playerYAt(gridY, visibleRows) + 15;
    inventoryLabel = MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, invLabelY);
    playerInventory = MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, invLabelY + 12);

    if (scrolling && grid != null) {
      final GridPaneUI.ScrolledGrid pane = grid;
      final Label invLabel = inventoryLabel;
      final InventorySlots inventory = playerInventory;
      new VisibleRowsButton(panel, AbstractFillTab.LEFT_TAB_X, 0, next -> {
        int rows = next.resolve(screenMax);
        pane.resize(rows, rows * MMRMachineUI.SLOT);
        panel.lss("height", GridPaneUI.panelHeightAt(gridY, rows));
        GridPaneUI.recenter(panel, width - GridPaneUI.TAB_INSET, GridPaneUI.panelHeightAt(gridY, rows));
        int invY = GridPaneUI.playerYAt(gridY, rows);
        invLabel.lss("top", invY + 15);
        inventory.lss("top", invY + 15 + 12);
        UIStates.put(holder.player, holder.pos, rowsState, next);
        ClientPacketDistributor.sendToServer(new CUpdateUIStatePacket<>(holder.pos, next));
      }, screenMax, UIStates.get(holder.player, holder.pos, rowsState));
    }
  }
}
