package es.degrassi.mmr.client.screen.widget.buttons;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEventListener;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class IconButton extends UIElement {
  private static final Icon BG_NORMAL = Icon.TOOLBAR_BUTTON_BACKGROUND;
  private static final Icon BG_HOVER = Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER;
  private static final Icon BG_FOCUS = Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS;

  private final Mode mode;
  /** Draws the bare icon without the toolbar plate (tab strip icons). */
  private boolean disableBackground;
  @Getter
  private boolean enabled = true;
  /** Half-size mode: plate and icon draw at half scale inside the 8x8 hit box. */
  private final boolean small;
  @Nullable
  private final Supplier<List<Component>> tooltips;

  private IconButton(Mode mode, @Nullable Supplier<List<Component>> tooltips, boolean small) {
    this.mode = mode;
    this.tooltips = tooltips;
    this.small = small;
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      if (this.tooltips == null || this.disableBackground) {
        return;
      }
      List<Component> lines = this.tooltips.get();
      if (lines != null && !lines.isEmpty()) {
        event.hoverTooltips = HoverTooltips.create(lines.toArray());
      }
    });
  }

  public IconButton setDisableBackground(boolean disableBackground) {
    this.disableBackground = disableBackground;
    return this;
  }

  public IconButton setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @Nullable
  public ItemStack getItemStack() {
    return this.mode.item() == null ? null : this.mode.item().get();
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext guiContext)) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    int yOffset = isHover() && enabled ? 1 : 0;
    if (!this.disableBackground) {
      Icon bg = isHover() ? BG_HOVER : BG_NORMAL;
      guiContext.drawTexture(MMRMachineUI.sprite(bg.TEXTURE, bg.x, bg.y, bg.width, bg.height),
          x - 1, y + yOffset, this.small ? 10 : bg.width, this.small ? 10 : bg.height);
    }
    ItemStack item = getItemStack();
    if (item != null && !item.isEmpty() && this.mode.item() != null) {
      if (this.mode.halfSize()) {
        guiContext.pose.pushPose();
        guiContext.pose.translate(x, y + yOffset);
        guiContext.pose.scale(0.75f, 0.75f);
        guiContext.graphics.item(item, (int) (8 * 0.45f), (int) (8 * 0.65f));
        guiContext.pose.popPose();
      } else {
        DrawerHelperClient.drawItemStack(guiContext, item, x, y + 1 + yOffset, 0);
      }
    } else if (this.mode.icon() != null) {
      Icon icon = this.mode.icon();
      IGuiTexture texture = MMRMachineUI.sprite(icon.TEXTURE, icon.x, icon.y, icon.width, icon.height);
      if (!this.enabled) {
        texture = texture.copy().setColor(0x80FFFFFF);
      }
      guiContext.drawTexture(texture, x, y + 1 + yOffset,
          this.small ? 8 : icon.width, this.small ? 8 : icon.height);
    }
  }

  public static IconButton iconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, Mode.icon(icon, null), x, y, null, onClick, null, false);
  }

  public static IconButton iconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick,
                                      @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, Mode.icon(icon, null), x, y, tooltips, onClick, null, false);
  }

  public static IconButton itemButton(UIElement parent, Supplier<ItemStack> item, int x, int y,
                                      @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, Mode.icon(null, item), x, y, null, onClick, null, false);
  }

  public static IconButton itemButton(UIElement parent, Supplier<ItemStack> item, int x, int y,
                                      @Nullable Consumer<UIEvent> onClick,
                                      @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, Mode.icon(null, item), x, y, tooltips, onClick, null, false);
  }

  public static IconButton smallIconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, Mode.icon(icon, null), x, y, null, onClick, null, true);
  }

  public static IconButton smallIconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick,
                                           @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, Mode.icon(icon, null), x, y, tooltips, onClick, null, true);
  }

  private static IconButton buildIconButton(UIElement parent, Mode mode, int x, int y,
                                            @Nullable Supplier<List<Component>> tooltips,
                                            @Nullable Consumer<UIEvent> onClick,
                                            @Nullable UIEventListener serverClick,
                                            boolean small) {
    IconButton button = new IconButton(mode, tooltips, small);
    button.lss("width", small ? 8 : 16).lss("height", small ? 8 : 16);
    if (onClick != null) {
      button.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        if (event.button == 0) {
          event.stopPropagation();
          UISoundUtils.playButtonClickSound();
          onClick.accept(event);
        }
      });
    }
    if (serverClick != null) {
      button.addServerEventListener(UIEvents.MOUSE_DOWN, serverClick);
    }
    return MMRMachineUI.add(parent, button, x, y);
  }

  record Mode(@Nullable Icon icon, @Nullable Supplier<ItemStack> item, boolean halfSize) {
    static Mode icon(@Nullable Icon icon, @Nullable Supplier<ItemStack> item) {
      return new Mode(icon, item, false);
    }
  }
}
