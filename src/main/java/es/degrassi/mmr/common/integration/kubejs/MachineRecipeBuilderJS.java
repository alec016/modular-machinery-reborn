package es.degrassi.mmr.common.integration.kubejs;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.integration.kubejs.builder.ProgressDataJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.BiomeRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.ChunkloadRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.CommandRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.DimensionRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.DurabilityPerTickRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.DurabilityRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.EffectRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.EmptyRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.EnergyPerTickRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.EnergyRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.EntityRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.ExperiencePerTickRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.ExperienceRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.FluidPerTickRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.FluidRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.FuelRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.FunctionRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.HeightRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.ItemRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.LootTableRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.RedstoneRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.StructureRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.TimeRequirementJS;
import es.degrassi.mmr.common.integration.kubejs.requirement.WeatherRequirementJS;
import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MachineRecipeBuilderJS extends KubeRecipe implements RecipeJSBuilder,
    EnergyPerTickRequirementJS,
    EnergyRequirementJS,
    ItemRequirementJS,
    FluidRequirementJS,
    FluidPerTickRequirementJS,
    DimensionRequirementJS,
    BiomeRequirementJS,
    WeatherRequirementJS,
    TimeRequirementJS,
    ChunkloadRequirementJS,
    LootTableRequirementJS,
    ExperienceRequirementJS,
    ExperiencePerTickRequirementJS,
    HeightRequirementJS,
    FunctionRequirementJS,
    DurabilityRequirementJS,
    DurabilityPerTickRequirementJS,
    FuelRequirementJS,
    EmptyRequirementJS,
    EffectRequirementJS,
    EntityRequirementJS,
    StructureRequirementJS,
    RedstoneRequirementJS,
    CommandRequirementJS
{

  @HideFromJS
  private boolean jei = false;

  @HideFromJS
  public MachineRecipeBuilderJS() {}

  public MachineRecipeBuilderJS jei() {
    this.jei = true;
    return this;
  }

  public MachineRecipeBuilderJS hide(Context cx) {
    set(cx, "hidden", true);
    return this;
  }

  public MachineRecipeBuilderJS renderProgress(Context cx, boolean value) {
    set(cx, "renderProgress", value);
    return this;
  }

  public MachineRecipeBuilderJS progressData(Context cx, ProgressDataJS data) {
    set(cx, "progressData", data.build());
    return this;
  }

  public MachineRecipeBuilderJS width(Context cx, int width) {
    set(cx, "width", width);
    return this;
  }

  public MachineRecipeBuilderJS height(Context cx, int height) {
    set(cx, "height", height);
    return this;
  }

  public MachineRecipeBuilderJS voidOnFailure(Context cx, boolean v) {
    set(cx, "voidOnFailure", v);
    return this;
  }

  public MachineRecipeBuilderJS priority(Context cx, int priority) {
    set(cx, "priority", priority);
    return this;
  }

  @Override
  @HideFromJS
  @SuppressWarnings("unchecked, rawtypes")
  public MachineRecipeBuilderJS addRequirement(RecipeRequirement<?, ?, ?> requirement) {
    for(RecipeComponentValue<?> value : this.getRecipeComponentValues()) {
      if(value.key.name.equals("requirements") && !this.jei)
        setValue((RecipeKey)value.key, addToList("requirements", requirement));
      else if(value.key.name.equals("jei_requirements") && this.jei)
        setValue((RecipeKey)value.key, addToList("jei_requirements", requirement));
    }
    return this;
  }

  @HideFromJS
  @SuppressWarnings("unchecked")
  private <E> List<E> addToList(String key, E element) {
    List<E> list = new ArrayList<>((List<E>) get(key));
    list.add(element);
    return list;
  }

  @Override
  @HideFromJS
  public MachineRecipeBuilderJS error(String error, Object... args) {
    throw new KubeRuntimeException(MessageFormatter.arrayFormat(error, args).getMessage()).source(this.sourceLine);
  }
}
