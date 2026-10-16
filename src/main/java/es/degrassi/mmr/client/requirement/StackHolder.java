package es.degrassi.mmr.client.requirement;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface StackHolder {
  Either<ItemStack, FluidStack> getStack();

  default ItemStack getItemStack() {
    return getStack().left().orElse(ItemStack.EMPTY);
  }

  default FluidStack getFluidStack() {
    return getStack().right().orElse(FluidStack.EMPTY);
  }
}
