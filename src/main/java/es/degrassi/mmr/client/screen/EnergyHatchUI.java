package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.container.MMRBusSlot;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.gauges.EnergyColumnGauge;
import es.degrassi.mmr.client.screen.widget.gauges.Gauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.AbstractFillTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.FillTabs;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import net.minecraft.network.chat.Component;

public final class EnergyHatchUI {

  private EnergyHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, EnergyHatchEntity hatch) {
    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT + 7);

    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    if (hatch.getMode().isInput()) {
      FillTabs.fillEnergyTab(panel, holder.player, hatch, AbstractFillTab.LEFT_TAB_X, 0,
          () -> Math.max(0L, hatch.getEnergyHandler().getCapacityAsLong()
              - hatch.getEnergyHandler().getAmountAsLong()));
    }

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.energy_hatch"), 8, 6);

    int left = MMRMachineUI.centeredLeft(Gauge.TANK_WIDTH + 45);

    MMRMachineUI.add(panel, new EnergyColumnGauge(hatch.getEnergyHandler()::getAmountAsLong, hatch.getEnergyHandler()::getCapacityAsLong)
        .lss("width", Gauge.TANK_WIDTH)
        .lss("height", Gauge.TANK_HEIGHT), left, 10 + 7);

    MMRMachineUI.inputSlotAt(panel, left + Gauge.TANK_WIDTH + 8, 32 + 7)
        .bind(new MMRBusSlot(hatch.getCapabilityInventory(), 0));
    MMRMachineUI.outputSlotAt(panel, left + Gauge.TANK_WIDTH + 28, 32 + 7)
        .bind(new MMRBusSlot(hatch.getCapabilityInventory(), 1));

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y + 7);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y + 7);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
