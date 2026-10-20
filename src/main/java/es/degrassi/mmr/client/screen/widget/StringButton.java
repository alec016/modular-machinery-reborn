package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class StringButton {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.google.common.collect.Lists;
// import com.mojang.blaze3d.systems.RenderSystem;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.screen.TooltipRender;
// import lombok.Getter;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.Font;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.Button;
// import net.minecraft.client.gui.components.WidgetSprites;
// import net.minecraft.network.chat.Component;
// import net.minecraft.util.Mth;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
//
// @ParametersAreNonnullByDefault
// public class StringButton extends Button implements TooltipRender {
//   protected static final WidgetSprites SPRITES = new WidgetSprites(
//       ModularMachineryReborn.rl("widget/button/button"),
//       ModularMachineryReborn.rl("widget/button/button_disabled"),
//       ModularMachineryReborn.rl("widget/button/button_highlighted")
//   );
//
//   private final boolean renderString;
//
//   private boolean renderTooltip = true;
//   @Getter
//   @Nullable
//   private List<Component> tooltips;
//
//   public StringButton(Builder builder, boolean renderString) {
//     super(builder);
//     this.renderString = renderString;
//   }
//
//   public StringButton renderTooltip(boolean render) {
//     this.renderTooltip = render;
//     return this;
//   }
//
//   public StringButton setTooltips(Component... components) {
//     this.tooltips = Lists.newArrayList(components);
//     return this;
//   }
//
//   @Override
//   protected void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     Minecraft minecraft = Minecraft.getInstance();
//     GuiGraphicsExtractor.setColor(1.0F, 1.0F, 1.0F, this.alpha);
//     RenderSystem.enableBlend();
//     RenderSystem.enableDepthTest();
//     GuiGraphicsExtractor.blitSprite(
//         SPRITES.get(this.active, this.isHoveredOrFocused()),
//         this.getX(), this.getY(),
//         this.getWidth(), this.getHeight());
//     GuiGraphicsExtractor.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//     int i = getFGColor();
//     if (renderString)
//       this.renderString(GuiGraphicsExtractor, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
//   }
//
//   protected void renderScrollingString(GuiGraphicsExtractor GuiGraphicsExtractor, Font font, int width, int color) {
//     int i = this.getX() + width;
//     int j = this.getX() + this.getWidth() - width;
//     renderScrollingString(GuiGraphicsExtractor, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight() - 3, color);
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     if (isMouseOver(mouseX, mouseY) && renderTooltip && getTooltips() != null && !getTooltips().isEmpty()) {
//       for (Component tooltip : getTooltips()) {
//         GuiGraphicsExtractor.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
//       }
//     }
//   }
// }
//
