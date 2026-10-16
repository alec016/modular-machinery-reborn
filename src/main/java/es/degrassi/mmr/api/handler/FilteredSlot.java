package es.degrassi.mmr.api.handler;

import es.degrassi.experiencelib.api.network.ISyncableStuff;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.function.Predicate;

public interface FilteredSlot<VALUE> extends ISyncableStuff, ValueIOSerializable {
  int getSlot();

  void setFilter(Predicate<VALUE> filter);

  VALUE getValue();

  boolean isEmpty();

  boolean isInput();

  boolean isOutput();
}
