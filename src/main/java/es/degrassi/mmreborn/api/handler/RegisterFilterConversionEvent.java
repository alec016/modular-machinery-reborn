package es.degrassi.mmreborn.api.handler;

import es.degrassi.mmreborn.api.MMREvent;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class RegisterFilterConversionEvent extends MMREvent<Item, FilterConverterFactory<?>> {

  public <T> void register(Item from, T to) {
    if (to instanceof FilterConverterFactory<?> t) super.register(from , t);
    else if (to instanceof Supplier<?> t) super.register(from, t::get);
    else super.register(from, () -> to);
  }
}
