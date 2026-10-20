package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class GuiScrollbar {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.client.screen.BaseScreen;
// import es.degrassi.mmr.common.util.TextureSizeHelper;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.resources.Identifier;
// import org.jetbrains.annotations.NotNull;
//
// import java.util.function.IntSupplier;
//
// public class GuiScrollbar extends GuiScrollableElement {
//   private static final Identifier BAR = ModularMachineryReborn.rl("small_scroller");
//   private static final Identifier BACK_BAR = ModularMachineryReborn.rl("small_scroller_disabled");
//
//   private static final int TEXTURE_WIDTH = TextureSizeHelper.getWidth(BAR.withPrefix("textures/gui/sprites/"));
//   private static final int TEXTURE_HEIGHT = TextureSizeHelper.getHeight(BAR.withPrefix("textures/gui/sprites/"));
//
//   private final IntSupplier maxElements;
//   private final IntSupplier focusedElements;
//   private final boolean needsScrolling;
//
//   public GuiScrollbar(BaseScreen<?, ?> gui, int x, int y, int height, IntSupplier maxElements,
//                       IntSupplier focusedElements, boolean needsScrolling) {
//     super(BAR, BACK_BAR, gui, x, y, TEXTURE_WIDTH, height, 1, -1, TEXTURE_WIDTH * 3/2, TEXTURE_HEIGHT, height);
//     this.maxElements = maxElements;
//     this.focusedElements = focusedElements;
//     this.needsScrolling = needsScrolling;
//   }
//
//   @Override
//   public void drawBackground(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
//     super.drawBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
//     if (needsScrolling) {
//       //Draw background and border
//       GuiGraphicsExtractor.blitSprite(BACK_BAR,
//           barX, barY,
//           0,
//           barWidth, height);
//       GuiGraphicsExtractor.blitSprite(getResource(),
//           barX, barY + getScroll(),
//           0,
//           barWidth, barHeight);
//     }
//   }
//
//   @Override
//   protected int getMaxElements() {
//     return maxElements.getAsInt();
//   }
//
//   @Override
//   protected int getFocusedElements() {
//     return focusedElements.getAsInt();
//   }
// }
//
