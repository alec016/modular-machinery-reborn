package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.gauges.FluidTankGauge;
import es.degrassi.mmr.client.screen.widget.gauges.Gauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import net.minecraft.network.chat.Component;

/**
 * LDLib2 replacement for the old {@code FluidHatchContainer} + {@code FluidHatchScreen} pair.
 *
 * <p>176x166 nine-slice panel ({@code sprites/background.png}) with one 20x61 fluid gauge per tank
 * (black-backed, like the legacy guibar tank box), the capability slot and the filter slot kept at
 * the legacy relative spacings — gauge, 7px gap, slot pair 20px apart — and the whole group
 * horizontally centered in the panel. The item interactions run through the capability and filter
 * slots; the gauges remain pure display areas ({@code FluidTankWidget}).
 */
public final class FluidHatchUI {

  private FluidHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, FluidTankEntity hatch) {
    var tank = hatch.getTank();
    int tanks = Math.max(1, tank.size());

    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT + 7);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator for this hatch's io orientation.
    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());
    // Operator-only fill tab on the left edge (gamemaster/custom permission), input hatches only.
    if (hatch.getMode().isInput()) {
      FillTab.fillFluidTab(panel, holder.player, hatch, FillTab.LEFT_TAB_X, 0, () -> hatch.getTank());
    }

    // Vanilla-style title fills the header above the gauges, the legacy (8, 6) caption spot.
    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.fluid_hatch"), 8, 6);

    // Legacy row geometry: gauges at a 20px pitch, then the capability and filter slots 7px behind
    // the last gauge and 20px apart. The whole group is horizontally centered in the panel.
    int left = MMRMachineUI.centeredLeft(tanks * Gauge.TANK_WIDTH + 45);

    for (int i = 0; i < tanks; i++) {
      MMRMachineUI.add(panel, new FluidTankGauge(tank, i)
          .lss("width", Gauge.TANK_WIDTH)
          .lss("height", Gauge.TANK_HEIGHT), left + i * 20, 10 + 7);
    }

    MMRMachineUI.slotAt(panel, left + tanks * 20 + 8, 32 + 7).bind(hatch.getCapabilityInventory(), 0);
    MMRMachineUI.slotAt(panel, left + tanks * 20 + 28, 32 + 7).bind(hatch.getFilterInventory(), 0);

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y + 7);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y + 7);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
