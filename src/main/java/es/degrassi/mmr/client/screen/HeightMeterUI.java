package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.HeightMeterEntity;
import net.minecraft.network.chat.Component;

public final class HeightMeterUI {
  private HeightMeterUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, HeightMeterEntity hatch) {
    UIElement panel = InfoUIs.panel(5);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.height_meter"));

    InfoUIs.line(panel, 0,
        () -> UITexts.heightText.text(hatch.getBlockPos().getY()).getString(), null);
    MMRMachineUI.label(panel, UITexts.heightNote.text(), InfoUIs.LINE_X, InfoUIs.lineY(1));
    InfoUIs.line(panel, 2, () -> InfoUIs.positions(hatch), null);
    InfoUIs.line(panel, 4,
        () -> {
          var controller = hatch.getController();
          if (controller == null) {
            return null;
          }
          return UITexts.machineStatus
              .text(controller.getStatus().text())
              .getString();
        }, null);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
