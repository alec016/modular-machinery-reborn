package es.degrassi.mmr.api.client;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum CoreActionType implements StringRepresentable {
  ADD(Icon.PLUS),
  REMOVE(Icon.MINUS);

  private final Icon icon;
  CoreActionType(Icon icon) {
    this.icon = icon;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public Icon icon() {
    return this.icon;
  }
}
