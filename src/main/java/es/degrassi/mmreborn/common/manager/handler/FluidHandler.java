package es.degrassi.mmreborn.common.manager.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmreborn.common.manager.handler.slot.HybridTank;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class FluidHandler extends AbstractHandler<HybridTank, FluidStack> implements IFluidHandler {

  public FluidHandler(int capacity) {
    super(capacity);
  }

  public FluidHandler(int[] inSlots, int[] outSlots, int capacity) {
    super(inSlots, outSlots, capacity);
  }

  public FluidHandler(int[] inSlots, int[] outSlots, Predicate<FluidStack> filter, int capacity, Direction... accessibleFrom) {
    super(inSlots, outSlots, filter, capacity, accessibleFrom);
  }

  public FluidHandler(int[] inSlots, int[] outSlots, int capacity, Direction... accessibleFrom) {
    super(inSlots, outSlots, capacity, accessibleFrom);
  }

  public void setFilter(Predicate<FluidStack> filter) {
    this.getInventory().forEach(tank -> tank.setFilter(filter));
  }

  @Override
  public void removeFromInputs(FluidStack fluidStack, int amount) {
    AtomicInteger toRemove = new AtomicInteger(amount);
    this.getInputs().stream()
        .filter(component -> FluidStack.isSameFluidSameComponents(component.getValue(), fluidStack))
        .forEach(component -> {
          int maxExtract = Math.min(component.getValue().getAmount(), toRemove.get());
          toRemove.addAndGet(-maxExtract);
          component.extractFluidBypassLimit(maxExtract, false);
          component.setChanged();
        });
  }

  public void removeFromInputs(FluidIngredient ingredient, int amount) {
    AtomicInteger toRemove = new AtomicInteger(amount);
    this.getInputs().stream()
        .filter(component -> ingredient.test(component.getValue()))
        .forEach(component -> {
          int maxExtract = Math.min(component.getValue().getAmount(), toRemove.get());
          toRemove.addAndGet(-maxExtract);
          component.extractFluidBypassLimit(maxExtract, false);
          component.setChanged();
        });
  }

  @Override
  public void addToOutputs(FluidStack stack, int amount) {
    AtomicInteger toAdd = new AtomicInteger(amount);
    this.getOutputs().stream()
        .filter(component -> canPlaceOutput(component, stack))
        .forEach(component -> {
          int maxInsert = toAdd.get() - component.insertFluidBypassLimit(stack, true).getAmount();
          toAdd.addAndGet(-maxInsert);
          component.insertFluidBypassLimit(stack.copyWithAmount(maxInsert), false);
          component.setChanged();
        });
  }

  public boolean canPlaceOutput(HybridTank component, FluidStack stack) {
    //Check component filter and variant
    if (!component.isFluidValid(0, stack))
      return false;

    //If the slot is empty, any item can go inside
    if (component.getValue().isEmpty())
      return true;

    //If the item present in the slot in not the same item, they won't stack
    if (!FluidStack.isSameFluidSameComponents(component.getValue(), stack))
      return false;

    //Check if the stack present in the slot can accept more items
    return component.getValue().getAmount() < component.getCapacity();
  }

  public int getSpaceForFluid(FluidStack stack) {
    return this.getOutputs().stream().filter(component -> canPlaceOutput(component, stack))
        .mapToInt(component -> {
          if (component.getValue().isEmpty())
            return Math.min(component.getCapacity(), slotLimit);
          else
            return Math.min(component.getCapacity() - component.getValue().getAmount(), slotLimit - component.getValue().getAmount());
        })
        .sum();
  }

  public int getFluidAmount() {
    return this.getInputs().stream()
        .mapToInt(component -> component.getValue().getAmount())
        .sum();
  }

  public int getFluidAmount(FluidIngredient ingredient) {
    return this.getInputs().stream()
        .filter(component -> ingredient.test(component.getValue()))
        .mapToInt(component -> component.getValue().getAmount())
        .sum();
  }

  public int getFluidAmount(FluidStack stack) {
    return this.getInputs().stream().filter(component -> FluidStack.isSameFluidSameComponents(component.getValue(), stack))
        .mapToInt(component -> component.getValue().getAmount())
        .sum();
  }

  public int getSlotLimit(int slot) {
    return getSlotLimit();
  }

  @Override
  protected List<HybridTank> generateInventory(Predicate<FluidStack> filter) {
    List<HybridTank> inventory = new ArrayList<>();
    for (Integer slot : inSlots) {
      HybridTank itemSlot = new HybridTank(slot, this, getSlotLimit(slot), getSlotLimit(slot), 0, filter);
      this.getInputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    for (Integer slot : outSlots) {
      HybridTank itemSlot = new HybridTank(slot, this, getSlotLimit(slot), 0, getSlotLimit(slot), filter);
      this.getOutputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    return inventory;
  }

  @Override
  protected HybridTank createSlot(HolderLookup.Provider pRegistries, CompoundTag componentNBT) {
    return new HybridTank(this, getDefaultFilter(), componentNBT, pRegistries);
  }

  public static FluidHandler mergeBuild(FluidHandler... inventories) {
    FluidHandler merged = new FluidHandler(0);
    int slotOffset = 0;
    Map<Integer, FluidHandler> slotLimitIndex = Maps.newHashMap();
    List<Integer> inSlots = Lists.newArrayList();
    List<Integer> outSlots = Lists.newArrayList();
    List<Direction> sides = Lists.newArrayList(Direction.values());
    List<HybridTank> inputs = Lists.newArrayList();
    List<HybridTank> outputs = Lists.newArrayList();
    int stackLimit = 0;
    for (FluidHandler inventory : inventories) {
      stackLimit += inventory.getSlotLimit();
      for (HybridTank key : inventory.getInventory()) {
        merged.getInventory().add(key.getSlot() + slotOffset, key);
      }
      int finalSlotOffset = slotOffset;
      Arrays.stream(inventory.inSlots).map(in -> in + finalSlotOffset).forEach(inSlots::add);
      Arrays.stream(inventory.outSlots).map(out -> out + finalSlotOffset).forEach(outSlots::add);
      sides = sides.stream().map(side -> {
        if (inventory.accessibleSides.contains(side))
          return side;
        return null;
      }).filter(Objects::nonNull).toList();
      slotOffset += inventory.getInventory().size();
      slotLimitIndex.put(slotOffset, inventory);
      inputs.addAll(inventory.getInputs());
      outputs.addAll(inventory.getOutputs());
    }
    merged.accessibleSides = sides;
    var builder = IntStream.builder();
    inSlots.forEach(builder::add);
    merged.inSlots = builder.build().toArray();
    builder = IntStream.builder();
    outSlots.forEach(builder::add);
    merged.outSlots = builder.build().toArray();
    builder = IntStream.builder();
    merged.getInputs().addAll(inputs);
    merged.getOutputs().addAll(outputs);
    merged.slotLimit = stackLimit;
    merged.setListener((slot, stack) ->
        slotLimitIndex.forEach((slotLimit, inventory) -> {
          if (slotLimit < slot)
            inventory.getListener().onChange(slot - slotLimit, stack);
        })
    );
    return merged;
  }

  public int getIngredientAmount(FluidIngredient ingredient) {
    return getInventory().stream().mapToInt(h -> h.getIngredientAmount(ingredient)).sum();
  }

  public boolean contains(FluidIngredient ingredient) {
    return getInventory().stream().anyMatch(h -> ingredient.test(h.getValue()));
  }

  /** IFluidHandler Stuff **/

  @Override
  public int getTanks() {
    return this.getInventory().size();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    validateTankIndex(tank);
    return this.getInventory().get(tank).getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    validateTankIndex(tank);
    return this.getInventory().get(tank).getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    validateTankIndex(tank);
    return this.getInventory().get(tank).isFluidValid(0, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    FluidStack toFill = resource.copy();
    for(HybridTank component : this.getInventory()) {
      toFill.shrink(component.fill(toFill, action));
      setChanged(getInventory().indexOf(component), component.getValue());
      if(toFill.isEmpty())
        break;
    }
    return resource.getAmount() - toFill.getAmount();
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    int toDrain = 0;
    for(HybridTank component : this.getInventory()) {
      toDrain += component.drain(resource.copyWithAmount(resource.getAmount() - toDrain), action).getAmount();
      setChanged(getInventory().indexOf(component), component.getValue());
      if(toDrain == resource.getAmount())
        break;
    }
    return resource.copyWithAmount(toDrain);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    for (HybridTank component : this.getInventory()) {
      FluidStack drained = component.drain(maxDrain, action);
      if(!drained.isEmpty()){
        setChanged(getInventory().indexOf(component), component.getValue());
        return drained;
      }
    }
    return FluidStack.EMPTY;
  }

  protected void validateTankIndex(int tank) {
    if (tank < 0 || tank >= this.getTanks())
      throw new RuntimeException("Tank " + tank + " not in valid range - [0," + this.getTanks() + ")");
  }

  public FluidIngredient getFluids() {
    var stacks = getFluidStacks();
    if (stacks.length < 1) return FluidIngredient.empty();
    if (stacks[0].isEmpty()) return FluidIngredient.empty();
    return FluidIngredient.of(stacks);
  }

  public FluidStack[] getFluidStacks() {
    return getInventory().stream().map(HybridTank::getValue).toArray(FluidStack[]::new);
  }

  public void setFluid(int slot, FluidStack fluid) {
    getInventory().get(slot).setValue(fluid);
    setChanged(slot, fluid);
  }

  public boolean isFull() {
    return getInventory().stream().allMatch(HybridTank::isFull);
  }

  public int getCapacity() {
    return getInventory()
        .stream()
        .mapToInt(HybridTank::getCapacity)
        .sum();
  }
}
