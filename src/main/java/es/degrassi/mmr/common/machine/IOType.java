package es.degrassi.mmr.common.machine;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.ComponentTranslatable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;

public enum IOType implements StringRepresentable, ComponentTranslatable {
  INPUT,
  OUTPUT,
  NONE;

  public static final NamedCodec<IOType> CODEC = NamedCodec.enumCodec(IOType.class);

  public static IOType value(String mode) {
    return valueOf(mode.toUpperCase(Locale.ENGLISH));
  }

  @Nullable
  public static IOType getByString(String name) {
    for (IOType val : values()) {
      if (val.name().equalsIgnoreCase(name)) {
        return val;
      }
    }
    return null;
  }

  public boolean isInput() {
    return this == INPUT;
  }

  public boolean isOutput() {
    return this == OUTPUT;
  }

  public boolean isNone() {
    return this == NONE;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  @Override
  public Component getTranslation() {
    return Component.translatable(ModularMachineryReborn.MODID + ".requirement.mode." + getSerializedName());
  }
}
