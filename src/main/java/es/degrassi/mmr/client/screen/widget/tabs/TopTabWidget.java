package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class TopTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import net.minecraft.resources.Identifier;
// import org.jetbrains.annotations.Nullable;
//
// public class TopTabWidget extends HorizontalTabWidget {
//   public static final Identifier TAB = ModularMachineryReborn.rl("textures/gui/widget/base_tab_top.png");
//   private static final Identifier TAB_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_top.png");
//
//   public TopTabWidget(int x, int y, @Nullable ItemOrIconButton icon) {
//     super(x, y, icon);
//   }
//
//   public TopTabWidget(int x, int y, @Nullable ItemOrIconButton icon, @Nullable TabWidget.OnClick onClick) {
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
// }
//
