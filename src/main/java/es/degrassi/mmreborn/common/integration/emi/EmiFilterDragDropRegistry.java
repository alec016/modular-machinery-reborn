package es.degrassi.mmreborn.common.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.api.integration.emi.EmiFilterDragDropFactory;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiFilterDragDropEvent;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.neoforged.fml.ModLoader;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmiFilterDragDropRegistry {
  private static Map<Class<? extends EmiStack>, EmiFilterDragDropFactory<? extends EmiStack>> map;

  public static void init() {
    RegisterEmiFilterDragDropEvent event = new RegisterEmiFilterDragDropEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    map = event.getMap();
  }

  public static <T extends EmiStack> boolean canDrop(Class<T> clazz) {
    return map.containsKey(clazz);
  }

  @SuppressWarnings("unchecked")
  public static <T extends EmiStack> void drop(FilterSlotComponent<?, ?> fs, T stack) {
    ((EmiFilterDragDropFactory<T>)map.get(stack.getClass())).drop(fs, stack);
  }
}
