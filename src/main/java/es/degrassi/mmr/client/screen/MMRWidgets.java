package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEventListener;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.Icon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * LDLib2 rebuilds of the classic Modular Machinery vanilla widgets.
 *
 * <p>These are the equivalents of the legacy 1.21.1 {@code ItemOrIconButton} and
 * {@code StringButton}: a 16x16 button that paints the 18x20 toolbar background of
 * {@code states.png} with its one-pixel-inset icon, and a text button that uses the
 * {@code widget/button/button.png} nine-slice sprites. They never fall back to any
 * LDLib2 default texture.
 */
public final class MMRWidgets {

  private MMRWidgets() {}

  /** The classic bluish button sprite ({@code widget/button/button.png}, nine-slice border 3). */
  public static IGuiTexture buttonBase() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button.png").toString())
        .setBorder(3);
  }

  public static IGuiTexture buttonHovered() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button_highlighted.png").toString())
        .setBorder(3);
  }

  public static IGuiTexture buttonDisabled() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button_disabled.png").toString())
        .setBorder(3);
  }

  /**
   * A classic {@code StringButton}: text button sized to the given bounds, drawn with the MMR
   * button nine-slice sprites ({@code button.png}, {@code button_highlighted.png}) like the old
   * vanilla {@code WidgetSprites} rendering.
   */
  public static Button spriteButton(UIElement parent, Component text, int x, int y, int width, int height,
                                    @Nullable Consumer<UIEvent> onClick) {
    Button button = new Button();
    button.setText(text);
    button.textStyle(style -> {
      style.textWrap(TextWrap.HOVER_ROLL);
    });
    button.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    button.buttonStyle(style -> {
      style.baseTexture(buttonBase());
      style.hoverTexture(buttonHovered());
      style.pressedTexture(buttonHovered());
    });
    if (onClick != null) {
      button.setOnClick(onClick::accept);
    }
    return MMRMachineUI.add(parent, button, x, y);
  }

  /** Adds a static caption drawn on a {@code spriteButton}-compatible row. */
  public static Label spriteCaption(UIElement parent, Component text, int x, int y, int width, int height) {
    Label label = new Label();
    label.setText(text);
    label.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    label.textStyle(style -> style.textAlignHorizontal(Horizontal.CENTER).textAlignVertical(Vertical.CENTER));
    return MMRMachineUI.add(parent, label, x, y);
  }

  /**
   * The classic {@code ItemOrIconButton}: a 16x16 hit area at {@code (x, y)} showing the 18x20
   * toolbar background shifted one pixel up while hovered and the 16x16 icon one pixel below it,
   * exactly like the legacy widget.
   */
  public static IconButton iconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, IconButton.Mode.icon(icon, null), x, y, null, onClick, null, false);
  }

  /** Same as {@link #iconButton} but with a dynamic tooltip line list. */
  public static IconButton iconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick,
                                      @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, IconButton.Mode.icon(icon, null), x, y, tooltips, onClick, null, false);
  }

  /** Item-based variant of {@link #iconButton}: renders the item stack instead of an icon. */
  public static IconButton itemButton(UIElement parent, Supplier<ItemStack> item, int x, int y,
                                      @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, IconButton.Mode.icon(null, item), x, y, null, onClick, null, false);
  }

  /** Item-based variant with tooltips. */
  public static IconButton itemButton(UIElement parent, Supplier<ItemStack> item, int x, int y,
                                      @Nullable Consumer<UIEvent> onClick,
                                      @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, IconButton.Mode.icon(null, item), x, y, tooltips, onClick, null, false);
  }

  /**
   * Half-size {@code iconButton}: an 8x8 hit box and icon with the 18x20 toolbar plate scaled
   * down along with everything else. Used by the auto-IO panel's compact button row.
   */
  public static IconButton smallIconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick) {
    return buildIconButton(parent, IconButton.Mode.icon(icon, null), x, y, null, onClick, null, true);
  }

  /** Same as {@link #smallIconButton} but with a dynamic tooltip line list. */
  public static IconButton smallIconButton(UIElement parent, Icon icon, int x, int y, @Nullable Consumer<UIEvent> onClick,
                                           @Nullable Supplier<List<Component>> tooltips) {
    return buildIconButton(parent, IconButton.Mode.icon(icon, null), x, y, tooltips, onClick, null, true);
  }

  private static IconButton buildIconButton(UIElement parent, IconButton.Mode mode, int x, int y,
                                            @Nullable Supplier<List<Component>> tooltips,
                                            @Nullable Consumer<UIEvent> onClick,
                                            @Nullable UIEventListener serverClick,
                                            boolean small) {
    IconButton button = new IconButton(mode, tooltips, small);
    button.lss("width", small ? 8 : 16).lss("height", small ? 8 : 16);
    if (onClick != null) {
      button.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        if (event.button == 0) {
          // Handled here: stop the event from bubbling to ancestors. Otherwise a parent that
          // toggles its own state on MOUSE_DOWN (the auto-IO tab's open click) would run right
          // after the close button already closed it and instantly re-open the panel.
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

  /**
   * A 16x16 button in the exact legacy {@code ItemOrIconButton} style. The toolbar background
   * ({@code states.png} region 176x128, 18x20) is drawn at {@code (-1, hoverShift)} and the icon
   * at {@code (0, 1 + hoverShift)}, so the visual anchor lines up with the old screens.
   */
  public static final class IconButton extends UIElement {
    private static final Icon BG_NORMAL = Icon.TOOLBAR_BUTTON_BACKGROUND;
    private static final Icon BG_HOVER = Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER;
    private static final Icon BG_FOCUS = Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS;

    private final Mode mode;
    /** Draws the bare icon without the toolbar plate (tab strip icons). */
    private boolean disableBackground;
    private boolean enabled = true;
    /** Half-size mode: plate and icon draw at half scale inside the 8x8 hit box. */
    private final boolean small;
    @Nullable
    private final Supplier<List<Component>> tooltips;

    private IconButton(Mode mode, @Nullable Supplier<List<Component>> tooltips) {
      this(mode, tooltips, false);
    }

    private IconButton(Mode mode, @Nullable Supplier<List<Component>> tooltips, boolean small) {
      this.mode = mode;
      this.tooltips = tooltips;
      this.small = small;
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        // Legacy ItemOrIconButton parity: toolbar buttons only ever own a tooltip while the
        // background plate is on; anonymous tab strip icons fall through to the parent chain.
        if (this.tooltips == null || this.disableBackground) {
          return;
        }
        List<Component> lines = this.tooltips.get();
        if (lines != null && !lines.isEmpty()) {
          event.hoverTooltips = com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips.create(lines.toArray());
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

    public boolean isEnabled() {
      return this.enabled;
    }

    @Nullable
    public ItemStack getItemStack() {
      return this.mode.item() == null ? null : this.mode.item().get();
    }

    /** Renders on the client only, mirroring the legacy {@code renderWidget}. */
    @Override
    protected void drawBackgroundAdditional(com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext context) {
      if (!(context instanceof com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext guiContext)) {
        return;
      }
      int x = (int) getContentX();
      int y = (int) getContentY();
      int yOffset = isHover() && enabled ? 1 : 0;
      if (!this.disableBackground) {
        Icon bg = isHover() ? BG_HOVER : BG_NORMAL;
        // Half-size buttons scale the 18x20 plate down with the icon (10x10 keeps one pixel of
        // margin around the 8x8 icon, exactly the full-size proportions).
        context.drawTexture(MMRMachineUI.sprite(bg.TEXTURE, bg.x, bg.y, bg.width, bg.height),
            x - 1, y + yOffset, this.small ? 10 : bg.width, this.small ? 10 : bg.height);
      }
      ItemStack item = getItemStack();
      if (item != null && !item.isEmpty() && this.mode.item() != null) {
        if (this.mode.halfSize()) {
          // Legacy half-size preview: half width/height, 0.75 scaled render, no decorations.
          guiContext.pose.pushPose();
          guiContext.pose.translate(x, y + yOffset);
          guiContext.pose.scale(0.75f, 0.75f);
          guiContext.graphics.item(item, (int) (8 * 0.45f), (int) (8 * 0.65f));
          guiContext.pose.popPose();
        } else {
          com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient.drawItemStack(guiContext, item, x, y + 1 + yOffset, 0);
        }
      } else if (this.mode.icon() != null) {
        Icon icon = this.mode.icon();
        IGuiTexture texture = MMRMachineUI.sprite(icon.TEXTURE, icon.x, icon.y, icon.width, icon.height);
        if (!this.enabled) {
          texture = texture.copy().setColor(0x80FFFFFF);
        }
        context.drawTexture(texture, x, y + 1 + yOffset,
            this.small ? 8 : icon.width, this.small ? 8 : icon.height);
      }
    }

    /** What the button draws: an atals icon and/or a dynamic item stack. */
    record Mode(@Nullable Icon icon, @Nullable Supplier<ItemStack> item, boolean halfSize) {
      static Mode icon(@Nullable Icon icon, @Nullable Supplier<ItemStack> item) {
        return new Mode(icon, item, false);
      }
    }
  }
}
