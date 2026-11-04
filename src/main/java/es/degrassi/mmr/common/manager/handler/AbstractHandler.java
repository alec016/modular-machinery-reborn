package es.degrassi.mmr.common.manager.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.util.TransferPreconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Arrays;
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
  private Predicate<RESOURCE> filter = resource -> true;
  private long capacity, maxIn, maxOut;

  private boolean recipe = false;

  @Getter
  private UpdateValueListener<STACK> listener = null;

  private int inputSlotCount = -1;

  private Direction[] accessibleSides;

  protected AbstractHandler(int size, long capacity, long maxIn, long maxOut, STACK emptyStack, Codec<STACK> stackCodec, Direction...accessibleSides) {
    super(size, emptyStack, stackCodec);
    this.capacity = capacity;
    this.maxIn = maxIn;
    this.maxOut = maxOut;
    this.accessibleSides = Optional.of(accessibleSides).orElse(Direction.values());
  }

  @Override
  public void serialize(ValueOutput output) {
    super.serialize(output);
    output.putLong("c", capacity);
    output.putLong("mi", maxIn);
    output.putLong("mo", maxOut);
    output.store("as", Direction.CODEC.listOf(), Arrays.asList(accessibleSides));
  }

  @Override
  public void deserialize(ValueInput input) {
    super.deserialize(input);
    this.capacity = input.getLongOr("c", 0);
    this.maxIn = input.getLongOr("mi", capacity);
    this.maxOut = input.getLongOr("mo", capacity);
    this.accessibleSides = input.read("as", Direction.CODEC.listOf()).map(l -> l.toArray(Direction[]::new)).orElse(Direction.values());
  }

  public List<Direction> getAccessibleSides() {
    return ImmutableList.copyOf(accessibleSides);
  }

  public int getInputSlotCount() {
    return inputSlotCount < 0 ? size() : inputSlotCount;
  }

  public boolean isInputSlot(int index) {
    return inputSlotCount < 0 || index < inputSlotCount;
  }

  public boolean isOutputSlot(int index) {
    return inputSlotCount < 0 || index >= inputSlotCount;
  }

  public STACK getStack(int index) {
    Objects.checkIndex(index, size());
    return stacks.get(index);
  }

  public void setStack(int index, STACK stack) {
    Objects.checkIndex(index, size());
    stacks.set(index, stack);
  }

  public long getCapacityPerSlot() {
    return capacity;
  }

  public long getMaxInsert() {
    return maxIn;
  }

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
    if (!isInputSlot(index)) return false;
    return filter.test(resource);
  }

  protected boolean acceptsInternally(int index, RESOURCE resource) {
    TransferPreconditions.checkNonEmptyNonNegative(resource, index);
    return (isInputSlot(index) && filter.test(resource)) || filter.test(resource);
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
