package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import net.minecraft.network.chat.Component;

/**
 * LDLib2 replacement for the old {@code FuelTankContainer} + {@code FuelTankScreen} pair.
 *
 * <p>Parity with 1.21.1 — including the legacy quirk that the fuel tank screen reused the
 * {@code guiexperience.png} panel sprite. The 14x14 fuel gauge at (16, 32) is the legacy {@code
 * FuelWidget} ({@code empty_fuel.png} + bottom-anchored {@code filled_fuel.png}) with the
 * {@code tooltip.fuel_tank.tank} tooltip. Unlike the other harness screens this one kept the vanilla
 * {@code renderLabels}: the machine title at (8, 6) <em>and</em> the Inventory caption.
 */
public final class FuelTankUI {

  private FuelTankUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FuelTankEntity tank) {
    var fuel = tank.getFuelHandler();

    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUIEXPERIENCE, MMRMachineUI.PANEL_WIDTH, 166);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator (fuel tanks are always inputs).
    MMRTabs.autoIoTab(panel, tank, MMRMachineUI.PANEL_WIDTH, 0, true);

    // The legacy FuelWidget spot: (16 + leftPos, 10 + 61/2 - 8 + topPos) => (16, 32).
    MMRMachineUI.add(panel, MMRGauges.fuel(fuel::getFuel, fuel::getMaxFuel)
        .lss("width", 14)
        .lss("height", 14), 16, 32);

    MMRMachineUI.slotAt(panel, 43, 32).bind(tank.getInventory(), 0);

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.fuel_tank"), 8, 6);
    MMRMachineUI.inventoryLabel(panel, 8, 72);
    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
