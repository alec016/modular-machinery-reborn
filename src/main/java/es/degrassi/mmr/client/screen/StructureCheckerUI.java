package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import net.minecraft.network.chat.Component;

public final class StructureCheckerUI {
  private StructureCheckerUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, StructureCheckerEntity hatch) {
    UIElement panel = InfoUIs.panel(3);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.structure_checker"));

    InfoUIs.line(panel, 0,
        () -> {
          var machine = hatch.getBoundMachine();
          return machine == null ? null : UITexts.boundMachine.text(machine.getName()).getString();
        }, UITexts.noBoundMachine.text().getString());
    InfoUIs.line(panel, 1, () -> InfoUIs.positions(hatch), null);
    InfoUIs.line(panel, 2,
        () -> {
          var controller = hatch.getController();
          if (controller == null) {
            return null;
          }
          return UITexts.machineStatus
              .text(Component.translatable(controller.getStatus().getTranslationKey()))
              .getString();
        }, null);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
