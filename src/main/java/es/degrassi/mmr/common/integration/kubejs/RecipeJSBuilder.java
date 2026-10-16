package es.degrassi.mmr.common.integration.kubejs;

import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;

public interface RecipeJSBuilder {
    MachineRecipeBuilderJS addRequirement(RecipeRequirement<?, ?, ?> requirement);

    MachineRecipeBuilderJS error(String error, Object... args);

    @HideFromJS
    boolean isJei();
}
