package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.entity.base.EntityBaseEntity;
import net.minecraft.network.chat.Component;

public final class EntityHatchUI {
  private EntityHatchUI() {}

  public static ModularUI create(
      BlockUIMenuType.BlockUIHolder holder, EntityBaseEntity hatch, Component title) {
    var actions = hatch.getValidActions();
    int lines = actions.length + 4;
    UIElement panel = InfoUIs.panel(lines);
    InfoUIs.caption(panel, title);

    MMRMachineUI.label(panel,
        UITexts.mode.text(hatch.getMode().text()),
        InfoUIs.LINE_X, InfoUIs.lineY(0));
    MMRMachineUI.label(panel, UITexts.acceptedActions.text(),
        InfoUIs.LINE_X, InfoUIs.lineY(1));
    for (int i = 0; i < actions.length; i++) {
      MMRMachineUI.label(panel, actionText(actions[i]), InfoUIs.LINE_X, InfoUIs.lineY(2 + i));
    }

    int liveIndex = 2 + actions.length;
    InfoUIs.line(panel, liveIndex, () -> InfoUIs.positions(hatch), null);
    InfoUIs.line(panel, liveIndex + 1, () -> {
      var controller = hatch.getController();
      if (controller == null) {
        return UITexts.recipeRadiusNote.text().getString();
      }
      return UITexts.machineStatus
          .text(Component.translatable(controller.getStatus().getTranslationKey()))
          .getString();
    }, null);
    return MMRMachineUI.menu(panel, holder.player);
  }

  private static Component actionText(RequirementEntity.Action action) {
    return switch (action) {
      case CHECK_AMOUNT -> UITexts.actionCheckAmount.text();
      case CHECK_HEALTH -> UITexts.actionCheckHealth.text();
      case CONSUME_HEALTH -> UITexts.actionConsumeHealth.text();
      case ADD_HEALTH -> UITexts.actionAddHealth.text();
      case SPAWN -> UITexts.actionSpawn.text();
      case KILL -> UITexts.actionKill.text();
    };
  }
}
