package es.degrassi.mmr.api.core;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public interface EnumRepresentable extends StringRepresentable {
  String name();

  @Override
  default String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
