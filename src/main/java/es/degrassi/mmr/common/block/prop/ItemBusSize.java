package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import net.neoforged.fml.config.IConfigSpec;

import java.util.Locale;

public enum ItemBusSize implements EnumRepresentable, ConfigLoaded {
  TINY(1, 9, 64),
  SMALL(4, 9, 64),
  NORMAL(6, 9, 64),
  REINFORCED(9, 9, 64),
  BIG(12, 9, 64),
  HUGE(16, 9, 64),
  LUDICROUS(32, 9, 64);

  public int slots;
  public int cols;
  public int stackSize;

  public final int defaultSlots;
  public final int defaultCols;
  public final int defaultStackSize;

  ItemBusSize(int defaultSlots, int defaultCols, int defaultStackSize) {
    this.defaultSlots = defaultSlots;
    this.defaultCols = defaultCols;
    this.defaultStackSize = defaultStackSize;
  }

  public static ItemBusSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      default -> TINY;
    };
  }

  public int getSlotCount() {
    return slots;
  }

  @Override
  public IConfigSpec get() {
    return ItemBusConfig.getSpec();
  }
}
