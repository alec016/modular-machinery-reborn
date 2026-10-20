package es.degrassi.mmr.common.integration.theoneprobe.element;

import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CustomProgressRenderer {
  private CustomProgressRenderer() {}
  private static final Identifier ICONS = Identifier.fromNamespaceAndPath("theoneprobe", "textures/gui/icons.png");
  public static void render(IProgressStyle style, float current, float max, GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
    RenderHelper.drawThickBeveledBox(graphics, x, y, x + w, y + h, 1, style.getBorderColor(), style.getBorderColor(), style.getBackgroundColor());
    if (current > 0L && max > 0L) {
      int dx = (int)Math.min(current * (long)(w - 2) / max, (long)(w - 2));
      if (style.getFilledColor() == style.getAlternatefilledColor()) {
        if (dx > 0) {
          RenderHelper.drawThickBeveledBox(graphics, x + 1, y + 1, x + dx + 1, y + h - 1, 1, style.getFilledColor(), style.getFilledColor(), style.getFilledColor());
        }
      } else {
        for(int xx = x + 1; xx < x + dx + 1; ++xx) {
          int color = (xx & 1) == 0 ? style.getFilledColor() : style.getAlternatefilledColor();
          RenderHelper.drawVerticalLine(graphics, xx, y + 1, y + h - 1, color);
        }
      }
    }

    renderText(graphics, x, y, w, current, max, style);
  }

  private static void renderText(GuiGraphicsExtractor graphics, int x, int y, int w, float current, float max, IProgressStyle style) {
    if (style.isShowText()) {
      Minecraft mc = Minecraft.getInstance();
      Font render = mc.font;
      Component s = style.getPrefixComp().copy().append(CustomProgress.format(current, max, style.getNumberFormat(),
          style.getSuffixComp()));
      int textWidth = render.width(s.getVisualOrderText());
      switch (style.getAlignment()) {
        case ALIGN_BOTTOMRIGHT -> RenderHelper.renderText(mc, graphics, x + w - 3 - textWidth, y + 2, s);
        case ALIGN_CENTER -> RenderHelper.renderText(mc, graphics, x + w / 2 - textWidth / 2, y + 2, s);
        case ALIGN_TOPLEFT -> RenderHelper.renderText(mc, graphics, x + 3, y + 2, s);
      }
    }
  }
}
