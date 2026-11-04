package es.degrassi.mmr.common.integration.jei;

import es.degrassi.mmr.api.integration.jei.JeiFilterDragDropFactory;
import es.degrassi.mmr.api.integration.jei.RegisterJeiFilterDragDropEvent;
import es.degrassi.mmr.client.container.FilterSlotComponent;
import es.degrassi.mmr.common.network.client.CSetFilterSlotFluidPacket;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JeiFilterDragDropRegistry {
  private static Map<IIngredientType<?>, JeiFilterDragDropFactory<?>> map;

  public static void init() {
    RegisterJeiFilterDragDropEvent event = new RegisterJeiFilterDragDropEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    map = event.getMap();
  }

  public static <T> boolean canDrop(ITypedIngredient<T> ingredient) {
    var factory = factoryFor(ingredient.getType());
    return factory != null;
  }

  public static <T> void drop(FilterSlotComponent<T, ?> fs, ITypedIngredient<T> ingredient) {
    var factory = factoryFor(ingredient.getType());
    assert factory != null;
    factory.drop(fs, ingredient);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private static <T> JeiFilterDragDropFactory<T> factoryFor(IIngredientType<T> type) {
    if (map != null) {
      var factory = map.get(type);
      if (factory != null) return (JeiFilterDragDropFactory<T>) factory;
    }
    return (JeiFilterDragDropFactory<T>) BUILT_IN.get(type);
  }

  /** The conversions every hatch understands, so the drop works even if the event was posted too early. */
  private static final Map<IIngredientType<?>, JeiFilterDragDropFactory<?>> BUILT_IN = new HashMap<>();

  static {
    JeiFilterDragDropFactory<ItemStack> itemDrop = (fs, stack) -> fs
        .setFromClient(stack.getIngredient());
    BUILT_IN.put(VanillaTypes.ITEM_STACK, itemDrop);

    JeiFilterDragDropFactory<FluidStack> fluidDrop = (fs, stack) -> fs
        .setGenericFromClient(pos -> new CSetFilterSlotFluidPacket(stack.getIngredient().copyWithAmount(1), pos));
    BUILT_IN.put(NeoForgeTypes.FLUID_STACK, fluidDrop);
  }
}
