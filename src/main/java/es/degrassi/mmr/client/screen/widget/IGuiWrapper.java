package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public interface IGuiWrapper {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.client.screen.BaseScreen;
// import es.degrassi.mmr.client.util.GuiUtils;
// import es.degrassi.mmr.common.util.MMRLogger;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.events.ContainerEventHandler;
// import net.minecraft.client.gui.screens.Screen;
// import net.minecraft.network.chat.Component;
// import net.minecraft.world.inventory.Slot;
// import net.minecraft.world.item.ItemStack;
// import org.jetbrains.annotations.NotNull;
// import org.jetbrains.annotations.Nullable;
//
// import java.util.ArrayList;
// import java.util.List;
//
// public interface IGuiWrapper extends ContainerEventHandler, IFancyFontRenderer {
//
//   @NotNull
//   default ItemStack getCarriedItem() {
//     return ItemStack.EMPTY;
//   }
//
//   int getGuiLeft();
//
//   int getGuiTop();
//
//   @Override
//   int getXSize();
//
//   int getYSize();
//
//   default boolean currentlyQuickCrafting() {
//     return false;
//   }
//
//   default void renderItem(GuiGraphicsExtractor GuiGraphicsExtractor, @NotNull ItemStack stack, int xAxis, int yAxis) {
//     renderItem(GuiGraphicsExtractor, stack, xAxis, yAxis, 1);
//   }
//
//   default void renderItem(GuiGraphicsExtractor GuiGraphicsExtractor, @NotNull ItemStack stack, int xAxis, int yAxis, float scale) {
//     GuiUtils.renderItem(GuiGraphicsExtractor, stack, xAxis, yAxis, scale, font(), null, false);
//   }
//
//   default void renderItemTooltipWithExtra(GuiGraphicsExtractor GuiGraphicsExtractor, @NotNull ItemStack stack, int xAxis, int yAxis, List<Component> toAppend) {
//     if (toAppend.isEmpty()) {
//       GuiGraphicsExtractor.renderTooltip(font(), stack, xAxis, yAxis);
//     } else {
//       List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
//       tooltip.addAll(toAppend);
//       GuiGraphicsExtractor.renderTooltip(font(), tooltip, stack.getTooltipImage(), stack, xAxis, yAxis);
//     }
//   }
//
//   default void renderItemWithOverlay(GuiGraphicsExtractor GuiGraphicsExtractor, @NotNull ItemStack stack, int xAxis, int yAxis, float scale, @Nullable String text) {
//     GuiUtils.renderItem(GuiGraphicsExtractor, stack, xAxis, yAxis, scale, font(), text, true);
//   }
//
//   @Nullable
//   default BaseScreen<?,?> getWindowHovering(double mouseX, double mouseY) {
//     return null;
//   }
//
//   default void addWindow(BaseScreen<?, ?> window) {
//     MMRLogger.INSTANCE.error("Tried to call 'addWindow' but unsupported in {}", getClass().getName());
//   }
//
//   default void removeWindow(BaseScreen<?, ?> window) {
//     MMRLogger.INSTANCE.error("Tried to call 'removeWindow' but unsupported in {}", getClass().getName());
//   }
//
//   void renderSlotHighlight(GuiGraphicsExtractor GuiGraphicsExtractor, Slot slot, int mouseX, int mouseY, float partialTick);
//
//   int getSlotColor();
//
//   default void renderSlotHighlight(GuiGraphicsExtractor GuiGraphicsExtractor, Slot slot, int mouseX, int mouseY, float partialTick, int z) {
//     if (slot.isHighlightable()) {
//       BaseScreen.renderSlotHighlight(GuiGraphicsExtractor, slot.x, slot.y, getSlotColor(), z);
//     }
//   }
// }
//
