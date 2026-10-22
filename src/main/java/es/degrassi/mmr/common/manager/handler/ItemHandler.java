package es.degrassi.mmr.common.manager.handler;

import es.degrassi.experiencelib.api.network.SyncableList;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Objects;

public class ItemHandler extends AbstractHandler<ItemStack, ItemResource> {
  public ItemHandler(int size, long capacity, long maxIn, long maxOut, Direction... accessibleSides) {
    super(size, capacity, maxIn, maxOut, ItemStack.EMPTY, ItemStack.OPTIONAL_CODEC, accessibleSides);
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

  /* ------------------------------------------------------------------
   * Machine-side helpers.
   *
   * The legacy implementation exposed per-slot views ({@code es.degrassi.mmr.common.manager.handler.slot.ItemSlot})
   * with input/output classification and "…BypassLimit" operations. That package was removed on purpose;
   * the same behaviour is provided here on top of the transfer API, iterating slots by index.
   *
   * In the new model a handler only ever holds input slots (input buses, fuel tanks, durability hatches)
   * or output slots (output buses), so "inputs"/"outputs" collapse to "all slots", and the machine-side
   * operations bypass maxIn/maxOut exactly like the old *BypassLimit methods did.
   * ------------------------------------------------------------------ */

  /** True when {@code stack} could be added to slot {@code index} (filter + stacking rules). */
  public boolean canPlaceOutput(int index, ItemStack stack) {
    ItemResource resource = ItemResource.of(stack);
    if (!isValid(index, resource)) return false;
    ItemStack slotStack = getStack(index);
    if (slotStack.isEmpty()) return true;
    if (!ItemStack.isSameItemSameComponents(slotStack, stack)) return false;
    return slotStack.count() < Math.min(stack.getMaxStackSize(), getCapacity(index, resource));
  }

  /**
   * Inserts into any slot bypassing the external maxIn limit. Returns the amount actually inserted.
   * Writes go through {@link #set}, so the per-slot change listeners (update packets to tracking
   * players) fire like with any transactional write — plain {@code stacks.set} would mutate the
   * storage silently and leave every listener and client copy stale until the next full sync.
   */
  public int addToOutputs(ItemStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      if (!canPlaceOutput(i, stack)) continue;
      ItemStack slotStack = getStack(i);
      int capacity = Math.min(getCapacity(i, ItemResource.of(stack)), stack.getMaxStackSize());
      int maxInsert = Math.min(capacity - slotStack.count(), remaining);
      if (maxInsert <= 0) continue;
      set(i, ItemResource.of(stack), slotStack.count() + maxInsert);
      remaining -= maxInsert;
    }
    return amount - remaining;
  }

  /** Extracts matching items bypassing the external maxOut limit. Returns the amount actually extracted. */
  public int extractFromInputs(ItemStack stack, int amount) {
    int remaining = amount;
    for (int i = 0; i < size() && remaining > 0; i++) {
      ItemStack slotStack = getStack(i);
      if (slotStack.isEmpty() || !ItemStack.isSameItemSameComponents(slotStack, stack)) continue;
      int maxExtract = Math.min(slotStack.count(), remaining);
      if (maxExtract <= 0) continue;
      set(i, ItemResource.of(slotStack), slotStack.count() - maxExtract);
      remaining -= maxExtract;
    }
    return amount - remaining;
  }

  public int getSpaceForItem(ItemStack stack) {
    int space = 0;
    for (int i = 0; i < size(); i++) {
      if (!canPlaceOutput(i, stack)) continue;
      ItemStack slotStack = getStack(i);
      int capacity = Math.min(getCapacity(i, ItemResource.of(stack)), stack.getMaxStackSize());
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

  /** Vanilla comparator-style fill level of the whole inventory (0-15). */
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

  /* ---- durability helpers (durability hatch) ---- */

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

  /** Same item ignoring the damage component (legacy semantics of the old ItemHandler.isSameItem). */
  private static boolean isSameItem(ItemStack toTest, ItemStack ingredient) {
    if (toTest.getItem() != ingredient.getItem()) return false;
    for (var type : ingredient.getComponents().keySet()) {
      if (type == DataComponents.DAMAGE) continue;
      if (!toTest.has(type) || !Objects.equals(toTest.get(type), ingredient.get(type))) return false;
    }
    return true;
  }

  /**
   * Builds a live view over several handlers (legacy {@code mergeBuild} shared the underlying slot
   * objects so that mutations still hit the source block entities; a plain copy would desynchronise them).
   */
  public static ItemHandler mergeBuild(ItemHandler... inventories) {
    return new MergedItemHandler(inventories);
  }
}
