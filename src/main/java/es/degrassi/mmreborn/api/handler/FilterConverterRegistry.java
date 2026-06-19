package es.degrassi.mmreborn.api.handler;

import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;

import javax.annotation.Nullable;
import java.util.Map;

public class FilterConverterRegistry {
  private FilterConverterRegistry() {}

  @Getter
  private static Map<ItemStack, FilterConverterFactory<?>> converters;

  public static void init() {
    RegisterFilterConversionEvent event = new RegisterFilterConversionEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    converters = event.getMap();
  }

  public static boolean hasConverter(Item itemStack) {
    return hasConverter(itemStack.getDefaultInstance());
  }

  public static boolean hasConverter(ItemStack itemStack) {
    return converters.keySet()
        .parallelStream()
        .anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
  }

  public static <T> boolean isConvertible(T value) {
    return converters.values()
        .parallelStream()
        .anyMatch(f -> f.get() == value);
  }

  public static <T> ItemStack convertBack(T value) {
    return converters.entrySet()
        .parallelStream()
        .filter(entry -> entry.getValue().get() == value)
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(ItemStack.EMPTY);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public static <T> T convert(ItemStack from) {
    return (T) converters.entrySet()
        .parallelStream()
        .filter(entry -> ItemStack.isSameItemSameComponents(from, entry.getKey()))
        .findFirst()
        .map(Map.Entry::getValue)
        .map(FilterConverterFactory::get)
        .orElse(null);
  }

  @Nullable
  public static <T> T convert(Item from) {
    return convert(from.getDefaultInstance());
  }
}
