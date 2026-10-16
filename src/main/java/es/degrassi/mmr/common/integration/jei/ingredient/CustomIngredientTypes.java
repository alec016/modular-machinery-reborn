package es.degrassi.mmr.common.integration.jei.ingredient;

import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Duration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Energy;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Fuel;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import mezz.jei.api.ingredients.IIngredientType;

public class CustomIngredientTypes {
  private CustomIngredientTypes() {}
  public static final IIngredientType<Energy> ENERGY = () -> Energy.class;
  public static final IIngredientType<Fuel> FUEL = () -> Fuel.class;
  public static final IIngredientType<Duration> DURATION = () -> Duration.class;
  public static final IIngredientType<Void> VOID = () -> Void.class;
}
