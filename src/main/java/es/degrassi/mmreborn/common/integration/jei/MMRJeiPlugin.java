package es.degrassi.mmreborn.common.integration.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.client.machine.TooltipUse;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.integration.almostunified.RecipeIndicator;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import es.degrassi.mmreborn.client.screen.BaseScreen;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.widget.tabs.ITabGroupScreen;
import es.degrassi.mmreborn.client.screen.widget.tabs.TabGroupWidget;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.almostunified.AlmostUnifiedAdapter;
import es.degrassi.mmreborn.common.integration.jei.category.MMRMultiblockRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.integration.jei.ingredient.DummyIngredientRenderer;
import es.degrassi.mmreborn.common.integration.jei.ingredient.IntegerIngredientHelper;
import es.degrassi.mmreborn.common.integration.jei.ingredient.LongIngredientHelper;
import es.degrassi.mmreborn.common.integration.jei.ingredient.VoidIngredientHelper;
import es.degrassi.mmreborn.common.integration.xei.MultiblockRecipe;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JeiPlugin
public class MMRJeiPlugin implements IModPlugin {
  public static final ResourceLocation PLUGIN_ID = ModularMachineryReborn.rl("jei_plugin");
  private static final Map<ResourceLocation, MMRRecipeCategory> recipeCategories = Maps.newHashMap();
  private static final Map<ResourceLocation, MMRMultiblockRecipeCategory> multiblockCategories = Maps.newHashMap();
  public static IJeiHelpers jeiHelpers;

  @Nullable
  public static MMRRecipeCategory getCategory(DynamicMachine machine) {
    return recipeCategories.get(machine.getRegistryName());
  }

  @Nullable
  private static MMRMultiblockRecipeCategory getMultiblockCategory(DynamicMachine machine) {
    return multiblockCategories.get(machine.getRegistryName());
  }

  public static Optional<MMRRecipeCategory> getCategory(ResourceLocation machine) {
    return Optional.ofNullable(recipeCategories.get(machine));
  }

  private static Optional<MMRMultiblockRecipeCategory> getMultiblockCategory(ResourceLocation machine) {
    return Optional.ofNullable(multiblockCategories.get(machine));
  }

  @Override
  public void registerIngredientAliases(IIngredientAliasRegistration registration) {
    Collection<ItemStack> stacks = ModularMachineryReborn.MACHINES
        .values()
        .stream()
        .map(DynamicMachine::getRegistryName)
        .map(ControllerItem::makeMachineItem)
        .toList();
    registration.addAliases(VanillaTypes.ITEM_STACK, stacks, List.of(
        "Controller",
        "multiblock",
        "Multiblock"
    ));
  }

