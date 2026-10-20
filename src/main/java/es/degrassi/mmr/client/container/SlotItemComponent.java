package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
// ==== original implementation, commented out ====
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

@Getter
public class SlotItemComponent extends Slot {
  private static final Container EMPTY = new SimpleContainer(0);
  private final ItemHandler component;
  public SlotItemComponent(ItemHandler container, int slot, int x, int y) {
     super(EMPTY, slot, x, y);
     this.component = container;
  }
  @Override
  public ItemStack getItem() {
    return this.component.getStack(getSlotIndex());
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return this.component.isValid(getSlotIndex(), ItemResource.of(stack));
  }

  @Override
  public void set(ItemStack stack) {
    this.component.setStack(getSlotIndex(), stack);
  }

  @Override
  public int getMaxStackSize() {
    return this.component.getCapacity(getSlotIndex(), ItemResource.of(getItem()));
  }

  @Override
  public ItemStack remove(int amount) {
    return getItem().copyWithCount(this.component.extractFromInputs(getItem(), amount));
  }

  @Override
  public void setChanged() {
    if (this.component.getListener() != null) {
      this.component.getListener().onChange(getSlotIndex(), getItem());
    }
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return component.getCapacity(getSlotIndex(), ItemResource.of(stack));
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
