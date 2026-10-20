package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ShowRecipesTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.machine.DynamicMachine;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
//
// @ParametersAreNonnullByDefault
// public class ShowRecipesTabWidget extends TopTabWidget {
//   public ShowRecipesTabWidget(@Nullable ItemOrIconButton icon, DynamicMachine machine) {
//     super(0, 0, icon, (mouseX, mouseY, button) -> {
//     });
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//   }
// }
//
