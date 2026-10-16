package es.degrassi.mmr.api.network.syncable;

import es.degrassi.mmr.api.network.data.ItemResourceData;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ItemResourceSyncable extends ResourceSyncable<ItemResource, ItemResourceData> {
  @Override
  public ItemResourceData getData(short id) {
    return new ItemResourceData(id, get());
  }

  @Override
  protected ItemResource copy(ItemResource value) {
    return ItemResource.of(value.getItem(), value.getComponentsPatch());
  }

  @Override
  protected boolean matches(ItemResource other) {
    return this.lastKnownValue.matches(other.toStack(1));
  }

  public static ItemResourceSyncable create(Supplier<ItemResource> supplier, Consumer<ItemResource> consumer) {
    return new ItemResourceSyncable() {
      @Override
      public ItemResource get() {
        return supplier.get();
      }

      @Override
      public void set(ItemResource value) {
        consumer.accept(value);
      }
    };
  }
}
