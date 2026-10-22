package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.gauges.EnergyColumnGauge;
import es.degrassi.mmr.client.screen.widget.gauges.Gauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import net.minecraft.network.chat.Component;

/**
 * LDLib2 replacement for the old {@code EnergyHatchContainer} + {@code EnergyHatchScreen} pair.
 *
 * <p>176x166 nine-slice panel ({@code sprites/background.png}) with the 20x61 energy fill cropped
 * from the legacy {@code guibar.png} (source x = 196, bottom anchored) over its solid-black backing,
 * and the single capability slot kept at the legacy spacing — gauge, 7px gap — with the whole group
 * horizontally centered in the panel.
 */
public final class EnergyHatchUI {

  private EnergyHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, EnergyHatchEntity hatch) {
    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT + 7);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator for this hatch's io orientation.
    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    // Operator-only fill tab, hanging left: pours energy straight into the buffer (input hatches).
    if (hatch.getMode().isInput()) {
      FillTab.fillEnergyTab(panel, holder.player, hatch, FillTab.LEFT_TAB_X, 0,
          () -> (int) Math.clamp(hatch.getEnergyHandler().getCapacityAsLong() - hatch.getEnergyHandler().getAmountAsLong(), 0L,
          Integer.MAX_VALUE));
    }

    // Legacy blue-recipe-tab block gone; vanilla-style title fills the old (8, 6) spot.
    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.energy_hatch"), 8, 6);

    // Legacy renderBg cropped the fill sprite out of the same guibar.png (u = 196), bottom-anchored.
    // Group geometry: gauge 20 wide, 7px gap, then the 18px slot frame => 45 wide, centered.
    int left = MMRMachineUI.centeredLeft(Gauge.TANK_WIDTH + 25);

    MMRMachineUI.add(panel, new EnergyColumnGauge(hatch.getEnergyHandler()::getAmountAsLong, hatch.getEnergyHandler()::getCapacityAsLong)
        .lss("width", Gauge.TANK_WIDTH)
        .lss("height", Gauge.TANK_HEIGHT), left, 10 + 7);

    MMRMachineUI.slotAt(panel, left + Gauge.TANK_WIDTH + 8, 32 + 7).bind(hatch.getCapabilityInventory(), 0);

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y + 7);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y + 7);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
