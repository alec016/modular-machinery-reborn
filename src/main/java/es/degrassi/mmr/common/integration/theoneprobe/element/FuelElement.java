package es.degrassi.mmr.common.integration.theoneprobe.element;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

@Getter
@Setter
@Accessors(chain = true)
public class FuelElement implements IElement {
  private Identifier emptyTexture = ModularMachineryReborn.rl("textures/gui/empty_fuel.png");
  private Identifier filledTexture = ModularMachineryReborn.rl("textures/gui/filled_fuel.png");
  public static final Identifier ID = ModularMachineryReborn.rl("fuel_element");
  private final float progress;

  public FuelElement(float progress) {
    this.progress = progress;
  }

  public FuelElement(RegistryFriendlyByteBuf buf) {
    this.progress = buf.readFloat();
  }

  private float scale() {
    return 1.25f;
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    x = (int) (x * scale());
    y = (int) (y * scale());
    GuiGraphicsExtractor.pose().pushMatrix();
    GuiGraphicsExtractor.pose().scale(1/scale(), 1/scale());
    GuiGraphicsExtractor.blit(emptyTexture, x, y, 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    var calcHeight = (int) (progress * getHeight());
    var offset = getHeight() - calcHeight;
    GuiGraphicsExtractor.blit(filledTexture, x + 1, y + offset + 1, 0, offset, getWidth(), calcHeight, getWidth(), getHeight());
    GuiGraphicsExtractor.pose().popMatrix();
  }

  @Override
  public int getWidth() {
    return TextureSizeHelper.getWidth(emptyTexture);
  }

  @Override
  public int getHeight() {
    return TextureSizeHelper.getHeight(emptyTexture);
  }

  @Override
  public void toBytes(RegistryFriendlyByteBuf buf) {
    buf.writeFloat(this.progress);
  }

  @Override
  public Identifier getID() {
    return ID;
  }

  public static class FuelElementFactory implements IElementFactory {

    @Override
    public FuelElement createElement(RegistryFriendlyByteBuf buffer) {
      return new FuelElement(buffer);
    }

    @Override
    public Identifier getId() {
      return ID;
    }
  }
}
