package es.degrassi.mmreborn.api.handler;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModLoader;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class FilterConverterRegistry {
  private FilterConverterRegistry() {}

  private static Map<Item, FilterConverterFactory<?>> converters;

  public static void init() {
    RegisterFilterConversionEvent event = new RegisterFilterConversionEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    converters = event.getConverters();
  }

  public static boolean hasConverter(Item itemStack) {
    return converters.containsKey(itemStack);
  }

  public static boolean hasConverter(ItemStack itemStack) {
    return hasConverter(itemStack.getItem());
  }

  public static <T> boolean isConvertible(T value) {
    return converters.values()
        .parallelStream()
        .anyMatch(f -> f.get() == value);
  }

  public static <T> Item convertBack(T value) {
    return converters.entrySet()
        .stream()
        .filter(entry -> entry.getValue().get() == value)
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(Items.AIR);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public static <T> T convert(Item from) {
    return (T) Optional.ofNullable(converters.get(from)).map(FilterConverterFactory::get).orElse(null);
  }

  @Nullable
  public static <T> T convert(ItemStack from) {
    return convert(from.getItem());
  }
}
