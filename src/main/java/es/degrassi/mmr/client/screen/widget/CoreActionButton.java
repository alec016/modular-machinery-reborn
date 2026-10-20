package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class CoreActionButton {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.google.common.collect.Lists;
// import es.degrassi.mmr.api.client.CoreActionType;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.gui.components.Tooltip;
// import net.minecraft.client.gui.screens.Screen;
// import net.minecraft.network.chat.Component;
// import org.jetbrains.annotations.Nullable;
//
// import java.util.List;
//
// public class CoreActionButton extends ItemOrIconButton {
//
//   private final CoreActionType type;
//
//   public interface OnPressT {
//     void onPress(CoreActionType type);
//   }
//
//   public CoreActionButton(int x, int y, OnPressT onPress, CoreActionType type) {
//     super(x, y, type.icon(), btn -> onPress.onPress(type));
//     this.type = type;
//   }
//
//   public CoreActionButton(OnPressT onPress, CoreActionType type) {
//     this(0, 0, onPress, type);
//   }
//
//   @Override
//   public @Nullable Tooltip getTooltip() {
//     return Tooltip.create(type.component(Screen.hasShiftDown(), Screen.hasControlDown()));
//   }
//
//   @Override
//   public List<Component> getTooltipMessage() {
//     List<Component> components = Lists.newArrayList(type.component(Screen.hasShiftDown(), Screen.hasControlDown()));
//     components.add(
//         Component.translatable(
//             "%s %s",
//             Component.translatable("modular_machinery_reborn.controller.shift").withStyle(ChatFormatting.AQUA),
//             type.component(true, false).copy().withStyle(ChatFormatting.GRAY)
//         )
//     );
//     components.add(
//         Component.translatable(
//             "%s %s",
//             Component.translatable("modular_machinery_reborn.controller.control").withStyle(ChatFormatting.AQUA),
//             type.component(false, true).copy().withStyle(ChatFormatting.GRAY)
//         )
//     );
//     return components;
//   }
// }
//