  @Override
  public void registerModInfo(IModInfoRegistration register) {
    register.addModAliases(
        ModularMachineryReborn.rootLC(ModularMachineryReborn.MODID),
        ModularMachineryReborn.rootUC(ModularMachineryReborn.MODID),
        ModularMachineryReborn.rootLC("mmr"),
        ModularMachineryReborn.rootUC("MMR"),
        ModularMachineryReborn.rootLC("mm"),
        ModularMachineryReborn.rootUC("MM")
    );
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(CustomIngredientTypes.LONG, Lists.newArrayList(), new LongIngredientHelper(),
        new DummyIngredientRenderer<>(), NamedCodec.LONG.codec());
    registration.register(CustomIngredientTypes.INTEGER, Lists.newArrayList(), new IntegerIngredientHelper(),
        new DummyIngredientRenderer<>(), NamedCodec.INT.codec());
    registration.register(CustomIngredientTypes.VOID, Lists.newArrayList(), new VoidIngredientHelper(),
        new DummyIngredientRenderer<>(), NamedCodec.VOID.codec());
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addGuiContainerHandler(BaseScreen.class, new IGuiContainerHandler<>() {
      @Override
      public List<Rect2i> getGuiExtraAreas(BaseScreen screen) {
        List<Rect2i> extraAreas = Lists.newArrayList();
        if (!(screen instanceof ITabGroupScreen tabScreen)) return extraAreas;
        TabGroupWidget tabs = tabScreen.getTabs();
        extraAreas.add(new Rect2i(tabs.getX(), tabs.getY(), tabs.getWidth(), tabs.getHeight()));
        return extraAreas;
      }
    });
    registration.addGuiContainerHandler(ControllerScreen.class, new IGuiContainerHandler<>() {
      @Override
      public Collection<IGuiClickableArea> getGuiClickableAreas(ControllerScreen containerScreen, double mouseX, double mouseY) {
        if (containerScreen.getPopupUnderMouse(mouseX, mouseY) != null)
          return List.of();
        var tab = containerScreen.getTabs().getTabs().get(0);
        return List.of(createBasic(
            tab.getWidth() * Optional.ofNullable(ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(containerScreen.getMenu().getId()))
                .map(c -> c.get(TooltipUse.GUI))
                .map(c -> !c.isEmpty() ? 4 : 3)
                .orElse(3),
            -tab.getHeight(),
            tab.getWidth(),
            tab.getHeight(),
            containerScreen.getMenu().getId()
        ));
      }

      @Override
      public List<Rect2i> getGuiExtraAreas(ControllerScreen screen) {
        List<Rect2i> extraAreas = Lists.newArrayList();
        TabGroupWidget tabs = screen.getTabs();
        extraAreas.add(new Rect2i(tabs.getX(), tabs.getY(), tabs.getWidth(), tabs.getHeight()));
        screen.popups().forEach(popup -> extraAreas.add(new Rect2i(popup.x, popup.y, popup.xSize, popup.ySize)));
        return extraAreas;
      }
    });

    registration.addGhostIngredientHandler(BaseScreen.class, new IGhostIngredientHandler<>() {
      @Override
      @SuppressWarnings("unchecked")
      public <I> List<Target<I>> getTargetsTyped(BaseScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var stack = ingredient;
        if (JeiFilterDragDropRegistry.canDrop(stack)) {
          return screen.getMenu().slots.stream()
              .filter(slot -> slot instanceof FilterSlotComponent<?,?>)
              .map(slot -> {
                FilterSlotComponent<I,?> filterSlot = (FilterSlotComponent<I,?>) slot;
                return new Target<I>() {
                  @Override
                  public Rect2i getArea() {
                    return new Rect2i(screen.getGuiLeft() + filterSlot.x, screen.getGuiTop() + filterSlot.y, 16, 16);
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
    });
  }

  private static IGuiClickableArea createBasic(
      int xPos,
      int yPos,
      int width,
      int height,
      ResourceLocation id
  ) {
    Rect2i area = new Rect2i(xPos, yPos, width, height);
    ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
    stack.set(DataComponentRegistration.MACHINE_DATA, id);
    return new IGuiClickableArea() {
      @Override
      public Rect2i getArea() {
        return area;
      }

      @Override
      public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
        recipesGui.show(focusFactory.createFocus(RecipeIngredientRole.CATALYST, VanillaTypes.ITEM_STACK, stack));
      }
    };
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registration) {
    registration.registerSubtypeInterpreter(ItemRegistration.CONTROLLER.get(), CONTROLLER_ITEM_INTERPRETER);
  }

  public static final ISubtypeInterpreter<ItemStack> CONTROLLER_ITEM_INTERPRETER = new ISubtypeInterpreter<>() {
    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return ControllerItem.getMachine(ingredient).map(machine -> machine.getRegistryName().toString()).orElse("dummy");
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
      return ControllerItem.getMachine(ingredient).map(machine -> machine.getRegistryName().toString()).orElse("dummy");
    }
  };

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    if (jeiHelpers == null) jeiHelpers = registration.getJeiHelpers();
    recipeCategories.clear();
    multiblockCategories.clear();
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine == null || machine == DynamicMachine.DUMMY) continue;
      MMRRecipeCategory recipe = new MMRRecipeCategory(machine);
      registration.addRecipeCategories(recipe);
      recipeCategories.put(machine.getRegistryName(), recipe);
      if (Mods.isLDLibLoaded()) {
        MMRMultiblockRecipeCategory multiblockCategory = new MMRMultiblockRecipeCategory(machine);
        registration.addRecipeCategories(multiblockCategory);
        multiblockCategories.put(machine.getRegistryName(), multiblockCategory);
      }
    }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    if (jeiHelpers == null) jeiHelpers = registration.getJeiHelpers();
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine == null || machine == DynamicMachine.DUMMY) continue;
      ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
      stack.set(DataComponentRegistration.MACHINE_DATA, machine.getRegistryName());
      registration.addRecipeCatalysts(getCategory(machine).getRecipeType(), ItemRegistration.BLUEPRINT.get().getDefaultInstance(), stack);
      if (Mods.isLDLibLoaded()) {
        registration.addRecipeCatalysts(getMultiblockCategory(machine).getRecipeType(), ItemRegistration.BLUEPRINT.get().getDefaultInstance(), stack);
      }
    }
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    if (Minecraft.getInstance().level == null) return;
    Map<ResourceLocation, List<MachineRecipe>> machineRecipes = Minecraft.getInstance().level.getRecipeManager()
            .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
            .stream()
            .map(RecipeHolder::value)
            .filter(recipe -> !recipe.isHidden())
            .sorted(Comparator.comparingInt(MachineRecipe::getConfiguredPriority).reversed())
            .collect(Collectors.groupingBy(MachineRecipe::getOwningMachineIdentifier));

