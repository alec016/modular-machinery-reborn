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
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.concurrent.atomic.AtomicInteger;

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

  public int getIngredientAmount(Ingredient ingredient) {
    return this.handler.getInputs().stream().filter(component -> ingredient.test(component.getItemStack()))
        .mapToInt(component -> component.getItemStack().getCount())
        .sum();
  }

  public int getItemAmount(ItemStack stack) {
    return this.handler.getItemAmount(stack);
  }

  public int getSpaceForItem(ItemStack stack) {
    return handler.getSpaceForItem(stack);
  }

  public void removeFromInputs(Ingredient ingredient, int amount) {
    AtomicInteger toRemove = new AtomicInteger(amount);
    try (Transaction root = Transaction.openRoot()) {
      this.handler.getInputs().stream().filter(component -> ingredient.test(component.getItemStack())).forEach(component -> {
        try (Transaction sub = Transaction.open(root)) {
          int maxExtract = Math.min(component.getItemStack().getCount(), toRemove.get());
          var extracted = component.extractItemBypassLimit(maxExtract, sub);
          toRemove.addAndGet(-extracted.getCount());
          sub.commit();
        }
      });
      root.commit();
    }
  }

  public void addToOutputs(ItemStack stack, int amount) {
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
