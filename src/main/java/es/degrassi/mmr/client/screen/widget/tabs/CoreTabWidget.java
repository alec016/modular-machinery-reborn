package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class CoreTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.ControllerScreen;
// import es.degrassi.mmr.client.screen.popup.CoreGridPopupScreen;
// import es.degrassi.mmr.api.client.Icon;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.network.chat.Component;
//
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
//
// @ParametersAreNonnullByDefault
// public class CoreTabWidget extends TopTabWidget {
//   private final ControllerScreen parentScreen;
//
//   public final Component component = Component.translatable("modular_machinery_reborn.gui.core_button");
//
//   public CoreTabWidget(ControllerScreen parentScreen) {
//     super(0, 0, new ItemOrIconButton(5, 5, Icon.CHECK, (button) -> {}));
//     this.parentScreen = parentScreen;
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     GuiGraphicsExtractor.renderTooltip(
//         Minecraft.getInstance().font,
//         List.of(component.getVisualOrderText()),
//         x,
//         y
//     );
//   }
//
//   @Override
//   public void onClick(double mouseX, double mouseY, int button) {
//     parentScreen.setFocused(this);
//     parentScreen.getMenu().getEntity().setLastFocus(2);
//     parentScreen.openPopup(new CoreGridPopupScreen(
//         parentScreen,
//         10 * 16 + 5*3 + 5*9,
//         9 * 20
//     ).addCloseButton(), "popup");
//   }
// }
//
