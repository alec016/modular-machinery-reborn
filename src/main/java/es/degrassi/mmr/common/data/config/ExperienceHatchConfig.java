package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ExperienceHatchConfig {
  private static final ExperienceHatchConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<ExperienceHatchConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ExperienceHatchConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static ExperienceHatchConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> TINY_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> SMALL_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> NORMAL_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> REINFORCED_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> BIG_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> HUGE_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> LUDICROUS_experience_size;
  public final ModConfigSpec.ConfigValue<Integer> VACUUM_experience_size;

  public ExperienceHatchConfig(ModConfigSpec.Builder builder) {
    builder.push(ExperienceHatchSize.TINY.getSerializedName());
    TINY_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.TINY.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.SMALL.getSerializedName());
    SMALL_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.SMALL.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.NORMAL.getSerializedName());
    NORMAL_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.NORMAL.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.REINFORCED.getSerializedName());
    REINFORCED_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.REINFORCED.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.BIG.getSerializedName());
    BIG_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.BIG.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.HUGE.getSerializedName());
    HUGE_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.HUGE.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.LUDICROUS.getSerializedName());
    LUDICROUS_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.LUDICROUS.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
    builder.push(ExperienceHatchSize.VACUUM.getSerializedName());
    VACUUM_experience_size = builder
        .comment("Defines the Experience Hatch")
        .defineInRange("capacity", ExperienceHatchSize.VACUUM.defaultCapacity, 1, Integer.MAX_VALUE);
    builder.pop();
  }

  public int experienceSize(ExperienceHatchSize size) {
    return switch (size) {
      case TINY -> TINY_experience_size.get();
      case SMALL -> SMALL_experience_size.get();
      case NORMAL -> NORMAL_experience_size.get();
      case REINFORCED -> REINFORCED_experience_size.get();
      case BIG -> BIG_experience_size.get();
      case HUGE -> HUGE_experience_size.get();
      case LUDICROUS -> LUDICROUS_experience_size.get();
      case VACUUM -> VACUUM_experience_size.get();
    };
  }
}
