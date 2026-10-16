package es.degrassi.mmr.common.integration.kubejs.requirement;

import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementStructure;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.integration.kubejs.builder.StructureBuilderJS;

public interface StructureRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS requireStructure(StructureBuilderJS builder) {
    return requireStructure(builder, RequirementStructure.Action.CHECK);
  }

  default MachineRecipeBuilderJS destroyStructure(StructureBuilderJS builder) {
    return requireStructure(builder, RequirementStructure.Action.DESTROY);
  }

  default MachineRecipeBuilderJS breakStructure(StructureBuilderJS builder) {
    return requireStructure(builder, RequirementStructure.Action.BREAK);
  }

  default MachineRecipeBuilderJS placeStructure(StructureBuilderJS builder) {
    return placeStructure(builder, true);
  }

  default MachineRecipeBuilderJS placeStructure(StructureBuilderJS builder, boolean drops) {
    return requireStructure(builder, drops ? RequirementStructure.Action.PLACE_BREAK : RequirementStructure.Action.PLACE_DESTROY);
  }

  @HideFromJS
  default MachineRecipeBuilderJS requireStructure(StructureBuilderJS builder, RequirementStructure.Action action) {
    try {
      return addRequirement(new RecipeRequirement<>(new RequirementStructure(builder.build(), action)));
    } catch(Exception e) {
      return error("Error while creating structure requirement: {}", e.getMessage());
    }
  }
}
