package es.degrassi.mmr.client.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MMRItemTooltip implements ClientTooltipComponent {
  private static final Identifier CHECK = ModularMachineryReborn.rl("textures/gui/check.png");
  private final ItemStack stack;
  private final Component text;

  private final boolean completed;
  private final int iconWidth;
  private final int iconHeight;
  private final int textWidth;
  private static final int maxTextWidth = Minecraft.getInstance().font.width("100x [#modular_machinery_reborn:energyoutputhatch]");

  public MMRItemTooltip(ItemStack stack, Component text, boolean completed) {
    this.stack = stack;
    MutableComponent tempText = Component.empty();
    if (text.getString().contains("#")) {
      tempText
          .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
          .append(stack.getHoverName().copy().withStyle(ChatFormatting.GOLD))
          .append(Component.literal(") ").withStyle(ChatFormatting.GRAY));
    }
    this.text = tempText.append(text.copy().withStyle(completed ? ChatFormatting.GREEN : ChatFormatting.GRAY));
    this.iconWidth = TextureSizeHelper.getWidth(CHECK);
    this.iconHeight = TextureSizeHelper.getHeight(CHECK);
    this.completed = completed;
    this.textWidth = Math.min(Minecraft.getInstance().font.width(this.text), maxTextWidth);
  }

  @Override
  public int getHeight(Font font) {
    return Math.max(font.wordWrapHeight(this.text, maxTextWidth), 18) + 6;
  }

  @Override
  public int getWidth(Font font) {
    return 16 + 8 + textWidth + iconWidth;
  }

  @Override
  public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
    if (completed) {
      graphics.blit(
          RenderPipelines.GUI_TEXTURED,
          CHECK,
          x,
          y,
          0,
          0,
          iconWidth,
          iconHeight,
          iconWidth,
          iconHeight
      );
    }
    x += iconWidth + 4;
    graphics.item(stack, x, y);
    graphics.itemDecorations(font, stack, x, y);
    graphics.textWithWordWrap(font, text, x + 16 + 4, y + 4, textWidth, -1);
  }
}
