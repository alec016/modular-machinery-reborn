package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class RightTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.resources.Identifier;
// import org.jetbrains.annotations.Nullable;
//
// public class RightTabWidget extends VerticalTabWidget {
//   public static final Identifier TAB = ModularMachineryReborn.rl("textures/gui/widget/base_tab_right.png");
//   private static final Identifier TAB_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_right.png");
//
//   public RightTabWidget(int x, int y, @Nullable ItemOrIconButton icon) {
//     super(x, y, icon);
//   }
//
//   public RightTabWidget(int x, int y, @Nullable ItemOrIconButton icon, @Nullable TabWidget.OnClick onClick) {
//     super(x, y, icon, onClick);
//   }
//
//   @Override
//   public Identifier getTab() {
//     return TAB;
//   }
//
//   @Override
//   public Identifier getTabHovered() {
//     return TAB_HOVERED;
//   }
//
//   @Override
//   public void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     Identifier tab = isHoveredOrFocused() ? getTabHovered() : getTab();
//     int x = getX() + (isHoveredOrFocused() ? -2 : 0);
//     int y = getY();
//     int width = TextureSizeHelper.getWidth(tab), height = TextureSizeHelper.getHeight(tab);
//     this.width = width;
//     this.height = height;
//     GuiGraphicsExtractor.blit(tab, x, y, 0, 0, width, height, width, height);
//     if (getIconButton() != null) {
//       getIconButton().setDisableBackground(true);
//       getIconButton().setPosition(5 + getX(), 5 + y);
//       getIconButton().renderTooltip(false);
//       getIconButton().renderWidget(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//     }
//   }
// }
//
