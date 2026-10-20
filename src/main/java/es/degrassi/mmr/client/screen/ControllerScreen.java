package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ControllerScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import com.mojang.blaze3d.systems.RenderSystem;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.machine.TooltipUse;
// import es.degrassi.mmr.client.container.ControllerContainer;
// import es.degrassi.mmr.client.screen.popup.BasePopupScreen;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.client.screen.widget.tabs.ControllerExtraTooltipsTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.CoreTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ShowRecipesTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.StructureBreakWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.StructurePlacerWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.TopTabGroupWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.TopTabWidget;
// import es.degrassi.mmr.common.machine.DynamicMachine;
// import es.degrassi.mmr.common.util.Mods;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import lombok.Getter;
// import net.minecraft.ChatFormatting;
// import net.minecraft.util.Util;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.Renderable;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.resources.Identifier;
// import net.minecraft.util.FormattedCharSequence;
// import net.minecraft.util.Mth;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.inventory.Slot;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Items;
//
// import java.util.List;
// import java.util.Optional;
//
// @Getter
// public class ControllerScreen extends BasePopupScreen<ControllerContainer> {
//   protected static final Identifier BASE_SLOT = ModularMachineryReborn.rl("textures/gui/base_slot.png");
//   protected static final Identifier BASE_SLOT_HOVERED = ModularMachineryReborn.rl("textures/gui/base_slot_hovered.png");
//   private static final int screenWidth = 158;
//
//   private TopTabGroupWidget tabs;
//
//   public ControllerScreen(ControllerContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
//     super(pMenu, pPlayerInventory, pTitle, TextureSizeHelper.getWidth(getTexture()), TextureSizeHelper.getHeight(getTexture()));
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//     createWidgets();
//   }
//
//   @Override
//   public void removed() {
//     super.removed();
//     if (this.minecraft.player != null) {
//       this.menu.removed(this.minecraft.player);
//     }
//   }
//
//   private void createWidgets() {
//     tabs = addRenderableWidget(TabGroupWidget.createTop(x, y - TextureSizeHelper.getHeight(TopTabWidget.TAB)));
//     tabs.addTab(new StructurePlacerWidget(
//             this,
//             getMenu().getId(),
//             getMenu().getEntity().getBlockPos())
//         )
//         .addTab(
//             new StructureBreakWidget(
//                 this,
//                 getMenu().getId(),
//                 getMenu().getEntity().getBlockPos()
//             )
//         )
//         .addTab(new CoreTabWidget(this));
//     Optional.ofNullable(ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(getMenu().getId()))
//         .map(enumTooltips -> Optional.ofNullable(enumTooltips.get(TooltipUse.GUI)).orElse(List.of()))
//         .ifPresent(tooltips -> {
//           if (tooltips.isEmpty()) return;
//           tabs.addTab(new ControllerExtraTooltipsTabWidget(getMenu().getId(), getMenu()));
//         });
//     if (Mods.isJEILoaded())
//       tabs.addTab(new ShowRecipesTabWidget(new ItemOrIconButton(0, 0, Items.CRAFTING_TABLE, (b) -> {}), getMenu().getEntity().getFoundMachine()));
//     tabs.setInitialFocus(getMenu().getEntity().getLastFocus());
//   }
//
//   public static Identifier getTexture() {
//     return ModularMachineryReborn.rl("textures/gui/guicontroller.png");
//   }
//
//   public void renderBackground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
//     int i = this.x;
//     int j = this.y;
//
//     // render image background
//     this.renderTransparentBackground(GuiGraphicsExtractor);
//     renderBg(GuiGraphicsExtractor, partialTicks, mouseX, mouseY);
//
//     for (Renderable renderable : this.renderables) {
//       renderable.render(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
//     }
//     // Neo: replicate the super method's implementation to insert the event between background and widgets
//     RenderSystem.disableDepthTest();
//     GuiGraphicsExtractor.pose().pushPose();
//     GuiGraphicsExtractor.pose().translate((float) i, (float) j, 0.0F);
//     this.hoveredSlot = null;
//
//     for (int k = 0; k < this.menu.slots.size(); k++) {
//       Slot slot = this.menu.slots.get(k);
//       if (slot.isActive()) {
//         this.renderSlot(GuiGraphicsExtractor, slot);
//         if (this.isHovering(slot, mouseX, mouseY)) {
//           this.hoveredSlot = slot;
//           this.renderSlotHighlight(GuiGraphicsExtractor, slot, mouseX, mouseY, partialTicks);
//         }
//       }
//     }
//
//     this.renderLabels(GuiGraphicsExtractor, mouseX, mouseY);
//     ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
//     if (!itemstack.isEmpty()) {
//       int l1 = 8;
//       int i2 = this.draggingItem.isEmpty() ? 8 : 16;
//       String s = null;
//       if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
//         itemstack = itemstack.copyWithCount(Mth.ceil((float) itemstack.getCount() / 2.0F));
//       } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
//         itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
//         if (itemstack.isEmpty()) {
//           s = ChatFormatting.YELLOW + "0";
//         }
//       }
//
//       this.renderFloatingItem(GuiGraphicsExtractor, itemstack, mouseX - i - 8, mouseY - j - i2, s);
//     }
//
//     if (!this.snapbackItem.isEmpty()) {
//       float f = (float) (Util.getMillis() - this.snapbackTime) / 100.0F;
//       if (f >= 1.0F) {
//         f = 1.0F;
//         this.snapbackItem = ItemStack.EMPTY;
//       }
//
//       int j2 = this.snapbackEnd.x - this.snapbackStartX;
//       int k2 = this.snapbackEnd.y - this.snapbackStartY;
//       int j1 = this.snapbackStartX + (int) ((float) j2 * f);
//       int k1 = this.snapbackStartY + (int) ((float) k2 * f);
//       this.renderFloatingItem(GuiGraphicsExtractor, this.snapbackItem, j1, k1, null);
//     }
//
//     GuiGraphicsExtractor.pose().popPose();
//
//     RenderSystem.enableDepthTest();
//
//     GuiGraphicsExtractor.pose().pushPose();
//     GuiGraphicsExtractor.pose().translate(this.x, this.y, 0);
//     float scale = 0.72f;
//     GuiGraphicsExtractor.pose().scale(scale, scale, scale);
//     int offsetX = 14;
//     int offsetY = 14;
//
//     DynamicMachine machine = getMenu().getEntity().getFoundMachine();
//     if (machine != DynamicMachine.DUMMY) {
//       // render if the structure of machine is not null
//       List<FormattedCharSequence> out = font.split(Component.literal(machine.getLocalizedName()), Mth.floor(screenWidth * (1 / scale)));
//       offsetY -= 7;
//       for (FormattedCharSequence draw : out) {
//         offsetY += 7;
//         GuiGraphicsExtractor.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
//         offsetY += 7;
//       }
//       offsetY -= 7;
//     } else {
//       // render if the structure of machine is null
//       Component drawnHead = Component.translatable("gui.controller.structure", Component.translatable("gui.controller.structure.none"));
//       GuiGraphicsExtractor.drawString(font, drawnHead, offsetX, offsetY, 0xFFFFFF);
//     }
//     offsetY += 10;
//
//     if (getMenu().getEntity().isPaused()) {
//       // render if redstone paused the machine
//       Component drawnStop = Component.translatable("gui.controller.status.redstone_stopped");
//       List<FormattedCharSequence> out = font.split(drawnStop, Mth.floor(screenWidth * (1 / scale)));
//       for (FormattedCharSequence draw : out) {
//         offsetY += 7;
//         GuiGraphicsExtractor.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
//         offsetY += 7;
//       }
//       GuiGraphicsExtractor.pose().popPose();
//       renderTooltip(GuiGraphicsExtractor, mouseX, mouseY);
//       return;
//     }
//
//     MutableComponent errorInfos = getMenu().getEntity().getErrorInfo().isEmpty() ? Component.empty() : Component.translatable("gui.controller.error.info");
//     List<FormattedCharSequence> o = font.split(
//         errorInfos.append(getMenu().getEntity().getErrorInfo().get()),
//         Mth.floor(screenWidth * (1 / scale))
//     );
//
//     if (!getMenu().getEntity().getErrorInfo().isEmpty()) {
//       for (FormattedCharSequence draw : o) {
//         offsetY += 7;
//         GuiGraphicsExtractor.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
//         offsetY += 7;
//       }
//     }
//
//     // render the current status
//     MutableComponent status = Component.translatable("gui.controller.status");
//     List<FormattedCharSequence> out = font.split(status.append(getMenu().getEntity().getCraftingStatus().getUnlocMessage()), Mth.floor(screenWidth * (1 / scale)));
//     for (FormattedCharSequence draw : out) {
//       offsetY += 7;
//       GuiGraphicsExtractor.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
//       offsetY += 7;
//     }
//     GuiGraphicsExtractor.pose().popPose();
//     renderTooltip(GuiGraphicsExtractor, mouseX, mouseY);
//   }
//
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     GuiGraphicsExtractor.pose().pushPose();
//     GuiGraphicsExtractor.setColor(1f, 1f, 1f, 1f);
//     int xSize = getXSize();
//     int ySize = getYSize();
//     int leftPos = (this.width - xSize) / 2;
//     int topPos = (this.height - ySize) / 2;
//     GuiGraphicsExtractor.blit(getTexture(), leftPos, topPos, 0, 0, xSize, ySize, xSize, ySize);
//     GuiGraphicsExtractor.pose().popPose();
//   }
//
//   protected void renderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     // Do not render the default texts
//   }
//
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
//       ItemStack itemstack = this.hoveredSlot.getItem();
//       GuiGraphicsExtractor.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
//     }
//
//     tabs.renderTooltip(GuiGraphicsExtractor, x, y);
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//   }
//
//   protected void renderSlotHighlight(GuiGraphicsExtractor GuiGraphicsExtractor, Slot slot, int mouseX, int mouseY, float partialTick) {
//     if (slot.isHighlightable()) {
//       renderSlotHighlight(GuiGraphicsExtractor, slot.x, slot.y, getSlotColor(slot.index));
//     }
//   }
//
//   public static void renderSlotHighlight(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y, int color) {
//     GuiGraphicsExtractor.pose().pushPose();
//     int width = TextureSizeHelper.getWidth(BASE_SLOT_HOVERED), height = TextureSizeHelper.getHeight(BASE_SLOT_HOVERED);
//     GuiGraphicsExtractor.blit(BASE_SLOT_HOVERED, x - 1, y - 1, 0, 0, width, height, width, height);
//     GuiGraphicsExtractor.fillGradient(x, y, x + 16, y + 16, color, color);
//     GuiGraphicsExtractor.pose().popPose();
//   }
// }
//
