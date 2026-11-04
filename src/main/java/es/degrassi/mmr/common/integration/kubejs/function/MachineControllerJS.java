package es.degrassi.mmr.common.integration.kubejs.function;

import dev.latvian.mods.kubejs.level.CachedLevelBlock;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.machine.component.FluidComponent;
import es.degrassi.mmr.common.machine.component.ItemComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.util.Chunkloader;
import es.degrassi.mmr.common.util.TaskDelayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SimpleFluidIngredient;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MachineControllerJS {
  private final MachineControllerEntity internal;

  @HideFromJS
  public MachineControllerJS(MachineControllerEntity internal) {
    this.internal = internal;
  }

  public static MachineControllerJS of(Object o) {
    if (o instanceof Wrapper w) {
      o = w.unwrap();
    }
    if (o instanceof BlockEntity be && be instanceof MachineControllerEntity mce) {
      return new MachineControllerJS(mce);
    }
    if (o instanceof CachedLevelBlock bc) {
      return of(bc.getEntity());
    }
    return null;
  }

  public String getId() {
    return this.internal.getId().toString();
  }

  public void setId(String id) {
    Identifier loc = Identifier.tryParse(id);
    if (loc != null) {
      TaskDelayer.enqueue(0, () -> this.internal.setMachine(loc));
    } else {
      throw new IllegalArgumentException("Invalid machine ID: " + id);
    }
  }

  public boolean getPaused() {
    return this.internal.isPaused();
  }

  public void setPaused(boolean paused) {
    this.internal.setPaused(paused);
  }

  /** ENERGY STUFF **/

  public long getEnergyCapacity(IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof EnergyComponent)
        .map(c -> (EnergyComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .mapToLong(c -> c.getContainerProvider().getCapacityAsLong())
        .sum();
  }

  public long getEnergyStored(IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof EnergyComponent)
        .map(c -> (EnergyComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .mapToLong(c -> c.getContainerProvider().getAmountAsLong())
        .sum();
  }

  /**
   * This method is only available for energy output hatches
   * @param energy to be added
   * @return energy added
   */
  public long addEnergy(long energy) {
    AtomicLong energyAtomic = new AtomicLong(energy);
    AtomicLong inserted = new AtomicLong(0);
    try (Transaction tx = Transaction.open(null)) {
      this.internal.getComponentManager()
          .getFoundComponentsList()
          .stream()
          .filter(c -> c instanceof EnergyComponent)
          .map(c -> (EnergyComponent) c)
          .filter(c -> !c.getIOType().isInput())
          .forEach(c -> {
            try (Transaction subTx = Transaction.open(tx)) {
              if (energyAtomic.get() <= 0) return;
              EnergyHandler handler = c.getContainerProvider();
              int received = handler.insert((int) energyAtomic.get(), subTx);
              energyAtomic.addAndGet(-received);
              inserted.addAndGet(received);
              subTx.commit();
            }
          });
      tx.commit();
      return inserted.get();
    }
  }

  /**
   * This method is only available for energy input hatches
   * @param energy to be removed
   * @return energy extracted
   */
  public long removeEnergy(long energy) {
    try (var tx = Transaction.open(null)) {
      AtomicLong energyAtomic = new AtomicLong(energy);
      AtomicLong extracted = new AtomicLong(0);
      this.internal.getComponentManager()
          .getFoundComponentsList()
          .stream()
          .filter(c -> c instanceof EnergyComponent)
          .map(c -> (EnergyComponent) c)
          .filter(c -> c.getIOType().isInput())
          .forEach(c -> {
            try (var subTx = Transaction.open(tx)) {
              if (energyAtomic.get() <= 0) return;
              EnergyHandler handler = c.getContainerProvider();
              int drained = handler.extract((int) energyAtomic.get(), subTx);
              energyAtomic.addAndGet(-drained);
              extracted.addAndGet(drained);
              subTx.commit();
            }
          });
      tx.commit();
      return extracted.get();
    }
  }

  /** FLUID STUFF **/

  public List<FluidStack> getFluidsStored(IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof FluidComponent)
        .map(c -> (FluidComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .map(c -> Arrays.asList(c.getContainerProvider().getFluidStacks()))
        .flatMap(List::stream)
        .toList();
  }

  public int getFluidCapacity(IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof FluidComponent)
        .map(c -> (FluidComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .mapToInt(c -> c.getContainerProvider().getCapacity())
        .sum();
  }

  public int getFluidCapacity(FluidStack fluid, IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof FluidComponent)
        .map(c -> (FluidComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .filter(c -> c.getContainerProvider().contains(SimpleFluidIngredient.of(fluid)))
        .mapToInt(c -> c.getContainerProvider().getCapacity())
        .sum();
  }


  /**
   * This method is only available for fluid input hatches
   * @param stack to be added
   * @return fluid added
   */
  public long addFluid(FluidStack stack) {
    AtomicLong filled = new AtomicLong(0);
    this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof FluidComponent)
        .map(c -> (FluidComponent) c)
        .filter(c -> !c.getIOType().isInput())
        .reduce(FluidComponent::merge)
        .ifPresent(c -> filled.addAndGet(c.getContainerProvider().addToOutputs(stack, stack.getAmount())));

    return filled.get();
  }

  /**
   * This method is only available for fluid output hatches
   * @param stack to be removed
   * @return fluid extracted
   */
  public FluidStack removeFluid(FluidStack stack) {
    AtomicInteger extracted = new AtomicInteger();

    this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof FluidComponent)
        .map(c -> (FluidComponent) c)
        .filter(c -> c.getIOType().isInput())
        .reduce(FluidComponent::merge)
        .ifPresent(c -> {
          var handler = c.getContainerProvider();
          AtomicInteger toRemove = new AtomicInteger(stack.getAmount());
          if (toRemove.get() <= 0) return;
          int maxExtract = Math.min(handler.getFluidAmount(stack), toRemove.get());
          toRemove.addAndGet(-maxExtract);
          extracted.addAndGet(maxExtract);
          handler.removeFromInputs(stack, maxExtract);
        });

    return stack.copyWithAmount(extracted.get());
  }

  /** ITEM STUFF **/

  public List<ItemStack> getItemsStored(IOType mode) {
    return this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof ItemComponent)
        .map(c -> (ItemComponent) c)
        .filter(c -> c.getIOType().equals(mode))
        .map(ItemComponent::getContainerProvider)
        .map(handler -> {
          List<ItemStack> stacks = new ArrayList<>();
          handler.forEach(s -> stacks.add(s));
          return stacks;
        })
        .flatMap(List::stream)
        .toList();
  }

  /**
   * This method is only available for item output buses
   * @param stack to be added
   * @return items that couldn't be added
   */
  public ItemStack addItem(ItemStack stack) {
    AtomicInteger toAdd = new AtomicInteger(stack.getCount());
    this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof ItemComponent)
        .map(c -> (ItemComponent) c)
        .filter(c -> !c.getIOType().isInput())
        .forEach(c -> {
          if (toAdd.get() <= 0) return;
          if (c.getSpaceForItem(stack) <= 0) return;
          int inserted = c.getContainerProvider().addToOutputs(stack, toAdd.get());
          toAdd.addAndGet(-inserted);
        });

    return stack.copyWithCount(toAdd.get());
  }

  /**
   * This method is only available for item input buses
   * @param stack to be removed
   */
  public void removeItem(ItemStack stack) {
    AtomicReference<ItemStack> item = new AtomicReference<>(stack);
    AtomicReference<ItemStack> extracted = new AtomicReference<>(ItemStack.EMPTY);
    this.internal.getComponentManager()
        .getFoundComponentsList()
        .stream()
        .filter(c -> c instanceof ItemComponent)
        .map(c -> (ItemComponent) c)
        .filter(c -> c.getIOType().isInput())
        .forEach(c -> {
          if (item.get().isEmpty()) return;
          if (c.getItemAmount(item.get()) <= 0) return;
          AtomicInteger toRemove = new AtomicInteger(item.get().getCount());
          AtomicInteger removed = new AtomicInteger(0);
          c.removeFromInputs(Ingredient.of(stack.getItem()), stack.count());
        });
  }

  /** FUEL STUFF **/
  public long getFuelAmount() throws ExecutionException {
    return internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_FUEL.get(), IOType.INPUT)
        .stream()
        .mapToLong(comp -> comp.getContainerProvider().getFuel())
        .sum();
  }

  public long getFuelCapacity() throws ExecutionException {
    return internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_FUEL.get(), IOType.INPUT)
        .stream()
        .mapToLong(comp -> comp.getContainerProvider().getMaxFuel())
        .sum();
  }

  public void addFuel(long amount) throws ExecutionException {
    AtomicLong amt = new AtomicLong(amount);
    internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_FUEL.get(), IOType.INPUT)
        .map(MachineComponent::getContainerProvider)
        .ifPresent(comp -> {
          if (amt.get() <= 0) return;
          long toInsert = Math.min(comp.getMaxFuel() - comp.getFuel(), amt.get());
          amt.addAndGet(-toInsert);
          comp.addFuel(toInsert);
        });
  }

  public void removeFuel(long amount) throws ExecutionException {
    AtomicLong amt = new AtomicLong(amount);
    internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_FUEL.get(), IOType.INPUT)
        .map(MachineComponent::getContainerProvider)
        .ifPresent(comp -> {
          if (amt.get() <= 0) return;
          long toRemove = Math.min(comp.getFuel(), amt.get());
          amt.addAndGet(-toRemove);
          comp.removeFuel(toRemove);
        });
  }

  /** CHUNKLOAD STUFF **/

  public void enableChunkload(int radius) throws ExecutionException {
    this.internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_CHUNKLOAD.get(), IOType.OUTPUT)
        .map(MachineComponent::getContainerProvider)
        .ifPresent(component -> component.setActive((ServerLevel) this.internal.getLevel(), radius));
  }

  public void disableChunkload() throws ExecutionException {
    this.internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_CHUNKLOAD.get(), IOType.OUTPUT)
        .map(MachineComponent::getContainerProvider)
        .ifPresent(component -> component.setInactive((ServerLevel) this.internal.getLevel()));
  }

  public boolean isChunkloadEnabled() throws ExecutionException {
    return this.internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_CHUNKLOAD.get(), IOType.OUTPUT)
        .map(MachineComponent::getContainerProvider)
        .map(Chunkloader::isActive)
        .orElse(false);
  }

  public int getChunkloadRadius() throws ExecutionException {
    return this.internal.getComponentManager()
        .getComponent(ComponentRegistration.COMPONENT_CHUNKLOAD.get(), IOType.OUTPUT)
        .map(MachineComponent::getContainerProvider)
        .map(Chunkloader::getRadius)
        .orElse(0);
  }
}
