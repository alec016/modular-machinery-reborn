package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementTime;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.util.IntRange;

public interface TimeRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS time(String time) {
    try {
      IntRange range = IntRange.createFromString(time);
      return this.addRequirement(new RecipeRequirement<>(new RequirementTime(range)));
    } catch (IllegalArgumentException e) {
      return error("Impossible to parse time range: \"{}\", ", time, e);
    }
  }
}
