package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import lombok.Getter;
import lombok.Setter;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public abstract class JeiComponent<X, R extends RecipeRequirement<?, ?, ?>> implements IIngredientRenderer<X>,
    IJeiRequirement<R> {
  protected static final ResourceLocation LOCATION_JEI_ICONS = ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/jeirecipeicons.png");

  protected R requirement;
  protected final int uOffset, vOffset;

  protected JeiComponent(R requirement, int uOffset, int vOffset) {
    this.requirement = requirement;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
  }

  public abstract int getWidth();

  public abstract int getHeight();

  public ResourceLocation texture() {
    return LOCATION_JEI_ICONS;
  }

  @Override
  public void render(GuiGraphics guiGraphics,X ingredient) {
    guiGraphics.blit(texture(), -1, -1, 0, uOffset, vOffset, getWidth(), getHeight(), TextureSizeHelper.getWidth(texture()), TextureSizeHelper.getHeight(texture()));
  }

  @Override
  public List<Component> getTooltip(X ingredient, TooltipFlag tooltipFlag) {
    return new LinkedList<>();
  }

  public abstract List<X> ingredients();

  public RecipeIngredientRole role() {
    if (requirement == null) return RecipeIngredientRole.RENDER_ONLY;
    return requirement.requirement().getMode().isInput() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
  }
}
