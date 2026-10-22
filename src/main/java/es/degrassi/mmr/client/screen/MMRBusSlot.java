package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.slot.ItemResourceHandlerSlot;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A bus slot with the legacy manual-access semantics ({@code SlotItemComponent} of the 1.21.1
 * build): a player can always take items back out of an input bus and can place items into an
 * output bus to keep slots reserved — regardless of the handler's external auto-IO throughput
 * ({@code maxOut}/{@code maxIn}). Only the per-slot capacity and the handler's filter still apply
 * ({@link #mayPlace} delegates to {@link ItemHandler#isValid}).
 *
 * <p>Writes are direct replacements of the slot content — the legacy
 * {@code setItemStack}/{@code extractItemBypassLimit} behaviour — instead of the
 * extract-then-reinsert dance of the generic transfer slot, so repeated clicks or stale packets
 * can never accumulate extra items in a slot.
 */
public class MMRBusSlot extends ItemResourceHandlerSlot {
  private final ItemHandler handler;
  private final int index;

  public MMRBusSlot(ItemHandler handler, int index) {
    super(handler, index);
    this.handler = handler;
    this.index = index;
  }

  /** Any non-empty slot is manually takeable, exactly like the legacy container slot. */
  @Override
  public boolean mayPickup(@Nullable Player playerIn) {
    return !this.getItem().isEmpty();
  }

  /**
   * Vanilla slot semantics: how many fit in a slot reserved for {@code stack} (callers subtract
   * the current content themselves). Manual placements only obey the filter and the capacity.
   */
  @Override
  public int getMaxStackSize(ItemStack stack) {
    if (stack.isEmpty() || !handler.isValid(index, ItemResource.of(stack))) {
      return 0;
    }
    return handler.getCapacity(index, ItemResource.of(stack));
  }

  /** Direct replacement of the slot content, clamped to the per-slot capacity. */
  @Override
  public void set(ItemStack stack) {
    if (stack.isEmpty()) {
      handler.set(index, ItemResource.EMPTY, 0);
    } else {
      int count = Math.min(stack.getCount(), handler.getCapacity(index, ItemResource.of(stack)));
      handler.set(index, ItemResource.of(stack), count);
    }
    this.setChanged();
  }

  /** Direct take from the slot, bypassing the {@code maxOut} limit ({@code extractItemBypassLimit}). */
  @Nonnull
  @Override
  public ItemStack remove(int amount) {
    if (amount <= 0) {
      return ItemStack.EMPTY;
    }
    ItemStack current = handler.getStack(index);
    if (current.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int taken = Math.min(amount, current.getCount());
    ItemStack result = current.copyWithCount(taken);
    int left = current.getCount() - taken;
    if (left > 0) {
      handler.set(index, ItemResource.of(current), left);
    } else {
      handler.set(index, ItemResource.EMPTY, 0);
    }
    this.setChanged();
    return result;
  }
}
