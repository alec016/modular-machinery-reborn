package es.degrassi.mmr.client.screen.popup;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class CorePopupScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.popup;
//
// import es.degrassi.mmr.client.container.ControllerContainer;
// import es.degrassi.mmr.client.screen.ControllerScreen;
// import es.degrassi.mmr.api.client.Icon;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.crafting.helper.CraftingStatus;
// import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
// import es.degrassi.mmr.common.util.Utils;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.components.StringWidget;
// import net.minecraft.client.gui.layouts.GridLayout;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.util.Mth;
//
// import javax.annotation.ParametersAreNonnullByDefault;
//
// @ParametersAreNonnullByDefault
// public class CorePopupScreen extends PopupScreen<ControllerContainer> {
//   private final int core;
//   private boolean closeButton = false;
//   private final int page;
//   private final int initialXSize;
//   private final Font font = Minecraft.getInstance().font;
//
//   private final StringWidget progress;
//
//   public CorePopupScreen(ControllerScreen parent, int xSize, int ySize,
//                          int page, int core) {
//     super(parent, xSize, ySize);
//     this.core = core;
//     this.page = page;
//     this.initialXSize = xSize;
//     progress = new StringWidget(this.initialXSize - 10, font.lineHeight, Component.empty(), font).alignLeft();
//   }
//
//   public CorePopupScreen addCloseButton() {
//     this.closeButton = true;
//     return this;
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//     MachineProcessorCore core = getMenu().getEntity().getProcessor().cores().get(this.core - 1);
//     GridLayout layout = new GridLayout(this.x + 5, this.y).columnSpacing(2).rowSpacing(5);
//     int cols = 2;
//     GridLayout.RowHelper row = layout.createRowHelper(cols);
//     row.defaultCellSetting().alignHorizontallyLeft();
//
//     row.addChild(new StringWidget(this.initialXSize - 10, 0, Component.literal(""), font), cols);
//
//     if (closeButton) {
//       ItemOrIconButton returnButton = new ItemOrIconButton(0, 0, Icon.BACK, (btn) -> {
//         CoreGridPopupScreen screen = new CoreGridPopupScreen((ControllerScreen) this.parent, 10 * 16 + 5*3 + 5*9, 9 * 20).addCloseButton();
//         screen.setPage(page);
//         this.parent.closePopup(this);
//         this.parent.openPopup(screen, "popup");
//       }).setTooltips(Component.translatable("mmr.gui.button.back"));
//       row.addChild(
//           returnButton,
//           cols,
//           row.newCellSettings().alignHorizontallyRight()
//       );
//     }
//
//     addCoreInfo(row, core);
//
//     layout.arrangeElements();
//     layout.visitWidgets(this::addRenderableWidget);
//   }
//
//   @Override
//   public void containerTick() {
//     MachineProcessorCore core = getMenu().getEntity().getProcessor().cores().get(this.core - 1);
//     if (core.isActive()) {
//       MutableComponent status = Component.translatable("gui.controller.status");
//       if (core.isHasActiveRecipe()) {
//         String percProgress = Utils.decimalFormatWithPercentage(Mth.clamp(core.getCurrentActiveRecipeProgress() * 100F, 0, 100));
//         status.append(Component.translatable("gui.controller.status.crafting.progress", percProgress));
//       } else {
//         status.append(CraftingStatus.NO_RECIPE.getUnlocMessage());
//       }
//       progress.setMessage(status);
//       return;
//     }
//     progress.setMessage(Component.empty());
//   }
//
//   private void addCoreInfo(GridLayout.RowHelper row, MachineProcessorCore core) {
//     row.addChild(
//         new StringWidget(this.initialXSize - 10, font.lineHeight, Component.translatable("mmr.core.active." + core.isActive()), font).alignLeft(),
//         2,
//         row.newCellSettings().alignHorizontallyLeft()
//     );
//
//     row.addChild(
//         new StringWidget(this.initialXSize - 10, font.lineHeight, Component.translatable("mmr.core.number", core.getCore()), font).alignLeft(),
//         2,
//         row.newCellSettings().alignHorizontallyLeft()
//     );
//
//     if (core.isActive()) {
//       row.addChild(
//           progress,
//           2,
//           row.newCellSettings().alignHorizontallyLeft()
//       );
//     }
//   }
// }
//
