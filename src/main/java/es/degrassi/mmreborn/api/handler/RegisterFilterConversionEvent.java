package es.degrassi.mmreborn.api.handler;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Supplier;

@Getter
public class RegisterFilterConversionEvent extends Event implements IModBusEvent {
  private final Map<Item, FilterConverterFactory<?>> converters = Maps.newHashMap();

  public <T> void register(Item from, T to) {
    if (converters.containsKey(from)) {
      throw new IllegalArgumentException("Cannot register a converter that already exists for the item " + from.getDefaultInstance().getDisplayName().getString());
    }
    if (to instanceof FilterConverterFactory<?> t) converters.put(from, t);
    else if (to instanceof Supplier<?> t) converters.put(from, t::get);
    else converters.put(from, () -> to);
  }
}
