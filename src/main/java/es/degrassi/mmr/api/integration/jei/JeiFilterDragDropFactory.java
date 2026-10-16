package es.degrassi.mmr.api.integration.jei;

import es.degrassi.mmr.client.container.FilterSlotComponent;
import mezz.jei.api.ingredients.ITypedIngredient;

public interface JeiFilterDragDropFactory<T> {
  void drop(FilterSlotComponent<T, ?> fs, ITypedIngredient<T> ingredient);
}
