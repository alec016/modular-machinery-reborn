package es.degrassi.mmr.client.requirement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public interface FluidRendering extends StackHolder, Position {
  default void renderFluid(GuiGraphicsExtractor graphics) {
    graphics.pose().pushMatrix();
    var fluidStack = getFluidStack();
    if (!fluidStack.isEmpty() && fluidStack.getFluid() instanceof Fluid fluid) {
      int x = 1;
      int y = 1;
      int w = getWidth();
      int h = getHeight();
      int filledHeight = Math.max(1, Math.min(h, (fluidStack.getAmount() * h / fluidStack.getAmount())));
      int sy = y + h;
      for (int oy = 0; oy < filledHeight; oy += 16) {
        int rh = Math.min(16, filledHeight - oy);
        for (int ox = 0; ox < w; ox += 16) {
          int rw = Math.min(16, w - ox);

          fluid.getFluidType();

          IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
          TextureAtlasSprite sprite = Minecraft.getInstance()
              .getAtlasManager().get(new SpriteId(AtlasIds.BLOCKS, extensions.getRenderOverlayTexture(Minecraft.getInstance())));
          graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + ox, sy + (oy + rh) * -1, rw, rh);
        }
      }
    }
    graphics.pose().popMatrix();
  }
}
