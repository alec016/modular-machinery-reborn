package es.degrassi.mmr.common.integration.theoneprobe.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.TankReference;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.function.Function;

public class CustomProgressRenderer {
  private CustomProgressRenderer() {}
  private static final Identifier ICONS = Identifier.fromNamespaceAndPath("theoneprobe", "textures/gui/icons.png");
  public static void render(IProgressStyle style, float current, float max, GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
    if (style.isLifeBar()) {
      renderLifeBar(current, graphics, x, y, w, h);
    } else if (style.isArmorBar()) {
      renderArmorBar(current, graphics, x, y, w, h);
    } else {
      RenderHelper.drawThickBeveledBox(graphics, x, y, x + w, y + h, 1, style.getBorderColor(), style.getBorderColor(), style.getBackgroundColor());
      if (current > 0L && max > 0L) {
        int dx = (int)Math.min(current * (long)(w - 2) / max, (long)(w - 2));
        if (style.getFilledColor() == style.getAlternatefilledColor()) {
          if (dx > 0) {
            RenderHelper.drawThickBeveledBox(graphics, x + 1, y + 1, x + dx + 1, y + h - 1, 1, style.getFilledColor(), style.getFilledColor(), style.getFilledColor());
          }
        } else {
          for(int xx = x + 1; xx < x + dx + 1; ++xx) {
            int color = (xx & 1) == 0 ? style.getFilledColor() : style.getAlternatefilledColor();
            RenderHelper.drawVerticalLine(graphics, xx, y + 1, y + h - 1, color);
          }
        }
      }
    }

    renderText(graphics, x, y, w, current, max, style);
  }

  private static void renderText(GuiGraphicsExtractor graphics, int x, int y, int w, float current, float max, IProgressStyle style) {
    if (style.isShowText()) {
      Minecraft mc = Minecraft.getInstance();
      Font render = mc.font;
      Component s = style.getPrefixComp().copy().append(CustomProgress.format(current, max, style.getNumberFormat(),
          style.getSuffixComp()));
      int textWidth = render.width(s.getVisualOrderText());
      switch (style.getAlignment()) {
        case ALIGN_BOTTOMRIGHT -> RenderHelper.renderText(mc, graphics, x + w - 3 - textWidth, y + 2, s);
        case ALIGN_CENTER -> RenderHelper.renderText(mc, graphics, x + w / 2 - textWidth / 2, y + 2, s);
        case ALIGN_TOPLEFT -> RenderHelper.renderText(mc, graphics, x + 3, y + 2, s);
      }
    }

  }

  private static void renderLifeBar(float current, GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, ICONS);
    PoseStack matrixStack = graphics.pose();
    Matrix4f matrix = matrixStack.last().pose();
    if (current * 4L >= w) {
      RenderHelper.drawTexturedModalRect(matrix, x, y, 52, 0, 9, 9);
      Minecraft var10000 = Minecraft.getInstance();
      int var10002 = x + 12;
      String var10004 = String.valueOf(ChatFormatting.WHITE);
      RenderHelper.renderText(var10000, graphics, var10002, y, var10004 + String.valueOf(current / 2L));
    } else {
      for(int i = 0; i < current / 2L; ++i) {
        RenderHelper.drawTexturedModalRect(matrix, x, y, 52, 0, 9, 9);
        x += 8;
      }

      if (current % 2L != 0L) {
        RenderHelper.drawTexturedModalRect(matrix, x, y, 61, 0, 9, 9);
      }
    }

  }

  private static void renderArmorBar(float current, GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, ICONS);
    PoseStack matrixStack = graphics.pose();
    Matrix4f matrix = matrixStack.last().pose();
    if (current * 4L >= w) {
      RenderHelper.drawTexturedModalRect(matrix, x, y, 43, 9, 9, 9);
      Minecraft var10000 = Minecraft.getInstance();
      int var10002 = x + 12;
      String var10004 = String.valueOf(ChatFormatting.WHITE);
      RenderHelper.renderText(var10000, graphics, var10002, y, var10004 + String.valueOf(current / 2L));
    } else {
      for(int i = 0; i < current / 2L; ++i) {
        RenderHelper.drawTexturedModalRect(matrix, x, y, 43, 9, 9, 9);
        x += 8;
      }

      if (current % 2L != 0L) {
        RenderHelper.drawTexturedModalRect(matrix, x, y, 25, 9, 9, 9);
      }
    }

  }

  public static void renderTank(GuiGraphicsExtractor graphics, int x, int y, int width, int height, IProgressStyle style, TankReference tank) {
    RenderHelper.drawThickBeveledBox(graphics, x, y, x + width, y + height, 1, style.getBorderColor(), style.getBorderColor(), style.getBackgroundColor());
    if (tank.getStored() <= 0) {
      if (style.isShowText()) {
        renderText(graphics, x, y, width, 0L, 0L, style);
      }

    } else {
      Minecraft mc = Minecraft.getInstance();
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      Function<Identifier, TextureAtlasSprite> map = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
      width -= 2;
      FluidStack[] fluids = tank.getFluids();
      int start = 1;
      int tanks = fluids.length;
      int max = tank.getCapacity();
      PoseStack matrixStack = graphics.pose();
      Matrix4f matrix = matrixStack.last().pose();

      for(FluidStack stack : fluids) {
        int lvl = (int)(stack == null ? (double)0.0F : (double)stack.getAmount() / (double)max * (double)width);
        if (lvl > 0) {
          Identifier stillTexture = IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);
          TextureAtlasSprite liquidIcon = map.apply(stillTexture);
          if (!Objects.equals(liquidIcon, map.apply(MissingTextureAtlasSprite.getLocation()))) {
            int color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);
            RenderSystem.setShaderColor((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F);

            while(lvl > 0) {
              int maxX = Math.min(16, lvl);
              lvl -= maxX;
              RenderHelper.drawTexturedModalRect(matrix, x + start, y + 1, liquidIcon, maxX, height - 2);
              start += maxX;
            }
          }
        }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (style.isShowText()) {
        renderText(graphics, x, y, width + 2, tank.getStored(), tank.getCapacity(), style);
      }

    }
  }
}
