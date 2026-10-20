package es.degrassi.mmr.common.integration.jei.category;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.api.crafting.requirement.DisplayInfoTemplate;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.api.crafting.requirement.RequirementDisplayInfo;
import es.degrassi.mmr.client.MMRClientEvents;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementDuration;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiDurationComponent;
import es.degrassi.mmr.common.integration.jei.JeiComponentRegistry;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.jei.category.drawable.DrawableWrappedText;
import es.degrassi.mmr.common.item.ControllerItem;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.registration.RecipeRegistration;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

;

public class MMRRecipeCategory implements IRecipeCategory<MachineRecipe> {
  protected static final int ICON_SIZE = 10;
  private DynamicMachine machine;
  private final String title;
  private final IDrawable background, icon;

  public final int initialX = 8, gap = 8;
  @Getter
  protected int width = 256, height = 256;
  protected final LoadingCache<RecipeRequirement<?, ?, ?>, RequirementDisplayInfo> infoCache;
  protected boolean hasInfoRow;
  protected int rowY;
  protected int maxIconPerRow;

  @Getter
  protected final IRecipeType<MachineRecipe> recipeType;

  private boolean dimensionsCalculated = false;
  private int cachedWidth;
  private int cachedHeight;

  public MMRRecipeCategory(DynamicMachine machine) {
    this.machine = machine;
    this.title = machine.getLocalizedName();
    this.background = MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(256, 256);
    ItemStack stack = ControllerItem.makeMachineItem(machine.getRegistryName());
    this.icon = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
    this.infoCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
      @Override
      public @NonNull RequirementDisplayInfo load(RecipeRequirement<?, ?, ?> requirement) {
        RequirementDisplayInfo info = new RequirementDisplayInfo();
        requirement.getDisplayInfo(info);
        DisplayInfoTemplate template = requirement.info;
        if(template != null) {
          if(!template.getTooltips().isEmpty())
            info.getTooltips().clear();
          template.build(info);
        }
        return info;
      }
    });
    this.recipeType = IRecipeType.create(machine.getRegistryName(), MachineRecipe.class);
  }

  public void updateMachine(DynamicMachine machine) {
    this.machine = machine;
  }

  @Override
  public @NonNull Component getTitle() {
    return Component.literal(title);
  }

  @Override 
  public @Nullable IDrawable getIcon() {
    return icon;
  }

  private void setupRecipeDimensions() {
    this.maxIconPerRow = this.width / (ICON_SIZE + 2);
    long maxDisplayRequirement = MMRClientEvents.getClientRecipes()
        .byType(RecipeRegistration.RECIPE_TYPE.get())
        .stream()
        .map(RecipeHolder::value)
        .filter(recipe -> recipe.getOwningMachineIdentifier().equals(this.machine.getRegistryName()) && !recipe.isHidden())
        .mapToLong(recipe -> recipe.getDisplayInfoRequirements().stream().map(this.infoCache).filter(RequirementDisplayInfo::shouldRender).count())
        .max()
        .orElse(0);
    this.hasInfoRow = maxDisplayRequirement != 0;
    this.rowY = this.height;
    int rows = this.hasInfoRow ? Math.toIntExact(maxDisplayRequirement) / this.maxIconPerRow + 1 : 0;
    this.height = this.rowY + (ICON_SIZE + 2) * rows;
  }

  @Override
  public void draw(MachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
    IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

    //Render the line between the gui elements and the requirements icons
    if(this.hasInfoRow)
      guiGraphics.fill(-3, this.rowY, this.width + 3, this.rowY + 1, 0x30000000);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    this.width = recipe.getWidth();
    this.height = recipe.getHeight();

    /*
    Jei doesn't allow, apparently, dynamic size recipes displays, so we need to get the max size on both
    to cached it and not need to calculate them again.
    Since we would do that on every recipe, and if we have lots of them, things scalate fast
    Basically a loop inside a loop
    */
    if (!this.dimensionsCalculated) {
        this.setupRecipeDimensions();
        this.cachedWidth = this.width;
        this.cachedHeight = this.height;
        this.dimensionsCalculated = true;
    } else {
        this.width = this.cachedWidth;
        this.height = this.cachedHeight;
    }

    recipe.textsToRender.clear();
    recipe.chanceTexts.clear();
    if (recipe.isShouldRenderProgress()) {
      new JeiDurationComponent(
          new RecipeRequirement<>(new RequirementDuration(recipe.getRecipeTotalTickTime(), recipe.getProgressData().position())),
          20, recipe.getProgressData()
      ).setRecipe(this, builder, recipe, focuses);
    }

    (recipe.getJeiRequirements().isEmpty() ? recipe.getRequirements() : recipe.getJeiRequirements())
        .stream()
        .filter(component -> JeiComponentRegistry.hasJeiComponent(component.getType()))
        .map(JeiComponentRegistry::create)
        .forEach(requirement -> requirement.setRecipe(this, builder, recipe, focuses));
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    IPlaceable<?> text = builder.addDrawableWidget(
        new DrawableWrappedText(
            Lists.newArrayList(recipe.textsToRender.iterator()),
            recipe.getWidth() - 8,
            false
        )
    );
    text.setPosition(initialX, recipe.getHeight() - gap - text.getHeight());

    recipe.chanceTexts.stream()
        .map(Pair::getSecond)
        .map(DrawableWrappedText.class::cast)
        .forEach(builder::addDrawableWidget);
    //If no recipes have display infos stop here
    if(!this.hasInfoRow)
      return;

    AtomicInteger index = new AtomicInteger();
    AtomicInteger row = new AtomicInteger(0);
    recipe.getDisplayInfoRequirements()
        .stream()
        .map(this.infoCache)
        .filter(RequirementDisplayInfo::shouldRender)
        .forEach(info -> {
      int x = index.get() * (ICON_SIZE + 2) - 2;
      int y = this.rowY + 2 + (ICON_SIZE + 2) * row.get();
      if(index.incrementAndGet() >= this.maxIconPerRow) {
        index.set(0);
        row.incrementAndGet();
      }
      DisplayInfoWidget widget = new DisplayInfoWidget(x, y, info, recipe);
      builder.addWidget(widget);
      builder.addInputHandler(widget);
    });
  }

  public class DisplayInfoWidget implements IRecipeWidget, IJeiInputHandler {

    private final ScreenPosition pos;
    private final ScreenRectangle area;
    private final RequirementDisplayInfo info;
    private final MachineRecipe recipe;

    public DisplayInfoWidget(int x, int y, RequirementDisplayInfo info, MachineRecipe recipe) {
      this.pos = new ScreenPosition(x, y);
      this.area = new ScreenRectangle(x, y, ICON_SIZE, ICON_SIZE);
      this.info = info;
      this.recipe = recipe;
    }

    @Override
    public ScreenPosition getPosition() {
      return this.pos;
    }

    @Override
    public void drawWidget(GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
      this.info.renderIcon(graphics, ICON_SIZE);
    }

    @Override
    public void getTooltip(ITooltipBuilder builder, double mouseX, double mouseY) {
      if(mouseX > ICON_SIZE || mouseY > ICON_SIZE || mouseX < -1 || mouseY < 0)
        return;
      this.info.getTooltips().stream()
          .filter(pair -> pair.getSecond().shouldDisplay(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips))
          .map(Pair::getFirst)
          .forEach(builder::add);
    }

    @Override
    public ScreenRectangle getArea() {
      return this.area;
    }

    @Override
    public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
      if (Minecraft.getInstance().screen == null)
        return false;
      if (input.isSimulate())
        return true;
      return this.info.handleClick(MMRRecipeCategory.this.machine, this.recipe, input.getKey().getValue());
    }
  }
}
