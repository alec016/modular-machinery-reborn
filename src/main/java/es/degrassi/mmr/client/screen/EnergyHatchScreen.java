package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class EnergyHatchScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.container.EnergyHatchContainer;
// import es.degrassi.mmr.client.screen.widget.GuiElement;
// import es.degrassi.mmr.client.screen.widget.IGuiWrapper;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoInputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoOutputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ITabGroupScreen;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.client.util.EnergyDisplayUtil;
// import es.degrassi.mmr.client.util.GuiUtils;
// import es.degrassi.mmr.common.entity.EnergyInputHatchEntity;
// import es.degrassi.mmr.common.entity.EnergyOutputHatchEntity;
// import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import lombok.Getter;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.events.GuiEventListener;
// import net.minecraft.client.input.CharacterEvent;
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.client.renderer.RenderPipelines;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.util.Mth;
// import net.minecraft.world.entity.player.Inventory;
// import org.lwjgl.glfw.GLFW;
//
// import java.util.List;
//
// @Getter
// public class EnergyHatchScreen extends BaseScreen<EnergyHatchContainer, EnergyHatchEntity> implements IGuiWrapper, ITabGroupScreen {
//   private TabGroupWidget tabs;
//   public EnergyHatchScreen(EnergyHatchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
//     super(pMenu, pPlayerInventory, pTitle, false);
//   }
//
//   @Override
//   protected void renderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     GuiGraphicsExtractor.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//
//     tabs = TabGroupWidget.createRight(getLeftPos() + getImageWidth(), getTopPos());
//     if (this.entity.getMode().isInput()) tabs.addTab(new AutoInputTabWidget<>((EnergyInputHatchEntity) this.entity));
//     else tabs.addTab(new AutoOutputTabWidget<>((EnergyOutputHatchEntity) this.entity));
//
//     addRenderableWidget(tabs);
//   }
//
//   @Override
//   public Identifier getTexture() {
//     return ModularMachineryReborn.rl("textures/gui/guibar.png");
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
//     // render image background:
//     super.renderBg(graphics, partialTick, mouseX, mouseY);
//     graphics.pose().pushMatrix();
//     float percFilled = ((float) entity.getCurrentEnergy()) / ((float) entity.getMaxEnergy());
//     int pxFilled = Mth.ceil(percFilled * 61F);
//     graphics.blit(
//         RenderPipelines.GUI_TEXTURED,
//         getTexture(),
//         leftPos + 15,
//         topPos + 10 + 61 - pxFilled,
//         196f,
//         61f - pxFilled,
//         20,
//         pxFilled,
//         TextureSizeHelper.getWidth(getTexture()),
//         TextureSizeHelper.getHeight(getTexture())
//     );
//     graphics.pose().popMatrix();
//     renderSlots(graphics);
//   }
//
//   @Override
//   protected void renderTooltip(GuiGraphicsExtractor graphics, int x, int y) {
//     super.renderTooltip(graphics, x, y);
//
//     int offsetX = (this.width - this.getImageWidth()) / 2;
//     int offsetZ = (this.height - this.getImageHeight()) / 2;
//
//     if(x >= 15 + offsetX && x <= 35 + offsetX && y >= 10 + offsetZ && y <= 71 + offsetZ) {
//       long currentEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(entity.getCurrentEnergy());
//       long maxEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(entity.getMaxEnergy());
//
//       Component text = Component.translatable("tooltip.energyhatch.charge",
//           String.valueOf(currentEnergy),
//           String.valueOf(maxEnergy),
//           Component.translatable(EnergyDisplayUtil.type.getUnlocalizedFormat()));
//
//       Font font = Minecraft.getInstance().font;
//       graphics.setComponentTooltipForNextFrame(font, List.of(text), x, y);
//     }
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
//     return GuiUtils.checkChildren(children(),
//         event,
//         (child, ev) -> child instanceof GuiElement && child.keyPressed(ev)) ||
//         super.keyPressed(event);
//   }
//
//   @Override
//   public boolean charTyped(CharacterEvent event) {
//     return GuiUtils.checkChildrenChar(children(),
//         event,
//         (child, ev) -> child instanceof GuiElement && child.charTyped(ev)) || super.charTyped(event);
//   }
//
//   @Override
//   public int getSlotColor() {
//     return 0;
//   }
// }
//
