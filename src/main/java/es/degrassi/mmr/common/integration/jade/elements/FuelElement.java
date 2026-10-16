package es.degrassi.mmr.common.integration.jade.elements;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.ProgressStyle;

@Getter
@Setter
@Accessors(chain = true)
public class FuelElement extends Element {
  private float progress;
  private ProgressStyle style;
  private BoxStyle boxStyle;
  private Identifier emptyTexture = ModularMachineryReborn.rl("textures/gui/empty_fuel.png");
  private Identifier filledTexture = ModularMachineryReborn.rl("textures/gui/filled_fuel.png");

  public FuelElement(float percent) {
    this.progress = percent;
    this.width = TextureSizeHelper.getWidth(emptyTexture);
    this.height = TextureSizeHelper.getHeight(emptyTexture);
  }

  public ProgressStyle getProgressStyle() {
    return style;
  }

  public BoxStyle getStyle() {
    return boxStyle;
  }

  private float scale() {
    return 2f;
  }

  @Override
  public @Nullable Component getNarration() {
    return null;
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
    int x = (int) (getX() * scale());
    int y = (int) (getY() * scale());
    GuiGraphicsExtractor.pose().pushMatrix();
    GuiGraphicsExtractor.pose().scale(1/scale(), 1/scale());
    GuiGraphicsExtractor.blit(emptyTexture, x, y, 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    var calcHeight = (int) (progress * getHeight());
    var offset = getHeight() - calcHeight;
    GuiGraphicsExtractor.blit(filledTexture, x + 1, y + offset + 1, 0, offset, getWidth(), calcHeight, getWidth(), getHeight());
    GuiGraphicsExtractor.pose().popMatrix();
  }

  public Vec2 getSize() {
    return new Vec2(getWidth() / scale(), getHeight() / scale());
  }
}
