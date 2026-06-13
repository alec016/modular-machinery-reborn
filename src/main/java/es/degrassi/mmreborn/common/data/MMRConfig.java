package es.degrassi.mmreborn.common.data;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.util.LoggingLevel;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MMRConfig {
  private static final MMRConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<MMRConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(MMRConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public final ConfigValue<LoggingLevel> debugLevel;
  public final ConfigValue<Boolean> logMissingOptional;
  public final ConfigValue<Boolean> logFirstEitherError;

  public final ConfigValue<String> general_casing_color;
  public final ConfigValue<String> chance_color;
  public final ConfigValue<Integer> checkStructureTicks;
  public final ConfigValue<Integer> checkRecipeTicks;
  public final ConfigValue<Integer> dynamicTooltipTicks;
  public final ConfigValue<Integer> maxParallel;

  // Structures config
  public final ConfigValue<Boolean> shouldReplace;
  public final ConfigValue<Boolean> sendReplaceMessage;
  public final ConfigValue<Boolean> sendMissingBlockMessage;
  public final ConfigValue<Boolean> sendErrorMessage;
  public final ConfigValue<Integer> maxVeinNumber;
  public final ConfigValue<Boolean> forceAir;

  public final ConfigValue<Integer> structureRenderTime;
  public final ConfigValue<Integer> blockTagCycleTime;
  public final ConfigValue<String> machineDirectory;
  public final ConfigValue<List<String>> modelFolders;

  public static MMRConfig get() {
    return INSTANCE;
  }

  public MMRConfig(ModConfigSpec.Builder builder) {
    //LOGS
    {
      builder.push("Logs");
      this.logMissingOptional = builder
          .comment("If true, all missing optional properties\nand their default values will be logged\nwhen parsing custom machines jsons.")
          .define("log_missing_optional", false);
      this.logFirstEitherError = builder
          .comment("When parsing custom machines json files,\nsome properties can be read with 2 serializers.\nSet this to true to log when the first serializer throw an error,\neven if the second succeed.")
          .define("log_first_either_error", false);
      this.debugLevel = builder
          .comment("Configure what logs will be printed in the custommachinery.log file.\nOnly logs with level higher or equal than selected will be printed.\nFATAL > ERROR > WARN > INFO > DEBUG > ALL")
          .defineEnum("debug_level", LoggingLevel.INFO);
      builder.pop();
    }
    //GENERAL
    {
      builder.push("General");
      this.general_casing_color = builder
          .comment("Defines the _default_ color for machine casings as items or blocks. (Hex color with alpha at start) Has to be defined both server and clientside!")
          .define("general_casing_color", "#FFFF4900");
      this.chance_color = builder
          .comment("Defines the _default_ color for EMI/JEI chance text color. (Hex color without alpha) Has to be defined both server and clientside!")
          .define("chance_color", "#FFFFFF");
      this.checkStructureTicks = builder
          .comment("Defines the time in ticks that the machine should check for a structure update.\n20 ticks = 1 second. Default: 5")
          .defineInRange("check_structure_ticks", 5, 1, Integer.MAX_VALUE);
      this.checkRecipeTicks = builder
          .comment("Defines the time in ticks that the machine should check for a recipe update.\n20 ticks = 1 second. Default: 80")
          .defineInRange("check_recipe_ticks", 20, 1, Integer.MAX_VALUE);
      this.dynamicTooltipTicks = builder
          .comment("Defines the time between updates in dynamic extra tooltips")
          .defineInRange("dynamic_tooltip_interval", 20, 1, Integer.MAX_VALUE);
      this.maxParallel = builder
          .comment("Defines the number of max parallel recipes that can be run on multiblocks. If this number is " +
              "below than any on [parallel hatch] config path, it will use the max value of them instead.")
          .defineInRange("maxParallel", 256, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    // STRUCTURE
    {
      builder.push("Structure");
      this.shouldReplace = builder
          .comment("Defines if it should break and place the non-matching blocks on trying to place structure. Default: true")
          .define("should_replace", true);
      this.sendReplaceMessage = builder
          .comment("Defines if should sent a message to the player for each replaced block. Default: true")
          .define("replaceMessage", true);
      this.sendMissingBlockMessage = builder
          .comment("Defines if should sent a message to the player for each missing block. Default: true")
          .define("missingBlockMessage", true);
      this.sendErrorMessage = builder
          .comment("Defines if should sent a message to the player for each error on place block. Default: true")
          .define("errorMessage", true);
      this.forceAir = builder
          .comment("Determines if structure tools should force air as block or change it to ANY state. Default: true")
          .define("force_air", true);
      this.maxVeinNumber = builder
          .comment("Defines the max value that can be chained in vein mode in structure tool. Use at your own risk. Default: 64")
          .defineInRange("max_vein_number", 64, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    // RENDERING
    {
      builder.push("Rendering");
      this.structureRenderTime = builder
          .comment("The time in milliseconds the structure requirement\nstructure will render in world when clicking\non the icon in the jei recipe.")
          .defineInRange("structure_render_time", 10000, 1, Integer.MAX_VALUE);
      this.blockTagCycleTime = builder
          .comment("The time in milliseconds each blocks will be shown\nwhen using a block tag in a structure.")
          .defineInRange("block_tag_cycle_time", 1000, 1, Integer.MAX_VALUE);
      builder.pop();
    }
    // DIRECTORIES
    {
      builder.push("Directories");
      this.machineDirectory = builder
          .comment("A folder name where MMR will load machine structure json.\nThese folder must be under the \"data/<namespace>\" folder.")
          .define("machine_directory", "machines");
      this.modelFolders = builder
          .comment("A list of folder names where MMR will load controller models json. These folders must be under the 'assets/namespace/models' folder.")
          .define("model_folders", Lists.newArrayList("controller", "controllers"));
      builder.pop();
    }
  }
}
