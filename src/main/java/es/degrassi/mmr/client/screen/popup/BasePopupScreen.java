package es.degrassi.mmr.client.screen.popup;

import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public abstract class BasePopupScreen<T extends AbstractContainerMenu> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.popup;
//
// import com.google.common.collect.Maps;
// import com.mojang.blaze3d.platform.InputConstants;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.screen.TooltipRender;
// import es.degrassi.mmr.common.util.LRU;
// import lombok.Getter;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.ComponentPath;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.events.GuiEventListener;
// import net.minecraft.client.gui.navigation.FocusNavigationEvent;
// import net.minecraft.client.gui.navigation.ScreenDirection;
// import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
// import net.minecraft.client.input.CharacterEvent;
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.client.renderer.RenderPipelines;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import org.jetbrains.annotations.Nullable;
// import org.lwjgl.glfw.GLFW;
//
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.Collection;
// import java.util.Iterator;
// import java.util.Map;
//
// @ParametersAreNonnullByDefault
// public abstract class BasePopupScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
//   protected static final Identifier BLANK_BACKGROUND = ModularMachineryReborn.rl("background");
//
//   //Position of the top left corner of the popup.
//   public int x;
//   public int y;
//
//   //Size of the screen, not same as width/height which is the size of MC windows.
//   public int xSize;
//   public int ySize;
//   private final LRU<PopupScreen<?>> popups = new LRU<>();
//   private final Map<PopupScreen<?>, String> popupToId = Maps.newHashMap();
//
//   private int freezePopupsTicks;
//
//   @Getter
//   private final Inventory playerInventory;
//
//   protected BasePopupScreen(T pMenu, Inventory pPlayerInventory, Component component, int xSize, int ySize) {
//     super(pMenu, pPlayerInventory, component);
//     this.xSize = xSize;
//     this.ySize = ySize;
//     this.playerInventory = pPlayerInventory;
//   }
//
//   public void openPopup(PopupScreen<?> popup) {
//     if (this.popups.contains(popup))
//       return;
//     this.setFocused(null);
//     this.popups.add(popup);
//     popup.init(this.width, this.height);
//   }
//
//   //Prevents opening another popup with same id
//   public void openPopup(PopupScreen<?> popup, String id) {
//     if (this.popupToId.containsValue(id))
//       return;
//     this.popupToId.put(popup, id);
//     this.openPopup(popup);
//     this.freezePopupsTicks = 40;
//   }
//
//   public void closePopup(PopupScreen<?> popup) {
//     popup.removed();
//     this.popups.remove(popup);
//     this.popupToId.remove(popup);
//   }
//
//   public Collection<PopupScreen<?>> popups() {
//     return this.popups;
//   }
//
//   @Nullable
//   public PopupScreen<?> getPopupUnderMouse(double mouseX, double mouseY) {
//     return this.popups.stream()
//         .filter(popup -> mouseX >= popup.x && mouseX <= popup.x + popup.xSize && mouseY >= popup.y && mouseY <= popup.y + popup.ySize)
//         .findFirst()
//         .orElse(null);
//   }
//
//   @Override
//   public void removed() {
//     this.popups.forEach(PopupScreen::removed);
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//     this.x = (this.width - this.xSize) / 2;
//     this.y = (this.height - this.ySize) / 2;
//     this.popups.forEach(popup -> popup.init(this.width, this.height));
//   }
//
//   @Override
//   public void containerTick() {
//     this.popups.forEach(PopupScreen::containerTick);
//     if (this.freezePopupsTicks > 0)
//       this.freezePopupsTicks--;
//   }
//
//   @Override
//   public void resize(int width, int height) {
//     this.x = (width - this.xSize) / 2;
//     this.y = (height - this.ySize) / 2;
//     super.resize(width, height);
//   }
//
//   public abstract void renderBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);
//
//   @Override
//   public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
//     PopupScreen<?> hoveredPopup = this.getPopupUnderMouse(mouseX, mouseY);
//
//     graphics.pose().pushMatrix();
//
//     if (hoveredPopup != null)
//       this.renderBackground(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
//     else
//       this.renderBackground(graphics, mouseX, mouseY, partialTicks);
//
//     for (Iterator<PopupScreen<?>> iterator = this.popups.descendingIterator(); iterator.hasNext(); ) {
//       graphics.pose().translate(0, 0, 305); //Items are rendered at z=232, item text count at 300, tooltips z=400
//       PopupScreen<?> popup = iterator.next();
//       if (hoveredPopup == popup)
//         popup.renderWithTooltip(graphics, mouseX, mouseY, partialTicks);
//       else
//         popup.render(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
//     }
//
//     graphics.pose().popMatrix();
//   }
//
//   @Override
//   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.isMouseOver(event.x(), event.y())) {
//         boolean clicked = popup.mouseClicked(event, doubleClick);
//         if (this.freezePopupsTicks <= 0)
//           this.popups.moveUp(popup);
//         return clicked;
//       }
//     }
//     for (GuiEventListener guieventlistener : this.children()) {
//       if (guieventlistener.mouseClicked(event, doubleClick)) {
//         this.setFocused(guieventlistener);
//         if (event.button() == 0) {
//           this.setDragging(true);
//         }
//
//         return true;
//       }
//     }
//
//     return false;
//   }
//
//   @Override
//   public void extractTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     getChildAt(mouseX, mouseY)
//         .filter(listener -> listener instanceof TooltipRender)
//         .map(listener -> (TooltipRender) listener)
//         .ifPresent(render -> render.renderTooltip(GuiGraphicsExtractor, mouseX, mouseY));
//   }
//
//   @Override
//   public boolean mouseReleased(MouseButtonEvent event) {
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.isMouseOver(event.x(), event.y())) {
//         boolean released = popup.mouseReleased(event);
//         if (this.freezePopupsTicks <= 0)
//           this.popups.moveUp(popup);
//         return released;
//       }
//     }
//     this.setDragging(false);
//     if (this.getFocused() != null && this.getFocused().mouseReleased(event))
//       return true;
//     return this.getChildAt(event.x(), event.y()).filter(guiEventListener -> guiEventListener.mouseReleased(event)).isPresent();
//   }
//
//   @Override
//   public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
//     for (PopupScreen<?> popup : this.popups) {
//       boolean dragged = popup.mouseDragged(event, dx, dy);
//       if (this.freezePopupsTicks <= 0)
//         this.popups.moveUp(popup);
//       return dragged;
//     }
//     return this.getFocused() != null && this.isDragging() && event.button() == 0 && this.getFocused().mouseDragged(event, dx, dy);
//   }
//
//   @Override
//   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.isMouseOver(mouseX, mouseY)) {
//         boolean scrolled = popup.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
//         if (this.freezePopupsTicks <= 0)
//           this.popups.moveUp(popup);
//         return scrolled;
//       }
//     }
//     return this.getChildAt(mouseX, mouseY).filter(p_293596_ -> p_293596_.mouseScrolled(mouseX, mouseY, scrollX, scrollY)).isPresent();
//   }
//
//   @Override
//   public boolean keyPressed(KeyEvent event) {
//     if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
//       if (!this.popups.isEmpty()) {
//         PopupScreen<?> toClose = this.getPopupUnderMouse(Minecraft.getInstance().mouseHandler.xpos(),
//             Minecraft.getInstance().mouseHandler.ypos());
//         if (toClose == null)
//           toClose = this.popups.iterator().next();
//         this.closePopup(toClose);
//         return true;
//       }
//       if (this.getFocused() != null && this.getFocused().keyPressed(event))
//         return true;
//       this.onClose();
//       return true;
//     }
//
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.keyPressed(event))
//         return true;
//     }
//
//     if (this.getFocused() != null && this.getFocused().keyPressed(event))
//       return true;
//
//     FocusNavigationEvent ev = switch (event.key()) {
//       case GLFW.GLFW_KEY_LEFT -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.LEFT);
//       case GLFW.GLFW_KEY_RIGHT -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.RIGHT);
//       case GLFW.GLFW_KEY_UP -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.UP);
//       case GLFW.GLFW_KEY_DOWN -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.DOWN);
//       case GLFW.GLFW_KEY_TAB -> new FocusNavigationEvent.TabNavigation(!event.hasShiftDown());
//       default -> null;
//     };
//
//     if (ev != null) {
//       ComponentPath path =
//           this.popups.stream().findFirst().map(popup -> popup.nextFocusPath(ev)).orElse(this.nextFocusPath(ev));
//       if (path == null && ev instanceof FocusNavigationEvent.TabNavigation) {
//         ComponentPath componentPath = this.getCurrentFocusPath();
//         if (componentPath != null)
//           componentPath.applyFocus(false);
//         path = super.nextFocusPath(ev);
//       }
//
//       if (path != null)
//         this.changeFocus(path);
//
//       return true;
//     }
//
//     if (event.key() == 256 && this.shouldCloseOnEsc()) {
//       this.onClose();
//       return true;
//     } else if (super.keyPressed(event)) {
//       return true;
//     } else {
//       FocusNavigationEvent focusnavigationevent = switch (event.key()) {
//         case 258 -> this.createTabEvent();
//         case 262 -> this.createArrowEvent(ScreenDirection.RIGHT);
//         case 263 -> this.createArrowEvent(ScreenDirection.LEFT);
//         case 264 -> this.createArrowEvent(ScreenDirection.DOWN);
//         case 265 -> this.createArrowEvent(ScreenDirection.UP);
//         default -> null;
//       };
//       if (focusnavigationevent != null) {
//         ComponentPath componentpath = super.nextFocusPath(focusnavigationevent);
//         if (componentpath == null && focusnavigationevent instanceof FocusNavigationEvent.TabNavigation) {
//           this.clearFocus();
//           componentpath = super.nextFocusPath(focusnavigationevent);
//         }
//
//         if (componentpath != null) {
//           this.changeFocus(componentpath);
//         }
//       }
//
//       return false;
//     }
//   }
//
//   private static boolean hasShiftDown() {
//     return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
//   }
//
//   private FocusNavigationEvent.TabNavigation createTabEvent() {
//     boolean flag = !hasShiftDown();
//     return new FocusNavigationEvent.TabNavigation(flag);
//   }
//
//   private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection direction) {
//     return new FocusNavigationEvent.ArrowNavigation(direction);
//   }
//
//   @Override
//   public boolean keyReleased(KeyEvent event) {
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.keyReleased(event))
//         return true;
//     }
//     return this.getFocused() != null && this.getFocused().keyReleased(event);
//   }
//
//   @Override
//   public boolean charTyped(CharacterEvent event) {
//     for (PopupScreen<?> popup : this.popups) {
//       if (popup.charTyped(event))
//         return true;
//     }
//     return this.getFocused() != null && this.getFocused().charTyped(event);
//   }
//
//   @Override
//   public boolean isMouseOver(double mouseX, double mouseY) {
//     if (this.getPopupUnderMouse(mouseX, mouseY) != null)
//       return false;
//     return mouseX >= this.x && mouseX <= this.x + this.xSize && mouseY >= this.y && mouseY <= this.y + this.ySize;
//   }
//
//   @Override
//   public void setFocused(@Nullable GuiEventListener focused) {
//     if (this.getFocused() != focused)
//       super.setFocused(focused);
//     if (focused != null)
//       this.popups.forEach(popup -> popup.setFocused(null));
//   }
//
//   public static void blankBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
//     graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BLANK_BACKGROUND, x, y, width, height);
//   }
//
//   public static void drawCenteredScaledString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, float scale, int color, boolean shadow) {
//     graphics.pose().pushMatrix();
//     graphics.pose().scale(scale, scale);
//     graphics.text(font, text, (int) ((x - (font.width(text) * scale) / 2) / scale), (int) ((y - (float) font.lineHeight / 2) / scale), color, shadow);
//     graphics.pose().popMatrix();
//   }
//
//   public static void drawScaledString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, float scale, int color, boolean shadow) {
//     graphics.pose().pushMatrix();
//     graphics.pose().scale(scale, scale);
//     graphics.text(font, text, (int) (x / scale), (int) (y / scale), color, shadow);
//     graphics.pose().popMatrix();
//   }
// }
//
