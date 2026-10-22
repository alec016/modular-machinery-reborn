package es.degrassi.mmr.client.screen.widget.tabs;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A top tab at {@code (x, y)} (the legacy strip stacks across {@code x + i * 28} above the
 * panel). The icon is drawn bare at {@code (5, 5)}; the plate comes from
 * {@code base_tab(_hovered)_top.png}.
 */
public class TopTab extends TabElement {

  /** Legacy tab sizes: {@code base_tab_top.png} 28x27. */
  public static final int TOP_TAB_WIDTH = 28;
  public static final int TOP_TAB_HEIGHT = 27;
  /** Consecutive tab pitch of the legacy {@code TabGroupWidget} top strip. */
  public static final int TOP_TAB_PITCH = 28;

  public static final Identifier TEX_TAB_TOP = ModularMachineryReborn.rl("textures/gui/widget/base_tab_top.png");
  public static final Identifier TEX_TAB_TOP_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_top.png");

  /**
   * Adds a top tab at {@code (x, y)}. This overload draws an atlas icon instead of an item.
   */
  public TopTab(UIElement parent, int x, int y,
                Icon spriteIcon, Supplier<List<Object>> tooltips,
                @Nullable Consumer<UIEvent> onClick) {
    this(parent, x, y, () -> ItemStack.EMPTY, tooltips, spriteIcon, onClick);
  }

  /** Adds a top tab at {@code (x, y)} with the given item stack icon. */
  public TopTab(UIElement parent, int x, int y,
                Supplier<ItemStack> icon, Supplier<List<Object>> tooltips,
                @Nullable Consumer<UIEvent> onClick) {
    this(parent, x, y, icon, tooltips, null, onClick);
  }

  public TopTab(UIElement parent, int x, int y,
                Supplier<ItemStack> icon, Supplier<List<Object>> tooltips,
                @Nullable Icon spriteIcon, @Nullable Consumer<UIEvent> onClick) {
    super(icon, tooltips);
    this.spriteIcon = spriteIcon;
    lss("width", TOP_TAB_WIDTH).lss("height", TOP_TAB_HEIGHT);
    if (onClick != null) {
      addEventListener(UIEvents.MOUSE_DOWN, event -> {
        UISoundUtils.playButtonClickSound();
        onClick.accept(event);
      });
    }
    MMRMachineUI.add(parent, this, x, y);
  }

  @Override
  protected void drawTab(GUIContext gui, int x, int y) {
    if (isHover()) {
      gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_TOP_HOVERED, 0, 0, TOP_TAB_WIDTH, TOP_TAB_HEIGHT + 2),
          x, y, TOP_TAB_WIDTH, TOP_TAB_HEIGHT + 2);
    } else {
      gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_TOP, 0, 0, TOP_TAB_WIDTH, TOP_TAB_HEIGHT),
          x, y, TOP_TAB_WIDTH, TOP_TAB_HEIGHT);
    }
    ItemStack stack = icon.get();
    if (stack != null && !stack.isEmpty()) {
      DrawerHelperClient.drawItemStack(gui, stack, x + 5, y + 5, 0);
    } else if (spriteIcon != null) {
      gui.drawTexture(MMRMachineUI.sprite(spriteIcon.TEXTURE, spriteIcon.x, spriteIcon.y, spriteIcon.width, spriteIcon.height),
          x + 5, y + 5, spriteIcon.width, spriteIcon.height);
    }
  }
}
