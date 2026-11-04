package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.DimensionalDetectorEntity;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public final class DimensionalDetectorUI {
  private DimensionalDetectorUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, DimensionalDetectorEntity hatch) {
    UIElement panel = InfoUIs.panel(2);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.dimension_detector"));

    InfoUIs.line(panel, 0,
        () -> {
          var level = hatch.getLevel();
          if (level == null) {
            return null;
          }
          var key = level.dimension().identifier();
          String id = "dimension." + key.getNamespace() + "." + key.getPath();
          return Language.getInstance().has(id)
              ? Component.translatable(id).getString()
              : null;
        }, null);
    InfoUIs.line(panel, 1,
        () -> hatch.getLevel() == null ? null
            : UITexts.dimensionId.text(hatch.getLevel().dimension().identifier().toString()).getString(), null);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
