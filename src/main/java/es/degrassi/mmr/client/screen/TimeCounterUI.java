package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.TimeCounterEntity;
import net.minecraft.network.chat.Component;

public final class TimeCounterUI {
  private TimeCounterUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, TimeCounterEntity hatch) {
    UIElement panel = InfoUIs.panel(3);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.time_counter"));

    InfoUIs.line(panel, 0,
        () -> {
          var level = hatch.getLevel();
          if (level == null) {
            return null;
          }
          long dayTime = level.getOverworldClockTime();
          return UITexts.dayTime.text(dayTime % 24000L).getString();
        }, null);
    MMRMachineUI.label(panel, UITexts.timeNote.text(), InfoUIs.LINE_X, InfoUIs.lineY(2));
    return MMRMachineUI.menu(panel, holder.player);
  }
}
