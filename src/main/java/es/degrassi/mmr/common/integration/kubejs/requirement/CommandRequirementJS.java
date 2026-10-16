package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementCommand;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;

public interface CommandRequirementJS extends RecipeJSBuilder {

  default RecipeJSBuilder runCommandOnStart(String command) {
    return this.runCommandOnStart(command, 2, false);
  }

  default RecipeJSBuilder runCommandOnStart(String command, int permissionLevel) {
    return this.runCommandOnStart(command, permissionLevel, false);
  }

  default RecipeJSBuilder runCommandOnStart(String command, boolean log) {
    return this.runCommandOnStart(command, 2, log);
  }

  default RecipeJSBuilder runCommandOnStart(String command, int permissionLevel, boolean log) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementCommand(RequirementCommand.Phase.START, command, permissionLevel, log)));
  }

  default RecipeJSBuilder runCommandEachTick(String command) {
    return this.runCommandEachTick(command, 2, false);
  }

  default RecipeJSBuilder runCommandEachTick(String command, int permissionLevel) {
    return this.runCommandEachTick(command, permissionLevel, false);
  }

  default RecipeJSBuilder runCommandEachTick(String command, boolean log) {
    return this.runCommandEachTick(command, 2, log);
  }

  default RecipeJSBuilder runCommandEachTick(String command, int permissionLevel, boolean log) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementCommand(RequirementCommand.Phase.EACH_TICK, command, permissionLevel, log)));
  }

  default RecipeJSBuilder runCommandOnEnd(String command) {
    return this.runCommandOnEnd(command, 2, false);
  }

  default RecipeJSBuilder runCommandOnEnd(String command, int permissionLevel) {
    return this.runCommandOnEnd(command, permissionLevel, false);
  }

  default RecipeJSBuilder runCommandOnEnd(String command, boolean log) {
    return this.runCommandOnEnd(command, 2, log);
  }

  default RecipeJSBuilder runCommandOnEnd(String command, int permissionLevel, boolean log) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementCommand(RequirementCommand.Phase.END, command, permissionLevel, log)));
  }
}
