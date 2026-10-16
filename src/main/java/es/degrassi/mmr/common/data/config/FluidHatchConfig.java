package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FluidHatchConfig {
  private static final FluidHatchConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<FluidHatchConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(FluidHatchConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static FluidHatchConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> TINY_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> LUDICROUS_fluid_size;
  public final ModConfigSpec.ConfigValue<Integer> VACUUM_fluid_size;

  public FluidHatchConfig(ModConfigSpec.Builder builder) {
    builder.push(FluidHatchSize.TINY.getSerializedName());
    TINY_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.TINY.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.SMALL.getSerializedName());
    SMALL_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.SMALL.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.NORMAL.getSerializedName());
    NORMAL_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.NORMAL.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.REINFORCED.getSerializedName());
    REINFORCED_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.REINFORCED.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.BIG.getSerializedName());
    BIG_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.BIG.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.HUGE.getSerializedName());
    HUGE_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.HUGE.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.LUDICROUS.getSerializedName());
    LUDICROUS_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.LUDICROUS.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(FluidHatchSize.VACUUM.getSerializedName());
    VACUUM_fluid_size = builder
        .comment("Defines the tank size of fluid hatch in mB")
        .defineInRange("size", FluidHatchSize.VACUUM.defaultConfigurationValue, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public int fluidSize(FluidHatchSize size) {
    return switch (size) {
      case TINY -> TINY_fluid_size.get();
      case SMALL -> SMALL_fluid_size.get();
      case NORMAL -> NORMAL_fluid_size.get();
      case REINFORCED -> REINFORCED_fluid_size.get();
      case BIG -> BIG_fluid_size.get();
      case HUGE -> HUGE_fluid_size.get();
      case LUDICROUS -> LUDICROUS_fluid_size.get();
      case VACUUM -> VACUUM_fluid_size.get();
    };
  }
}
