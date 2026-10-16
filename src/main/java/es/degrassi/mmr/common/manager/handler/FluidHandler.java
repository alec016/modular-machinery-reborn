package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.mmr.api.network.syncable.FluidStackSyncable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
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
}
