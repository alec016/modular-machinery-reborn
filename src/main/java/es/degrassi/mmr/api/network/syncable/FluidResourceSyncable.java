package es.degrassi.mmr.api.network.syncable;

import es.degrassi.mmr.api.network.data.FluidResourceData;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FluidResourceSyncable extends ResourceSyncable<FluidResource, FluidResourceData> {
  @Override
  public FluidResourceData getData(short id) {
    return new FluidResourceData(id, get());
  }

  @Override
  protected FluidResource copy(FluidResource value) {
    return FluidResource.of(value.getFluid(), value.getComponentsPatch());
  }

  @Override
  protected boolean matches(FluidResource other) {
    return this.lastKnownValue.matches(other.toStack(1));
  }

  public static FluidResourceSyncable create(Supplier<FluidResource> supplier, Consumer<FluidResource> consumer) {
    return new FluidResourceSyncable() {
      @Override
      public FluidResource get() {
        return supplier.get();
      }

      @Override
      public void set(FluidResource value) {
        consumer.accept(value);
      }
    };
  }
}
