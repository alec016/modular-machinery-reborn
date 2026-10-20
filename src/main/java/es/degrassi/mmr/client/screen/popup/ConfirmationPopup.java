package es.degrassi.mmr.client.screen.popup;

import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ConfirmationPopup<T extends AbstractContainerMenu> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.popup;
//
// import es.degrassi.mmr.client.screen.widget.StringButton;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.components.Button;
// import net.minecraft.client.gui.components.MultiLineTextWidget;
// import net.minecraft.client.gui.components.StringWidget;
// import net.minecraft.client.gui.layouts.GridLayout;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import org.jetbrains.annotations.Nullable;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
//
// public class ConfirmationPopup<T extends AbstractContainerMenu> extends PopupScreen<T> {
//
//   public Component CONFIRM = Component.translatable("mmr.gui.popup.confirm").withStyle(ChatFormatting.GREEN);
//   public Component CANCEL = Component.translatable("mmr.gui.popup.cancel").withStyle(ChatFormatting.RED);
//
//   private final Runnable onConfirm;
//   private final List<Component> text = new ArrayList<>();
//   private Component title;
//   @Nullable
//   private Runnable onCancel;
//
//   private boolean closeButton = false;
//
//   public ConfirmationPopup(BasePopupScreen<T> parent, int xSize, int ySize,
//                            Runnable onConfirm) {
//     super(parent, xSize, ySize);
//     this.onConfirm = onConfirm;
//     this.onCancel = null;
//   }
//
//   public ConfirmationPopup<T> addCloseButton() {
//     this.closeButton = true;
//     return this;
//   }
//
//   public ConfirmationPopup<T> confirmText(Component confirmText) {
//     CONFIRM = confirmText;
//     return this;
//   }
//
//   public ConfirmationPopup<T> cancelText(Component cancelText) {
//     CANCEL = cancelText;
//     return this;
//   }
//
//   public ConfirmationPopup<T> cancelCallback(Runnable callback) {
//     this.onCancel = callback;
//     return this;
//   }
//
//   public ConfirmationPopup<T> title(Component title) {
//     this.title = title;
//     return this;
//   }
//
//   public ConfirmationPopup<T> text(Component... components) {
//     this.text.addAll(Arrays.asList(components));
//     return this;
//   }
//
//   public void confirm() {
//     this.onConfirm.run();
//     this.parent.closePopup(this);
//   }
//
//   public void cancel() {
//     if (this.onCancel != null)
//       this.onCancel.run();
//     this.parent.closePopup(this);
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//     Font font = Minecraft.getInstance().font;
//     GridLayout layout = new GridLayout(this.x, this.y).columnSpacing(10);
//     GridLayout.RowHelper row = layout.createRowHelper(2);
//     row.defaultCellSetting().padding(5).alignHorizontallyCenter();
//
//     if (closeButton)
//       row.addChild(
//           Button
//               .builder(Component.literal("X"), b -> this.parent.closePopup(this))
//               .bounds(
//                   0,
//                   0,
//                   20,
//                   20
//               )
//               .build(builder -> new StringButton(builder, true).setTooltips(Component.translatable("mmr.gui.button.close"))),
//           2,
//           row.newCellSettings().alignHorizontallyRight()
//       );
//
//     if (this.title != null)
//       row.addChild(new StringWidget(this.xSize, 10, title, font).alignCenter(), 2);
//
//     if (!this.text.isEmpty()) {
//       MutableComponent text = Component.empty();
//       for (Component component : this.text)
//         text.append("\n").append(component);
//       MultiLineTextWidget textWidget = new MultiLineTextWidget(text, font).setCentered(true).setMaxWidth(this.xSize - 10);
//       row.addChild(textWidget, 2, row.newCellSettings().alignHorizontallyLeft());
//     }
//
//     row.addChild(Button.builder(CONFIRM, b -> this.confirm()).bounds(0, 0, 75, 20).build(builder -> new StringButton(builder, true)),
//         row.newCellSettings().alignHorizontallyLeft());
//     row.addChild(Button.builder(CANCEL, b -> this.cancel()).bounds(0, 0, 75, 20).build(builder -> new StringButton(builder, true)),
//         row.newCellSettings().alignHorizontallyLeft());
//     layout.arrangeElements();
//     layout.visitWidgets(this::addRenderableWidget);
//     this.ySize = layout.getHeight() + 5;
//   }
// }
//
