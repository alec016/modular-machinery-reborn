package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class RightTabGroupWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.concurrent.atomic.AtomicInteger;
//
// @ParametersAreNonnullByDefault
// @SuppressWarnings("unused")
// public class RightTabGroupWidget extends VerticalTabGroupWidget {
//   private final AtomicInteger lastY = new AtomicInteger();
//
//   public RightTabGroupWidget(int x, int y) {
//     super(x, y);
//     this.lastY.set(y);
//   }
//
//   public RightTabGroupWidget addTab(RightTabWidget tab) {
//     return addTab(0, 0, tab);
//   }
//
//   public RightTabGroupWidget addTab(int xOffset, int yOffset, RightTabWidget tab) {
//     tab.setY(lastY.getAndAdd(tab.getHeight() + yOffset));
//     tab.setX(this.getX() + xOffset);
//     tabs.add(tab);
//     return this;
//   }
//
//   public RightTabGroupWidget addTab(ItemOrIconButton icon, @Nullable RightTabWidget.OnClick action) {
//    return addTab(0, 0, icon, action);
//   }
//
//   public RightTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon, @Nullable RightTabWidget.OnClick action) {
//     RightTabWidget tab = new RightTabWidget(getX() + xOffset, lastY.get() + yOffset, icon, action);
//     lastY.getAndAdd(tab.getHeight() + yOffset);
//     return addTab(tab);
//   }
//
//   public RightTabGroupWidget addTab(ItemOrIconButton icon) {
//     return addTab(0, 0, icon);
//   }
//
//   public RightTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon) {
//     RightTabWidget tab = new RightTabWidget(getX() + xOffset, lastY.get() + yOffset, icon);
//     lastY.getAndAdd(tab.getHeight() + yOffset);
//     return addTab(tab);
//   }
// }
//
