package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.gauges.FuelGauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import net.minecraft.network.chat.Component;

/**
 * LDLib2 replacement for the old {@code FuelTankContainer} + {@code FuelTankScreen} pair.
 *
 * <p>176x166 nine-slice panel ({@code sprites/background.png} — the legacy screen reused the
 * {@code guiexperience.png} sprite, but the shared nine-slice background is now the harness
 * standard). The 14x14 fuel gauge is the legacy {@code FuelWidget} ({@code empty_fuel.png} +
 * bottom-anchored {@code filled_fuel.png}) with the {@code tooltip.fuel_tank.tank} tooltip; the
 * slot keeps the legacy relative spacing — gauge, 12px gap — and the group is horizontally
 * centered. Both the machine title at (8, 6) and the Inventory caption are kept.
 */
public final class FuelTankUI {

  private FuelTankUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FuelTankEntity tank) {
    var fuel = tank.getFuelHandler();

    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator (fuel tanks are always inputs).
    new AutoIoTab<>(panel, tank, MMRMachineUI.PANEL_WIDTH, 0, true);
    // Operator-only fill tab on the left edge (gamemaster/custom permission): fuel items.
    // The operator fill tab only lists burnable stacks — the same
    // {@code level.fuelValues().isFuel} predicate the tank's own filter enforces.
    FillTab.fillItemTab(panel, holder.player, tank, FillTab.LEFT_TAB_X, 0, () -> tank.getInventory(),
        stack -> tank.getLevel() != null && tank.getLevel().fuelValues().isFuel(stack));

    // Legacy FuelWidget spot (16 + leftPos, 10 + 61/2 - 8 + topPos) => (16, 32).
    // Group geometry: gauge 14 wide, 12px gap, then the 18px slot frame => 44 wide, centered.
    int left = MMRMachineUI.centeredLeft(44);

    MMRMachineUI.add(panel, new FuelGauge(fuel::getFuel, fuel::getMaxFuel)
        .lss("width", 14)
        .lss("height", 14), left, 32);

    MMRMachineUI.slotAt(panel, left + 14 + 13, 32).bind(tank.getInventory(), 0);

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.fuel_tank"), 8, 6);
    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
