package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DurabilityHatchConfig {
  private static final DurabilityHatchConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<DurabilityHatchConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(DurabilityHatchConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static DurabilityHatchConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> TINY_durability_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_durability_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_durability_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_durability_size;

  public final ModConfigSpec.ConfigValue<Integer> TINY_durability_cols;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_durability_cols;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_durability_cols;
  public final ModConfigSpec.ConfigValue<Integer> BIG_durability_cols;

  public final ModConfigSpec.ConfigValue<Integer> TINY_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_stack_size;

  public DurabilityHatchConfig(ModConfigSpec.Builder builder) {
    builder.push(ItemDurabilityHatchSize.TINY.getSerializedName());
    TINY_durability_size = builder
        .comment("Defines the slots number of item durability hatch")
        .defineInRange("slots", ItemDurabilityHatchSize.TINY.defaultSlots, 1, Integer.MAX_VALUE);
    TINY_durability_cols = builder
        .comment("Defines the slot cols number of item durability hatch")
        .defineInRange("cols", ItemDurabilityHatchSize.TINY.defaultCols, 1, Integer.MAX_VALUE);
    TINY_stack_size = builder
        .comment("Defined the durability hatch slot max stack size")
        .defineInRange("stackSize", ItemDurabilityHatchSize.TINY.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ItemDurabilityHatchSize.SMALL.getSerializedName());
    SMALL_durability_size = builder
        .comment("Defines the slots number of item durability hatch")
        .defineInRange("slots", ItemDurabilityHatchSize.SMALL.defaultSlots, 1, Integer.MAX_VALUE);
    SMALL_durability_cols = builder
        .comment("Defines the slot cols number of item durability hatch")
        .defineInRange("cols", ItemDurabilityHatchSize.SMALL.defaultCols, 1, Integer.MAX_VALUE);
    SMALL_stack_size = builder
        .comment("Defined the durability hatch slot max stack size")
        .defineInRange("stackSize", ItemDurabilityHatchSize.SMALL.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ItemDurabilityHatchSize.NORMAL.getSerializedName());
    NORMAL_durability_size = builder
        .comment("Defines the slots number of item durability hatch")
        .defineInRange("slots", ItemDurabilityHatchSize.NORMAL.defaultSlots, 1, Integer.MAX_VALUE);
    NORMAL_durability_cols = builder
        .comment("Defines the slot cols number of item durability hatch")
        .defineInRange("cols", ItemDurabilityHatchSize.NORMAL.defaultCols, 1, Integer.MAX_VALUE);
    NORMAL_stack_size = builder
        .comment("Defined the durability hatch slot max stack size")
        .defineInRange("stackSize", ItemDurabilityHatchSize.NORMAL.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ItemDurabilityHatchSize.BIG.getSerializedName());
    BIG_durability_size = builder
        .comment("Defines the slots number of item durability hatch")
        .defineInRange("slots", ItemDurabilityHatchSize.BIG.defaultSlots, 1, Integer.MAX_VALUE);
    BIG_durability_cols = builder
        .comment("Defines the slot cols number of item durability hatch")
        .defineInRange("cols", ItemDurabilityHatchSize.BIG.defaultCols, 1, Integer.MAX_VALUE);
    BIG_stack_size = builder
        .comment("Defined the durability hatch slot max stack size")
        .defineInRange("stackSize", ItemDurabilityHatchSize.BIG.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public int durabilitySize(ItemDurabilityHatchSize size) {
    return switch (size) {
      case TINY -> TINY_durability_size.get();
      case SMALL -> SMALL_durability_size.get();
      case NORMAL -> NORMAL_durability_size.get();
      case BIG -> BIG_durability_size.get();
    };
  }

  public int durabilityCols(ItemDurabilityHatchSize size) {
    return switch (size) {
      case TINY -> TINY_durability_cols.get();
      case SMALL -> SMALL_durability_cols.get();
      case NORMAL -> NORMAL_durability_cols.get();
      case BIG -> BIG_durability_cols.get();
    };
  }

  public int stackSize(ItemDurabilityHatchSize size) {
    return (switch (size) {
      case TINY -> TINY_stack_size;
      case SMALL -> SMALL_stack_size;
      case NORMAL -> NORMAL_stack_size;
      case BIG -> BIG_stack_size;
    }).get();
  }
}
