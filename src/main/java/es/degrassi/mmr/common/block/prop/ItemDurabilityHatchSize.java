package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;
import es.degrassi.mmr.common.data.config.DurabilityHatchConfig;
import net.neoforged.fml.config.IConfigSpec;

import java.util.Locale;

public enum ItemDurabilityHatchSize implements EnumRepresentable, ConfigLoaded {
  TINY(1, 9),
  SMALL(4, 9),
  NORMAL(6, 9),
  BIG(9, 9);

  public int slots;
  public int cols;
  public int stackSize;

  public final int defaultSlots;
  public final int defaultCols;
  public final int defaultStackSize;

  ItemDurabilityHatchSize(int defaultSlots, int defaultCols) {
    this.defaultSlots = defaultSlots;
    this.defaultCols = defaultCols;
    this.defaultStackSize = 64;
  }

  public static ItemDurabilityHatchSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "BIG" -> BIG;
      default -> TINY;
    };
  }

  public int getSlotCount() {
    return slots;
  }

  @Override
  public IConfigSpec get() {
    return DurabilityHatchConfig.getSpec();
  }
}
