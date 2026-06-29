package es.degrassi.mmreborn.common.integration.jei.category.drawable;

import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.common.gui.elements.DrawableAnimated;
import mezz.jei.common.gui.elements.DrawableCombined;
import mezz.jei.common.gui.elements.DrawableResource;
import mezz.jei.common.gui.elements.OffsetDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FuelDrawable implements IDrawableAnimated {
  private final int ticksPerCycle;
  private final IDrawableAnimated.StartDirection startDirection;
  private final IDrawable drawable;
  private final ResourceLocation emptyTexture, fillTexture;

  public FuelDrawable(int ticksPerCycle, IDrawableAnimated.StartDirection startDirection,
                          ResourceLocation emptyTexture, ResourceLocation fillTexture) {
    this.ticksPerCycle = ticksPerCycle;
    this.startDirection = startDirection;
    this.emptyTexture = emptyTexture;
    this.fillTexture = fillTexture;
    this.drawable = createProgress();
  }

  private IDrawable createProgress() {
    IDrawableStatic recipeArrowFilled = new DrawableResource(
        fillTexture,
        0,
        0,
        TextureSizeHelper.getWidth(fillTexture),
        TextureSizeHelper.getHeight(fillTexture),
        0,
        0,
        0,
        0,
        TextureSizeHelper.getWidth(fillTexture),
        TextureSizeHelper.getHeight(fillTexture)
    );
    IDrawableStatic recipeArrow = new DrawableResource(
        emptyTexture,
        0,
        0,
        TextureSizeHelper.getWidth(emptyTexture),
        TextureSizeHelper.getHeight(emptyTexture),
        0,
        0,
        0,
        0,
        TextureSizeHelper.getWidth(emptyTexture),
        TextureSizeHelper.getHeight(emptyTexture)
    );
    IDrawable animatedFill = new DrawableAnimated(recipeArrowFilled, ticksPerCycle, startDirection, false);
    IDrawable drawableCombined = new DrawableCombined(recipeArrow, new OffsetDrawable(animatedFill, 1, 1));
    return new OffsetDrawable(drawableCombined, 0, 0);
  }

  @Override
  public int getWidth() {
    return drawable.getWidth();
  }

  @Override
  public int getHeight() {
    return drawable.getHeight();
  }

  @Override
  public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
    drawable.draw(guiGraphics, xOffset, yOffset);
  }
}
