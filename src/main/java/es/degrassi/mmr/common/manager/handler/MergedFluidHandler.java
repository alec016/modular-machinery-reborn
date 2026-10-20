package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.ISyncable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Live view over several {@link FluidHandler}s (fluid counterpart of {@link MergedItemHandler}).
 *
 * <p>The legacy {@code mergeBuild} shared the underlying tanks between the merged handler and the
 * source block entities; every operation is routed here so mutations still hit the real inventories.</p>
 */
public class MergedFluidHandler extends FluidHandler {
  private final FluidHandler[] sources;
  private final int[] offsets;

  public MergedFluidHandler(FluidHandler... sources) {
    super(totalSize(sources), maxCapacity(sources), maxOperation(sources, true), maxOperation(sources, false),
        commonSides(sources));
    this.sources = sources;
    this.offsets = new int[sources.length];
    int offset = 0;
    for (int i = 0; i < sources.length; i++) {
      offsets[i] = offset;
      offset += sources[i].size();
    }
  }

  private int locate(int index) {
    Objects.checkIndex(index, size());
    for (int i = sources.length - 1; i >= 0; i--) {
      if (index >= offsets[i]) return i;
    }
    throw new IndexOutOfBoundsException("merged tank " + index);
  }

  private int local(int source, int index) {
    return index - offsets[source];
  }

  @Override
  public FluidStack getStack(int index) {
    int s = locate(index);
    return sources[s].getStack(local(s, index));
  }

  @Override
  public void setStack(int index, FluidStack stack) {
    int s = locate(index);
    sources[s].setStack(local(s, index), stack);
  }

  @Override
  public boolean isValid(int index, FluidResource resource) {
    int s = locate(index);
    return sources[s].isValid(local(s, index), resource);
  }

  @Override
  public int getCapacity(int index, FluidResource resource) {
    int s = locate(index);
    return sources[s].getCapacity(local(s, index), resource);
  }

  @Override
  public long getCapacityAsLong(int index, FluidResource resource) {
    int s = locate(index);
    return sources[s].getCapacityAsLong(local(s, index), resource);
  }

  @Override
  public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
    int s = locate(index);
    return sources[s].insert(local(s, index), resource, amount, transaction);
  }

  @Override
  public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
    int s = locate(index);
    return sources[s].extract(local(s, index), resource, amount, transaction);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    // Each source block entity already registers its own tank syncables.
  }

  @Override
  public void forEach(Consumer<FluidStack> consumer) {
    for (int i = 0; i < size(); i++) consumer.accept(getStack(i));
  }

  @Override
  public void forEach(BiConsumer<FluidStack, Integer> consumer) {
    for (int i = 0; i < size(); i++) consumer.accept(getStack(i), i);
  }

  @Override
  public boolean isFull() {
    for (int i = 0; i < size(); i++) {
      FluidStack stack = getStack(i);
      if (stack.isEmpty() || stack.amount() < getCapacity(i, FluidResource.of(stack))) return false;
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    for (int i = 0; i < size(); i++) {
      if (!getStack(i).isEmpty()) return false;
    }
    return true;
  }

  private static int totalSize(FluidHandler... sources) {
    int size = 0;
    for (FluidHandler handler : sources) size += handler.size();
    return size;
  }

  private static long maxCapacity(FluidHandler... sources) {
    long capacity = 0;
    for (FluidHandler handler : sources) capacity = Math.max(capacity, handler.getCapacityPerSlot());
    return capacity;
  }

  private static long maxOperation(FluidHandler[] sources, boolean insert) {
    long limit = 0;
    for (FluidHandler handler : sources) {
      limit = Math.max(limit, insert ? handler.getMaxInsert() : handler.getMaxExtract());
    }
    return limit;
  }

  private static Direction[] commonSides(FluidHandler... sources) {
    List<Direction> sides = new ArrayList<>(List.of(Direction.values()));
    for (FluidHandler handler : sources) {
      sides.removeIf(side -> {
        for (Direction direction : handler.getAccessibleSides()) {
          if (direction == side) return true;
        }
        return false;
      });
    }
    return sides.toArray(new Direction[0]);
  }
}
