package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.FuelTankSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FuelTankConfig {
  private static final FuelTankConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<FuelTankConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(FuelTankConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public final ModConfigSpec.ConfigValue<Long> TINY_fuelCapacity;
  public final ModConfigSpec.ConfigValue<Long> SMALL_fuelCapacity;
  public final ModConfigSpec.ConfigValue<Long> NORMAL_fuelCapacity;
  public final ModConfigSpec.ConfigValue<Long> REINFORCED_fuelCapacity;
  public final ModConfigSpec.ConfigValue<Long> BIG_fuelCapacity;
  public final ModConfigSpec.ConfigValue<Long> HUGE_fuelCapacity;

  public final ModConfigSpec.ConfigValue<Integer> TINY_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_stack_size;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_stack_size;

  public final ModConfigSpec.ConfigValue<Boolean> reduceFuelPerTick;

  public static FuelTankConfig get() {
    return INSTANCE;
  }

  public FuelTankConfig(ModConfigSpec.Builder builder) {
    builder.push("General");
    reduceFuelPerTick = builder
        .comment("Weather the fuel tank should reduce its fuel when no recipe is running or not")
        .define("reduceFuel", true);
    builder.pop();
    builder.push(FuelTankSize.TINY.getSerializedName());
    TINY_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.TINY.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    TINY_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.TINY.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FuelTankSize.SMALL.getSerializedName());
    SMALL_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.SMALL.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    SMALL_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.SMALL.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FuelTankSize.NORMAL.getSerializedName());
    NORMAL_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.NORMAL.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    NORMAL_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.NORMAL.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FuelTankSize.REINFORCED.getSerializedName());
    REINFORCED_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.REINFORCED.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    REINFORCED_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.REINFORCED.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FuelTankSize.BIG.getSerializedName());
    BIG_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.BIG.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    BIG_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.BIG.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FuelTankSize.HUGE.getSerializedName());
    HUGE_fuelCapacity = builder
        .comment("Defines the burntime capacity in ticks (1s = 20ticks)")
        .defineInRange("fuelCapacity", FuelTankSize.HUGE.defaultBurnTimeCapacity, 1, Long.MAX_VALUE);
    HUGE_stack_size = builder
        .comment("Defined the fuel tank slot max stack size")
        .defineInRange("stackSize", FuelTankSize.HUGE.defaultStackSize, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public long fuelCapacity(FuelTankSize size) {
    return switch (size) {
      case TINY -> TINY_fuelCapacity.get();
      case SMALL -> SMALL_fuelCapacity.get();
      case NORMAL -> NORMAL_fuelCapacity.get();
      case REINFORCED -> REINFORCED_fuelCapacity.get();
      case BIG -> BIG_fuelCapacity.get();
      case HUGE -> HUGE_fuelCapacity.get();
    };
  }

  public int stackSize(FuelTankSize size) {
    return (switch (size) {
      case TINY -> TINY_stack_size;
      case SMALL -> SMALL_stack_size;
      case NORMAL -> NORMAL_stack_size;
      case REINFORCED -> REINFORCED_stack_size;
      case BIG -> BIG_stack_size;
      case HUGE -> HUGE_stack_size;
    }).get();
  }
}
