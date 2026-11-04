package es.degrassi.mmr.client.screen.widget.gauges;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.experiencelib.util.ExperienceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.LongSupplier;

public final class ExperienceBarGauge extends Gauge {

  private final int width;
  private final LongSupplier amount;
  private final LongSupplier capacity;

  public ExperienceBarGauge(int width, LongSupplier amount, LongSupplier capacity) {
    this.width = width;
    this.amount = amount;
    this.capacity = capacity;
  }

  @Override
  protected List<Component> tooltipLines() {
    long value = amount.getAsLong();
    long cap = capacity.getAsLong();
    String literal = ExperienceUtils.format(value);
    String capacityLiteral = ExperienceUtils.format(cap) + "XP";
    String level = ExperienceUtils.getLevelFromXp(value) + "";
    String capacityLevel = ExperienceUtils.getLevelFromXp(cap) + " levels";
    return List.of(
        GaugesTexts.experienceTooltip.text(literal, capacityLiteral).withStyle(ChatFormatting.GRAY),
        GaugesTexts.experienceTooltip.text(level, capacityLevel).withStyle(ChatFormatting.GRAY));
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    GUIContext gui = clientContext(context);
    if (gui == null) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    long value = amount.getAsLong();
    long level = ExperienceUtils.getLevelFromXp(value);
    String levels = Long.toString(level);
    DrawerHelperClient.drawStringSized(gui, levels, x + width / 2f, y, 0xFF80FF20, true, 1f, true);
    DrawerHelperClient.drawSolidRect(gui, x, y + 9, width, 3, 0xFF000000);
    long xpDiff = value - ExperienceUtils.getXpFromLevel(level);
    if (xpDiff > 0) {
      double percent = (double) xpDiff / ExperienceUtils.getXpNeededForNextLevel(level);
      int fill = Math.max((int) Math.ceil(width * percent) - 2, 0);
      DrawerHelperClient.drawSolidRect(gui, x + 1, y + 10, fill, 1, 0xFF80FF20);
    }
  }
}
