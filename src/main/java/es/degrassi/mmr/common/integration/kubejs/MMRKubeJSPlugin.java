package es.degrassi.mmr.common.integration.kubejs;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessorTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.PartialBlockState;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.common.integration.kubejs.builder.FuelDataJS;
import es.degrassi.mmr.common.integration.kubejs.builder.ModifierBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.builder.ProgressDataJS;
import es.degrassi.mmr.common.integration.kubejs.builder.StructureBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.components.IdentifierComponent;
import es.degrassi.mmr.common.integration.kubejs.components.ProgressDataComponent;
import es.degrassi.mmr.common.integration.kubejs.components.RecipeRequirementComponent;
import es.degrassi.mmr.common.integration.kubejs.events.DynamicTooltipKubeEvent;
import es.degrassi.mmr.common.integration.kubejs.events.ExtraTooltipsKubeEvent;
import es.degrassi.mmr.common.integration.kubejs.events.MachineKubeEvent;
import es.degrassi.mmr.common.integration.kubejs.events.FunctionKubeEvent;
import es.degrassi.mmr.common.integration.kubejs.function.MachineControllerJS;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.util.IntRange;
import es.degrassi.mmr.common.util.MachineModelLocation;
import es.degrassi.mmr.common.util.sound.AmbientSound;
import es.degrassi.mmr.common.util.sound.MMRSoundType;
import es.degrassi.mmr.common.util.sound.Sounds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.EnumMap;

public class MMRKubeJSPlugin implements KubeJSPlugin {
  public static final EventGroup MMR_EVENTS = EventGroup.of("MMREvents");
  public static final EventHandler MACHINES = MMR_EVENTS.server("machines", () -> MachineKubeEvent.class);
  public static final TargetedEventHandler<String> FUNCTIONS = MMR_EVENTS
      .server("recipeFunction", () -> FunctionKubeEvent.class)
      .hasResult(TypeInfo.of(Component.class))
      .requiredTarget(EventTargetType.STRING);
  public static final EventHandler TOOLTIPS = MMR_EVENTS.server("extraTooltips", () -> ExtraTooltipsKubeEvent.class);
  public static final TargetedEventHandler<Identifier> DYNAMIC_TOOLTIPS = MMR_EVENTS
      .server("dynamicTooltip", () -> DynamicTooltipKubeEvent.class)
      .hasResult(TypeInfo.of(Component.class))
      .requiredTarget(EventTargetType.ID);

  @Override
  public void registerEvents(EventGroupRegistry registry) {
    registry.register(MMR_EVENTS);
  }

  @Override
  public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
    registry.unit(IdentifierComponent.IDENTIFIER);
    registry.unit(RecipeRequirementComponent.RECIPE_REQUIREMENT);
    registry.unit(ProgressDataComponent.PROGRESS_DATA);
  }

  @Override
  public void registerRecipeFactories(RecipeFactoryRegistry registry) {
    registry.register(ModularMachineryReborn.rl("machine_recipe"), MachineRecipeBuilderJS.class, MachineRecipeBuilderJS::new);
  }

  @Override
  public void registerRecipePostProcessors(RecipePostProcessorTypeRegistry registry) {
    registry.register(RecipeIdPostProcessor.TYPE);
  }

  @Override
  public void beforeScriptsLoaded(ScriptManager manager) {
    RecipeIdPostProcessor.IDS.clear();
  }

  @Override
  public void registerBindings(BindingRegistry registry) {
    registry.add("MMRStructureBuilder", StructureBuilderJS.class);
    registry.add("MMRModifierReplacement", ModifierBuilderJS.class);
    registry.add("MMRRecipeModifier", ModifierBuilderJS.RecipeModifierBuilderJS.class);
    registry.add("ControllerModel", MachineModelLocation.class);
    registry.add("MachineController", MachineControllerJS.class);
    registry.add("ProgressData", ProgressDataJS.class);
    registry.add("FuelData", FuelDataJS.class);
    registry.add("IOType", IOType.class);
    registry.add("TooltipUse", TooltipUse.class);
  }

  @Override
  public void registerTypeWrappers(TypeWrapperRegistry registry) {
    registry.register(IntRange.class, IntRange::of);
    registry.registerCodec(Sounds.class, Sounds.CODEC.codec(), Sounds.DEFAULT);
    registry.registerCodec(AmbientSound.class, AmbientSound.CODEC.codec(), AmbientSound.DEFAULT);
    registry.registerCodec(MMRSoundType.class, MMRSoundType.CODEC.codec(), MMRSoundType.DEFAULT);
    registry.registerEnumFromStringCodec(IOType.class, IOType.CODEC.codec());
    registry.registerEnumFromStringCodec(TooltipUse.class, TooltipUse.CODEC.codec());
    registry.register(BlockIngredient.class, o -> {
      try {
          return BlockIngredient.create(o);
        } catch (CommandSyntaxException e) {
          throw new IllegalArgumentException("Error parsing block ingredient: " + o + "\n" + e.getMessage());
        }
    });
    registry.register(PartialBlockState.class, o -> {
      if(o instanceof CharSequence charSequence) {
        try {
          return PartialBlockState.of(charSequence.toString());
        } catch (CommandSyntaxException e) {
          throw new IllegalArgumentException("Error parsing BlockState: " + o + "\n" + e.getMessage());
        }
      }
      throw new IllegalArgumentException("BlockState must be a string");
    });
  }

  @Override
  public void generateData(KubeDataGenerator generator) {
    KubeJSPlugin.super.generateData(generator);
    var event = new ExtraTooltipsKubeEvent();
    TOOLTIPS.post(event);
    event.getBuilders().forEach(builder -> {
      var id = builder.getId();
      var useOn = builder.getUseOn();
      ModularMachineryReborn
          .MACHINE_EXTRA_TOOLTIPS
          .compute(
              id,
              (k, v) -> v == null ? new EnumMap<>(TooltipUse.class) : v
          ).compute(
              useOn,
              (k, v ) -> builder.build()
          );
    });
  }
}
