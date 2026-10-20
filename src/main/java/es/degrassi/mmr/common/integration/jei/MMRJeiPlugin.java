package es.degrassi.mmr.common.integration.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.MMRClientEvents;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.integration.jei.category.MMRMultiblockRecipeCategory;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.DummyIngredientRenderer;
import es.degrassi.mmr.common.integration.jei.ingredient.helpers.DurationIngredientHelper;
import es.degrassi.mmr.common.integration.jei.ingredient.helpers.EnergyIngredientHelper;
import es.degrassi.mmr.common.integration.jei.ingredient.helpers.FuelIngredientHelper;
import es.degrassi.mmr.common.integration.jei.ingredient.helpers.VoidIngredientHelper;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Duration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Energy;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Fuel;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import es.degrassi.mmr.common.integration.xei.MultiblockRecipe;
import es.degrassi.mmr.common.item.ControllerItem;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import es.degrassi.mmr.common.registration.RecipeRegistration;
import es.degrassi.mmr.common.util.Comparators;
import es.degrassi.mmr.common.util.Mods;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
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
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JeiPlugin
public class MMRJeiPlugin implements IModPlugin {
  public static final Identifier PLUGIN_ID = ModularMachineryReborn.rl("jei_plugin");
  private static final Map<Identifier, MMRRecipeCategory> recipeCategories = Maps.newHashMap();
  private static final Map<Identifier, MMRMultiblockRecipeCategory> multiblockCategories = Maps.newHashMap();
  public static IJeiHelpers jeiHelpers;

  @Nullable
  public static MMRRecipeCategory getCategory(DynamicMachine machine) {
    return recipeCategories.get(machine.getRegistryName());
  }

  public static Optional<MMRRecipeCategory> getCategory(Identifier machine) {
    return Optional.ofNullable(recipeCategories.get(machine));
  }

  public static Optional<MMRMultiblockRecipeCategory> getMultiblockCategory(Identifier machine) {
    return Optional.ofNullable(multiblockCategories.get(machine));
  }

  @Nullable
  private static IJeiRuntime jeiRuntime;

  /**
   * Opens the JEI recipe screen on the whole machine's categories (the "Show Recipes" tab click of
   * the controller screen): the normal category plus the LDLib2 multiblock preview one when built.
   * No-op when JEI is present but had not handed over its runtime yet.
   */
  public static void showRecipes(Identifier machine) {
    if (jeiRuntime == null) {
      return;
    }
    List<IRecipeType<?>> types = new ArrayList<>();
    getCategory(machine).ifPresent(category -> types.add(category.getRecipeType()));
    getMultiblockCategory(machine).ifPresent(category -> types.add(category.getRecipeType()));
    if (!types.isEmpty()) {
      jeiRuntime.getRecipesGui().showTypes(types);
    }
  }

  @Nullable
  private static MMRMultiblockRecipeCategory getMultiblockCategory(DynamicMachine machine) {
    return multiblockCategories.get(machine.getRegistryName());
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
    registration.register(CustomIngredientTypes.ENERGY, Lists.newArrayList(), new EnergyIngredientHelper(),
        new DummyIngredientRenderer<>(), Energy.CODEC.codec());
    registration.register(CustomIngredientTypes.FUEL, Lists.newArrayList(), new FuelIngredientHelper(),
        new DummyIngredientRenderer<>(), Fuel.CODEC.codec());
    registration.register(CustomIngredientTypes.DURATION, Lists.newArrayList(), new DurationIngredientHelper(),
        new DummyIngredientRenderer<>(), Duration.CODEC.codec());
    registration.register(CustomIngredientTypes.VOID, Lists.newArrayList(), new VoidIngredientHelper(),
        new DummyIngredientRenderer<>(), Void.CODEC.codec());
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    // LEGACY: the extra-area / clickable-area / ghost-ingredient handlers below targeted the
    // old AbstractContainerScreen UI (BaseScreen, ControllerScreen, FilterSlotComponent), which
    // has been replaced by the LDLib2 ModularUI screens. JEI still renders the recipes; the
    // extras of the new screens can be re-registered here once their layout is final.
}

  private static IGuiClickableArea createBasic(
      int xPos,
      int yPos,
      int width,
      int height,
      Identifier id
  ) {
    Rect2i area = new Rect2i(xPos, yPos, width, height);
    ItemStack stack = BlockRegistration.CONTROLLER.stack(1);
    stack.set(DataComponentRegistration.MACHINE_DATA, id);
    return new IGuiClickableArea() {
      @Override
      public Rect2i getArea() {
        return area;
      }

      @Override
      public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
        recipesGui.show(focusFactory.createFocus(RecipeIngredientRole.CRAFTING_STATION, VanillaTypes.ITEM_STACK, stack));
      }
    };
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registration) {
    registration.registerSubtypeInterpreter(BlockRegistration.CONTROLLER.asItem(), CONTROLLER_ITEM_INTERPRETER);
  }

  public static final ISubtypeInterpreter<ItemStack> CONTROLLER_ITEM_INTERPRETER = (ingredient, context) ->
      ControllerItem.getMachine(ingredient).map(machine -> machine.getRegistryName().toString()).orElse("dummy");

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
      ItemStack stack = BlockRegistration.CONTROLLER.stack(1);
      stack.set(DataComponentRegistration.MACHINE_DATA, machine.getRegistryName());
      registration.addCraftingStation(getCategory(machine).getRecipeType(), stack);
      if (Mods.isLDLibLoaded()) {
        registration.addCraftingStation(getMultiblockCategory(machine).getRecipeType(), ItemRegistration.BLUEPRINT.get().getDefaultInstance(), stack);
      }
    }
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    if (Minecraft.getInstance().level == null) return;
    Map<Identifier, List<MachineRecipe>> machineRecipes = MMRClientEvents.getClientRecipes()
            .byType(RecipeRegistration.RECIPE_TYPE.get())
            .stream()
            .filter(holder -> !holder.value().isHidden())
            .sorted(Comparators::compare)
            .map(RecipeHolder::value)
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
  public Identifier getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    MMRJeiPlugin.jeiRuntime = jeiRuntime;
    jeiHelpers = jeiRuntime.getJeiHelpers();
  }
}
