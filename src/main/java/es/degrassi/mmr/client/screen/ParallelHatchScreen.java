package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ParallelHatchScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.screen.TooltipRender;
// import es.degrassi.mmr.client.container.ParallelHatchContainer;
// import es.degrassi.mmr.client.screen.widget.CoreActionButton;
// import es.degrassi.mmr.api.client.CoreActionType;
// import es.degrassi.mmr.common.entity.ParallelHatchEntity;
// import es.degrassi.mmr.common.network.client.CCoreButtonClickedPacked;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.MultiLineTextWidget;
// import net.minecraft.client.gui.layouts.GridLayout;
// import net.minecraft.client.gui.screens.Screen;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.inventory.Slot;
// import net.neoforged.neoforge.network.PacketDistributor;
// import org.jetbrains.annotations.Nullable;
//
// public class ParallelHatchScreen extends BaseScreen<ParallelHatchContainer, ParallelHatchEntity> {
//   public ParallelHatchScreen(ParallelHatchContainer menu, Inventory playerInventory, Component title) {
//     super(menu, playerInventory, title, false);
//   }
//
//   @Override
//   protected void renderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     GuiGraphicsExtractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
//   }
//
//   @Override
//   public @Nullable Identifier getTexture() {
//     return ModularMachineryReborn.rl("background");
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     clearWidgets();
//     if (getTexture() != null) {
//       GuiGraphicsExtractor.pose().pushMatrix();
//       this.leftPos = (this.width - this.imageWidth) / 2;
//       this.topPos = (this.height - this.imageHeight) / 2;
//       GuiGraphicsExtractor.blitSprite(getTexture(), leftPos, topPos, 176, 176);
//       GuiGraphicsExtractor.pose().popMatrix();
//     }
//     GridLayout layout = new GridLayout(leftPos + 8, topPos + 25);
//     GridLayout.RowHelper row = layout.createRowHelper(5);
//     row.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().paddingHorizontal(20);
//     row.addChild(
//         new CoreActionButton(this::action, CoreActionType.REMOVE).renderTooltip(true)
//     );
//     row.addChild(
//         new MultiLineTextWidget(Component.literal(String.format("%s", getMenu().getEntity().getCores())), font),
//         3,
//         row.newCellSettings().alignVerticallyMiddle().alignHorizontallyCenter()
//     );
//     row.addChild(
//         new CoreActionButton(this::action, CoreActionType.ADD).renderTooltip(true)
//     );
//     layout.arrangeElements();
//     layout.visitWidgets(this::addRenderableWidget);
//     for (Slot slot : getMenu().slots) {
//       GuiGraphicsExtractor.blit(BASE_SLOT, slot.x + getLeftPos() - 1, slot.y + getTopPos() - 1, 0, 0,
//           TextureSizeHelper.getWidth(BASE_SLOT),
//           TextureSizeHelper.getHeight(BASE_SLOT),
//           TextureSizeHelper.getWidth(BASE_SLOT),
//           TextureSizeHelper.getHeight(BASE_SLOT));
//     }
//   }
//
//   private void action(CoreActionType action) {
//     int amount = Screen.hasShiftDown() ? 10 : Screen.hasControlDown() ? -1 : 1;
//     int minAmount = 1;
//     int maxAmount = getMenu().getEntity().getSize().max;
//     int toSet = getMenu().getEntity().getCores();
//     if (action == CoreActionType.ADD) {
//       if (amount == -1) {
//         toSet = maxAmount;
//       } else {
//         toSet += amount;
//         toSet = Math.min(maxAmount, toSet);
//       }
//     } else if (action == CoreActionType.REMOVE) {
//       if (amount == -1) {
//         toSet = minAmount;
//       } else {
//         toSet -= amount;
//         toSet = Math.max(minAmount, toSet);
//       }
//     }
//     PacketDistributor.sendToServer(new CCoreButtonClickedPacked(getMenu().getEntity().getBlockPos(), toSet));
//   }
//
//   @Override
//   protected void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     this.children()
//         .stream()
//         .filter(listener -> listener instanceof TooltipRender)
//         .map(listener -> (TooltipRender) listener)
//         .forEach(renderer -> renderer.renderTooltip(GuiGraphicsExtractor, x, y));
//   }
// }
//
