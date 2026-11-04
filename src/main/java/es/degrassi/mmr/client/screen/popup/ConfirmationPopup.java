package es.degrassi.mmr.client.screen.popup;

import com.lowdragmc.lowdraglib2.client.font.LDFonts;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.utils.TextUtilities;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.ScrollingTextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.List;

public final class ConfirmationPopup extends BasePopup {

  private static final int SCROLLER_GUTTER = 11;
  private static final float LINE_HEIGHT = 10f;
  private static final int BOTTOM_CHROME = 30;

  public ConfirmationPopup(UIElement parent, int width, int minHeight,
                           @Nullable Component title, List<Component> text,
                           @Nullable Component confirmText, @Nullable Component cancelText,
                           @Nullable Runnable onConfirm, @Nullable Runnable onCancel,
                           boolean closeButton) {
    super(width, computeHeight(width, minHeight, title, text, closeButton));
    int y = 5;
    if (closeButton) {
      addCloseButton();
      y = 25;
    }
    if (title != null) {
      popupLabel(title, 0, y, width);
      y += 20;
    }
    if (!text.isEmpty()) {
      int wrapWidth = width - SCROLLER_GUTTER;
      addScrollingText(joined(text), y, wrapWidth, height - BOTTOM_CHROME - y);
    }
    ScrollingTextButton.scrollingButton(contentContainer,
        confirmText == null ? CONFIRM : confirmText, 5, height - 25, 80, 20,
        event -> {
          if (onConfirm != null) {
            onConfirm.run();
          }
          close();
        });
    ScrollingTextButton.scrollingButton(contentContainer,
        cancelText == null ? CANCEL : cancelText, width - 85, height - 25, 80, 20,
        event -> {
          if (onCancel != null) {
            onCancel.run();
          }
          close();
        });
    show(parent);
  }

  private static Component joined(List<Component> text) {
    MutableComponent joined = Component.empty();
    for (Component line : text) {
      joined.append("\n").append(line);
    }
    return joined;
  }

  private static int computeHeight(int width, int minHeight,
                                   @Nullable Component title, List<Component> text, boolean closeButton) {
    int top = 5;
    if (closeButton) {
      top = 25;
    }
    if (title != null) {
      top += 20;
    }
    int lines = text.isEmpty() ? 0
        : TextUtilities.computeFormattedLines(LDFonts.font(), joined(text), 9f, width - SCROLLER_GUTTER).size();
    int desired = top + (int) Math.ceil(lines * LINE_HEIGHT - 1) + BOTTOM_CHROME;
    int cap = Minecraft.getInstance().getWindow().getGuiScaledHeight() * 2 / 3;
    return Mth.clamp(desired, minHeight, Math.max(minHeight, cap));
  }

  private void addScrollingText(Component joinedText, int top, int wrapWidth, int viewportHeight) {
    ScrollerView scroller = new ScrollerView();
    scroller.lss("width", wrapWidth + SCROLLER_GUTTER).lss("height", viewportHeight);
    MMRMachineUI.add(contentContainer, scroller, 5, top);
    scroller.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO).scrollerViewStyle(4);
    scroller.viewPort.layout(layout -> layout.paddingAll(0));
    scroller.viewPort.style(style -> style.background(IGuiTexture.EMPTY));
    scroller.verticalScroller.lss("margin-left", 4).lss("width", 7);
    scroller.verticalScroller.scrollContainer.style(style ->
        style.background(MMRMachineUI.spriteNineSlice(MMRMachineUI.TEX_SMALL_SCROLLER_DISABLED, 3, 4, 3, 4)));
    scroller.verticalScroller.scrollBar.buttonStyle(style -> {
      var thumb = MMRMachineUI.spriteNineSlice(MMRMachineUI.TEX_SMALL_SCROLLER, 3, 4, 3, 4);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    popupLabel(scroller.viewContainer, joinedText, 0, 0, wrapWidth);
  }
}
