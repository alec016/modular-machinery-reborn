package es.degrassi.mmr.api.handler;

import es.degrassi.mmr.api.MMREvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class RegisterFilterConversionEvent extends MMREvent<Supplier<ItemStack>, FilterConverterFactory<?>> {
  public <T> void register(Supplier<ItemStack> from, T to) {
    if (to instanceof FilterConverterFactory<?> t) super.register(from , t);
    else if (to instanceof Supplier<?> t) super.register(from, t::get);
    else super.register(from, () -> to);
  }
}
