package es.degrassi.mmreborn.common.integration.jei;

import com.google.common.collect.Maps;
import es.degrassi.mmreborn.api.integration.jei.JeiFilterDragDropFactory;
import es.degrassi.mmreborn.api.integration.jei.RegisterJeiFilterDragDropEvent;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.neoforged.fml.ModLoader;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JeiFilterDragDropRegistry {
  private static Map<IIngredientType<?>, JeiFilterDragDropFactory<?>> map;

  public static void init() {
    RegisterJeiFilterDragDropEvent event = new RegisterJeiFilterDragDropEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    map = event.getMap();
  }

  public static <T> boolean canDrop(ITypedIngredient<T> ingredient) {
    return map.containsKey(ingredient.getType());
  }

  @SuppressWarnings("unchecked")
  public static <T> void drop(FilterSlotComponent<T, ?> fs, ITypedIngredient<T> ingredient) {
    ((JeiFilterDragDropFactory<T>)map.get(ingredient.getType())).drop(fs, ingredient);
  }
}
