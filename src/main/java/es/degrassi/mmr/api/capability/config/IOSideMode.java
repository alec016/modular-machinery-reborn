package es.degrassi.mmr.api.capability.config;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public enum IOSideMode implements SideConfig.SideMode {

  ENABLED(Component.translatable("mmr.side.enabled").withStyle(ChatFormatting.LIGHT_PURPLE), ARGB.color(255, 255, 85, 255)),
  DISABLED(Component.translatable("mmr.side.disabled").withStyle(ChatFormatting.GRAY), ARGB.color(0, 255, 255, 255));

  public static final NamedCodec<IOSideMode> CODEC = NamedCodec.enumCodec(IOSideMode.class);

  private final Component title;
  private final int color;

  IOSideMode(Component title, int color) {
    this.title = title;
    this.color = color;
  }

  public boolean isEnabled() {
    return this == ENABLED;
  }

  public boolean isDisabled() {
    return this == DISABLED;
  }

  @Override
  public Component title() {
    return this.title;
  }

  @Override
  public int color() {
    return this.color;
  }
}
