package es.degrassi.mmr.client.container;

import com.lowdragmc.lowdraglib2.gui.slot.ItemResourceHandlerSlot;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;
import java.util.Optional;

public class MMRBusSlot extends ItemResourceHandlerSlot {
  protected final ItemHandler handler;
  protected final int index;

  @Setter
  @Getter
  private boolean locked;

  public MMRBusSlot(ItemHandler handler, int index) {
    super(handler, index);
    this.handler = handler;
    this.index = index;
  }

  public MMRBusSlot(ItemHandler handler, int index, int x, int y) {
    super(handler, index, x, y);
    this.handler = handler;
    this.index = index;
  }

  public void unlock() {
    this.locked = false;
  }

  private boolean inBounds() {
    return this.index < this.handler.size();
  }

  @Override
  public boolean mayPickup(@Nullable Player playerIn) {
    return !locked && !this.getItem().isEmpty();
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return !locked && !stack.isEmpty() && handler.isValid(index, ItemResource.of(stack));
  }

  @Override
  public ItemStack getItem() {
    if (!inBounds()) {
      return ItemStack.EMPTY;
    }
    return super.getItem();
  }

  @Override
  public int getMaxStackSize() {
    if (!inBounds()) return 0;
    return handler.getCapacity(index, ItemResource.EMPTY);
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    if (!inBounds() || stack.isEmpty() || !handler.isValid(index, ItemResource.of(stack))) {
      return 0;
    }
    return handler.getCapacity(index, ItemResource.of(stack));
  }

  @Override
  public void set(ItemStack stack) {
    if (!inBounds()) {
      return;
    }
    if (stack.isEmpty()) {
      handler.set(index, ItemResource.EMPTY, 0);
    } else {
      int count = Math.min(stack.getCount(), handler.getCapacity(index, ItemResource.of(stack)));
      handler.set(index, ItemResource.of(stack), count);
    }
    this.setChanged();
  }

  @Override
  public ItemStack remove(int amount) {
    if (amount <= 0 || !inBounds()) {
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

  @Override
  public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
    if (!mayPickup(player)) {
      return Optional.empty();
    }
    var current = getItem();
    if (current.isEmpty()) {
      return Optional.empty();
    }
    int amount = Math.min(Math.min(count, current.getCount()), current.getMaxStackSize());
    if (amount <= 0) {
      return Optional.empty();
    }
    var taken = remove(amount);
    return taken.isEmpty() ? Optional.empty() : Optional.of(taken);
  }
}
