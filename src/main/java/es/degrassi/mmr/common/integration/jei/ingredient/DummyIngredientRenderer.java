package es.degrassi.mmr.common.integration.jei.ingredient;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class DummyIngredientRenderer<T> implements IIngredientRenderer<T> {

  @Override
  public void render(GuiGraphicsExtractor graphics, @Nullable T t) {
    // Do not render anything as it is dummy
  }

  //Safe to remove
  @Override
  public List<Component> getTooltip(T t, TooltipFlag iTooltipFlag) {
    return new ArrayList<>();
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, T ingredient, TooltipFlag tooltipFlag) {
    // Do not get any tooltip as it is dummy
  }
}
