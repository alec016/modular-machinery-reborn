package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;
import lombok.Getter;

import java.util.Locale;

public enum ExperienceHatchSize implements EnumRepresentable, ConfigLoaded {
  TINY(1_000),
  SMALL(4_000),
  NORMAL(10_000),
  REINFORCED(20_000),
  BIG(45_000),
  HUGE(80_000),
  LUDICROUS(160_000),
  VACUUM(320_000);

  @Getter
  public int capacity;

  public final int defaultCapacity;

  ExperienceHatchSize(int defaultCapacity) {
    this.defaultCapacity = defaultCapacity;
  }

  public static ExperienceHatchSize value(String value) {
    return switch(value.toUpperCase(Locale.ENGLISH)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "VACUUM" -> VACUUM;
      default -> TINY;
    };
  }
}
