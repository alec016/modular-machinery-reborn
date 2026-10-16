package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class EffectDispenserConfig {
  private static final EffectDispenserConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<EffectDispenserConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(EffectDispenserConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static EffectDispenserConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Integer> smallRadius;
  public final ModConfigSpec.ConfigValue<Integer> mediumRadius;
  public final ModConfigSpec.ConfigValue<Integer> bigRadius;

  /*public final ModConfigSpec.ConfigValue<Boolean> smallDimensional;
  public final ModConfigSpec.ConfigValue<Boolean> mediumDimensional;
  public final ModConfigSpec.ConfigValue<Boolean> bigDimensional;*/

  public final ModConfigSpec.ConfigValue<Boolean> smallShowParticles;
  public final ModConfigSpec.ConfigValue<Boolean> mediumShowParticles;
  public final ModConfigSpec.ConfigValue<Boolean> bigShowParticles;

  public EffectDispenserConfig(ModConfigSpec.Builder builder) {
    builder.push(EffectDispenserSize.SMALL.getSerializedName());
    smallRadius = builder
        .comment("Defines the effect area radius in blocks", "This is ignored if the hatch is interdimensional")
        .defineInRange("radius", EffectDispenserSize.SMALL.defaultRadius, 1, Integer.MAX_VALUE);
    /*smallDimensional = builder
        .comment("Defines if this hatch tier ignores the radius and gives effects interdimensional")
        .define("interdimensional", EffectDispenserSize.SMALL.defaultDimensional);*/
    smallShowParticles = builder
        .comment("Defines if this hatch should show the area effect in form of particles", "This is ignored if the hatch is interdimensional")
        .define("showParticles", EffectDispenserSize.SMALL.defaultShowParticles);
    builder.pop();
    builder.push(EffectDispenserSize.MEDIUM.getSerializedName());
    mediumRadius = builder
        .comment("Defines the effect area radius in blocks", "This is ignored if the hatch is interdimensional")
        .defineInRange("radius", EffectDispenserSize.MEDIUM.defaultRadius, 1, Integer.MAX_VALUE);
    /*mediumDimensional = builder
        .comment("Defines if this hatch tier ignores the radius and gives effects interdimensional")
        .define("interdimensional", EffectDispenserSize.MEDIUM.defaultDimensional);*/
    mediumShowParticles = builder
        .comment("Defines if this hatch should show the area effect in form of particles", "This is ignored if the hatch is interdimensional")
        .define("showParticles", EffectDispenserSize.MEDIUM.defaultShowParticles);
    builder.pop();
    builder.push(EffectDispenserSize.BIG.getSerializedName());
    bigRadius = builder
        .comment("Defines the effect area radius in blocks", "This is ignored if the hatch is interdimensional")
        .defineInRange("radius", EffectDispenserSize.BIG.defaultRadius, 1, Integer.MAX_VALUE);
    /*bigDimensional = builder
        .comment("Defines if this hatch tier ignores the radius and gives effects interdimensional")
        .define("interdimensional", EffectDispenserSize.BIG.defaultDimensional);*/
    bigShowParticles = builder
        .comment("Defines if this hatch should show the area effect in form of particles.", "This is ignored if the hatch is interdimensional")
        .define("showParticles", EffectDispenserSize.BIG.defaultShowParticles);
    builder.pop();
  }

  public int radius(EffectDispenserSize size) {
    return (switch (size) {
      case SMALL -> smallRadius;
      case MEDIUM -> mediumRadius;
      case BIG -> bigRadius;
    }).get();
  }

  public boolean interdimensional(EffectDispenserSize size) {
    return false;
    /*return (switch (size) {
      case SMALL -> smallDimensional;
      case MEDIUM -> mediumDimensional;
      case BIG -> bigDimensional;
    }).get();*/
  }

  public boolean showParticles(EffectDispenserSize size) {
    return (switch (size) {
      case SMALL -> smallShowParticles;
      case MEDIUM -> mediumShowParticles;
      case BIG -> bigShowParticles;
    }).get();
  }
}
