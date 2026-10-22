package es.degrassi.mmr.client.screen.widget.tabs;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A right-side tab at {@code (x, y)}, traditionally right past the panel's edge; the hovered
 * plate grows two pixels to the left, exactly like the legacy implementation.
 */
public class RightTab extends TabElement {

  /** Legacy tab size: {@code base_tab_right.png} 27x28. */
  public static final int RIGHT_TAB_WIDTH = 27;
  public static final int RIGHT_TAB_HEIGHT = 28;

  public static final Identifier TEX_TAB_RIGHT = ModularMachineryReborn.rl("textures/gui/widget/base_tab_right.png");
  public static final Identifier TEX_TAB_RIGHT_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_right.png");

  public RightTab(UIElement parent, int x, int y,
                  Supplier<ItemStack> icon, Supplier<List<Object>> tooltips,
                  @Nullable Consumer<UIEvent> onClick) {
    super(icon, tooltips);
    lss("width", RIGHT_TAB_WIDTH).lss("height", RIGHT_TAB_HEIGHT);
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
      gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT_HOVERED, 0, 0, 29, RIGHT_TAB_HEIGHT),
          x - 2, y, 29, RIGHT_TAB_HEIGHT);
    } else {
      gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT, 0, 0, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT),
          x, y, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT);
    }
    ItemStack stack = icon.get();
    if (stack != null && !stack.isEmpty()) {
      DrawerHelperClient.drawItemStack(gui, stack, x + 5, y + 5, 0);
    }
  }
}
