package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.core.EnumRepresentable;
import es.degrassi.mmr.common.data.config.ParallelHatchConfig;
import net.neoforged.fml.config.IConfigSpec;

public enum ParallelHatchSize implements ConfigLoaded, EnumRepresentable {
  BASIC(2),
  MEDIUM(25),
  ADVANCED(50),
  ULTIMATE(100),
  MAX(256);

  public static ParallelHatchSize value(String value) {
    return switch (ModularMachineryReborn.rootUC(value)) {
      case "MEDIUM" -> MEDIUM;
      case "ADVANCED" -> ADVANCED;
      case "ULTIMATE" -> ULTIMATE;
      case "MAX" -> MAX;
      default -> BASIC;
    };
  }

  public final int defaultMax;
  public int max;

  ParallelHatchSize(int max) {
    this.defaultMax = max;
  }

  @Override
  public IConfigSpec get() {
    return ParallelHatchConfig.getSpec();
  }
}
