package es.degrassi.mmr.common.integration.theoneprobe.element;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.CycleTimer;
import mcjty.theoneprobe.api.Color;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.apiimpl.client.ElementIconRender;
import mcjty.theoneprobe.apiimpl.styles.IconStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;

public class SizedFluidIngredientRenderer implements IElement {
  public static final Identifier ID = ModularMachineryReborn.rl("sized_fluid_ingredient");
  private static final Vector2i DEFAULT_SIZE = new Vector2i(16, 16);
  private final CycleTimer timer = new CycleTimer(() -> 1000, true);
  private final List<FluidStack> fluids;
  private final SizedFluidIngredient ingredient;
  private final IIconStyle style;
  private final float chance;

  private static final DecimalFormat dfCommas = new DecimalFormat("0.##");
  private static final DecimalFormat[] dfCommasArray = new DecimalFormat[]{dfCommas, new DecimalFormat("0.#"), new DecimalFormat("0")};

  public SizedFluidIngredientRenderer(SizedFluidIngredient ingredient, float chance, IIconStyle style) {
    this.ingredient = ingredient;
    this.fluids = ingredient.ingredient().fluids().stream().map(holder -> new FluidStack(holder, ingredient.amount())).toList();
    this.style = style;
    this.chance = chance;
  }

  public SizedFluidIngredientRenderer(RegistryFriendlyByteBuf buffer) {
    this.ingredient = buffer.readLenientJsonWithCodec(SizedFluidIngredient.CODEC);
    this.chance = buffer.readFloat();
    this.fluids = ingredient.ingredient().fluids().stream().map(holder -> new FluidStack(holder, ingredient.amount())).toList();
    this.style = (new IconStyle()).width(buffer.readInt()).height(buffer.readInt()).textureWidth(buffer.readInt()).textureHeight(buffer.readInt()).color(buffer.readInt());
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    timer.onDraw();
    var font = Minecraft.getInstance().font;
    var fluid = timer.getOrDefault(fluids, FluidStack.EMPTY);
    var fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet()
        .get(fluid.getFluid().defaultFluidState());
    var tintSource = fluidModel.fluidTintSource();
    var tintColor = -1;
    if (tintSource != null) {
      tintColor = tintSource.colorAsStack(fluid);
    }
    Color color = new Color(tintColor);
    ElementIconRender.render(fluidModel.stillMaterial().sprite().atlasLocation(), GuiGraphicsExtractor, x, y, 16, 16, -1, -1,
        this.style.getTextureWidth(),
        this.style.getTextureHeight(), color.getRGB());

    // Chance rendering
    if (this.chance < 1f) {
      GuiGraphicsExtractor.pose().pushMatrix();
      GuiGraphicsExtractor.pose().translate(0, -16);
      String s = (chance * 100) + "%";
      if (chance == 0f) s = "NC";
      GuiGraphicsExtractor.pose().translate(x + 19 - 2, y + 6 + 3);
      GuiGraphicsExtractor.pose().scale(0.75f, 0.75f);
      GuiGraphicsExtractor.text(font, s, - font.width(s), 0, 16777215, true);
      GuiGraphicsExtractor.pose().popMatrix();
    }
    // Amount rendering
    GuiGraphicsExtractor.pose().pushMatrix();

    String s = humanReadableNumber(this.ingredient.amount(), "B", true, null);
    GuiGraphicsExtractor.pose().translate(x + 19 - 2, y + 6 + 3);
    GuiGraphicsExtractor.pose().scale(0.75f, 0.75f);
    GuiGraphicsExtractor.text(font, s,  - font.width(s), 0, 16777215, true);
    GuiGraphicsExtractor.pose().popMatrix();
  }

  @Override
  public int getWidth() {
    return DEFAULT_SIZE.x();
  }

  @Override
  public int getHeight() {
    return DEFAULT_SIZE.y();
  }

  @Override
  public void toBytes(RegistryFriendlyByteBuf buffer) {
    buffer.writeJsonWithCodec(SizedFluidIngredient.CODEC, this.ingredient);
    buffer.writeFloat(this.chance);
    buffer.writeInt(this.style.getWidth());
    buffer.writeInt(this.style.getHeight());
    buffer.writeInt(this.style.getTextureWidth());
    buffer.writeInt(this.style.getTextureHeight());
    buffer.writeInt(this.style.getColor());
  }

  @Override
  public Identifier getID() {
    return ID;
  }

  public static class SizedFluidIngredientFactory implements IElementFactory {

    @Override
    public SizedFluidIngredientRenderer createElement(RegistryFriendlyByteBuf buffer) {
      return new SizedFluidIngredientRenderer(buffer);
    }

    @Override
    public Identifier getId() {
      return ID;
    }
  }

  private String humanReadableNumber(double number, String unit, boolean milli, @Nullable Format formatter) {
    if (Mth.equal(number, 0.0)) {
      return "0" + unit;
    } else {
      StringBuilder sb = new StringBuilder();
      boolean n = number < 0.0F;
      if (n) {
        number = -number;
        sb.append('-');
      }

      if (milli && number >= 1000.0) {
        number /= 1000.0d;
        milli = false;
      }

      int exp = formatter == null && number < 10000.0 ? 0 : (int)Math.log10(number) / 3;
      if (exp > 7) {
        exp = 7;
      }

      if (exp > 0) {
        number /= Math.pow((double)1000.0F, (double)exp);
      }

      if (formatter == null) {
        if (number < (double)10.0F) {
          formatter = dfCommasArray[0];
        } else if (number < (double)100.0F) {
          formatter = dfCommasArray[1];
        } else {
          formatter = dfCommasArray[2];
        }
      }

      if (formatter instanceof NumberFormat) {
        NumberFormat numberFormat = (NumberFormat)formatter;
        sb.append(numberFormat.format(number));
      } else {
        sb.append(formatter.format(new Object[]{number}));
      }

      if (exp == 0) {
        if (milli) {
          sb.append('m');
        }
      } else {
        char pre = "kMGTPEZ".charAt(exp - 1);
        sb.append(pre);
      }

      sb.append(unit);
      return sb.toString();
    }
  }
}
