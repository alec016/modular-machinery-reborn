package es.degrassi.mmr.api.integration.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiComponent;

@FunctionalInterface
public interface JeiComponentFactory<R extends RecipeRequirement<?, ?, ?>, X> {
  JeiComponent<X, R> create(R requirement);
}
