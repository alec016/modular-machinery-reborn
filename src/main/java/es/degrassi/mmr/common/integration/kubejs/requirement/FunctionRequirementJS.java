package es.degrassi.mmr.common.integration.kubejs.requirement;

import com.google.common.collect.Lists;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementFunction;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public interface FunctionRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFunctionToStart(String id, @Nullable String... args) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.CHECK, id, Optional.ofNullable(args).map(Arrays::asList).orElse(Lists.newArrayList()))));
  }

  default MachineRecipeBuilderJS requireFunctionOnStart(String id, @Nullable String... args) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.START, id, Optional.ofNullable(args).map(Arrays::asList).orElse(Lists.newArrayList()))));
  }

  default MachineRecipeBuilderJS requireFunctionEachTick(String id, @Nullable String... args) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.TICK, id, Optional.ofNullable(args).map(Arrays::asList).orElse(Lists.newArrayList()))));
  }

  default MachineRecipeBuilderJS requireFunctionOnEnd(String id, @Nullable String... args) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.END, id, Optional.ofNullable(args).map(Arrays::asList).orElse(Lists.newArrayList()))));
  }
}
