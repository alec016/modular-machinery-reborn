package es.degrassi.mmr.client.screen.popup;

import com.lowdragmc.lowdraglib2.gui.texture.Icons;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Dialog;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.util.WindowDragHelper;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;


public abstract class BasePopup extends Dialog {

  public static final Component CONFIRM = Component.translatable("mmr.gui.popup.confirm").withStyle(ChatFormatting.GREEN);
  public static final Component CANCEL = Component.translatable("mmr.gui.popup.cancel").withStyle(ChatFormatting.RED);

  /** Height of the draggable top strip of every popup. */
  private static final int DRAG_STRIP = 22;

  private final int width;
  /** The final overlay height (popups may grow it before super(...) resolves — see ConfirmationPopup). */
  protected final int height;

  /**
   * Builds a popup window: a {@code width x height} content area over the legacy nine-slice
   * {@code background.png}, no auto-close on outside clicks, ESC closes (topmost popup behavior).
   *
   * <p>The dialog is <em>not</em> shown here: {@link Dialog#show} hides every empty container at
   * showtime, so every popup must add its children first and then call {@code show(parent)}
   * (see {@link ConfirmationPopup}, {@link CoreGridPopup} and {@link CorePopup}). Showing an
   * empty content container is exactly the "fully transparent popup" bug.
   */
  protected BasePopup(int width, int height) {
    this.width = width;
    this.height = height;
    setAutoClose(false);
    setClickOutsideClose(false);
    overlay.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    contentContainer.layout(layout -> {
      layout.widthPercent(100);
      layout.heightPercent(100);
      layout.gapAll(0);
      layout.paddingAll(0);
    });
    contentContainer.style(style -> style.background(MMRMachineUI.nineSliceTexture(MMRMachineUI.TEX_BACKGROUND)));
    addEventListener(UIEvents.KEY_DOWN, event -> {
      if (event.keyCode == GLFW.GLFW_KEY_ESCAPE) {
        close();
      }
    });
    makeDraggable();
  }

  private void makeDraggable() {
    UIElement overlay = this.overlay;
    overlay.addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (!insideDragStrip(overlay, event)) {
        return;
      }
      float startX = overlay.getLayoutX();
      float startY = overlay.getLayoutY();
      overlay.lss("position", "absolute").lss("left", startX).lss("top", startY);
      overlay.startDrag(new WindowDragHelper.DragMove(startX, startY), Icons.MOVE)
          .setDragTexture(-6, -6, 12, 12);
      event.stopPropagation();
    });
    overlay.addEventListener(UIEvents.DRAG_SOURCE_UPDATE, event -> {
      if (event.dragHandler.draggingObject instanceof WindowDragHelper.DragMove move) {
        var delta = overlay.getLocalMouseNormal(event.x - event.dragStartX, event.y - event.dragStartY);
        overlay.getLayout().left(move.startX() + delta.x).top(move.startY() + delta.y);
      }
    });
  }

  private boolean insideDragStrip(UIElement overlay, UIEvent event) {
    Vector2f local = overlay.worldToLocalLayoutOffset(new Vector2f(event.x, event.y));
    if (local.y < 0 || local.y > DRAG_STRIP) {
      return false;
    }
    return !(local.x >= width - 27 && local.x <= width && local.y <= DRAG_STRIP);
  }

  protected void addCloseButton() {
    Button close = SpriteButton.spriteButton(contentContainer, Component.literal("X"),
        width - 25, 5, 20, 20, event -> close());
    close.style(style -> style.tooltips(Component.translatable("mmr.gui.button.close")));
  }

  protected Label popupLabel(Component text, int x, int y, int width) {
    return popupLabel(contentContainer, text, x, y, width);
  }

  protected Label popupLabel(UIElement parent, Component text, int x, int y, int width) {
    Label label = new Label();
    label.setText(text);
    label.layout(layout -> {
      layout.width(width);
      layout.gapAll(0);
      layout.paddingAll(0);
    });
    label.textStyle(style -> {
      style.textWrap(TextWrap.WRAP);
      style.adaptiveHeight(true);
      style.adaptiveWidth(false);
    });
    return MMRMachineUI.add(parent, label, x, y);
  }
}
