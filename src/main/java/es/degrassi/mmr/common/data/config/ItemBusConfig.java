package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.ItemBusSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ItemBusConfig {
  private static final ItemBusConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<ItemBusConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ItemBusConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static ItemBusConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> itemSlotXOffset;
  public final ModConfigSpec.ConfigValue<Integer> itemSlotYOffset;

  public final ModConfigSpec.ConfigValue<Integer> TINY_item_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_item_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_item_size;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_item_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_item_size;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_item_size;
  public final ModConfigSpec.ConfigValue<Integer> LUDICROUS_item_size;

  /*public final ModConfigSpec.ConfigValue<Integer> TINY_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> LUDICROUS_stack_size;*/

  public final ModConfigSpec.ConfigValue<Integer> TINY_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> BIG_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_item_cols;
  public final ModConfigSpec.ConfigValue<Integer> LUDICROUS_item_cols;

  public ItemBusConfig(ModConfigSpec.Builder builder) {
    builder.push("container");
    {
      itemSlotXOffset = builder
          .comment("Defines the container X offset relative to top-left corner of the texture")
          .defineInRange("xOffset", 8, 0, Integer.MAX_VALUE);
      itemSlotYOffset = builder
          .comment("Defines the container Y offset relative to top-left corner of the texture")
          .defineInRange("yOffset", 8, 0, Integer.MAX_VALUE);
    }
    builder.pop();
    builder.push(ItemBusSize.TINY.getSerializedName());
    TINY_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.TINY.defaultSlots, 1, Integer.MAX_VALUE);
    TINY_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.TINY.defaultCols, 1, Integer.MAX_VALUE);
    /*TINY_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.TINY.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.SMALL.getSerializedName());
    SMALL_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.SMALL.defaultSlots, 1, Integer.MAX_VALUE);
    SMALL_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.SMALL.defaultCols, 1, Integer.MAX_VALUE);
    /*SMALL_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.SMALL.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.NORMAL.getSerializedName());
    NORMAL_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.NORMAL.defaultSlots, 1, Integer.MAX_VALUE);
    NORMAL_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.NORMAL.defaultCols, 1, Integer.MAX_VALUE);
    /*NORMAL_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.NORMAL.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.REINFORCED.getSerializedName());
    REINFORCED_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.REINFORCED.defaultSlots, 1, Integer.MAX_VALUE);
    REINFORCED_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.REINFORCED.defaultCols, 1, Integer.MAX_VALUE);
    /*REINFORCED_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.REINFORCED.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.BIG.getSerializedName());
    BIG_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.BIG.defaultSlots, 1, Integer.MAX_VALUE);
    BIG_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.BIG.defaultCols, 1, Integer.MAX_VALUE);
    /*BIG_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.BIG.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.HUGE.getSerializedName());
    HUGE_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.HUGE.defaultSlots, 1, Integer.MAX_VALUE);
    HUGE_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.HUGE.defaultCols, 1, Integer.MAX_VALUE);
    /*HUGE_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.HUGE.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
    builder.push(ItemBusSize.LUDICROUS.getSerializedName());
    LUDICROUS_item_size = builder
        .comment("Defines the slots number of item bus")
        .defineInRange("slots", ItemBusSize.LUDICROUS.defaultSlots, 1, Integer.MAX_VALUE);
    LUDICROUS_item_cols = builder
        .comment("Defines the slot cols number of item bus")
        .defineInRange("cols", ItemBusSize.LUDICROUS.defaultCols, 1, Integer.MAX_VALUE);
    /*LUDICROUS_stack_size = builder
        .comment("Defined the item bus slot max stack size")
        .defineInRange("stackSize", ItemBusSize.LUDICROUS.defaultStackSize, 1, Integer.MAX_VALUE);*/
    builder.pop();
  }

  public int itemSize(ItemBusSize size) {
    return switch (size) {
      case TINY -> TINY_item_size.get();
      case SMALL -> SMALL_item_size.get();
      case NORMAL -> NORMAL_item_size.get();
      case REINFORCED -> REINFORCED_item_size.get();
      case BIG -> BIG_item_size.get();
      case HUGE -> HUGE_item_size.get();
      case LUDICROUS -> LUDICROUS_item_size.get();
    };
  }

  public int itemCols(ItemBusSize size) {
    return switch (size) {
      case TINY -> TINY_item_cols.get();
      case SMALL -> SMALL_item_cols.get();
      case NORMAL -> NORMAL_item_cols.get();
      case REINFORCED -> REINFORCED_item_cols.get();
      case BIG -> BIG_item_cols.get();
      case HUGE -> HUGE_item_cols.get();
      case LUDICROUS -> LUDICROUS_item_cols.get();
    };
  }

  public int stackSize(ItemBusSize size) {
    return 64;
    /*return (switch (size) {
      case TINY -> TINY_stack_size;
      case SMALL -> SMALL_stack_size;
      case NORMAL -> NORMAL_stack_size;
      case REINFORCED -> REINFORCED_stack_size;
      case BIG -> BIG_stack_size;
      case HUGE -> HUGE_stack_size;
      case LUDICROUS -> LUDICROUS_stack_size;
    }).get();*/
  }
}
