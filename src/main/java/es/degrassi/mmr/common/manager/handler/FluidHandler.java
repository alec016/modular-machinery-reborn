package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.mmr.api.network.syncable.FluidStackSyncable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.function.Consumer;

public class FluidHandler extends AbstractHandler<FluidStack, FluidResource> {
  public FluidHandler(int size, long capacity, long maxIn, long maxOut, Direction...accessibleSides) {
    super(size, capacity, maxIn, maxOut, FluidStack.EMPTY, FluidStack.OPTIONAL_CODEC, accessibleSides);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    for (int i = 0; i < size(); i++) {
      final var index = i;
      container.accept(FluidStackSyncable.create(() -> stacks.get(index), v -> stacks.set(index, v)));
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

  /* ------------------------------------------------------------------
   * Machine-side helpers (legacy per-tank views removed together with the handler.slot package).
   * Same slot-bypassing behaviour as the old insertFluidBypassLimit/extractFluidBypassLimit.
   * ------------------------------------------------------------------ */

  /** Total capacity of every tank. */
  public int getCapacity() {
    return (int) (getCapacityPerSlot() * size());
  }

  /** Total amount of fluid stored. */
  public int getFluidAmount() {
    int total = 0;
    for (int i = 0; i < size(); i++) total += getStack(i).amount();
    return total;
  }

  /** Total amount of fluid matching the given stack. */
  public int getFluidAmount(FluidStack stack) {
    int total = 0;
    for (int i = 0; i < size(); i++) {
      FluidStack value = getStack(i);
      if (FluidStack.isSameFluidSameComponents(value, stack)) total += value.amount();
    }
    return total;
  }

  /** Total amount of fluid matching the given ingredient. */
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

  /** True when {@code stack} could be poured into tank {@code index} (filter + fluid matching). */
  public boolean canPlaceOutput(int index, FluidStack stack) {
    FluidResource resource = FluidResource.of(stack);
    if (!isValid(index, resource)) return false;
    FluidStack value = getStack(index);
    if (value.isEmpty()) return true;
    if (!FluidStack.isSameFluidSameComponents(value, stack)) return false;
    return value.amount() < getCapacity(index, resource);
  }

  public int getSpaceForFluid(FluidStack stack) {
    int space = 0;
    for (int i = 0; i < size(); i++) {
      if (!canPlaceOutput(i, stack)) continue;
      FluidStack value = getStack(i);
      space += value.isEmpty() ? (int) getCapacityPerSlot() : (int) getCapacityPerSlot() - value.amount();
    }
    return Math.max(space, 0);
  }

  /** Pours into any tank bypassing the external maxIn limit. Returns the amount actually inserted. */
  public int addToOutputs(FluidStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!canPlaceOutput(i, stack)) continue;
      FluidStack value = getStack(i);
      int maxInsert = Math.min((int) getCapacityPerSlot() - value.amount(), remaining);
      if (maxInsert <= 0) continue;
      if (value.isEmpty()) {
        setStack(i, stack.copyWithAmount(maxInsert));
      } else {
        value.setAmount(value.amount() + maxInsert);
        setStack(i, value);
      }
      remaining -= maxInsert;
    }
    return amount - remaining;
  }

  /** Drains matching fluid bypassing the external maxOut limit. Returns the amount actually extracted. */
  public int removeFromInputs(FluidStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      FluidStack value = getStack(i);
      if (value.isEmpty() || !FluidStack.isSameFluidSameComponents(value, stack)) continue;
      int maxExtract = Math.min(value.amount(), remaining);
      if (maxExtract <= 0) continue;
      setStack(i, value.copyWithAmount(value.amount() - maxExtract));
      remaining -= maxExtract;
    }
    return amount - remaining;
  }

  /** Drains fluid matching the ingredient bypassing the external maxOut limit. */
  public void removeFromInputs(FluidIngredient ingredient, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      FluidStack value = getStack(i);
      if (value.isEmpty() || !ingredient.test(value)) continue;
      int maxExtract = Math.min(value.amount(), remaining);
      if (maxExtract <= 0) continue;
      setStack(i, value.copyWithAmount(value.amount() - maxExtract));
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

  /**
   * Builds a live view over several handlers (legacy {@code mergeBuild} shared the underlying tanks
   * so that mutations still hit the source block entities).
   */
  public static FluidHandler mergeBuild(FluidHandler... inventories) {
    return new MergedFluidHandler(inventories);
  }
}
