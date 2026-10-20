package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class EnumButton<E extends Enum<E>> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.api.client.ComponentTranslatable;
// import lombok.Getter;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.network.chat.Component;
//
// import java.util.Collections;
// import java.util.List;
// import java.util.function.Function;
//
// public class EnumButton<E extends Enum<E> & ComponentTranslatable> extends StringButton {
//
//   private final Function<E, Component> messageFunction;
//   private final List<E> values;
//   @Getter
//   private E value;
//   private final Font font = Minecraft.getInstance().font;
//
//   public EnumButton(Builder builder, Function<E, Component> messageFunction, List<E> values, E defaultValue) {
//     super(builder, true);
//     this.messageFunction = messageFunction;
//     this.values = values;
//     if(this.values.contains(defaultValue))
//       this.value = defaultValue;
//     else
//       this.value = this.values.get(0);
//
//     this.renderTooltip(true);
//   }
//
//   @Override
//   public int getWidth() {
//     return 8 + Math.min(font.width(getMessage()), 100);
//   }
//
//   @Override
//   public int getHeight() {
//     return Math.max(8 + font.wordWrapHeight(getMessage(), getWidth() - 8), 20);
//   }
//
//   @Override
//   public void onPress() {
//     this.value = getNextValue();
//     super.onPress();
//   }
//
//   @Override
//   public List<Component> getTooltips() {
//     return Collections.singletonList(Component.translatable("mmr.gui.tooltip.button.enum.cycle", getNextValue().getTranslation()));
//   }
//
//   @Override
//   public Component getMessage() {
//     return this.messageFunction.apply(this.value);
//   }
//
//   public E getNextValue() {
//     int index = this.values.indexOf(this.value);
//     if (index < this.values.size() - 1)
//       index++;
//     else
//       index = 0;
//     return this.values.get(index);
//   }
// }
//
