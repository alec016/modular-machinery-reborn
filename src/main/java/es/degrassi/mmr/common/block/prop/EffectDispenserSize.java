package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;
import es.degrassi.mmr.common.data.config.EffectDispenserConfig;
import net.neoforged.fml.config.IConfigSpec;

import java.util.Locale;

public enum EffectDispenserSize implements ConfigLoaded, EnumRepresentable {
  SMALL(5, false, false),
  MEDIUM(15, false, false),
  BIG(32, true, false);

  public final int defaultRadius;
  public final boolean defaultDimensional;
  public final boolean defaultShowParticles;
  public int radius;
  public boolean interdimensional, showParticles;

  EffectDispenserSize(int radius, boolean interdimensional, boolean showParticles) {
    this.defaultRadius = radius;
    this.defaultDimensional = interdimensional;
    this.defaultShowParticles = showParticles;
  }

  public static EffectDispenserSize value(String value) {
    return switch(value.toUpperCase(Locale.ENGLISH)) {
      case "MEDIUM" -> MEDIUM;
      case "BIG" -> BIG;
      default -> SMALL;
    };
  }

  @Override
  public IConfigSpec get() {
    return EffectDispenserConfig.getSpec();
  }
}
