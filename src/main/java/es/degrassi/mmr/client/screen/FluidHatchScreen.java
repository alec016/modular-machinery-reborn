package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class FluidHatchScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import com.google.common.collect.Lists;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.container.FluidHatchContainer;
// import es.degrassi.mmr.client.screen.widget.GuiElement;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoInputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoOutputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ITabGroupScreen;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.client.util.FluidRenderer;
// import es.degrassi.mmr.client.util.GuiUtils;
// import es.degrassi.mmr.common.entity.FluidInputHatchEntity;
// import es.degrassi.mmr.common.entity.FluidOutputHatchEntity;
// import es.degrassi.mmr.common.entity.base.FluidTankEntity;
// import es.degrassi.mmr.common.manager.handler.slot.HybridTank;
// import lombok.Getter;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.client.gui.components.events.GuiEventListener;
// import net.minecraft.client.gui.narration.NarrationElementOutput;
// import net.minecraft.client.input.CharacterEvent;
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.entity.player.Inventory;
// import net.neoforged.neoforge.fluids.FluidStack;
// import org.lwjgl.glfw.GLFW;
//
// import java.util.List;
//
// @Getter
// public class FluidHatchScreen extends BaseScreen<FluidHatchContainer, FluidTankEntity> implements ITabGroupScreen {
//   private TabGroupWidget tabs;
//   private final List<FluidTankWidget> tanks = Lists.newArrayList();
//
//   public FluidHatchScreen(FluidHatchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
//     super(pMenu, pPlayerInventory, pTitle, false);
//   }
//
//   @Override
//   protected void renderLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
//     graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//
//     tabs = TabGroupWidget.createRight(getLeftPos() + getImageWidth(), getTopPos());
//     if (this.entity.getIoType().isInput()) tabs.addTab(new AutoInputTabWidget<>((FluidInputHatchEntity)this.entity));
//     else tabs.addTab(new AutoOutputTabWidget<>((FluidOutputHatchEntity)this.entity));
//
//     addRenderableWidget(tabs);
//
//     int startX = 15, width = 20;
//     for (HybridTank tank : entity.getTank().getInventory()) {
//       tanks.add(addRenderableWidget(new FluidTankWidget(tank, startX + leftPos, 10 + topPos)));
//       startX += width;
//     }
//   }
//
//   @Override
//   public Identifier getTexture() {
//     return ModularMachineryReborn.rl("textures/gui/guibar.png");
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     // render image background:
//     super.renderBg(GuiGraphicsExtractor, partialTick, mouseX, mouseY);
//     renderSlots(GuiGraphicsExtractor);
//   }
//
//   @Override
//   protected void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     tanks.forEach(tank -> tank.renderTooltip(GuiGraphicsExtractor, x, y));
//   }
//
//   @Override
//   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
//     hasClicked = true;
//     for (var element : children()) {
//       if (element instanceof TabGroupWidget widget) {
//         if (widget.mouseClicked(event, doubleClick)) return true;
//       }
//     }
//     GuiEventListener clickedChild = GuiUtils.findChild(children(), event, doubleClick, GuiEventListener::mouseClicked);
//
//     if (clickedChild != null) {
//       setFocused(clickedChild);
//       if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
//         setDragging(true);
//       }
//       return super.mouseClicked(event, doubleClick);
//     } else {
//       //If we can't find a child, allow clearing whatever focus we currently have
//       clearFocus();
//     }
//     return super.mouseClicked(event, doubleClick);
//   }
//
//   @Override
//   public boolean mouseReleased(MouseButtonEvent event) {
//     if (hasClicked) {
//       // always pass mouse released events to windows for drag checks
//       return super.mouseReleased(event);
//     }
//     return false;
//   }
//
//   @Override
//   public boolean keyPressed(KeyEvent event) {
//     return GuiUtils.checkChildren(children(), event,
//         (child, ev) -> child instanceof GuiElement && child.keyPressed(ev)) ||
//         super.keyPressed(event);
//   }
//
//   @Override
//   public boolean charTyped(CharacterEvent event) {
//     return GuiUtils.checkChildrenChar(children(), event,
//         (child, ev) -> child instanceof GuiElement && child.charTyped(ev)) || super.charTyped(event);
//   }
//
//   private static class FluidTankWidget extends AbstractWidget {
//     private final HybridTank tank;
//     public FluidTankWidget(HybridTank tank, int x, int y) {
//       super(x, y, 20, 61, Component.empty());
//       this.tank = tank;
//     }
//
//     @Override
//     protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int x, int y, float v) {
//       FluidRenderer.renderFluid(this.getX(), this.getY(), width, height, tank.getValue(), tank.getCapacity());
//     }
//
//     @Override
//     protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
//
//     }
//
//     public void renderTooltip(GuiGraphicsExtractor graphics, int x, int y) {
//       if (!isMouseOver(x, y)) return;
//       List<Component> text = Lists.newArrayList();
//
//       FluidStack content = tank.getValue();
//       int amt;
//       if (content.getAmount() <= 0) {
//         text.add(Component.translatable("tooltip.fluidhatch.empty"));
//         amt = 0;
//       } else {
//         text.add(content.getHoverName());
//         amt = content.getAmount();
//       }
//       text.add(Component.translatable("tooltip.fluidhatch.tank", String.valueOf(amt), String.valueOf(tank.getCapacity())));
//
//       Font font = Minecraft.getInstance().font;
//       graphics.setTooltipForNextFrame(font, text.stream().map(Component::getVisualOrderText).toList(), x, y);
//     }
//   }
// }
//
