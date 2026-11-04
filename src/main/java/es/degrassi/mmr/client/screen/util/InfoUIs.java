package es.degrassi.mmr.client.screen.util;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.client.screen.UITexts;
import es.degrassi.mmr.client.screen.widget.LiveText;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.entity.renderer.HatchAreaRenderer;
import lombok.experimental.UtilityClass;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public final class InfoUIs {
  public static final int LINE_STEP = 12;
  public static final int LINE_X = 8;
  public static final int FIRST_LINE_Y = 25;
  public static final int AREA_BUTTON_X = MMRMachineUI.PANEL_WIDTH - 24;

  public static UIElement panel(int lineCount) {
    int height = Math.max(24, FIRST_LINE_Y + lineCount * LINE_STEP + 10);
    return MMRMachineUI.panelNineSlice(MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, height);
  }

  public static void caption(UIElement panel, Component title) {
    MMRMachineUI.captionLabel(panel, title, 8, 6);
  }

  public static int lineY(int index) {
    return FIRST_LINE_Y + index * LINE_STEP;
  }

  public static LiveText line(
      UIElement panel, int index, Supplier<@Nullable String> supplier, @Nullable String whenEmpty) {
    return new LiveText(panel, LINE_X, lineY(index), MMRMachineUI.PANEL_WIDTH - 16, supplier, whenEmpty);
  }
  public static LiveText line(
      UIElement panel, int index, Supplier<@Nullable String> supplier, @Nullable String whenEmpty, int color) {
    return new LiveText(panel, LINE_X, lineY(index), MMRMachineUI.PANEL_WIDTH - 16, supplier, whenEmpty, color, false);
  }
  public static LiveText line(UIElement panel, int index, Supplier<@Nullable String> supplier, @Nullable String whenEmpty, int color, boolean dropShadow) {
    return new LiveText(panel, LINE_X, lineY(index), MMRMachineUI.PANEL_WIDTH - 16, supplier, whenEmpty, color, dropShadow);
  }

  public static String positions(@Nullable ControllerAccessible entity) {
    if (entity == null) {
      return UITexts.notBound.text().getString();
    }
    var pos = entity.getControllerPos();
    if (pos == null) {
      return UITexts.notBound.text().getString();
    }
    return UITexts.boundController.text(pos.getX(), pos.getY(), pos.getZ()).getString();
  }

  /**
   * Area preview button. The preview is owned by the hatch's global position, so the same hatch
   * always shows the same preview (across screens and dimensions) and several hatches can be
   * previewed at the same time.
   */
  public static void areaButton(UIElement panel, Object owner, Supplier<AABB> area) {
    IconButton.iconButton(panel, Icon.ENTER, AREA_BUTTON_X, 20, event -> {
      event.stopPropagation();
      if (HatchAreaRenderer.isActive(owner)) {
        HatchAreaRenderer.hide(owner);
      } else {
        HatchAreaRenderer.show(owner, area.get());
      }
    }, () -> List.of(UITexts.areaToggle.text()));
  }
}
