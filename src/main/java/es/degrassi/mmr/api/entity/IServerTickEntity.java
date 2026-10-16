package es.degrassi.mmr.api.entity;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;

public interface IServerTickEntity extends ITickEntity {
  @OverridingMethodsMustInvokeSuper
  default void doRestrictedTick() {
    if (this instanceof IAutoInputEntity<?> auto)
      auto.tickAutoInput();
    if (this instanceof IAutoOutputEntity<?> auto)
      auto.tickAutoOutput();
  }
}
