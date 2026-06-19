package es.degrassi.mmreborn.api.handler;

import es.degrassi.mmreborn.api.MMREvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class RegisterFilterConversionEvent extends MMREvent<ItemStack, FilterConverterFactory<?>> {

  public <T> void register(ItemStack from, T to) {
    if (to instanceof FilterConverterFactory<?> t) super.register(from , t);
    else if (to instanceof Supplier<?> t) super.register(from, t::get);
    else super.register(from, () -> to);
  }
}
