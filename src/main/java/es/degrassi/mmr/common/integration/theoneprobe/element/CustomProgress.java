package es.degrassi.mmr.common.integration.theoneprobe.element;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.Utils;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

public class CustomProgress implements IElement {
  public static final Identifier ID = ModularMachineryReborn.rl("progress");
  private final float current;
  private final float max;
  private final IProgressStyle style;

  public CustomProgress(float current, float max, IProgressStyle style) {
    this.current = current;
    this.max = max;
    this.style = style;
  }

  public CustomProgress(RegistryFriendlyByteBuf buf) {
    this.current = buf.readFloat();
    this.max = buf.readFloat();
    this.style = (new ProgressStyle()).width(buf.readInt()).height(buf.readInt()).prefix(ComponentSerialization.STREAM_CODEC.decode(buf)).suffix(ComponentSerialization.STREAM_CODEC.decode(buf)).borderColor(buf.readInt()).filledColor(buf.readInt()).alternateFilledColor(buf.readInt()).backgroundColor(buf.readInt()).showText(buf.readBoolean()).numberFormat(NumberFormat.values()[buf.readByte()]).lifeBar(buf.readBoolean()).armorBar(buf.readBoolean()).alignment(buf.readEnum(ElementAlignment.class));
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    CustomProgressRenderer.render(this.style, current, max, GuiGraphicsExtractor, x, y, getWidth(), getHeight());
  }

  @Override
  public int getWidth() {
    if (this.style.isLifeBar()) {
      return this.current * 4L >= (long) this.style.getWidth() ? 100 : (int) (this.current * 4L + 2L);
    } else {
      return this.style.getWidth();
    }
  }

  @Override
  public int getHeight() {
    return this.style.getHeight();
  }

  @Override
  public void toBytes(RegistryFriendlyByteBuf buf) {
    buf.writeFloat(this.current);
    buf.writeFloat(this.max);
    buf.writeInt(this.style.getWidth());
    buf.writeInt(this.style.getHeight());
    ComponentSerialization.STREAM_CODEC.encode(buf, this.style.getPrefixComp());
    ComponentSerialization.STREAM_CODEC.encode(buf, this.style.getSuffixComp());
    buf.writeInt(this.style.getBorderColor());
    buf.writeInt(this.style.getFilledColor());
    buf.writeInt(this.style.getAlternatefilledColor());
    buf.writeInt(this.style.getBackgroundColor());
    buf.writeBoolean(this.style.isShowText());
    buf.writeByte(this.style.getNumberFormat().ordinal());
    buf.writeBoolean(this.style.isLifeBar());
    buf.writeBoolean(this.style.isArmorBar());
    buf.writeEnum(this.style.getAlignment());
  }

  @Override
  public Identifier getID() {
    return ID;
  }

  public static Component format(float in, float max, NumberFormat style, Component suffix) {
    float i = max >= 20 ? in / 20f : in;
    switch (style) {
      case FULL, COMMAS:
        return Component.literal(Utils.decimalFormat(i)).append(suffix);
      case COMPACT:
        if (i < 1000L) {
          return Component.literal(i + " ").append(suffix);
        } else {
          int unit = 1000;
          int exp = (int) (Math.log(i) / Math.log(unit));
          String s = suffix.getString();
          if (s.startsWith("m")) {
            s = s.substring(1);
            if (exp - 2 >= 0) {
              char pre = "kMGTPE".charAt(exp - 2);
              return Component.literal(String.format("%.1f %s", i / Math.pow(unit, exp), pre)).append(Component.literal(s).withStyle(suffix.getStyle()));
            }

            return Component.literal(String.format("%.1f", i / Math.pow(unit, exp))).append(Component.literal(s).withStyle(suffix.getStyle()));
          }

          char pre = "kMGTPE".charAt(exp - 1);
          return Component.literal(String.format("%.1f %s", i / Math.pow(unit, exp), pre)).append(suffix);
        }
      case NONE:
        return suffix;
      default:
        return Component.literal(Float.toString(i));
    }
  }

  public static class CustomProgressFactory implements IElementFactory {

    @Override
    public CustomProgress createElement(RegistryFriendlyByteBuf buffer) {
      return new CustomProgress(buffer);
    }

    @Override
    public Identifier getId() {
      return ID;
    }
  }
}
