package es.degrassi.mmr.common.manager.crafting;

import com.google.common.collect.Lists;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.api.crafting.CraftingContext;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.crafting.RequirementList.RequirementWithFunction;
import es.degrassi.mmr.common.util.Utils;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class MachineProcessorCore implements ISyncableStuff, ValueIOSerializable {

  private final MachineProcessor processor;
  private final MachineControllerEntity tile;
  private final RandomSource rand = Utils.RAND;
  private final MachineRecipeFinder recipeFinder;
  @Getter
  @Setter
  private boolean active = false;
  @Nullable
  @Getter
  private RecipeHolder<MachineRecipe> currentRecipe;
  private Identifier futureRecipeID;
  private CraftingContext context;
  @Getter
  private float recipeProgressTime;
  private int recipeTotalTime = 0;
  private boolean searchImmediately = false;
  private Phase phase = Phase.CONDITIONS;
  private boolean componentChanged = false;
  @Nullable
  @Getter
  private Component error = null;
  private boolean isLastRecipeTick = false;

  @Getter
  private boolean hasActiveRecipe;

  private RequirementList<? extends IRequirement<?, ?>, ? extends MachineComponent<?>, ?> requirementList;
  private final List<RequirementWithFunction<?, ?, ?>> currentProcessRequirements = Lists.newArrayList();
  private int core;

  public MachineProcessorCore(MachineProcessor processor, MachineControllerEntity tile, int core) {
    this.processor = processor;
    this.tile = tile;
    this.recipeFinder = new MachineRecipeFinder(tile, new CraftingContext.Mutable(tile, core - 1), this);
    this.core = core;
  }

  public void reload() {
    recipeFinder.reload();
  }

  public int getCore() {
    return this.processor.cores().indexOf(this) + 1;
  }

  public float getRecipeTotalTime() {
    return this.recipeTotalTime;
  }

  @SuppressWarnings("unchecked")
  public void init() {
    //Search for previous recipe
    if (isActive() && this.futureRecipeID != null && this.tile.getLevel() != null) {
      ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, this.futureRecipeID);
      ((net.minecraft.world.item.crafting.RecipeManager) this.tile.getLevel().recipeAccess())
          .byKey(recipeKey)
          .filter(holder -> holder.value() instanceof MachineRecipe)
          .map(holder -> (RecipeHolder<MachineRecipe>) (RecipeHolder<?>) holder)
          .ifPresent(recipe -> {
            this.setRecipe(recipe);
            //Remove all requirements that were already processed before the machine was unloaded.
            this.requirementList.getProcessRequirements().entrySet().removeIf(entry -> entry.getKey() < this.recipeProgressTime / this.recipeTotalTime);
          });
      this.futureRecipeID = null;
    }
    this.recipeFinder.init();
  }

  public boolean hasActiveRecipe() {
    return getCurrentRecipe() != null;
  }

  /**
   * Client-safe variant of {@link #hasActiveRecipe()}: the experience-lib synced flag, set by the
   * server even when the client cannot resolve the recipe object from its id.
   */
  public boolean hasSyncedActiveRecipe() {
    return hasActiveRecipe;
  }

  public void tick() {
    if (!isActive()) return;
    if (this.currentRecipe == null) {
      this.recipeFinder.findRecipe(this.searchImmediately).ifPresent(this::setRecipe);
      this.searchImmediately = false;
      this.componentChanged = false;
    }

    if (this.currentRecipe != null) {
      if (this.phase == Phase.CONDITIONS)
        this.checkConditions();

      if (this.phase == Phase.PROCESS)
        this.processRequirements();

      if (this.phase == Phase.PROCESS_TICK)
        this.processTickRequirements();

      if (this.currentRecipe != null && this.error == null && this.recipeProgressTime >= this.recipeTotalTime - this.context.getModifiedSpeed()) {
        if (this.isLastRecipeTick) {
          this.isLastRecipeTick = false;
          this.reset();
          this.recipeFinder.findRecipe(true).ifPresent(this::setRecipe);
        } else
          this.isLastRecipeTick = true;
      }
    }
  }

  private void checkConditions() {
    if (this.componentChanged) {
      this.componentChanged = false;
      for (RequirementWithFunction<?, ?, ?> requirement : this.requirementList.getInventoryConditions()) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
          if (this.currentRecipe != null && this.currentRecipe.value().isVoidPerTickFailure()) this.reset();
          else this.setError(result.getMessage());
          return;
        }
      }
    }

    for (RequirementWithFunction<?, ?, ?> requirement : this.requirementList.getWorldConditions()) {
      CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
      if (!result.isSuccess()) {
        if (this.currentRecipe != null && this.currentRecipe.value().isVoidPerTickFailure()) this.reset();
        else this.setError(result.getMessage());
        return;
      }
    }

    this.setRunning();
    this.phase = Phase.PROCESS;
  }

  private void processRequirements() {
    if (this.currentProcessRequirements.isEmpty()) {
      this.requirementList.getProcessRequirements().entrySet().removeIf(entry -> {
        //if the recipe is at last tick process all remaining requirements
        //Else process only requirements that have a delay lower than the current progress
        if (entry.getKey() <= this.recipeProgressTime / this.recipeTotalTime || this.isLastRecipeTick) {
          this.currentProcessRequirements.addAll(entry.getValue());
          return true;
        }
        return false;
      });
    }

    for (Iterator<RequirementWithFunction<?, ?, ?>> iterator = this.currentProcessRequirements.iterator(); iterator.hasNext(); ) {
      RequirementWithFunction<?, ?, ?> requirement = iterator.next();
      if (!requirement.requirement().shouldSkip(this.rand, this.context)) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
          if (this.currentRecipe != null && this.currentRecipe.value().isVoidPerTickFailure())
            this.reset();
          else
            this.setError(result.getMessage());
          return;
        }
      }
      iterator.remove();
    }

    this.setRunning();
    this.phase = Phase.PROCESS_TICK;
  }

  private void processTickRequirements() {
    if (this.currentProcessRequirements.isEmpty())
      this.currentProcessRequirements.addAll(this.requirementList.getTickableRequirements());

    for (Iterator<RequirementWithFunction<?, ?, ?>> iterator = this.currentProcessRequirements.iterator(); iterator.hasNext(); ) {
      RequirementWithFunction<?, ?, ?> requirement = iterator.next();
      if (!requirement.requirement().shouldSkip(this.rand, this.context)) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
          if (this.currentRecipe != null && this.currentRecipe.value().isVoidPerTickFailure()) this.reset();
          else this.setError(result.getMessage());
          return;
        }
      }
      iterator.remove();
    }

    this.setRunning();
    this.phase = Phase.CONDITIONS;
    this.recipeProgressTime += this.context.getModifiedSpeed();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void setRecipe(@NotNull RecipeHolder<MachineRecipe> recipe) {
    this.currentRecipe = recipe;
    this.context = new CraftingContext(this.tile, recipe, () -> this.recipeProgressTime, this.processor.cores().indexOf(this));
    this.recipeTotalTime = this.currentRecipe.value().getRecipeTotalTickTime();
    this.requirementList = new RequirementList<>();
    this.currentRecipe.value().getRequirements().forEach(requirement -> {
      this.requirementList.setCurrentRequirement(requirement);
      requirement.requirement().gatherRequirements((RequirementList) this.requirementList);
    });
    this.phase = Phase.CONDITIONS;
  }

  private void setRunning() {
    this.error = null;
    this.processor.setRunning();
  }

  private void setError(Component error) {
    this.error = error;
    this.processor.setError(error);
  }

  public void reset() {
    this.currentRecipe = null;
    this.futureRecipeID = null;
    this.recipeProgressTime = 0;
    this.recipeTotalTime = 0;
    this.requirementList = null;
    this.context = null;
    this.phase = Phase.CONDITIONS;
    this.currentProcessRequirements.clear();
  }

  public void setSearchImmediately() {
    if (this.currentRecipe == null)
      this.searchImmediately = true;
  }

  public void setComponentChanged() {
    this.recipeFinder.setComponentChanged(true);
    this.componentChanged = true;
  }

  public CompoundTag serialize() {
    CompoundTag nbt = new CompoundTag();
    nbt.putBoolean("active", active);
    nbt.putInt("core", core);
    if (this.currentRecipe != null)
      nbt.putString("recipe", this.currentRecipe.id().identifier().toString());
    nbt.putString("phase", this.phase.toString());
    nbt.putFloat("recipeProgressTime", this.recipeProgressTime);
    return nbt;
  }

  public void serialize(ValueOutput output) {
    output.putBoolean("active", active);
    output.putInt("core", core);
    output.putString("phase", phase.toString());
    output.putFloat("recipeProgressTime", recipeProgressTime);
    if (currentRecipe != null) {
      output.putString("recipe", currentRecipe.id().identifier().toString());
    }
  }

  public void deserialize(ValueInput nbt) {
    this.active = nbt.getBooleanOr("active", false);
    this.core = nbt.getIntOr("core", 0);
    this.futureRecipeID = nbt.getString("recipe").map(Identifier::parse).orElse(null);
    this.phase = nbt.getString("phase").map(Phase::valueOf).orElse(Phase.CONDITIONS);
    this.recipeProgressTime = nbt.getFloatOr("recipeProgressTime", 0f);
  }

  @Override
  public void getStuffToSync(SyncableList container) {
    container.syncBool(() -> active, active -> this.active = active);
    container.syncInt(() -> core, core -> this.core = core);
    container.syncFloat(() -> this.recipeProgressTime, recipeProgressTime -> this.recipeProgressTime = recipeProgressTime);
    container.syncInt(() -> this.recipeTotalTime, recipeTotalTime -> this.recipeTotalTime = recipeTotalTime);
    container.syncBool(() -> getCurrentRecipe() != null, hasActiveRecipe -> this.hasActiveRecipe = hasActiveRecipe);
  }

  public float getCurrentActiveRecipeProgress() {
    return getRecipeProgressTime() / getRecipeTotalTime();
  }
}
