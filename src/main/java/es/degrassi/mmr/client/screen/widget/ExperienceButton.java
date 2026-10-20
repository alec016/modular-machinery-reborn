package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ExperienceButton {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.api.client.ExperienceButtonType;
// import net.minecraft.client.gui.components.Tooltip;
// import net.minecraft.network.chat.Component;
//
// import java.util.List;
// import java.util.stream.Stream;
//
// public class ExperienceButton extends ItemOrIconButton {
//
//   private final ExperienceButtonType type;
//
//   public interface OnPressT {
//     void onPress(ExperienceButtonType type);
//   }
//
//   public ExperienceButton(int x, int y, OnPressT onPress, ExperienceButtonType type) {
//     super(x, y, type.icon(), btn -> onPress.onPress(type));
//     setTooltip(Tooltip.create(type.component()));
//     this.type = type;
//   }
//
//   public List<Component> getTooltipMessage() {
//     return Stream.of(type.component()).toList();
//   }
// }
//
