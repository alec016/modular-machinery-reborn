package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class VerticalTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import net.minecraft.client.renderer.Rect2i;
// import org.jetbrains.annotations.Nullable;
//
// public class VerticalTabWidget extends TabWidget {
//   public VerticalTabWidget(int x, int y, @Nullable ItemOrIconButton icon, @Nullable TabWidget.OnClick onClick) {
//     super(x, y, icon, onClick);
//   }
//
//   public VerticalTabWidget(int x, int y, @Nullable ItemOrIconButton icon) {
//     super(x, y, icon);
//   }
//
//   public Rect2i getTooltipArea() {
//     return new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight() - 5);
//   }
// }
//