    machineRecipes.forEach((id, recipes) -> getCategory(id)
        .ifPresent(cat -> registration
            .addRecipes(cat.getRecipeType(), recipes)
        )
    );

    if (Mods.isLDLibLoaded()) {
      for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
        getMultiblockCategory(machine.getRegistryName())
            .ifPresent(cat -> registration
              .addRecipes(cat.getRecipeType(), List.of(new MultiblockRecipe(machine)))
            );
      }
    }
  }

  @Override
  public void registerAdvanced(IAdvancedRegistration registration) {
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine == null || machine.equals(DynamicMachine.DUMMY)) continue;
      registration.addRecipeCategoryDecorator(getCategory(machine).getRecipeType(), new Decorator<>());
    }
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    /*recipeCategories.forEach((machine, category) -> {
      registration.addRecipeTransferHandler(
          new MMRJeiRecipeTransferHandler(
              machine.getRegistryName(),
              registration.getTransferHelper(),
              category.getRecipeType()
          ),
          category.getRecipeType()
      );
    });*/
  }

  @Override
  public ResourceLocation getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    jeiHelpers = jeiRuntime.getJeiHelpers();
  }


  /**
   * This decorator is adapted from AlmostUnified <a href="https://github.com/AlmostReliable/almostunified/blob/1.21.1/Common/src/main/java/com/almostreliable/unified/compat/viewer/AlmostJEI.java">AlmostJEI$Decorator</a>
   */
  private static class Decorator<T> implements IRecipeCategoryDecorator<T> {

    private static final int RECIPE_BORDER_PADDING = 4;

    @Override
    public void draw(T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      var recipeLink = resolveLink(recipeCategory, recipe);
      if (recipeLink == null) return;

      var pX = recipeCategory.getWidth() + (2 * RECIPE_BORDER_PADDING) - RecipeIndicator.RENDER_SIZE;
      var pY = recipeCategory.getHeight() + (2 * RECIPE_BORDER_PADDING) - RecipeIndicator.RENDER_SIZE;
      RecipeIndicator.renderIndicator(guiGraphics, pX, pY, RecipeIndicator.RENDER_SIZE);

      if (mouseX >= pX && mouseX <= pX + RecipeIndicator.RENDER_SIZE &&
          mouseY >= pY && mouseY <= pY + RecipeIndicator.RENDER_SIZE) {
        RecipeIndicator.renderTooltip(guiGraphics, recipeLink, mouseX, mouseY);
      }
    }

    @Nullable
    private static <R> MachineRecipe resolveLink(IRecipeCategory<R> recipeCategory, R recipe) {
      var recipeId = recipeCategory.getRegistryName(recipe);
      if (recipeId == null) return null;
      if (!(recipe instanceof MachineRecipe r)) return null;
      if (!AlmostUnifiedAdapter.isRecipeModified(r)) return null;
      return r;
    }
  }
}
