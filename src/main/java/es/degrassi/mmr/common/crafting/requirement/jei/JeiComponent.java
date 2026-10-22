package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import lombok.Getter;
import lombok.Setter;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public abstract class  JeiComponent<X, R extends RecipeRequirement<?, ?, ?>> implements IIngredientRenderer<X>,
    IJeiRequirement<R> {
  protected static final Identifier LOCATION_JEI_ICONS = Identifier.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/jeirecipeicons.png");

  protected R requirement;
  protected final int uOffset, vOffset;

  protected JeiComponent(R requirement, int uOffset, int vOffset) {
    this.requirement = requirement;
    this.uOffset = uOffset;
    this.vOffset = vOffset;
  }

  public abstract int getWidth();

  public abstract int getHeight();

  public Identifier texture() {
    return LOCATION_JEI_ICONS;
  }

  @Override
  public void render(GuiGraphicsExtractor guiGraphics, X ingredient) {
    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture(),
        -1, -1,
        uOffset, vOffset,
        getWidth(), getHeight(),
        TextureSizeHelper.getWidth(texture()), TextureSizeHelper.getHeight(texture())
    );
  }

  @Override
  @SuppressWarnings("removal")
  public @NotNull List<Component> getTooltip(X ingredient, TooltipFlag tooltipFlag) {
    return List.of();
  }

  @Override
  public @NotNull List<Component> getTooltip(X ingredient, Item.TooltipContext context, @Nullable Player player, TooltipFlag tooltipFlag) {
    return new LinkedList<>();
  }

  public abstract List<X> ingredients();

  public RecipeIngredientRole role() {
    if (requirement == null) return RecipeIngredientRole.RENDER_ONLY;
    return requirement.requirement().getMode().isInput() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
  }
}
