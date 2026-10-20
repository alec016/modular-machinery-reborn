package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;

/**
 * LDLib2 replacement for the old {@code ItemDurabilityContainer} + {@code ItemDurabilityScreen} pair.
 *
 * <p>The durability hatch is an item grid just like the item buses, but it never scrolls: the legacy
 * screen always showed every row and kept the panel at least vanilla-wide. The single auto-input
 * tab hangs 2px off the panel's right edge, exactly where the old {@code TabGroupWidget} sat.
 */
public final class ItemDurabilityUI {

  private ItemDurabilityUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, DurabilityHatchEntity hatch) {
    var handler = hatch.getInventory();
    int slots = Math.min(hatch.getSize().slots, handler.size());
    int cols = Math.max(1, hatch.getSize().cols);
    int rows = (slots + cols - 1) / cols;

    int gridX = ItemBusConfig.get().itemSlotXOffset.get();
    int gridY = ItemBusConfig.get().itemSlotYOffset.get();
    int width = Math.max(cols * MMRMachineUI.SLOT + 16, MMRMachineUI.PANEL_WIDTH + 2);
    int playerY = gridY + rows * MMRMachineUI.SLOT + 10;
    int hotbarY = playerY + 57;
    int height = hotbarY + MMRMachineUI.SLOT + 7;

    UIElement panel = MMRMachineUI.panelNineSlice(MMRMachineUI.TEX_BACKGROUND, width, height);
    ItemBusUI.addPlainGrid(panel, handler, slots, cols, gridX, gridY);

    // The legacy durability screen kept only the auto-input tab, 2px off the panel edge.
    MMRTabs.autoIoTab(panel, hatch, width, 0, true);
    MMRMachineUI.playerInventory(panel, 8, playerY);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
