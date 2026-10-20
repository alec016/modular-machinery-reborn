package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class GuiSlotScroll {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.client.container.SlotItemComponent;
// import es.degrassi.mmr.client.screen.BaseScreen;
// import es.degrassi.mmr.common.util.TextComponentUtil;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import es.degrassi.mmr.common.util.UnitDisplayUtils;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.network.chat.Component;
// import net.minecraft.util.Mth;
// import net.minecraft.world.item.ItemStack;
// import org.jetbrains.annotations.NotNull;
// import org.jetbrains.annotations.Nullable;
// import org.joml.Vector3f;
//
// import java.text.NumberFormat;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import java.util.function.Consumer;
// import java.util.function.Supplier;
//
// public class GuiSlotScroll extends GuiElement {
//   private static final Component ZERO = Component.literal("").withStyle(ChatFormatting.YELLOW);
//
//   private final GuiScrollbar scrollBar;
//
//   private final int xSlots, ySlots;
//   private final Supplier<@NotNull List<SlotItemComponent>> slotList;
//   private final ISlotClickHandler clickHandler;
//
//
//   public GuiSlotScroll(BaseScreen<?, ?> gui, int x, int y, int xSlots, int ySlots,
//                        Supplier<@NotNull List<SlotItemComponent>> slotList, ISlotClickHandler clickHandler,
//                        boolean needsScrolling) {
//     super(gui, x, y, xSlots * BaseScreen.SLOT_SIZE + BaseScreen.getScrollbarWidth() + 8, ySlots * BaseScreen.SLOT_SIZE);
//     this.xSlots = xSlots;
//     this.ySlots = ySlots;
//     this.slotList = slotList;
//     this.clickHandler = clickHandler;
//     this.scrollBar = addChild(new GuiScrollbar(
//         gui,
//         relativeX + xSlots * BaseScreen.SLOT_SIZE + 4,
//         y,
//         ySlots * BaseScreen.SLOT_SIZE,
//         () -> Mth.ceil((double) getSlotList().size() / this.xSlots),
//         () -> this.ySlots, needsScrolling
//     ));
//   }
//
//   @Override
//   public void visitWidgets(Consumer<AbstractWidget> consumer) {
//     children().forEach(consumer);
//   }
//
//   public List<SlotItemComponent> getSlotList() {
//     return slotList.get();
//   }
//
//   @Override
//   public void drawBackground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
//     super.drawBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
//     List<SlotItemComponent> list = getSlotList();
//     if (!list.isEmpty()) {
//       int slotStart = scrollBar.getCurrentSelection() * xSlots, max = xSlots * ySlots;
//       for (int i = 0; i < max; i++) {
//         int slot = slotStart + i;
//         // terminate if we've exceeded max slot pos
//         if (slot >= list.size()) {
//           break;
//         }
//         renderSlot(GuiGraphicsExtractor, list.get(slot), BaseScreen.SLOT_SIZE * (i % xSlots) + relativeX - 1,BaseScreen.SLOT_SIZE * (i / xSlots) + relativeY - 1);
//       }
//     }
//   }
//
//   @Override
//   public void renderForeground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     super.renderForeground(GuiGraphicsExtractor, mouseX, mouseY);
//     if (getSlot(mouseX, mouseY) == null) return;
//     int xAxis = mouseX - getGuiLeft(), yAxis = mouseY - getGuiTop();
//     int slotX = (xAxis - relativeX) / BaseScreen.SLOT_SIZE, slotY = (yAxis - relativeY) / BaseScreen.SLOT_SIZE;
//     if (slotX >= 0 && slotY >= 0 && slotX < xSlots && slotY < ySlots) {
//       int slotStartX = relativeX + slotX * BaseScreen.SLOT_SIZE, slotStartY = relativeY + slotY * BaseScreen.SLOT_SIZE;
//       if (xAxis >= slotStartX && xAxis < slotStartX + BaseScreen.SLOT_SIZE-2 && yAxis >= slotStartY && yAxis < slotStartY + BaseScreen.SLOT_SIZE-2 && checkWindows(mouseX, mouseY)) {
//         GuiGraphicsExtractor.blit(BaseScreen.BASE_SLOT_HOVERED, slotStartX - 1, slotStartY - 1, 0, 0, BaseScreen.SLOT_SIZE,
//             BaseScreen.SLOT_SIZE,
//             TextureSizeHelper.getWidth(BaseScreen.BASE_SLOT_HOVERED), TextureSizeHelper.getHeight(BaseScreen.BASE_SLOT_HOVERED));
//         BaseScreen.renderSlotHighlight(GuiGraphicsExtractor, slotStartX, slotStartY, gui().getSlotColor(), 0);
//         renderToolTip(GuiGraphicsExtractor, mouseX, mouseY);
//       }
//     }
//   }
//
//   @Override
//   public void renderToolTip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     super.renderToolTip(GuiGraphicsExtractor, mouseX, mouseY);
//     SlotItemComponent slot = getSlot(mouseX, mouseY);
//     if (slot != null) {
//       renderSlotTooltip(GuiGraphicsExtractor, slot, mouseX, mouseY);
//     }
//   }
//
//   @Override
//   public boolean mouseScrolled(double mouseX, double mouseY, double xDelta, double yDelta) {
//     return scrollBar.adjustScroll(yDelta) || super.mouseScrolled(mouseX, mouseY, xDelta, yDelta);
//   }
//
//   // TODO: look where is being the slot clicked when is not visible
//   @Override
//   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
//     var mouseX = event.x();
//     var mouseY = event.y();
//     return Optional.ofNullable(getSlot(mouseX, mouseY))
//         .map(slot -> {
//           if (!slot.isActive()) return false;
//           getChildAt(mouseX, mouseY).ifPresentOrElse(this::setFocused, gui()::clearFocus);
//           if (this.active && this.visible) {
//             if (this.isValidClickButton(event.button())) {
//               boolean flag = this.isMouseOver(mouseX, mouseY);
//               if (flag) {
//                 this.playDownSound(Minecraft.getInstance().getSoundManager());
//                 this.onClick(event, doubleClick);
//                 return true;
//               }
//             }
//           }
//           return false;
//         })
//         .orElse(false);
//   }
//
//   protected boolean isValidClickButton(int button) {
//     return false;
//   }
//
//   @Override
//   public boolean mouseReleased(MouseButtonEvent event) {
//     if (gui().currentlyQuickCrafting()) {
//       //If the player is currently quick crafting don't do any special handling for as if they clicked in the screen
//       return super.mouseReleased(event);
//     }
//     super.mouseReleased(event);
//     clickHandler.onClick(() -> getSlot(event.x(), event.y()), event.button(), event.buttonInfo().hasShiftDown(), gui().getCarriedItem());
//     return true;
//   }
//
//   @Nullable
//   private SlotItemComponent getSlot(double mouseX, double mouseY) {
//     List<SlotItemComponent> list = getSlotList();
//     if (list.isEmpty()) {
//       return null;
//     }
//     int slotX = (int) ((mouseX - getX()) / BaseScreen.SLOT_SIZE), slotY = (int) ((mouseY - getY()) / BaseScreen.SLOT_SIZE);
//     // terminate if we clicked the border of a slot
//     int slotStartX = getX() + slotX * BaseScreen.SLOT_SIZE + 1, slotStartY = getY() + slotY * BaseScreen.SLOT_SIZE + 1;
//     if (mouseX < slotStartX || mouseX >= slotStartX + BaseScreen.SLOT_SIZE - 2 || mouseY < slotStartY || mouseY >= slotStartY + BaseScreen.SLOT_SIZE - 2) {
//       return null;
//     }
//     // terminate if we aren't looking at a slot on-screen
//     if (slotX < 0 || slotY < 0 || slotX >= xSlots || slotY >= ySlots) {
//       return null;
//     }
//     int slot = (slotY + scrollBar.getCurrentSelection()) * xSlots + slotX;
//     // terminate if the slot doesn't exist
//     if (slot >= list.size()) {
//       return null;
//     }
//     return list.get(slot);
//   }
//
//   private void renderSlot(GuiGraphicsExtractor graphics, SlotItemComponent slot, int slotX, int slotY) {
//     graphics.blit(BaseScreen.BASE_SLOT, slotX, slotY, 0, 0, TextureSizeHelper.getWidth(BaseScreen.BASE_SLOT),
//         TextureSizeHelper.getHeight(BaseScreen.BASE_SLOT), TextureSizeHelper.getWidth(BaseScreen.BASE_SLOT),
//         TextureSizeHelper.getHeight(BaseScreen.BASE_SLOT));
//     ItemStack stack = slot.getItem();
//     if (stack.isEmpty()) {//Sanity check
//       return;
//     }
//     gui().renderItemWithOverlay(graphics, stack, relativeX + slotX + 2 - BaseScreen.SLOT_SIZE / 2,
//         relativeY + slotY + 1 - BaseScreen.SLOT_SIZE / 2, 1, "");
//     long count = slot.getItem().getCount();
//     graphics.pose().pushMatrix();
//     graphics.pose().transform(new Vector3f(0f, 0f, 100f));
//     Component text = null;
//     if (count == 0) {
//       //If there is no items stored, display the text in yellow, similar to what mojang does when it has to display a zero count
//       // See: AbstractContainerScreen#render(GuiGraphicsExtractor, int, int, float) and rendering the dragging item
//       text = ZERO;
//     } else if (count > 1) {
//       //Note: For cases like 9,999,999 we intentionally display as 9999.9K instead of 10M so that people
//       // do not think they have more stored than they actually have just because it is rounding up
//       if (count < 100) {
//         text = TextComponentUtil.getString(Long.toString(count));
//       } else {
//         text = UnitDisplayUtils.getDisplay(count, 1);
//       }
//     }
//     if (text != null) {
//       renderSlotText(graphics, text, slotX + 2 - BaseScreen.SLOT_SIZE / 2, slotY + 1 - BaseScreen.SLOT_SIZE / 2);
//     }
//     graphics.pose().popMatrix();
//   }
//
//   private void renderSlotTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, SlotItemComponent slot, int slotX, int slotY) {
//     ItemStack stack = slot.getItem();
//     if (stack.isEmpty()) {//Sanity check
//       return;
//     }
//     long count = slot.getItem().getCount();
//     if (count < 100) {
//       GuiGraphicsExtractor.setComponentTooltipForNextFrame(font(), List.of(stack.getDisplayName()),
//           slotX - getGuiLeft(),
//           slotY - getGuiTop());
//     } else {
//       //If the slot's displayed count is truncated, make sure we also add the actual amount to the tooltip
//       gui().renderItemTooltipWithExtra(GuiGraphicsExtractor, stack, slotX - getGuiLeft(), slotY - getGuiTop(),
//           Collections.singletonList(Component.literal(NumberFormat.getNumberInstance().format(count)).withStyle(ChatFormatting.GOLD).append(Component.literal("/").withStyle(ChatFormatting.GRAY))
//               .append(Component.literal(NumberFormat.getNumberInstance().format(slot.getMaxStackSize())).withStyle(ChatFormatting.GOLD))));
//     }
//   }
//
//   private void renderSlotText(GuiGraphicsExtractor graphics, Component text, int x, int y) {
//     float scale = 0.6F;
//     float scaledWidth = font().width(text) * scale;
//     if (scaledWidth >= 16) {
//       //If we need a lower scale slightly due to having a lot of text, calculate it
//       //Note: If it would still overflow, then we just let the scrolling text handle it
//       scale = 0.5F;
//     }
//     var pose = graphics.pose();
//     pose.pushMatrix();
//     pose.transform(new Vector3f(0f, 0f, 200f));
//     drawScaledScrollingString(graphics, text, x, y + 9, IFancyFontRenderer.TextAlignment.RIGHT, 0xFFFFFF, 16, 0, true, scale);
//     pose.popMatrix();
//   }
// }
//
