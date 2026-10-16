package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ParallelHatchConfig {
  private static final ParallelHatchConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<ParallelHatchConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ParallelHatchConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static ParallelHatchConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> BASIC_parallel;
  public final ModConfigSpec.ConfigValue<Integer> MEDIUM_parallel;
  public final ModConfigSpec.ConfigValue<Integer> ADVANCED_parallel;
  public final ModConfigSpec.ConfigValue<Integer> ULTIMATE_parallel;
  public final ModConfigSpec.ConfigValue<Integer> MAX_parallel;

  public ParallelHatchConfig(ModConfigSpec.Builder builder) {
    builder.push(ParallelHatchSize.BASIC.getSerializedName());
    BASIC_parallel = builder
        .comment("Defined the max amount of running recipes")
        .defineInRange("max", ParallelHatchSize.BASIC.defaultMax, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ParallelHatchSize.MEDIUM.getSerializedName());
    MEDIUM_parallel = builder
        .comment("Defined the max amount of running recipes")
        .defineInRange("max", ParallelHatchSize.MEDIUM.defaultMax, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ParallelHatchSize.ADVANCED.getSerializedName());
    ADVANCED_parallel = builder
        .comment("Defined the max amount of running recipes")
        .defineInRange("max", ParallelHatchSize.ADVANCED.defaultMax, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ParallelHatchSize.ULTIMATE.getSerializedName());
    ULTIMATE_parallel = builder
        .comment("Defined the max amount of running recipes")
        .defineInRange("max", ParallelHatchSize.ULTIMATE.defaultMax, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ParallelHatchSize.MAX.getSerializedName());
    MAX_parallel = builder
        .comment("Defined the max amount of running recipes")
        .defineInRange("max", ParallelHatchSize.MAX.defaultMax, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public int maxParallel(ParallelHatchSize size) {
    return switch (size) {
      case BASIC -> BASIC_parallel.get();
      case MEDIUM -> MEDIUM_parallel.get();
      case ADVANCED -> ADVANCED_parallel.get();
      case ULTIMATE -> ULTIMATE_parallel.get();
      case MAX -> MAX_parallel.get();
    };
  }

  public int getMaxParallel() {
    int max = BASIC_parallel.get();
    for (ParallelHatchSize size : ParallelHatchSize.values()) {
      int probable = maxParallel(size);
      if (probable > max) max = probable;
    }
    return max;
  }
}
