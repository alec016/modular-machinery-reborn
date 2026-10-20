package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class LeftTabGroupWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import com.google.common.collect.Lists;
// import com.mojang.datafixers.util.Either;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.client.gui.narration.NarrationElementOutput;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.FormattedText;
// import net.minecraft.world.inventory.tooltip.TooltipComponent;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
// import java.util.Optional;
// import java.util.concurrent.atomic.AtomicInteger;
//
// @ParametersAreNonnullByDefault
// @SuppressWarnings("unused")
// public class LeftTabGroupWidget extends VerticalTabGroupWidget {
//   private final AtomicInteger lastY = new AtomicInteger();
//
//   public LeftTabGroupWidget(int x, int y) {
//     super(x, y);
//     this.lastY.set(y);
//   }
//
//   public LeftTabGroupWidget addTab(LeftTabWidget tab) {
//     return addTab(0, 0, tab);
//   }
//
//   public LeftTabGroupWidget addTab(int xOffset, int yOffset, LeftTabWidget tab) {
//     tab.setY(lastY.getAndAdd(tab.getHeight() + yOffset));
//     tab.setX(this.getX() + xOffset);
//     tabs.add(tab);
//     return this;
//   }
//
//   public LeftTabGroupWidget addTab(ItemOrIconButton icon, @Nullable LeftTabWidget.OnClick action) {
//    return addTab(0, 0, icon, action);
//   }
//
//   public LeftTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon, @Nullable LeftTabWidget.OnClick action) {
//     LeftTabWidget tab = new LeftTabWidget(getX() + xOffset, lastY.get() + yOffset, icon, action);
//     lastY.getAndAdd(tab.getHeight() + yOffset);
//     return addTab(tab);
//   }
//
//   public LeftTabGroupWidget addTab(ItemOrIconButton icon) {
//     return addTab(0, 0, icon);
//   }
//
//   public LeftTabGroupWidget addTab(int xOffset, int yOffset, ItemOrIconButton icon) {
//     LeftTabWidget tab = new LeftTabWidget(getX() + xOffset, lastY.get() + yOffset, icon);
//     lastY.getAndAdd(tab.getHeight() + yOffset);
//     return addTab(tab);
//   }
// }
//
