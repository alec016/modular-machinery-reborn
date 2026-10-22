package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
import net.minecraft.network.chat.Component;

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

    UIElement panel = MMRMachineUI.panelNineSlice(MMRMachineUI.TEX_BACKGROUND, width, height + 15 + 12);
    ItemBusUI.addPlainGrid(panel, handler, slots, cols, gridX, gridY + 15);

    // Vanilla-style title fills the header above the gauges, the legacy (8, 6) caption spot.
    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.durability_hatch"), 8, 6);

    // The legacy durability screen kept only the auto-input tab, 2px off the panel edge. The
    // operator fill tab joins it on the left: damageable items only, with the extra durability
    // (remaining) field/slider pair.
    new AutoIoTab<>(panel, hatch, width, 0, true);
    FillTab.fillDurabilityTab(panel, holder.player, hatch, FillTab.LEFT_TAB_X, 0, hatch::getInventory);
    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, playerY + 15);
    MMRMachineUI.playerInventory(panel, 8, playerY + 15 + 12);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
