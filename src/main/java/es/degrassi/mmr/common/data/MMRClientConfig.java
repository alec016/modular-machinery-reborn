package es.degrassi.mmr.common.data;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIOConfig;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

  public final LongValue openMs;
  public final IntValue autoIOWidth;
  public final IntValue autoIOHeight;

  private final Map<Identifier, BooleanValue> requirements = new HashMap<>();

  /**
   * Use this to add new requirements to the Controller UI rendering
   * if only one core is running.
   * <p><strong>Recommended over mixins</strong></p>
   */
  @Nullable
  public static BooleanValue add(Identifier id, BooleanValue value) {
    return get().requirements.put(id, value);
  }

  /**
   * @param builder to add the common comment for all requirements
   * @return same builder
   */
  public static ModConfigSpec.@NonNull Builder oneCore(ModConfigSpec.Builder builder) {
    return builder.comment("This only applies if only 1 core is running");
  }

  public MMRClientConfig(ModConfigSpec.Builder builder) {
    final String advertising = "Modifying this value can cause issues";
    // AUTO IO TAB
    {
      builder.push("AutoIO Tab");
      this.autoIOWidth = builder
          .comment("Defines the opened width")
          .comment("of auto io tab")
          .comment(advertising)
          .defineInRange("width", AutoIOConfig.CONFIG.defaultWidth, 1, Integer.MAX_VALUE);
      this.autoIOHeight = builder
          .comment("Defines the opened height")
          .comment("of auto io tab")
          .comment(advertising)
          .defineInRange("height", AutoIOConfig.CONFIG.defaultHeight, 1, Integer.MAX_VALUE);
      this.openMs = builder
          .comment("Defines the time it takes to open")
          .comment("the auto io tab in ms")
          .defineInRange("open_time", AutoIOConfig.CONFIG.defaultMs, 1, Long.MAX_VALUE);
      builder.pop();
    }
    // CONTROLLER UI
    {
      builder.push("Controller UI");
      this.showRecipeId = oneCore(builder
          .comment("Defines if should be added the recipe id or not in Controller UI"))
          .define("show_recipe_id", false);
      this.showRecipeOutputs = oneCore(builder
          .comment("Defines if should be added the recipe outputs or not in ControllerUI"))
          .define("show_recipe_outputs", true);
      // REQUIREMENTS
      {
        builder.push("Requirements");
        var item = oneCore(builder
            .comment("Defines if should add to ControllerUI all produce item requirement"))
            .define("item", true);
        add(mmr("item"), item);
        var durability = oneCore(builder
            .comment("Defines if should add to ControllerUI all repair durability requirements"))
            .define("durability", true);
        add(mmr("durability"), durability);
        add(mmr("durability_per_tick"), durability);
        var fluid = oneCore(builder
            .comment("Defines if should add to ControllerUI all produce fluid requirements"))
            .define("fluid", true);
        add(mmr("fluid"), fluid);
        add(mmr("fluid_per_tick"), fluid);
        var effect = oneCore(builder
            .comment("Defines if should add to ControllerUI all giving effect requirements"))
            .define("effect", true);
        add(mmr("effect"), effect);
        var experience = oneCore(builder
            .comment("Defines if should add to ControllerUI all producing experience requirements"))
            .define("experience", true);
        add(mmr("experience"), experience);
        add(mmr("experience_per_tick"), experience);
        var energy = oneCore(builder
            .comment("Defines if should add to ControllerUI all producing energy requirements"))
            .define("energy", true);
        add(mmr("energy"), energy);
        add(mmr("energy_per_tick"), energy);
        var redstone = oneCore(builder
            .comment("Defines if should add to ControllerUI all producing redstone requirements"))
            .define("redstone", true);
        add(mmr("redstone"), redstone);
        var lootTable = oneCore(builder
            .comment("Defines if should add to ControllerUI all loot table output requirements"))
            .define("loot_table", true);
        add(mmr("loot_table"), lootTable);
        builder.pop();
      }
      builder.pop();
    }
  }

  private Identifier mmr(String path) {
    return ModularMachineryReborn.rl(path);
  }

  public boolean get(Identifier requirement) {
    return Optional.ofNullable(requirements.get(requirement)).map(BooleanValue::getAsBoolean).orElse(false);
  }
}
