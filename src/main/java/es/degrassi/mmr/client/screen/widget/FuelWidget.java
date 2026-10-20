package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class FuelWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.google.common.collect.Lists;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.capability.IFuelHandler;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.client.gui.narration.NarrationElementOutput;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
//
// import java.util.List;
//
// public class FuelWidget extends AbstractWidget {
//   private static final Identifier emptyTexture = ModularMachineryReborn.rl("textures/gui/empty_fuel.png");
//   private static final Identifier filledTexture = ModularMachineryReborn.rl("textures/gui/filled_fuel.png");
//   private final IFuelHandler handler;
//
//   public FuelWidget(int x, int y, IFuelHandler handler) {
//     super(x, y, TextureSizeHelper.getWidth(emptyTexture), TextureSizeHelper.getHeight(emptyTexture), Component.empty());
//     this.handler = handler;
//   }
//
//   @Override
//   protected void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int i1, float v) {
//     GuiGraphicsExtractor.blit(emptyTexture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
//     var calcHeight = (int) ((handler.getFuel() * 1d / handler.getMaxFuel()) * getHeight());
//     var offset = getHeight() - calcHeight;
//     GuiGraphicsExtractor.blit(filledTexture, getX() + 1, getY() + offset + 1, 0, offset, getWidth(), calcHeight, getWidth(),
//         getHeight());
//   }
//
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     List<Component> text = Lists.newArrayList();
//
//     text.add(Component.translatable("tooltip.fuel_tank.tank", String.valueOf(handler.getFuel()), String.valueOf(handler.getMaxFuel())));
//
//     Font font = Minecraft.getInstance().font;
//     GuiGraphicsExtractor.renderTooltip(font, text.stream().map(Component::getVisualOrderText).toList(), x, y);
//   }
//
//   @Override
//   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
// }
//
