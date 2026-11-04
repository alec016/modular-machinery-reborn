package es.degrassi.mmr.client.screen.widget.buttons;

import com.lowdragmc.lowdraglib2.client.font.LDFonts;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class ScrollingTextButton {

  private static final int HOVER_DELAY = 10;
  private static final float STEP_PER_TICK = 2f;
  private static final float GAP = 6f;

  private ScrollingTextButton() {}

  public static Button scrollingButton(UIElement parent, Component text, int x, int y, int width, int height,
                                       @Nullable Consumer<UIEvent> onClick) {
    Button button = new Button();
    button.noText();
    button.layout(layout -> {
      layout.width(width);
      layout.height(height);
      layout.gapAll(0);
    });
    button.buttonStyle(style -> {
      style.baseTexture(SpriteButton.buttonBase());
      style.hoverTexture(SpriteButton.buttonHovered());
      style.pressedTexture(SpriteButton.buttonHovered());
    });
    ScrollingLabel label = new ScrollingLabel(text);
    label.layout(layout -> {
      layout.widthPercent(100);
      layout.heightPercent(100);
      layout.paddingAll(0);
      layout.gapAll(0);
    });
    button.addChild(label);
    if (onClick != null) {
      button.setOnClick(onClick::accept);
    }
    return MMRMachineUI.add(parent, button, x, y);
  }

  private static final class ScrollingLabel extends UIElement {
    private final Component text;
    private float loopSpan;
    private float offset;
    private int hoverTicks = -1;

    private ScrollingLabel(Component text) {
      this.text = text;
      addEventListener(UIEvents.MOUSE_ENTER, event -> {
        hoverTicks = 0;
        offset = 0;
      });
      addEventListener(UIEvents.MOUSE_LEAVE, event -> {
        hoverTicks = -1;
        offset = 0;
      });
      addEventListener(UIEvents.TICK, event -> {
        if (hoverTicks < 0 || loopSpan <= 0) {
          return;
        }
        if (++hoverTicks < HOVER_DELAY) {
          return;
        }
        offset = (offset + STEP_PER_TICK) % loopSpan;
      });
    }

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui)) {
        super.drawBackgroundAdditional(context);
        return;
      }
      var font = Minecraft.getInstance().font;
      float textWidth = font.width(text);
      float x0 = getContentX();
      float y0 = getContentY();
      float boxWidth = getContentWidth();
      float lineY = y0 + (getContentHeight() - font.lineHeight) / 2;

      if (textWidth <= boxWidth) {
        LDFonts.drawText(gui, font, text, x0 + (boxWidth - textWidth) / 2, lineY, ARGB.opaque(0xFFFFFF), true);
        return;
      }

      loopSpan = textWidth + GAP;
      gui.enableScissor(x0, y0, boxWidth, getContentHeight());
      for (float drawX = x0 - offset; drawX < x0 + boxWidth; drawX += loopSpan) {
        LDFonts.drawText(gui, font, text, drawX, lineY, ARGB.opaque(0xFFFFFF), true);
      }
      gui.disableScissor();
    }
  }
}
