package es.degrassi.mmr.api.network.syncable;

import es.degrassi.experiencelib.api.network.AbstractSyncable;
import es.degrassi.mmr.api.controller.CorePopup;
import es.degrassi.mmr.api.network.data.CorePopupData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CorePopupSyncable extends AbstractSyncable<CorePopupData, CorePopup> {

  @Override
  public CorePopupData getData(short id) {
    return new CorePopupData(id, get());
  }

  public static CorePopupSyncable create(Supplier<CorePopup> supplier, Consumer<CorePopup> consumer) {
    return new CorePopupSyncable() {
      @Override
      public CorePopup get() {
        return supplier.get();
      }

      @Override
      public void set(CorePopup value) {
        consumer.accept(value);
      }
    };
  }
}
