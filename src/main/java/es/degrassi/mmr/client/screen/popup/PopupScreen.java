package es.degrassi.mmr.client.screen.popup;

import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public abstract class PopupScreen<T extends AbstractContainerMenu> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.popup;
//
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.Renderable;
// import net.minecraft.client.gui.components.events.GuiEventListener;
// import net.minecraft.client.gui.layouts.LayoutElement;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.network.chat.Component;
// import net.minecraft.world.inventory.AbstractContainerMenu;
//
// import javax.annotation.ParametersAreNonnullByDefault;
//
// @ParametersAreNonnullByDefault
// public abstract class PopupScreen<T extends AbstractContainerMenu> extends BasePopupScreen<T> {
//
//   public final BasePopupScreen<T> parent;
//
//   private boolean dragging;
//   private double dragX;
//   private double dragY;
//
//   protected PopupScreen(BasePopupScreen<T> parent, int xSize, int ySize) {
//     super(parent.getMenu(), parent.getPlayerInventory(), Component.literal("Popup"), xSize, ySize);
//     this.parent = parent;
//   }
//
//   public void move(int movedX, int movedY) {
//     this.x += movedX;
//     this.y += movedY;
//     this.children().forEach(c -> {
//       if (c instanceof LayoutElement widget)
//         widget.setPosition(widget.getX() + movedX, widget.getY() + movedY);
//     });
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
//     if ((int) Math.abs(this.dragX) >= 1 || (int) Math.abs(this.dragY) >= 1) {
//       int changedX = (int) this.dragX;
//       int changedY = (int) this.dragY;
//       this.move(changedX, changedY);
//       this.dragX -= changedX;
//       this.dragY -= changedY;
//     }
//     boolean parentHasTooltip = this.parent.deferredTooltipRendering != null;
//     for (Renderable renderable : this.renderables) {
//       renderable.render(graphics, mouseX, mouseY, partialTicks);
//     }
//     //If parent didn't have tooltip before rendering this popup, but now have it that means the tooltips were added by this popup.
//     //So we move them to this popup tooltips
//     if (!parentHasTooltip && this.parent.deferredTooltipRendering != null) {
//       if (this.deferredTooltipRendering == null)
//         this.deferredTooltipRendering = this.parent.deferredTooltipRendering;
//       this.parent.deferredTooltipRendering = null;
//     }
//
//     extractTooltip(graphics, mouseX, mouseY);
//   }
//
//   @Override
//   public void renderBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
//     blankBackground(graphics, this.x, this.y, this.xSize, this.ySize);
//     renderBg(graphics, partialTicks, mouseX, mouseY);
//   }
//
//   @Override
//   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
//     boolean onRenderable = false;
//     for (GuiEventListener renderable : this.children()) {
//       if (renderable.mouseClicked(event, doubleClick)) {
//         onRenderable = true;
//         break;
//       }
//     }
//     if (onRenderable || super.mouseClicked(event, doubleClick))
//       return true;
//     if (isMouseOver(event.x(), event.y()) && event.y() < this.y + 20) {
//       this.dragging = true;
//       return true;
//     } else {
//       this.setDragging(false);
//       return false;
//     }
//   }
//
//   @Override
//   public boolean mouseReleased(MouseButtonEvent event) {
//     boolean onRenderable = false;
//     for (GuiEventListener renderable : this.children()) {
//       if (renderable.mouseReleased(event)) {
//         onRenderable = true;
//         break;
//       }
//     }
//     this.dragging = false;
//     return onRenderable || super.mouseReleased(event);
//   }
//
//   @Override
//   public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
//     if (this.dragging) {
//       this.dragX += dx;
//       this.dragY += dy;
//     }
//     return super.mouseDragged(event, dx, dy);
//   }
//
//   @Override
//   public boolean isMouseOver(double mouseX, double mouseY) {
//     boolean onRenderable = false;
//     for (GuiEventListener renderable : this.children()) {
//       if (renderable.isMouseOver(mouseX, mouseY)) {
//         onRenderable = true;
//         break;
//       }
//     }
//     return onRenderable || this.parent.getPopupUnderMouse(mouseX, mouseY) == this;
//   }
// }
//
