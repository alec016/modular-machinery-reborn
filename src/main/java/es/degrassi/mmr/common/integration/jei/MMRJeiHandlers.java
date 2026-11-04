package es.degrassi.mmr.common.integration.jei;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import es.degrassi.mmr.client.container.FilterSlotComponent;
import lombok.experimental.UtilityClass;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@UtilityClass
public final class MMRJeiHandlers {
  @SuppressWarnings("rawtypes")
  public static final IGhostIngredientHandler<AbstractContainerScreen> GHOST_HANDLER = new IGhostIngredientHandler<>() {
    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(AbstractContainerScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
      var stack = ingredient;
      if (JeiFilterDragDropRegistry.canDrop(stack)) {
        return screen.getMenu().slots.stream()
            .filter(slot -> slot instanceof FilterSlotComponent<?,?>)
            .map(slot -> {
              FilterSlotComponent<I, ?> filterSlot = (FilterSlotComponent<I, ?>) slot;
              return new Target<I>() {
                public Rect2i getArea() {
                  return new Rect2i(
                      screen.getLeftPos() + filterSlot.x,
                      screen.getTopPos() + filterSlot.y,
                      16,
                      16
                  );
                }

                @Override
                public void accept(I ingredient) {
                  JeiFilterDragDropRegistry.drop(filterSlot, stack);
                }
              };
            }).collect(Collectors.toList());
      }
      return Collections.emptyList();
    }

    @Override
    public void onComplete() {}
  };
}
