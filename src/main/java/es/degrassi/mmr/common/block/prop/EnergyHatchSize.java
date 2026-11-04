package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;

import java.util.Locale;

public enum EnergyHatchSize implements EnumRepresentable, ConfigLoaded {
  TINY      (2048,    128),
  SMALL     (4096,    512),
  NORMAL    (8192,    512),
  REINFORCED(16384,   2048),
  BIG       (32768,   8192),
  HUGE      (131072,  32768),
  LUDICROUS (524288,  131072),
  ULTIMATE  (2097152, 131072);

  public static EnergyHatchSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "ULTIMATE" -> ULTIMATE;
      default -> TINY;
    };
  }

  public long maxEnergy;
  public long transferLimit;

  public final long defaultConfigurationEnergy;
  public final long defaultConfigurationTransferLimit;

  EnergyHatchSize(long maxEnergy, long transferLimit) {
    this.defaultConfigurationEnergy = maxEnergy;
    this.defaultConfigurationTransferLimit = transferLimit;
  }
}
