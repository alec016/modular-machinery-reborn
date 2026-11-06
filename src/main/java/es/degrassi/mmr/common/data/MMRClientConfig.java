package es.degrassi.mmr.common.data;

import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MMRClientConfig {
  private static final MMRClientConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<MMRClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(MMRClientConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static MMRClientConfig get() {
    return INSTANCE;
  }

  public final BooleanValue showRecipeId;
  public final BooleanValue showRecipeOutputs;

  private final Map<Identifier, BooleanValue> requirements = new HashMap<>();

  public MMRClientConfig(ModConfigSpec.Builder builder) {
    final String coreText = "This only applies if only 1 core is running";
    builder.push("Controller UI");
    this.showRecipeId = builder
        .comment("Defines if should be added the recipe id or not in Controller UI")
        .comment(coreText)
        .define("show_recipe_id", false);
    this.showRecipeOutputs = builder
        .comment("Defines if should be added the recipe outputs or not in ControllerUI")
        .comment(coreText)
        .define("show_recipe_outputs", true);
    builder.push("Requirements");
    var item = builder
        .comment("Defines if should add to ControllerUI all produce item requirement")
        .comment(coreText)
        .define("item", true);
    requirements.put(mmr("item"), item);
    var durability = builder
        .comment("Defines if should add to ControllerUI all repair durability requirements")
        .comment(coreText)
        .define("durability", true);
    requirements.put(mmr("durability"), durability);
    requirements.put(mmr("durability_per_tick"), durability);
    var fluid = builder
        .comment("Defines if should add to ControllerUI all produce fluid requirements")
        .comment(coreText)
        .define("fluid", true);
    requirements.put(mmr("fluid"), fluid);
    requirements.put(mmr("fluid_per_tick"), fluid);
    var effect = builder
        .comment("Defines if should add to ControllerUI all giving effect requirements")
        .comment(coreText)
        .define("effect", true);
    requirements.put(mmr("effect"), effect);
    builder.pop();
    builder.pop();
  }

  private Identifier mmr(String path) {
    return ModularMachineryReborn.rl(path);
  }

  public boolean get(Identifier requirement) {
    return Optional.ofNullable(requirements.get(requirement)).map(BooleanValue::getAsBoolean).orElse(true);
  }
}
