package es.degrassi.mmreborn.common.integration.emi;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeDecorator;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.integration.almostunified.RecipeIndicator;
import es.degrassi.mmreborn.client.screen.BaseScreen;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.widget.tabs.ITabGroupScreen;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.almostunified.AlmostUnifiedAdapter;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMRMultiblockCategory;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMRMultiblockEmiRecipe;
import es.degrassi.mmreborn.common.integration.xei.MultiblockRecipe;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@EmiEntrypoint
public class MMREmiPlugin implements EmiPlugin {
  public static final Map<DynamicMachine, EmiRecipeCategory> categories = Maps.newHashMap();

  public static void openCategories(DynamicMachine machine) {
    EmiApi.displayUses(EmiStack.of(ControllerItem.makeMachineItem(machine.getRegistryName())));
  }

  @Override
  public void register(EmiRegistry registry) {
    EmiStack controller = EmiStack.of(ItemRegistration.CONTROLLER);

    registry.setDefaultComparison(controller, Comparison.compareData(stack -> stack.get(DataComponentRegistration.MACHINE_DATA.get())));

    registry.addEmiStack(controller);
    registry.addAlias(controller, Component.literal("controller"));
    registry.addAlias(controller, Component.literal("Controller"));
    registry.addAlias(controller, Component.literal("multiblock"));
    registry.addAlias(controller, Component.literal("Multiblock"));

    RecipeManager manager = registry.getRecipeManager();
    List<RecipeHolder<MachineRecipe>> recipes = manager.getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get());

    ModularMachineryReborn.MACHINES.forEach((id, machine) -> {
      EmiStack stack = EmiStack.of(ControllerItem.makeMachineItem(id));
      EmiRecipeCategory category = new EmiRecipeCategory(id, stack) {
        @Override
        public Component getName() {
          return Component.literal(machine.getLocalizedName());
        }
      };
      categories.put(machine, category);
      registry.addCategory(category);
      registry.addWorkstation(category, EmiStack.of(ItemRegistration.BLUEPRINT.get()));
      registry.addWorkstation(category, stack);
      recipes.stream()
          .filter(recipe -> recipe.value().getOwningMachine() != null)
          .filter(recipe -> recipe.value().getOwningMachine().getRegistryName().equals(id))
          .filter(recipe -> !recipe.value().isHidden())
          .forEach(recipe -> registry.addDeferredRecipes(x -> x.accept(new MMREmiRecipe(category, recipe))));
      registry.addRecipeDecorator(category, new IndicatorDecorator());
      //registry.addRecipeHandler(ContainerRegistration.CONTROLLER.get(), new MMREmiRecipeHandler(machine));
      registry.addGenericDragDropHandler(new MMREmiDragDropHandler());
      if (Mods.isLDLibLoaded()) {
        MMRMultiblockCategory multiblockCategory = new MMRMultiblockCategory(machine, stack);
        registry.addCategory(multiblockCategory);
        registry.addWorkstation(multiblockCategory, EmiStack.of(ItemRegistration.BLUEPRINT.get()));
        registry.addWorkstation(multiblockCategory, stack);
        registry.addRecipe(new MMRMultiblockEmiRecipe(new MultiblockRecipe(machine), multiblockCategory));
      }
    });

