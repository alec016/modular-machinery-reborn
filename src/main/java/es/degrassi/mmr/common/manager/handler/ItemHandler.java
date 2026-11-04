package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.api.codec.BigStackCodecs;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Objects;

@Setter
@Getter
public class ItemHandler extends AbstractHandler<ItemStack, ItemResource> {
  /**
   * Set while the grid of this handler is still compiling on the client: the fill tab is blocked
   * until the pane is complete. The slot level lock (see MMRBusSlot) is what actually keeps the
   * items out, that one lives on the menu and dies with it.
   */
  private boolean compiling;

  public ItemHandler(int size, long capacity, long maxIn, long maxOut, Direction... accessibleSides) {
    // The big stack codec keeps the amount of a slot above the vanilla limits (a bus with a
    // capacity of 10k items per slot would not be savable otherwise).
    super(size, capacity, maxIn, maxOut, ItemStack.EMPTY, BigStackCodecs.OPTIONAL.codec(), accessibleSides);
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    for (int i = 0; i < size(); i++) {
      final int index = i;
      container.syncItemStack(() -> stacks.get(index), v -> stacks.set(index, v));
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

  /**
   * Whether a slot can still take the given stack. The handler's capacity is the authority: the
   * item's own maximum stack size is a vanilla per-item limit and must not cap a handler that is
   * configured for more (a bus with a capacity of 10k per slot, for example).
   */
  public boolean canPlaceOutput(int index, ItemStack stack) {
    ItemResource resource = ItemResource.of(stack);
    if (!acceptsInternally(index, resource)) return false;
    ItemStack slotStack = getStack(index);
    if (slotStack.isEmpty()) return true;
    if (!ItemStack.isSameItemSameComponents(slotStack, stack)) return false;
    return slotStack.count() < getCapacity(index, resource);
  }

  public int addToOutputs(ItemStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!isOutputSlot(i)) continue;
      if (!canPlaceOutput(i, stack)) continue;
      ItemStack slotStack = getStack(i);
      int capacity = getCapacity(i, ItemResource.of(stack));
      int maxInsert = Math.min(capacity - slotStack.count(), remaining);
      if (maxInsert <= 0) continue;
      set(i, ItemResource.of(stack), slotStack.count() + maxInsert);
      remaining -= maxInsert;
    }
    return amount - remaining;
  }

