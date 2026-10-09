package es.degrassi.mmreborn.common.manager.handler.slot;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.syncable.ItemStackSyncable;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemSlot extends AbstractSlot<ItemStack, Integer> implements IItemHandlerModifiable {
  @Getter
  private final ItemHandler manager;

  public ItemSlot(int slot, ItemHandler manager, int capacity, int maxInput, int maxOutput, Predicate<ItemStack> filter) {
    super(slot, ItemStack.EMPTY, capacity, maxInput, maxOutput, filter, maxIO -> maxIO > 0);
    this.manager = manager;
  }

  public ItemSlot(ItemHandler manager, Predicate<ItemStack> filter, CompoundTag nbt, HolderLookup.Provider registries) {
    super(
        nbt.getInt("slot"),
        getFromNBT(nbt, registries),
        nbt.getInt("capacity"),
        nbt.getInt("maxInput"),
        nbt.getInt("maxOutput"),
        filter,
        maxIO -> maxIO > 0
    );
    this.manager = manager;
  }

  private static ItemStack getFromNBT(CompoundTag nbt, HolderLookup.Provider registries) {
    if (nbt.contains("item"))
      return ItemStack.parseOptional(registries, nbt.getCompound("item"));
    return ItemStack.EMPTY;
  }

  public void deserialize(HolderLookup.Provider registries, CompoundTag nbt) {
    if (nbt.contains("item")) {
      var ops = registries.createSerializationContext(NbtOps.INSTANCE);
      var item = nbt.getCompound("item");
      var id = item.get("id");
      var components = item.get("components");
      var count = item.getInt("count");
      AtomicReference<DataComponentPatch> comps = new AtomicReference<>(DataComponentPatch.EMPTY);
      DataComponentPatch.CODEC.decode(ops, components).result()
          .map(Pair::getFirst)
          .ifPresent(comps::set);
      ItemStack.ITEM_NON_AIR_CODEC.decode(ops, id).result()
          .map(Pair::getFirst)
          .ifPresent(itemId -> {
            value = new ItemStack(itemId, count, comps.get());
          });
    }
  }

  public CompoundTag serializeNBT(HolderLookup.Provider registries) {
    var nbt = new CompoundTag();
    var ops = registries.createSerializationContext(NbtOps.INSTANCE);
    if(!this.value.isEmpty()) {
      CompoundTag item = new CompoundTag();
      ItemStack.ITEM_NON_AIR_CODEC.encodeStart(ops,
          value.getItemHolder()).result().ifPresent(id -> {
          DataComponentPatch.CODEC.encodeStart(ops, this.value.getComponentsPatch()).result().ifPresent(components -> {
            item.put("id", id);
            item.put("components", components);
            item.putInt("count", this.value.getCount());
          });
      });
      nbt.put("item", item);
    }
    nbt.putInt("slot", this.slot);
    nbt.putInt("capacity", capacity);
    nbt.putInt("maxInput", maxInput);
    nbt.putInt("maxOutput", maxOutput);
    return nbt;
  }

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    this.value = stack;
    this.setChanged();
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return value;
  }

  public ItemStack insertItemBypassLimit(ItemStack stack, boolean simulate) {
    this.bypassLimit = true;
    ItemStack remainder = this.insertItem(0, stack, simulate);
    this.bypassLimit = false;
    return remainder;
  }

  public ItemStack extractItemBypassLimit(int amount, boolean simulate) {
    this.bypassLimit = true;
    ItemStack extracted = this.extractItem(0, amount, simulate);
    this.bypassLimit = false;
    return extracted;
  }

  public boolean canOutput() {
    return true;
  }

  public ItemStack getItemStack() {
    return this.value;
  }

  public void setItemStack(ItemStack stack) {
    this.value = stack;
    setChanged();
  }

  public void setItemStackNoUpdate(ItemStack stack) {
    this.value = stack;
  }

  public boolean isEmpty() {
    return this.value == null || this.value.isEmpty();
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if(stack.isEmpty() || !isItemValid(0, stack) || (!this.value.isEmpty() && !ItemStack.isSameItemSameComponents(this.value, stack)))
      return stack;

    int amountToInsert = stack.getCount();

    //Check the per-tick limit
    if(!this.bypassLimit)
      amountToInsert = Math.min(amountToInsert, this.maxInput);

    //Check the inserted stack max size, in case a mod like AE2 try to insert a stack of non-stackable items
    amountToInsert = stack.isStackable() ? amountToInsert : stack.getMaxStackSize();
    //Check the current stack limit (if not empty stack)
    if(!this.value.isEmpty())
      amountToInsert = Math.min(amountToInsert, this.value.getMaxStackSize() - this.value.getCount());

    //Check the slot capacity
    amountToInsert = Math.min(amountToInsert, this.capacity - this.value.getCount());

    //If nothing can be inserted return input
    if(amountToInsert <= 0)
      return stack;

    //If this slot is empty copy the input and insert the max amount
    if(this.value.isEmpty()) {
      if(!simulate) {
        this.value = stack.copyWithCount(amountToInsert);
        setChanged();
      }
    } else {//If this slot is not empty simply grow the contained stack
      if(!simulate) {
        this.value.grow(amountToInsert);
        setChanged();
      }
    }

    //If everything from input was inserted return empty, else copy input and return remainder
    if(amountToInsert == stack.getCount())
      return ItemStack.EMPTY;
    else
      return stack.copyWithCount(stack.getCount() - amountToInsert);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if(amount <= 0 || this.value.isEmpty() || !this.canOutput())
      return ItemStack.EMPTY;

    //Check output limit
    if(!this.bypassLimit)
      amount = Math.min(amount, this.maxOutput);

    //Check current stack size
    amount = Math.min(amount, this.value.getCount());

    ItemStack extracted = this.value.copyWithCount(amount);

    if(!simulate) {
      this.value.shrink(amount);
      setChanged();
    }
    return extracted;
  }

  @Override
  public int getSlotLimit(int slot) {
    return capacity;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return filter.test(stack);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(ItemStackSyncable.create(() -> this.value, stack -> this.value = stack));
  }
  
  public void setChanged() {
    getManager().setChanged(slot, value);
  }
}
