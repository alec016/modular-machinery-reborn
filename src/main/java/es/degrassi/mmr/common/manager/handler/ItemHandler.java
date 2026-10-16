package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.mmr.api.network.syncable.ItemStackSyncable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Consumer;

public class ItemHandler extends AbstractHandler<ItemStack, ItemResource> {
  public ItemHandler(int size, long capacity, long maxIn, long maxOut, Direction... accessibleSides) {
    super(size, capacity, maxIn, maxOut, ItemStack.EMPTY, ItemStack.OPTIONAL_CODEC, accessibleSides);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    for (int i = 0; i < size(); i++) {
      final int index = i;
      container.accept(ItemStackSyncable.create(() -> stacks.get(index), v -> stacks.set(index, v)));
    }
  }

  @Override
  public ItemResource getResourceFrom(ItemStack stack) {
    return ItemResource.of(stack);
  }

  @Override
  public int getAmountFrom(ItemStack stack) {
    return stack.count();
  }

  @Override
  public ItemStack getStackFrom(ItemResource resource, int amount) {
    return resource.toStack(amount);
  }

  @Override
  public ItemStack copyOf(ItemStack stack) {
    return stack.copy();
  }

  @Override
  protected boolean isFull(ItemStack itemStack) {
    return itemStack.count() >= getCapacity(0, ItemResource.of(itemStack));
  }
}
