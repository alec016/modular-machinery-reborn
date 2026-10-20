package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;

/**
 * LDLib2 replacement for the old {@code EnergyHatchContainer} + {@code EnergyHatchScreen} pair.
 *
 * <p>Parity with 1.21.1: 176x166 {@code guibar.png} panel, the 20x61 energy fill cropped from the
 * very same texture (source x = 196, bottom anchored) at (15, 10) with the legacy charge tooltip,
 * and the single capability slot at (43, 32) on the classic {@code base_slot} frame.
 */
public final class EnergyHatchUI {

  private EnergyHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, EnergyHatchEntity hatch) {
    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUIBAR, MMRMachineUI.PANEL_WIDTH, 166);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator for this hatch's io orientation.
    MMRTabs.autoIoTab(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    // Legacy renderBg cropped the fill sprite out of the same guibar.png (u = 196), bottom-anchored.
    MMRMachineUI.add(panel, MMRGauges.energyColumn(hatch::getCurrentEnergy, hatch::getMaxEnergy)
        .lss("width", MMRGauges.TANK_WIDTH)
        .lss("height", MMRGauges.TANK_HEIGHT), 15, 10);

    MMRMachineUI.slotAt(panel, 43, 32).bind(hatch.getCapabilityInventory(), 0);

    MMRMachineUI.inventoryLabel(panel, 8, 72);
    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
