package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.container.MMRBusSlot;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.gauges.FluidTankGauge;
import es.degrassi.mmr.client.screen.widget.gauges.Gauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import net.minecraft.network.chat.Component;

public final class FluidHatchUI {

  private FluidHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FluidTankEntity hatch) {
    var tank = hatch.getTank();
    int tanks = Math.max(1, tank.size());

    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT + 7);

    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());
    if (hatch.getMode().isInput()) {
      FillTab.fillFluidTab(panel, holder.player, hatch, FillTab.LEFT_TAB_X, 0, hatch::getTank);
    }

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.fluid_hatch"), 8, 6);

    int left = MMRMachineUI.centeredLeft(tanks * Gauge.TANK_WIDTH + 65);

    for (int i = 0; i < tanks; i++) {
      MMRMachineUI.add(panel, new FluidTankGauge(tank, i)
          .lss("width", Gauge.TANK_WIDTH)
          .lss("height", Gauge.TANK_HEIGHT), left + i * 20, 10 + 7);
    }

    MMRMachineUI.inputSlotAt(panel, left + tanks * 20 + 8, 32 + 7)
        .bind(new MMRBusSlot(hatch.getCapabilityInventory(), 0));
    MMRMachineUI.outputSlotAt(panel, left + tanks * 20 + 28, 32 + 7)
        .bind(new MMRBusSlot(hatch.getCapabilityInventory(), 1));
    MMRMachineUI.filterSlotAt(hatch, panel, left + tanks * 20 + 48, 32 + 7);

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y + 7);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y + 7);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
