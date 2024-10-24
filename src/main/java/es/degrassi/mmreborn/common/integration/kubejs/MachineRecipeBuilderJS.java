package es.degrassi.mmreborn.common.integration.kubejs;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.TickDuration;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.requirements.EnergyRequirementJS;
import es.degrassi.mmreborn.common.integration.kubejs.requirements.FluidRequirementJS;
import es.degrassi.mmreborn.common.integration.kubejs.requirements.ItemRequirementJS;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class MachineRecipeBuilderJS extends KubeRecipe implements RecipeJSBuilder,
  EnergyRequirementJS, ItemRequirementJS, FluidRequirementJS
{

  public static final Map<ResourceLocation, Map<ResourceLocation, Integer>> IDS = new HashMap<>();

  public MachineRecipeBuilderJS(ResourceLocation machine, int time) {
    setValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID, machine);
    setValue(ModularMachineryRebornRecipeSchemas.TIME, new TickDuration(time));
  }

  public MachineRecipeBuilderJS() {}

  @Override
  public void afterLoaded() {
    super.afterLoaded();
    ResourceLocation machine = getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID);
    if(machine == null)
      throw new KubeRuntimeException("Invalid machine id: " + getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID));

    if(this.newRecipe) {
      int uniqueID = IDS.computeIfAbsent(RecipeRegistration.RECIPE_TYPE.getId(), id -> new HashMap<>()).computeIfAbsent(machine, m -> 0);
      IDS.get(RecipeRegistration.RECIPE_TYPE.getId()).put(machine, uniqueID + 1);
      this.id = ResourceLocation.fromNamespaceAndPath("kubejs", RecipeRegistration.RECIPE_TYPE.getId().getPath() + "/" + machine.getNamespace() + "/" + machine.getPath() + "/" + uniqueID);
    }
  }

  @Override
  public @Nullable KubeRecipe serializeChanges() {
    if(!this.newRecipe)
      return super.serializeChanges();

    MachineRecipe.MachineRecipeBuilder builder = new MachineRecipe.MachineRecipeBuilder(getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID), (int) getValue(ModularMachineryRebornRecipeSchemas.TIME).ticks());

    for (ComponentRequirement<?, ?> requirement : getValue(ModularMachineryRebornRecipeSchemas.REQUIREMENTS))
      builder.addRequirement(requirement);

    builder.withPriority(getValue(ModularMachineryRebornRecipeSchemas.PRIORITY));
    builder.shouldVoidOnFailure(getValue(ModularMachineryRebornRecipeSchemas.VOID));

    this.id = getOrCreateId();

    if (getValue(ModularMachineryRebornRecipeSchemas.RECIPE_ID) != null)
      this.id = getValue(ModularMachineryRebornRecipeSchemas.RECIPE_ID);

    builder.withId(this.id);

    this.json = (JsonObject) MachineRecipe.CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElse(null);
    if (this.json != null)
      this.json.addProperty("type", RecipeRegistration.RECIPE_TYPE.getId().toString());
    return this;
  }

  public MachineRecipeBuilderJS voidOnFailure(boolean v) {
    setValue(ModularMachineryRebornRecipeSchemas.VOID, v);
    return this;
  }

  public MachineRecipeBuilderJS priority(int priority) {
    setValue(ModularMachineryRebornRecipeSchemas.PRIORITY, priority);
    return this;
  }

  @Override
  public MachineRecipeBuilderJS addRequirement(ComponentRequirement<?, ?> requirement) {
    setValue(ModularMachineryRebornRecipeSchemas.REQUIREMENTS, addToList(ModularMachineryRebornRecipeSchemas.REQUIREMENTS, requirement));
    return this;
  }

  private <E> List<E> addToList(RecipeKey<List<E>> key, E element) {
    List<E> list = new ArrayList<>();
    List<E> values = getValue(key);
    if (values != null) list.addAll(values);
    list.add(element);
    return list;
  }
}
