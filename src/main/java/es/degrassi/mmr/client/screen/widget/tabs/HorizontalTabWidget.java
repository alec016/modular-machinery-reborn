package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class HorizontalTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import org.jetbrains.annotations.Nullable;
//
// public class HorizontalTabWidget extends TabWidget {
//   public HorizontalTabWidget(int x, int y, @Nullable ItemOrIconButton icon, @Nullable TabWidget.OnClick onClick) {
//     super(x, y, icon, onClick);
//   }
//
//   public HorizontalTabWidget(int x, int y, @Nullable ItemOrIconButton icon) {
//     super(x, y, icon);
//   }
// }
//
