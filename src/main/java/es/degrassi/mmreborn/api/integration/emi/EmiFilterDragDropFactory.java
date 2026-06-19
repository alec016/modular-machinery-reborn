package es.degrassi.mmreborn.api.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;

public interface EmiFilterDragDropFactory<T extends EmiStack> {
  void drop(FilterSlotComponent<?, ?> fs, T from);
}
