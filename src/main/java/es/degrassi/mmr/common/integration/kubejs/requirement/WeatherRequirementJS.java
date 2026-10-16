package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.api.crafting.requirement.WeatherType;
import es.degrassi.mmr.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;

public interface WeatherRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS weather(WeatherType type) {
    return addRequirement(new RecipeRequirement<>(new RequirementWeather(type)));
  }
}
