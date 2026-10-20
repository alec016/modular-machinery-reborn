package es.degrassi.mmr.common.manager.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.util.TransferPreconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Setter
public abstract class AbstractHandler<
    STACK,
    RESOURCE extends Resource
> extends StacksResourceHandler<STACK, RESOURCE> implements ISyncableStuff {
  /** Accepts everything unless a specific filter is installed (was {@code null} before, which NPE'd). */
  private Predicate<RESOURCE> filter = resource -> true;
  private final long capacity, maxIn, maxOut;

  private boolean recipe = false;

  @Getter
  private UpdateValueListener<STACK> listener = null;

  private final Direction[] accessibleSides;

  protected AbstractHandler(int size, long capacity, long maxIn, long maxOut, STACK emptyStack, Codec<STACK> stackCodec, Direction...accessibleSides) {
    super(size, emptyStack, stackCodec);
    this.capacity = capacity;
    this.maxIn = maxIn;
    this.maxOut = maxOut;
    this.accessibleSides = Optional.of(accessibleSides).orElse(Direction.values());
  }

  public List<Direction> getAccessibleSides() {
    return ImmutableList.copyOf(accessibleSides);
  }

  public STACK getStack(int index) {
    Objects.checkIndex(index, size());
    return stacks.get(index);
  }

  public void setStack(int index, STACK stack) {
    Objects.checkIndex(index, size());
    stacks.set(index, stack);
  }

  /** Per-slot capacity configured at construction time. */
  public long getCapacityPerSlot() {
    return capacity;
  }

  /** Maximum amount a single external {@code insert} operation may move. */
  public long getMaxInsert() {
    return maxIn;
  }

  /** Maximum amount a single external {@code extract} operation may move. */
  public long getMaxExtract() {
    return maxOut;
  }

  @Override
  protected void onContentsChanged(int index, STACK previousContents) {
    super.onContentsChanged(index, previousContents);
    if (listener != null) {
      listener.onChange(index, previousContents);
    }
  }

  @Override
  public boolean isValid(int index, RESOURCE resource) {
    TransferPreconditions.checkNonEmptyNonNegative(resource, index);
    return filter.test(resource);
  }

  @Override
  public int getCapacity(int index, RESOURCE resource) {
    return Ints.saturatedCast(capacity);
  }

  @Override
  public long getCapacityAsLong(int index, RESOURCE resource) {
    Objects.checkIndex(index, size());
    return resource.isEmpty() || isValid(index, resource) ? capacity : 0;
  }

  @Override
  public int insert(int index, RESOURCE resource, int amount, TransactionContext transaction) {
    return super.insert(index, resource, recipe ? amount : Math.min(amount, Ints.saturatedCast(maxIn)), transaction);
  }

  @Override
  public int extract(int index, RESOURCE resource, int amount, TransactionContext transaction) {
    return super.extract(index, resource, recipe ? amount : Math.min(amount, Ints.saturatedCast(maxOut)), transaction);
  }

  public void forEach(Consumer<STACK> consumer) {
    stacks.forEach(consumer);
  }

  public void forEach(BiConsumer<STACK, Integer> consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(stacks.get(i), i);
    }
  }

  protected abstract boolean isFull(STACK stack);

  public boolean isFull() {
    return stacks.stream().allMatch(this::isFull);
  }

  public boolean isEmpty() {
    return stacks.stream().allMatch(s -> getResourceFrom(s).isEmpty());
  }
}
