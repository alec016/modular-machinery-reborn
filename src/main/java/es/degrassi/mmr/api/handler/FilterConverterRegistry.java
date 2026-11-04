package es.degrassi.mmr.api.handler;

import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class FilterConverterRegistry {
  private FilterConverterRegistry() {}

  @Getter
  private static Map<Supplier<ItemStack>, FilterConverterFactory<?>> converters;

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
        .anyMatch(stack -> ItemStack.isSameItemSameComponents(stack.get(), itemStack));
  }

  public static <T> boolean isConvertible(T value) {
    return converters.values()
        .parallelStream()
        .anyMatch(f -> sameValue(f.get(), value));
  }

  public static <T> ItemStack convertBack(T value) {
    return converters.entrySet()
        .parallelStream()
        .filter(entry -> sameValue(entry.getValue().get(), value))
        .findFirst()
        .map(Map.Entry::getKey)
        .map(Supplier::get)
        .orElse(ItemStack.EMPTY);
  }

  /**
   * Compares two converted filter values. Resources (fluids, experience, ...) are value objects
   * with a proper equals implementation, while a factory may hand out a fresh instance on every
   * call, so identity is not enough; anything without value equality falls back to identity.
   */
  private static boolean sameValue(Object a, Object b) {
    if (a == null || b == null) {
      return a == b;
    }
    if (a == b) {
      return true;
    }
    return a.getClass() == b.getClass() && a.equals(b);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public static <T> T convert(ItemStack from) {
    return (T) converters.entrySet()
        .parallelStream()
        .filter(entry -> ItemStack.isSameItemSameComponents(from, entry.getKey().get()))
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
