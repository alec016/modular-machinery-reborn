package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class FuelTankScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.container.FuelTankContainer;
// import es.degrassi.mmr.client.screen.widget.FuelWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoInputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoOutputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ITabGroupScreen;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.common.entity.FuelTankEntity;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import lombok.Getter;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.entity.player.Inventory;
// import org.jetbrains.annotations.Nullable;
//
// public class FuelTankScreen extends BaseScreen<FuelTankContainer, FuelTankEntity> implements ITabGroupScreen {
//   private FuelWidget fuelWidget;
//   @Getter
//   private TabGroupWidget tabs;
//   public FuelTankScreen(FuelTankContainer menu, Inventory playerInventory, Component title) {
//     super(menu, playerInventory, title, true);
//   }
//
//   @Override
//   @Nullable
//   public Identifier getTexture() {
//     return ModularMachineryReborn.rl("textures/gui/guiexperience.png");
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//     this.fuelWidget = new FuelWidget(16 + getGuiLeft(), 10 + 61/2 - 8 + getGuiTop(), getMenu().getEntity().getFuelHandler());
//
//     tabs = TabGroupWidget.createRight(getGuiLeft() + getXSize(), getGuiTop());
//     tabs.addTab(new AutoInputTabWidget<>(this.entity));
//
//     addRenderableWidget(tabs);
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     // render image background:
//     super.renderBg(GuiGraphicsExtractor, partialTick, mouseX, mouseY);
//     renderSlots(GuiGraphicsExtractor);
//     GuiGraphicsExtractor.pose().pushPose();
//     fuelWidget.render(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//     GuiGraphicsExtractor.pose().popPose();
//   }
//
//   @Override
//   protected void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     if (fuelWidget.isMouseOver(x, y)) {
//       fuelWidget.renderTooltip(GuiGraphicsExtractor, x, y);
//     }
//   }
// }
//
