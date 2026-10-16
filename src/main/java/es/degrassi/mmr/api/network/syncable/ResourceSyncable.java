package es.degrassi.mmr.api.network.syncable;

import es.degrassi.experiencelib.api.network.AbstractSyncable;
import es.degrassi.mmr.api.network.data.ResourceData;
import net.neoforged.neoforge.transfer.resource.Resource;

public abstract class ResourceSyncable<R extends Resource, D extends ResourceData<R>> extends AbstractSyncable<D, R> {
  protected abstract boolean matches(R other);
  protected abstract R copy(R value);

  @Override
  public boolean needSync() {
    var value = get();
    boolean needSync = true;
    if (this.lastKnownValue != null) {
      needSync = !matches(value);
    }
    this.lastKnownValue = copy(value);
    return needSync;
  }
}
