package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.container.MMRBusSlot;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.gauges.FuelGauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import net.minecraft.network.chat.Component;

public final class FuelTankUI {

  private FuelTankUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FuelTankEntity tank) {
    var fuel = tank.getFuelHandler();

    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT);

    new AutoIoTab<>(panel, tank, MMRMachineUI.PANEL_WIDTH, 0, true);
    FillTab.fillItemTab(panel, holder.player, tank, FillTab.LEFT_TAB_X, 0, tank::getInventory,
        stack -> tank.getLevel() != null && tank.getLevel().fuelValues().isFuel(stack));

    int left = MMRMachineUI.centeredLeft(64);

    MMRMachineUI.add(panel, new FuelGauge(fuel::getFuel, fuel::getMaxFuel)
        .lss("width", 14)
        .lss("height", 14), left, 32);

    MMRMachineUI.inputSlotAt(panel, left + 14 + 12, 32)
        .bind(new MMRBusSlot(tank.getInventory(), 0));
    MMRMachineUI.outputSlotAt(panel, left + 14 + 12 + 20, 32)
        .bind(new MMRBusSlot(tank.getInventory(), 1));

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.fuel_tank"), 8, 6);
    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
