package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.common.manager.handler.slot.ItemSlot;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Getter
public class SlotItemComponent extends Slot {
  private static final Container EMPTY = new SimpleContainer(0);
  private final ItemSlot component;

  public SlotItemComponent(ItemSlot container, int slot, int x, int y) {
    super(EMPTY, slot, x, y);
    this.component = container;
  }

  @Override
  public ItemStack getItem() {
    return this.component.getItemStack();
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return this.component.isItemValid(0, stack);
  }

  @Override
  public void set(ItemStack stack) {
    this.component.setItemStack(stack);
  }

  @Override
  public int getMaxStackSize() {
    return this.component.getCapacity();
  }

  @Override
  public ItemStack remove(int amount) {
    return this.component.extractItemBypassLimit(amount, false);
  }

  @Override
  public void setChanged() {
    this.component.setChanged();
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return component.getCapacity();
  }

  @Override
  public ItemStack safeInsert(ItemStack stack, int increment) {
    if(!stack.isEmpty() && this.mayPlace(stack)) {
      ItemStack itemstack = this.getItem();
      int i = Math.min(Math.min(increment, stack.getCount()), this.getMaxStackSize(stack) - itemstack.getCount());
      if(itemstack.isEmpty()) {
        this.setByPlayer(stack.split(i));
      } else if(ItemStack.isSameItemSameComponents(itemstack, stack)) {
        stack.shrink(i);
        //itemstack.grow(i); DO NOT MODIFY THE STORED STACK DIRECTLY
        //this.setByPlayer(itemstack); Instead set a modified copy of the stack so upgrades are refreshed.
        this.setByPlayer(itemstack.copyWithCount(itemstack.getCount() + i));
      }
    }
    return stack;
  }

  @Override
  public String toString() {
    return "SlotItemComponent{" +
        "component=" + component +
        ", index=" + index +
        ", slot=" + getSlotIndex() +
        ", x=" + x +
        ", y=" + y +
        '}';
  }
}
