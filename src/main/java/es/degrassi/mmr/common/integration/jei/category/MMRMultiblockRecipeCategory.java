package es.degrassi.mmr.common.integration.jei.category;

import com.lowdragmc.lowdraglib2.integration.xei.jei.ModularUIRecipeCategory;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.xei.MultiblockRecipe;
import es.degrassi.mmr.common.item.ControllerItem;
import es.degrassi.mmr.common.machine.DynamicMachine;
import lombok.Getter;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.joml.Vector2f;

public class MMRMultiblockRecipeCategory extends ModularUIRecipeCategory<MultiblockRecipe> {
  private final DynamicMachine multiblock;
  @Getter
  private final IRecipeType<MultiblockRecipe> recipeType;
  @Getter
  private final IDrawable icon;
  public MMRMultiblockRecipeCategory(DynamicMachine multiblock) {
    super(MultiblockRecipe::createModularUI);
    this.multiblock = multiblock;
    var machine = multiblock.getRegistryName().withPrefix("multiblock_preview/");
    this.recipeType = IRecipeType.create(machine, MultiblockRecipe.class);
    this.icon = new JeiMultiblockCategoryIcon(multiblock);
  }

  public int getWidth() {
    return MultiblockRecipe.WIDTH;
  }

  public int getHeight() {
    return MultiblockRecipe.HEIGHT;
  }

  @Override
  public Component getTitle() {
    return Component.literal("Preview: ").append(multiblock.getName());
  }

  public static class JeiMultiblockCategoryIcon implements IDrawable {
    private final IDrawable controller;
    public JeiMultiblockCategoryIcon(DynamicMachine multiblock) {
      this.controller = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableItemStack(ControllerItem.makeMachineItem(multiblock.getRegistryName()));
    }

    @Override
    public int getWidth() {
      return 16;
    }

    @Override
    public int getHeight() {
      return 16;
    }

    @Override
    public void draw(GuiGraphicsExtractor GuiGraphicsExtractor, int xOffset, int yOffset) {
      controller.draw(GuiGraphicsExtractor, xOffset, yOffset);
      GuiGraphicsExtractor.pose().pushMatrix();
      var v = new Vector2f(xOffset / 2f, yOffset / 2f).add(0, 4.5f).div(0.5f);
      GuiGraphicsExtractor.pose().translate(v);
      GuiGraphicsExtractor.pose().scale(0.5f);
      GuiGraphicsExtractor.fakeItem(Items.STRUCTURE_BLOCK.getDefaultInstance(), 0, 0);
      GuiGraphicsExtractor.pose().popMatrix();
    }
  }
}
