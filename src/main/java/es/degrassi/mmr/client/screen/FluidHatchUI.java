package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;

/**
 * LDLib2 replacement for the old {@code FluidHatchContainer} + {@code FluidHatchScreen} pair.
 *
 * <p>Parity with 1.21.1: 176x166 {@code guibar.png} panel with one 20x61 fluid gauge per tank at
 * (15 + 20i, 10) — the legacy tanks were pure display areas ({@code FluidTankWidget}); the item
 * interactions run through the capability slot (43, 32) and the filter slot (63, 32).
 */
public final class FluidHatchUI {

  private FluidHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FluidTankEntity hatch) {
    var tank = hatch.getTank();
    int tanks = Math.max(1, tank.size());

    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUIBAR, MMRMachineUI.PANEL_WIDTH, 166);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator for this hatch's io orientation.
    MMRTabs.autoIoTab(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    for (int i = 0; i < tanks; i++) {
      MMRMachineUI.add(panel, MMRGauges.fluidTank(tank, i)
          .lss("width", MMRGauges.TANK_WIDTH)
          .lss("height", MMRGauges.TANK_HEIGHT), 15 + i * 20, 10);
    }

    MMRMachineUI.slotAt(panel, 43, 32).bind(hatch.getCapabilityInventory(), 0);
    MMRMachineUI.slotAt(panel, 63, 32).bind(hatch.getFilterInventory(), 0);

    MMRMachineUI.inventoryLabel(panel, 8, 72);
    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
