package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.ValueOutput;

public class ItemComponent extends MachineComponent<ItemHandler> {
  private final ItemHandler handler;

  public ItemComponent(ItemHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType<ItemHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public ItemHandler getContainerProvider() {
    return handler;
  }

  public long getIngredientAmount(Ingredient ingredient) {
    long amount = 0;
    for (int i = 0; i < handler.size(); i++) {
      ItemStack stack = handler.getStack(i);
      if (!stack.isEmpty() && ingredient.test(stack)) amount += stack.getCount();
    }
    return amount;
  }

  public long getItemAmount(ItemStack stack) {
    return this.handler.getItemAmount(stack);
  }

  public long getSpaceForItem(ItemStack stack) {
    return handler.getSpaceForItem(stack);
  }

  public void removeFromInputs(Ingredient ingredient, long amount) {
    long toRemove = amount;
    for (int i = 0; i < handler.size() && toRemove > 0; i++) {
      ItemStack stack = handler.getStack(i);
      if (stack.isEmpty() || !ingredient.test(stack)) continue;
      toRemove -= handler.extractFromInputs(stack, Math.min(stack.getCount(), toRemove));
    }
  }

  public void addToOutputs(ItemStack stack, long amount) {
    handler.addToOutputs(stack, amount);
  }

  @Override
  public void asTag(ValueOutput output) {
    super.asTag(output);
    output.putChild("handler", handler);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<ItemHandler>> C merge(C c) {
    ItemComponent comp = (ItemComponent) c;
    return (C) new ItemComponent(
        ItemHandler.mergeBuild(handler, comp.handler),
        getIOType()
    );
  }
}
