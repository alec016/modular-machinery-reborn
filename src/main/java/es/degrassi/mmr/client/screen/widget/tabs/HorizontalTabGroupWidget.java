package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class HorizontalTabGroupWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import net.minecraft.network.chat.Component;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
//
// @ParametersAreNonnullByDefault
// @SuppressWarnings("unused")
// public class HorizontalTabGroupWidget extends TabGroupWidget {
//   public HorizontalTabGroupWidget(int x, int y) {
//     super(x, y, 0, 0, Component.empty());
//   }
//
//   @Override
//   public int getWidth() {
//     return tabs.stream().mapToInt(TabWidget::getWidth).sum();
//   }
//
//   @Override
//   public int getHeight() {
//     return tabs.stream().mapToInt(TabWidget::getHeight).max().orElse(1);
//   }
//
//   public HorizontalTabGroupWidget addTab(TabWidget tab) {
//     return addTab(0, 0, tab);
//   }
//
//   public HorizontalTabGroupWidget addTab(int xOffset, int yOffset, TabWidget tab) {
//     tab.setX(lastX.getAndAdd(tab.getWidth() + xOffset));
//     tab.setY(this.getY() + yOffset);
//     tabs.add(tab);
//     return this;
//   }
//
//   public HorizontalTabGroupWidget addTab(ItemOrIconButton icon, @Nullable TabWidget.OnClick action) {
//    return addTab(0, 0, icon, action);
//   }
//
//   public HorizontalTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon, @Nullable TabWidget.OnClick action) {
//     TabWidget tab = new HorizontalTabWidget(lastX.get() + xOffset, getY() + yOffset, icon, action);
//     lastX.getAndAdd(tab.getWidth() + xOffset);
//     return addTab(tab);
//   }
//
//   public HorizontalTabGroupWidget addTab(ItemOrIconButton icon) {
//     return addTab(0, 0, icon);
//   }
//
//   public HorizontalTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon) {
//     TabWidget tab = new HorizontalTabWidget(lastX.get() + xOffset, getY() + yOffset, icon);
//     lastX.getAndAdd(tab.getWidth() + xOffset);
//     return addTab(tab);
//   }
// }
//