    registry.addExclusionArea(ControllerScreen.class, (screen, consumer) -> {
      int x = screen.getGuiLeft(), y = screen.getGuiTop();
      int width = screen.xSize, height = screen.ySize;
      List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> sizes = Lists.newArrayList();
      sizes.addAll(screen.getTabs().getTabs().stream()
          .filter(tab -> {
            boolean widths = tab.getX() >= x + width || tab.getX() + tab.getWidth() >= x + width || tab.getX() <= x;
            boolean heights = tab.getY() >= y + height || tab.getY() + tab.getHeight() >= y + height || tab.getY() <= y;
            return widths || heights;
          })
          .map(tab -> Pair.of(Pair.of(tab.getX(), tab.getY()), Pair.of(tab.getWidth(), tab.getHeight())))
          .toList()
      );
      sizes.addAll(screen.popups().stream()
          .filter(popup -> {
            boolean widths = popup.x >= x + width || popup.x + popup.xSize >= x + width || popup.x <= x;
            boolean heights = popup.y >= y + height || popup.y + popup.ySize >= y + height || popup.y <= y;
            return widths || heights;
          })
          .map(popup -> Pair.of(Pair.of(popup.x, popup.y), Pair.of(popup.xSize, popup.ySize)))
          .toList()
      );
      consumer.accept(calcFromSizes(sizes, x, y));
    });

    registry.addExclusionArea(BaseScreen.class, (screen, consumer) -> {
      int x = screen.getGuiLeft(), y = screen.getGuiTop();
      int width = screen.getXSize(), height = screen.getYSize();
      if (!(screen instanceof ITabGroupScreen tabScreen)) return;
      List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> sizes = tabScreen.getTabs().getTabs().stream()
          .filter(tab -> {
            boolean widths = tab.getX() >= x + width || tab.getX() + tab.getWidth() >= x + width || tab.getX() <= x;
            boolean heights = tab.getY() >= y + height || tab.getY() + tab.getHeight() >= y + height || tab.getY() <= y;
            return widths || heights;
          })
          .map(tab -> Pair.of(Pair.of(tab.getX(), tab.getY()), Pair.of(tab.getWidth(), tab.getHeight())))
          .toList();
      consumer.accept(calcFromSizes(sizes, x, y));
    });

    registry.removeEmiStacks(stack -> {
      ResourceLocation machineId = stack.getItemStack().getComponents().get(DataComponentRegistration.MACHINE_DATA.get());
      return stack.isEqual(controller) && (machineId == null || machineId.toString().equals(ControllerItem.DUMMY.toString()));
    });
  }

  private static Bounds calcFromSizes(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> sizes, int x, int y) {
    int minX = sizes.stream()
        .mapToInt(pair -> pair.getFirst().getFirst())
        .min()
        .orElse(x);
    int maxX = sizes.stream()
        .mapToInt(pair -> pair.getFirst().getFirst() + pair.getSecond().getFirst())
        .max()
        .orElse(x);
    int minY = sizes.stream()
        .mapToInt(pair -> pair.getFirst().getSecond())
        .min()
        .orElse(y);
    int maxY = sizes.stream()
        .mapToInt(pair -> pair.getFirst().getSecond() + pair.getSecond().getSecond())
        .max()
        .orElse(y);
    return new Bounds(minX, minY, maxX, maxY);
  }

  /**
   * This decorator is adapted from AlmostUnified  <a href="https://github.com/AlmostReliable/almostunified/blob/1.21.1/Common/src/main/java/com/almostreliable/unified/compat/viewer/AlmostEMI.java">AlmostEMI$IndicatorDecorator</a>
   */
  private static class IndicatorDecorator implements EmiRecipeDecorator {

    @Override
    public void decorateRecipe(EmiRecipe recipe, WidgetHolder widgets) {
      var recipeId = recipe.getId();
      if (recipeId == null) return;

      if (recipe instanceof MMREmiRecipe r) {
        int pX = r.getDisplayWidth() - 5;
        int pY = r.getDisplayHeight() - 3;
        int size = RecipeIndicator.RENDER_SIZE - 1;
        var link = r.getRecipe();
        if (!AlmostUnifiedAdapter.isRecipeModified(link)) return;

        widgets.addDrawable(0, 0, 0, 0, (guiGraphics, mX, mY, delta) ->
            RecipeIndicator.renderIndicator(guiGraphics, pX, pY, size));
        widgets.addTooltipText(RecipeIndicator.constructTooltip(link), pX, pY, size, size);
      }
    }
  }
}
