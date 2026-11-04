package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.CommandExecutionerEntity;
import net.minecraft.network.chat.Component;

public final class CommandExecutionerUI {
  private CommandExecutionerUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, CommandExecutionerEntity hatch) {
    UIElement panel = InfoUIs.panel(7);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.command_executioner"));

    InfoUIs.line(panel, 0, () -> InfoUIs.positions(hatch), null);
    InfoUIs.line(panel, 1,
        () -> {
          var controller = hatch.getController();
          if (controller == null) {
            return null;
          }
          return UITexts.machineStatus
              .text(controller.getStatus().text())
              .getString();
        }, null);
    InfoUIs.line(panel, 2, UITexts.commandNote.text()::getString, null, 0xffffffff, true);
    InfoUIs.line(panel, 5, UITexts.commandSourceNote.text()::getString, null, 0xffffffff, true);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
