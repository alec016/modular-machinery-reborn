package es.degrassi.mmreborn.api.integration.jei;

import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import mezz.jei.api.ingredients.ITypedIngredient;

public interface JeiFilterDragDropFactory<T> {
  void drop(FilterSlotComponent<T, ?> fs, ITypedIngredient<T> ingredient);
}
