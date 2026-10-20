package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public interface IFancyFontRenderer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.mojang.blaze3d.vertex.PoseStack;
// import net.minecraft.util.Util;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.network.chat.Component;
// import net.minecraft.util.FormattedCharSequence;
// import net.minecraft.util.Mth;
// import org.jetbrains.annotations.Nullable;
// import org.joml.Matrix4f;
//
// import java.util.Collections;
// import java.util.List;
//
// public interface IFancyFontRenderer {
//
//   int getXSize();
//
//   default Font font() {
//     return Minecraft.getInstance().font;
//   }
//
//   /**
//    * Time the gui was opened in ms, or zero if the time is unknown (scrolling text will just use the current time then)
//    */
//   default long getTimeOpened() {
//     //TODO: Try and improve how we handle the time opened concept for test in scrollable elements
//     //TODO: Gui elements that are part of a GuiWindow, should use the window's time instead of the gui's time
//     return 0;
//   }
//
//   default int titleTextColor() {
//     return 0xFF404040;
//   }
//
//   default int activeButtonTextColor() {
//     return 0xFFFFFFFF;
//   }
//
//   default int inactiveButtonTextColor() {
//     return 0xFFA0A0A0;
//   }
//
//   default void drawTitleText(GuiGraphicsExtractor graphics, Component text, int y) {
//     drawScrollingString(graphics, text, 0, y, IFancyFontRenderer.TextAlignment.CENTER, titleTextColor(), 4, false);
//   }
//
//   default void drawTitleTextTextWithOffset(GuiGraphicsExtractor graphics, Component text, int x, int y, int end) {
//     drawTitleTextTextWithOffset(graphics, text, x, y, end, 4, IFancyFontRenderer.TextAlignment.CENTER);
//   }
//
//   default void drawTitleTextTextWithOffset(GuiGraphicsExtractor graphics, Component text, int x, int y, int end, int maxLengthPad, IFancyFontRenderer.TextAlignment alignment) {
//     drawScrollingString(graphics, text, x, y, alignment, titleTextColor(), end - x, maxLengthPad, false);
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int maxLengthPad, boolean shadow) {
//     drawScrollingString(graphics, text, x, y, alignment, color, maxLengthPad, shadow, getTimeOpened());
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int maxLengthPad, boolean shadow, long msVisible) {
//     drawScrollingString(graphics, text, x, y, alignment, color, getXSize(), maxLengthPad, shadow, msVisible);
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int maxLengthPad, boolean shadow) {
//     drawScrollingString(graphics, text, x, y, alignment, color, width, maxLengthPad, shadow, getTimeOpened());
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int maxLengthPad, boolean shadow,
//                                    long msVisible) {
//     drawScrollingString(graphics, text, x, y, alignment, color, width, font().lineHeight, maxLengthPad, shadow, msVisible);
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int height, int maxLengthPad,
//                                    boolean shadow, long msVisible) {
//     drawScrollingString(graphics, text, x + maxLengthPad, y, x + width - maxLengthPad, y + height, alignment, color, shadow, msVisible);
//   }
//
//   default void drawScrollingString(GuiGraphicsExtractor graphics, Component text, int minX, int minY, int maxX, int maxY, IFancyFontRenderer.TextAlignment alignment, int color, boolean shadow,
//                                    long msVisible) {
//     Font font = font();
//     int textWidth = font.width(text);
//     int areaWidth = maxX - minX;
//     boolean isScrolling = textWidth > areaWidth;
//     //Note: Instead of doing what vanilla does, we divide to float, and don't add one
//     // That way if min and max are not just lineHeight away they will be more accurate, and otherwise it won't render one line below where it should be
//     float targetY = (minY + maxY - font.lineHeight) / 2F;
//     float targetX;
//     if (isScrolling) {
//       targetX = prepScrollingString(graphics, font, textWidth, areaWidth, minX, minY, maxX, maxY, Util.getMillis() - msVisible);
//     } else {
//       targetX = alignment.getTarget(font, minX, maxX, textWidth);
//     }
//     graphics.drawString(font, text.getVisualOrderText(), targetX, targetY, color, shadow);
//     if (isScrolling) {
//       graphics.disableScissor();
//     }
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int maxLengthPad, boolean shadow,
//                                          float scale) {
//     drawScaledScrollingString(graphics, text, x, y, alignment, color, maxLengthPad, shadow, scale, getTimeOpened());
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int maxLengthPad, boolean shadow,
//                                          float scale, long msVisible) {
//     drawScaledScrollingString(graphics, text, x, y, alignment, color, getXSize(), maxLengthPad, shadow, scale, msVisible);
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int maxLengthPad,
//                                          boolean shadow, float scale) {
//     drawScaledScrollingString(graphics, text, x, y, alignment, color, width, maxLengthPad, shadow, scale, getTimeOpened());
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int maxLengthPad,
//                                          boolean shadow, float scale, long msVisible) {
//     drawScaledScrollingString(graphics, text, x, y, alignment, color, width, font().lineHeight, maxLengthPad, shadow, scale, msVisible);
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int width, int height, int maxLengthPad,
//                                          boolean shadow, float scale, long msVisible) {
//     drawScaledScrollingString(graphics, text, x + maxLengthPad, y, x + width - maxLengthPad, y + height, alignment, color, shadow, scale, msVisible);
//   }
//
//   default void drawScaledScrollingString(GuiGraphicsExtractor graphics, Component text, int minX, int minY, int maxX, int maxY, IFancyFontRenderer.TextAlignment alignment, int color, boolean shadow,
//                                          float scale, long msVisible) {
//     if (scale == 1.0F) {
//       drawScrollingString(graphics, text, minX, minY, maxX, maxY, alignment, color, shadow, msVisible);
//       return;
//     }
//     Font font = font();
//     float textWidth = font.width(text) * scale;
//     int areaWidth = maxX - minX;
//     boolean isScrolling = textWidth > areaWidth;
//     //Note: Instead of doing what vanilla does, we divide to float, and don't add one
//     // That way if min and max are not just lineHeight away they will be more accurate, and otherwise it won't render one line below where it should be
//     float targetY = (minY + maxY - font.lineHeight) / 2F;
//     float targetX;
//     if (isScrolling) {
//       targetX = prepScrollingString(graphics, font, textWidth, areaWidth, minX, minY, maxX, maxY, Util.getMillis() - msVisible);
//     } else {
//       targetX = alignment.getTarget(font, minX, maxX, textWidth);
//     }
//     PoseStack pose = prepTextScale(graphics, font, targetX, targetY, scale);
//     graphics.drawString(font, text, 0, 0, color, shadow);
//     pose.popPose();
//     if (isScrolling) {
//       graphics.disableScissor();
//     }
//   }
//
//   /**
//    * Based off the logic for calculating the scissor area and draw target that vanilla does in
//    * {@link AbstractWidget#renderScrollingString(GuiGraphicsExtractor, Font, Component, int, int, int, int, int, int)}
//    *
//    * @param visibleDuration Time in ms that this string has been visible for.
//    *
//    * @apiNote Call {@link GuiGraphicsExtractor#disableScissor()} after using this method
//    */
//   private static float prepScrollingString(GuiGraphicsExtractor graphics, Font font, double textWidth, int areaWidth, int minX, int minY, int maxX, int maxY, long visibleDuration) {
//     //Note: We are drawing in relative coordinates, but GuiGraphicsExtractor#enableScissor, is expecting absolute coordinates,
//     // so we need to get the translations from our pose stack
//     //Note: This is equivalent to what Matrix4f#getTranslation(Vector3f) would do, without all the extra allocations.
//     Matrix4f matrix4f = graphics.pose().last().pose();
//     int left = (int) matrix4f.m30();
//     int top = (int) matrix4f.m31();
//     graphics.enableScissor(left + minX, top + minY, left + maxX, top + maxY);
//     //TODO: Re-evaluate this, as for text (especially scaled text) when moving very slowly near the edges, it makes the text a bit blurry
//     // Though maybe it is better to just make it not move so insanely slowly near the edges
//     //Note: Vanilla casts overflowedBy to an int, as it only bothers drawing based on int pixels.
//     // As we already handle and calculates with floats, casting to a float here provides a much smoother looking scroll
//     return minX - (float) getOverflowedBy(font, textWidth - areaWidth, visibleDuration);
//   }
//
//   private static double getOverflowedBy(Font font, double overflowWidth, long visibleDuration) {
//     //Seconds since the gui was opened
//     double seconds = visibleDuration / 1_000D;
//     double scrollPeriod = Math.max(overflowWidth * AbstractWidget.PERIOD_PER_SCROLLED_PIXEL, AbstractWidget.MIN_SCROLL_PERIOD);
//     //Controls the speed at which we go between the start of the scroll and the end
//     double scrollSpeedModifier = Math.cos((2 * Math.PI) * seconds / scrollPeriod);
//     if (!font.isBidirectional()) {
//       //If the text is left to right (such as english). We need to start the modifier at the opposite peak so that it starts
//       // at the beginning of the string
//       //Note: Mojang doesn't include this negative for rendering text in english, but that is because they just use the current ms
//       // for the seconds calculation, which means that they don't care where in the wave they start
//       scrollSpeedModifier = -scrollSpeedModifier;
//     }
//     //TODO: Do we want to improve this in some way or another, as it moves very slowly at the edges, and much quicker in the middle
//     // Potentially replace this with a sigmoid function?
//     // It is particularly slow when there is a large amount of overflow
//     //Shift it so that the range is from [0, 1]
//     double scrolledSoFar = Math.sin((Math.PI / 2) * scrollSpeedModifier) / 2.0 + 0.5;
//     //Vanilla uses: Mth.lerp(scrolledSoFar, 0.0, overflowWidth); to calculate overflowedBy. But that is equivalent to just performing the following multiplication
//     return scrolledSoFar * overflowWidth;
//   }
//
//   //Note: As translate will implicitly cast x and y to being floats, we might as well pass these in as floats to reduce duplicate code
//   // TODO: pose() now returns Matrix3x2fStack instead of PoseStack in MC 1.26.1.
//   // pushPose()/popPose()/translate(x,y,z)/scale(x,y,z) may not exist on this type.
//   // This method and all callers may need to be rewritten.
//   @SuppressWarnings("unchecked")
//   private static <T> T prepTextScale(GuiGraphicsExtractor graphics, Font font, float x, float y, float scale) {
//     T pose = (T) graphics.pose();
//     pose.pushPose();
//     float halfLineHeight = font.lineHeight / 2F;
//     float yAdd = halfLineHeight - halfLineHeight * scale;
//     pose.translate(x, y + yAdd, 0);
//     pose.scale(scale, scale, scale);
//     return pose;
//   }
//
//   enum TextAlignment {
//     LEFT,
//     CENTER,
//     RIGHT,
//     /**
//      * Represents that for left to right languages this will be left aligned, and for right to left it will be right aligned.
//      */
//     RELATIVE;//TODO: Make use of this in various spots that make sense
//
//     public float getTarget(Font font, int minX, int maxX, float textWidth) {
//       return switch (this) {
//         case LEFT -> minX;
//         case CENTER -> minX + ((maxX - minX) - textWidth) / 2F;
//         case RIGHT -> maxX - textWidth;
//         case RELATIVE -> font.isBidirectional() ? maxX - textWidth : minX;
//       };
//     }
//   }
//
//   class WrappedTextRenderer {
//
//     private final Component text;
//     private List<FormattedCharSequence> linesToDraw = Collections.emptyList();
//
//     IFancyFontRenderer fontRenderer;
//     @Nullable
//     private Font lastFont;
//     private int lastMaxLength = -1;
//
//     public WrappedTextRenderer(IFancyFontRenderer fontRenderer, Component text) {
//       this.fontRenderer = fontRenderer;
//       this.text = text;
//     }
//
//     public void render(GuiGraphicsExtractor graphics, int x, int y, int maxLength, IFancyFontRenderer.TextAlignment alignment, int color) {
//       render(graphics, x, y, maxLength, alignment, color, 1);
//     }
//
//     public int renderWithScale(GuiGraphicsExtractor graphics, int x, int y, IFancyFontRenderer.TextAlignment alignment, int color, int maxLength, float scale) {
//       PoseStack pose = prepTextScale(graphics, fontRenderer.font(), x, y, scale);
//       render(graphics, 0, 0, maxLength, alignment, color, scale);
//       pose.popPose();
//       return linesToDraw.size();
//     }
//
//     private void render(GuiGraphicsExtractor graphics, int x, int startY, int maxLength, IFancyFontRenderer.TextAlignment alignment, int color, float scale) {
//       Font font = fontRenderer.font();
//       //Divide by scale for calculating actual max length so that when the text is scaled it has the proper total space available
//       calculateLines(font, scale == 1 ? maxLength : Mth.floor(maxLength / scale));
//       int maxX = x + maxLength;
//       for (FormattedCharSequence line : linesToDraw) {
//         graphics.drawString(font, line, alignment.getTarget(font, x, maxX, scale * font.width(line)), startY, color, false);
//         startY += font.lineHeight;
//       }
//     }
//
//     private void calculateLines(Font font, int maxLength) {
//       //If something changed since the last time we calculated it
//       if (font != null && (lastFont != font || lastMaxLength != maxLength)) {
//         lastFont = font;
//         lastMaxLength = maxLength;
//         linesToDraw = font.split(text, maxLength);
//       }
//     }
//
//     public int getRequiredHeight(int maxLength) {
//       Font font = fontRenderer.font();
//       calculateLines(font, maxLength);
//       return font.lineHeight * linesToDraw.size();
//     }
//   }
//
//   class ReplaceableWrappedTextRenderer extends IFancyFontRenderer.WrappedTextRenderer {
//
//     public ReplaceableWrappedTextRenderer(IFancyFontRenderer parent, int width, Component text) {
//       super(new IFancyFontRenderer.ReplaceableWrappedTextRenderer.SimpleFancyFontRenderer(parent.font(), width), text);
//     }
//
//     public IFancyFontRenderer.WrappedTextRenderer replaceFont(IFancyFontRenderer font) {
//       this.fontRenderer = font;
//       return this;
//     }
//
//     private record SimpleFancyFontRenderer(Font font, int getXSize) implements IFancyFontRenderer {
//     }
//   }
// }
//
