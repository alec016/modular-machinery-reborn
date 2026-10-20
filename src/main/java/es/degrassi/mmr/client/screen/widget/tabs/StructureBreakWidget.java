package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class StructureBreakWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.client.screen.ControllerScreen;
// import es.degrassi.mmr.client.screen.popup.ConfirmationPopup;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.network.client.CBreakStructurePacket;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.core.BlockPos;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.item.Items;
// import net.neoforged.neoforge.network.PacketDistributor;
//
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
//
// @ParametersAreNonnullByDefault
// public class StructureBreakWidget extends TopTabWidget {
//   private final ControllerScreen parentScreen;
//
//   private final Identifier machine;
//   private final BlockPos controllerPos;
//
//   public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_breaker_button");
//
//   public StructureBreakWidget(ControllerScreen parentScreen, Identifier machine, BlockPos controllerPos) {
//     super(0, 0, new ItemOrIconButton(5, 5, Items.DIAMOND_PICKAXE, button -> {}));
//     this.parentScreen = parentScreen;
//     this.machine = machine;
//     this.controllerPos = controllerPos;
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//       GuiGraphicsExtractor.renderTooltip(
//           Minecraft.getInstance().font,
//           List.of(component.getVisualOrderText()),
//           x,
//           y
//       );
//   }
//
//   @Override
//   public void onClick(double mouseX, double mouseY, int button) {
//     parentScreen.setFocused(this);
//     parentScreen.getMenu().getEntity().setLastFocus(1);
//     parentScreen.openPopup(new ConfirmationPopup<>(
//         parentScreen,
//         180,
//         96,
//         () -> PacketDistributor.sendToServer(new CBreakStructurePacket(machine, controllerPos))
//     )
//         .text(Component.translatable("mmr.gui.structure.break"), Component.empty(), Component.empty()), "popup");
//   }
// }
//
