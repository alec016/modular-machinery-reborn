package es.degrassi.mmreborn.api.network.syncable;

import es.degrassi.mmreborn.api.network.AbstractSyncable;
import es.degrassi.mmreborn.api.network.data.ComponentData;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ComponentSyncable extends AbstractSyncable<ComponentData, Component> {
  @Override
  public ComponentData getData(short id) {
    return new ComponentData(id, get());
  }

  @Override
  public boolean needSync() {
    var value = get();
    boolean needSync;
    if(this.lastKnownValue != null)
      needSync = !value.equals(this.lastKnownValue);
    else needSync = true;
    this.lastKnownValue = value.copy();
    return needSync;
  }

  public static ComponentSyncable create(
      Supplier<Component> supplier,
      Consumer<Component> consumer
  ) {
    return new ComponentSyncable() {
      @Override
      public Component get() {
        return supplier.get();
      }

      @Override
      public void set(Component value) {
        consumer.accept(value);
      }
    };
  }
}
