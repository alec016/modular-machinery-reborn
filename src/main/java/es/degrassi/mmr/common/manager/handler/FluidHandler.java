package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.SyncableList;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class FluidHandler extends AbstractHandler<FluidStack, FluidResource> {
  public FluidHandler(int size, long capacity, long maxIn, long maxOut, Direction...accessibleSides) {
    super(size, capacity, maxIn, maxOut, FluidStack.EMPTY, FluidStack.OPTIONAL_CODEC, accessibleSides);
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    for (int i = 0; i < size(); i++) {
      final var index = i;
      container.syncFluidStack(() -> stacks.get(index), v -> stacks.set(index, v));
    }
  }

  @Override
  public FluidResource getResourceFrom(FluidStack stack) {
    return FluidResource.of(stack);
  }

  @Override
  public int getAmountFrom(FluidStack stack) {
    return stack.amount();
  }

  @Override
  public FluidStack getStackFrom(FluidResource resource, int amount) {
    return resource.toStack(amount);
  }

  @Override
  public FluidStack copyOf(FluidStack stack) {
    return stack.copy();
  }

  @Override
  protected boolean isFull(FluidStack fluidStack) {
    return fluidStack.amount() >= getCapacity(0, FluidResource.of(fluidStack));
  }

  public int getCapacity() {
    return (int) (getCapacityPerSlot() * size());
  }

  public int getFluidAmount() {
    int total = 0;
    for (int i = 0; i < size(); i++) total += getStack(i).amount();
    return total;
  }

  public int getFluidAmount(FluidStack stack) {
    int total = 0;
    for (int i = 0; i < size(); i++) {
      FluidStack value = getStack(i);
      if (FluidStack.isSameFluidSameComponents(value, stack)) total += value.amount();
    }
    return total;
  }

  public int getFluidAmount(FluidIngredient ingredient) {
    int total = 0;
    for (int i = 0; i < size(); i++) {
      FluidStack value = getStack(i);
      if (!value.isEmpty() && ingredient.test(value)) total += value.amount();
    }
    return total;
  }

  public int getIngredientAmount(FluidIngredient ingredient) {
    return getFluidAmount(ingredient);
  }

  public boolean contains(FluidIngredient ingredient) {
    for (int i = 0; i < size(); i++) {
      if (ingredient.test(getStack(i))) return true;
    }
    return false;
  }

  public boolean canPlaceOutput(int index, FluidStack stack) {
    FluidResource resource = FluidResource.of(stack);
    if (!isValid(index, resource)) return false;
    FluidStack value = getStack(index);
    if (value.isEmpty()) return true;
    if (!FluidStack.isSameFluidSameComponents(value, stack)) return false;
    return value.amount() < getCapacity(index, resource);
  }

  public long getSpaceForFluid(FluidStack stack) {
    long space = 0;
    for (int i = 0; i < size(); i++) {
      if (!canPlaceOutput(i, stack)) continue;
      FluidStack value = getStack(i);
      space += value.isEmpty() ? getCapacityPerSlot() : getCapacityPerSlot() - value.amount();
    }
    return Math.max(space, 0);
  }

  public long getTotalFreeSpace() {
    long space = 0;
    for (int i = 0; i < size(); i++) {
      FluidStack value = getStack(i);
      space += Math.max(0, getCapacityPerSlot() - value.amount());
    }
    return space;
  }

  public long addToOutputs(FluidStack stack, long amount) {
    long remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!canPlaceOutput(i, stack)) continue;
      FluidStack value = getStack(i);
      long slotRoom = Math.min(getCapacityPerSlot() - value.amount(), (long) Integer.MAX_VALUE - value.amount());
      long maxInsert = Math.min(slotRoom, remaining);
      if (maxInsert <= 0) continue;
      if (value.isEmpty()) {
        set(i, FluidResource.of(stack), (int) maxInsert);
      } else {
        set(i, FluidResource.of(value), value.amount() + (int) maxInsert);
      }
      remaining -= maxInsert;
    }
    return amount - remaining;
  }

  public int removeFromInputs(FluidStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      FluidStack value = getStack(i);
      if (value.isEmpty() || !FluidStack.isSameFluidSameComponents(value, stack)) continue;
      int maxExtract = Math.min(value.amount(), remaining);
      if (maxExtract <= 0) continue;
      set(i, FluidResource.of(value), value.amount() - maxExtract);
      remaining -= maxExtract;
    }
    return amount - remaining;
  }

  public void removeFromInputs(FluidIngredient ingredient, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      FluidStack value = getStack(i);
      if (value.isEmpty() || !ingredient.test(value)) continue;
      int maxExtract = Math.min(value.amount(), remaining);
      if (maxExtract <= 0) continue;
      set(i, FluidResource.of(value), value.amount() - maxExtract);
      remaining -= maxExtract;
    }
  }

  public FluidIngredient getFluids() {
    FluidStack[] stacks = getFluidStacks();
    if (stacks.length < 1 || stacks[0].isEmpty()) return FluidIngredient.of(new FluidStack[0]);
    return FluidIngredient.of(stacks);
  }

  public FluidStack[] getFluidStacks() {
    FluidStack[] out = new FluidStack[size()];
    for (int i = 0; i < size(); i++) out[i] = getStack(i);
    return out;
  }

  public void setFluid(int slot, FluidStack fluid) {
    setStack(slot, fluid);
  }

  public static FluidHandler mergeBuild(FluidHandler... inventories) {
    return new MergedFluidHandler(inventories);
  }
}