  public int addToInputs(ItemStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!isInputSlot(i)) continue;
      if (!canPlaceOutput(i, stack)) continue;
      ItemStack slotStack = getStack(i);
      int capacity = getCapacity(i, ItemResource.of(stack));
      int maxInsert = Math.min(capacity - slotStack.count(), remaining);
      if (maxInsert <= 0) continue;
      set(i, ItemResource.of(stack), slotStack.count() + maxInsert);
      remaining -= maxInsert;
    }
    return amount - remaining;
  }

  public int extractFromInputs(ItemStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!isInputSlot(i)) continue;
      ItemStack slotStack = getStack(i);
      if (slotStack.isEmpty() || !ItemStack.isSameItemSameComponents(slotStack, stack)) continue;
      int maxExtract = Math.min(slotStack.count(), remaining);
      if (maxExtract <= 0) continue;
      set(i, ItemResource.of(slotStack), slotStack.count() - maxExtract);
      remaining -= maxExtract;
    }
    return amount - remaining;
  }

  public boolean moveToOutputs(int index) {
    ItemStack stack = getStack(index);
    if (stack.isEmpty() || isOutputSlot(index)) return false;
    int moved = addToOutputs(stack, stack.getCount());
    if (moved <= 0) return false;
    if (moved >= stack.getCount()) {
      set(index, ItemResource.EMPTY, 0);
    } else {
      set(index, ItemResource.of(stack), stack.getCount() - moved);
    }
    return true;
  }

  public int getSpaceForItem(ItemStack stack) {
    int space = 0;
    for (int i = 0; i < size(); i++) {
      if (!isInputSlot(i)) continue;
      if (!canPlaceOutput(i, stack)) continue;
      ItemStack slotStack = getStack(i);
      // The handler's capacity is the limit, not the item's own maximum stack size.
      int capacity = getCapacity(i, ItemResource.of(stack));
      space += slotStack.isEmpty() ? capacity : capacity - slotStack.count();
    }
    return Math.max(space, 0);
  }

  public int getItemAmount(ItemStack stack) {
    int amount = 0;
    for (int i = 0; i < size(); i++) {
      ItemStack slotStack = getStack(i);
      if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack)) {
        amount += slotStack.count();
      }
    }
    return amount;
  }

  public int calcRedstoneFromInventory() {
    int filled = 0;
    float total = 0.0F;
    for (int i = 0; i < size(); i++) {
      ItemStack stack = getStack(i);
      if (!stack.isEmpty()) {
        total += (float) stack.count() / (float) Math.min(getCapacity(i, ItemResource.of(stack)), stack.getMaxStackSize());
        ++filled;
      }
    }
    total = total / (float) size();
    return Mth.floor(total * 14.0F) + (filled > 0 ? 1 : 0);
  }

  public int getDurabilityAmount(ItemStack stack) {
    int amount = 0;
    for (int i = 0; i < size(); i++) {
      ItemStack slotStack = getStack(i);
      if (!slotStack.isEmpty() && slotStack.isDamageableItem() && isSameItem(slotStack, stack)) {
        amount += slotStack.getMaxDamage() - slotStack.getDamageValue();
      }
    }
    return amount;
  }

  public int getSpaceForDurability(ItemStack stack) {
    int amount = 0;
    for (int i = 0; i < size(); i++) {
      ItemStack slotStack = getStack(i);
      if (!slotStack.isEmpty() && slotStack.isDamageableItem() && isSameItem(slotStack, stack)) {
        amount += slotStack.getDamageValue();
      }
    }
    return amount;
  }

  public void repairItem(ItemStack stack, int amount) {
    int toRepair = amount;
    for (int i = 0; i < size() && toRepair > 0; i++) {
      ItemStack slotStack = getStack(i);
      if (slotStack.isEmpty() || !slotStack.isDamageableItem() || !isSameItem(slotStack, stack)) continue;
      int maxRepair = Math.min(slotStack.getDamageValue(), toRepair);
      if (maxRepair <= 0) continue;
      slotStack.setDamageValue(slotStack.getDamageValue() - maxRepair);
      set(i, ItemResource.of(slotStack), slotStack.count());
      toRepair -= maxRepair;
    }
  }

  public void removeDurability(ItemStack input, int amount) {
    int toRemove = amount;
    for (int i = 0; i < size() && toRemove > 0; i++) {
      ItemStack slotStack = getStack(i);
      if (slotStack.isEmpty() || !slotStack.isDamageableItem() || !isSameItem(slotStack, input)) continue;
      int maxRemove = Math.min(slotStack.getMaxDamage() - slotStack.getDamageValue(), toRemove);
      if (maxRemove <= 0) continue;
      slotStack.setDamageValue(slotStack.getDamageValue() + maxRemove);
      if (slotStack.getDamageValue() >= slotStack.getMaxDamage()) slotStack.shrink(1);
      if (slotStack.isEmpty()) {
        set(i, ItemResource.EMPTY, 0);
      } else {
        set(i, ItemResource.of(slotStack), slotStack.count());
      }
      toRemove -= maxRemove;
    }
  }

  private static boolean isSameItem(ItemStack toTest, ItemStack ingredient) {
    if (toTest.getItem() != ingredient.getItem()) return false;
    for (var type : ingredient.getComponents().keySet()) {
      if (type == DataComponents.DAMAGE) continue;
      if (!toTest.has(type) || !Objects.equals(toTest.get(type), ingredient.get(type))) return false;
    }
    return true;
  }

  public static ItemHandler mergeBuild(ItemHandler... inventories) {
    return new MergedItemHandler(inventories);
  }
}
