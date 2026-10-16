package es.degrassi.mmr.common.manager.handler;

import es.degrassi.mmr.common.util.InventoryUpdateListener;

public interface UpdateValueListener<VALUE> extends InventoryUpdateListener {
  void onChange(int slot, VALUE oldValue);
  @Override
  default void onChange() {

  }
}
