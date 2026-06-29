package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.helper.ProgressData;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedSizedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergyPerTick;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
public class MachineRecipe implements Comparable<MachineRecipe>, Recipe<RecipeInput> {
  public static final NamedMapCodec<MachineRecipeBuilder> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.fieldOf("machine").forGetter(MachineRecipeBuilder::getMachine),
      NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(MachineRecipeBuilder::getTime),
      RecipeRequirement.CODEC.listOf().fieldOf("requirements").forGetter(MachineRecipeBuilder::getRequirements),
      RecipeRequirement.CODEC.listOf().optionalFieldOf("jeiRequirements", List.of()).forGetter(MachineRecipeBuilder::getJeiRequirements),
      NamedCodec.INT.optionalFieldOf("priority", 0).forGetter(MachineRecipeBuilder::getPrio),
      NamedCodec.BOOL.optionalFieldOf("hidden", false).forGetter(MachineRecipeBuilder::isHidden),
      NamedCodec.BOOL.optionalFieldOf("voidFailure", false).forGetter(MachineRecipeBuilder::isVoidF),
      NamedCodec.INT.optionalFieldOf("width", 256).forGetter(MachineRecipeBuilder::getWidth),
      NamedCodec.INT.optionalFieldOf("height", 256).forGetter(MachineRecipeBuilder::getHeight),
      NamedCodec.BOOL.optionalFieldOf("renderProgress", true).forGetter(MachineRecipeBuilder::isShouldRenderProgress),
      ProgressData.CODEC.optionalFieldOf("progressData", ProgressData.DEFAULT_PROGRESS).forGetter(MachineRecipeBuilder::getProgressData)
  ).apply(instance, MachineRecipeBuilder::new), "Machine recipe");

  private final ResourceLocation owningMachine;
  @Getter(AccessLevel.NONE)
  private final int tickTime;
  private final List<RecipeRequirement<?, ?, ?>> recipeRequirements = Lists.newArrayList();
  private final List<RecipeRequirement<?, ?, ?>> jeiRequirements = Lists.newArrayList();
  private final int configuredPriority;
  private final boolean voidPerTickFailure;
  private final ProgressData progressData;
  private final int width, height;
  public final List<Component> textsToRender = Lists.newArrayList();
  public final List<Pair<PositionedSizedRequirement, Object>> chanceTexts = Lists.newArrayList();
  private final boolean shouldRenderProgress;

  private boolean modified = false;
  private boolean hidden = false;

  public MachineRecipe(ResourceLocation owningMachine, int tickTime, int configuredPriority,
                       boolean voidPerTickFailure, int width, int height,
                       boolean shouldRenderProgress, ProgressData progressData) {
    this.owningMachine = owningMachine;
    this.tickTime = tickTime;
    this.configuredPriority = configuredPriority;
    this.voidPerTickFailure = voidPerTickFailure;
    this.progressData = progressData;
    this.shouldRenderProgress = shouldRenderProgress;
    this.width = width;
    this.height = height;
  }

  public void hide(boolean hide) {
    this.hidden = hide;
    if (hidden) MMRLogger.INSTANCE.info("Hiding recipe: {}", this);
  }

  public ResourceLocation getOwningMachineIdentifier() {
    return owningMachine;
  }

  public List<RecipeRequirement<?, ?, ?>> getRequirements() {
    return recipeRequirements;
  }

  public void addRequirement(RecipeRequirement<?, ?, ?> requirement) {
    if (requirement.requirement() instanceof RequirementEnergyPerTick) {
      for (RecipeRequirement<?, ?, ?> req : this.getRequirements()) {
        if (req.requirement() instanceof RequirementEnergyPerTick && req.requirement().getMode() == requirement.requirement().getMode()) {
          throw new IllegalStateException("Tried to add multiple energy requirements for the same ioType! Please only add one for each ioType!");
        }
      }
    }
    if (requirement.isModified()) setModified(true);
    this.recipeRequirements.add(requirement);
  }

  public void addJeiRequirement(RecipeRequirement<?, ?, ?> requirement) {
    if (requirement.requirement() instanceof RequirementEnergyPerTick) {
      for (RecipeRequirement<?, ?, ?> req : this.getJeiRequirements()) {
        if (req.requirement() instanceof RequirementEnergyPerTick && req.requirement().getMode() == requirement.requirement().getMode()) {
          throw new IllegalStateException("Tried to add multiple energy requirements for the same ioType! Please only add one for each ioType!");
        }
      }
    }
    if (requirement.isModified()) setModified(true);
    this.jeiRequirements.add(requirement);
  }

  public int getRecipeTotalTickTime() {
    return this.tickTime;
  }

  public boolean doesCancelRecipeOnPerTickFailure() {
    return this.voidPerTickFailure;
  }

  @Nullable
  public DynamicMachine getOwningMachine() {
    return ModularMachineryReborn.MACHINES.get(getOwningMachineIdentifier());
  }

  @Override
  public int compareTo(MachineRecipe o) {
    return Integer.compare(buildWeight(), o.buildWeight());
  }

  private int buildWeight() {
    return configuredPriority;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("owningMachine", owningMachine.toString());
    json.addProperty("tickTime", tickTime);
    JsonArray recipeRequirements = new JsonArray();
    this.recipeRequirements.forEach(req -> recipeRequirements.add(req.asJson()));
    JsonArray jeiRequirements = new JsonArray();
    this.jeiRequirements.forEach(req -> jeiRequirements.add(req.asJson()));
    json.add("recipeRequirements", recipeRequirements);
    json.add("jeiRequirements", jeiRequirements);
    json.addProperty("configuredPriority", configuredPriority);
    json.addProperty("voidPerTickFailure", voidPerTickFailure);
    json.addProperty("shouldRenderProgress", shouldRenderProgress);
    json.add("progressData", progressData.asJson());
    json.addProperty("modifiedByAU", modified);
    json.addProperty("hidden", hidden);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  @Override
  public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
    return false;
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull RecipeInput container, HolderLookup.@NotNull Provider registryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canCraftInDimensions(int i, int i1) {
    return false;
  }

  @Override
  public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public @NotNull MachineRecipeSerializer getSerializer() {
    return RecipeRegistration.RECIPE_SERIALIZER.get();
  }

  @Override
  public @NotNull RecipeType<?> getType() {
    return RecipeRegistration.RECIPE_TYPE.get();
  }

  public List<RecipeRequirement<?, ?, ?>> getDisplayInfoRequirements() {
    if(this.getJeiRequirements().isEmpty())
      return this.getRequirements();
    return this.getJeiRequirements();
  }

  @Getter
  public static class MachineRecipeBuilder {
    private final ResourceLocation machine;
    private final ProgressData progressData;
    private final int time;
    private final int width, height;
    private int prio;
    private boolean shouldRenderProgress;
    private final List<RecipeRequirement<?, ?, ?>> requirements;
    private final List<RecipeRequirement<?, ?, ?>> jeiRequirements;
    private boolean voidF;
    private boolean modified;
    private boolean hidden;

    public MachineRecipeBuilder(ResourceLocation machine, int time, int width, int height, ProgressData progressData) {
      this.requirements = Lists.newArrayList();
      this.jeiRequirements = Lists.newArrayList();
      this.machine = machine;
      this.time = time;
      this.progressData = progressData;
      this.width = width;
      this.height = height;
    }

    public void modified(boolean modified) {
      this.modified = modified;
    }

    public void withPriority(int prio) {
      this.prio = prio;
    }

    public void shouldVoidOnFailure(boolean v) {
      this.voidF = v;
    }

    public void shouldRenderProgress(boolean v) {
      this.shouldRenderProgress = v;
    }

    public void hide() {
      this.hidden = true;
    }

    public void addRequirement(RecipeRequirement<?, ?, ?> requirement) {
      requirements.add(requirement);
      if (requirement.isModified())
        modified(true);
    }

    public MachineRecipeBuilder(ResourceLocation machine, int time, List<RecipeRequirement<?, ?, ?>> requirements,
                                int prio, boolean hidden, boolean voidF, int width, int height,
                                boolean shouldRenderProgress, ProgressData progressData) {
      this.machine = machine;
      this.time = time;
      this.requirements = requirements;
      this.jeiRequirements = Lists.newArrayList();
      this.prio = prio;
      this.voidF = voidF;
      this.progressData = progressData;
      this.shouldRenderProgress = shouldRenderProgress;
      this.width = width;
      this.height = height;
      this.hidden = hidden;
    }

    public MachineRecipeBuilder(ResourceLocation machine, int time, List<RecipeRequirement<?, ?, ?>> requirements,
                                List<RecipeRequirement<?, ?, ?>> jeiRequirements,
                                int prio, boolean hidden, boolean voidF, int width, int height,
                                boolean shouldRenderProgress, ProgressData progressData) {
      this.machine = machine;
      this.time = time;
      this.requirements = requirements;
      this.jeiRequirements = jeiRequirements;
      this.prio = prio;
      this.voidF = voidF;
      this.hidden = hidden;
      this.progressData = progressData;
      this.shouldRenderProgress = shouldRenderProgress;
      this.width = width;
      this.height = height;
    }

    public MachineRecipeBuilder(MachineRecipe recipe) {
      this(recipe.getOwningMachineIdentifier(), recipe.tickTime, recipe.recipeRequirements,
          recipe.jeiRequirements, recipe.configuredPriority, recipe.hidden,
          recipe.voidPerTickFailure, recipe.width, recipe.height, recipe.shouldRenderProgress,
          recipe.progressData);
      modified(recipe.modified);
    }

    public MachineRecipe build() {
      try {
        MMRLogger.INSTANCE.debug("Building recipe...");
        MachineRecipe recipe = new MachineRecipe(machine, time, prio, voidF, width, height, shouldRenderProgress, progressData);
        requirements.forEach(recipe::addRequirement);
        jeiRequirements.forEach(recipe::addJeiRequirement);
        if (!recipe.modified)
          recipe.setModified(modified);
        recipe.hide(hidden);
        if (MMRConfig.get().logDebugRecipe.get()) {
          MMRLogger.INSTANCE.debug("Finished building recipe {}", recipe);
        }
        return recipe;
      } catch (Exception ex) {
        MMRLogger.INSTANCE.error("Error while building recipe for machine: {}", machine, ex);
      }
      return null;
    }

    public void addJeiRequirements(List<RecipeRequirement<?,?, ?>> requirements) {
      this.jeiRequirements.addAll(requirements);
    }
  }
}
