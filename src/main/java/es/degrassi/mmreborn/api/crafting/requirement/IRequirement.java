package es.degrassi.mmreborn.api.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.api.crafting.ComponentNotFoundException;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.IProcessor;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public interface IRequirement<C extends MachineComponent<T>, T> {

  /**
   * A dispatch codec, used by the {@link MachineRecipe} main codec to parse all requirements from json using the "type"
   * property of the requirement.
   */
  NamedMapCodec<IRequirement<?, ?>> CODEC = RegistrarCodec.REQUIREMENT_NEW.dispatch(IRequirement::getType, RequirementType::getCodec, "Requirement");

  /**
   * Used by the requirement dispatch codec to serialize an IRequirement.
   * This MUST return the same instance of the {@link RequirementType} as the one registered in the forge registry.
   * @return The type of this requirement.
   */
  RequirementType<? extends IRequirement<C, T>, C, T> getType();

  /**
   * Used by the crafting process to find which component the requirement use.
   * If the machine doesn't have the component, an {@link ComponentNotFoundException} will be thrown and the recipe will not be processed.
   * This MUST return the same instance of the {@link ComponentType} as the one registered in the forge registry.
   * @return The type of component used by this requirement.
   */
  ComponentType<T> getComponentType();

  /**
   * Currently only used by machine upgrades to find whether they apply to this requirement.
   * @return The INPUT or OUTPUT mode of this requirement.
   */
  IOType getMode();

  /**
   * The first step of the crafting process, the machine is idle and searching for recipes to process.
   * For each available recipes it will test all requirements using this method.
   * NOTE : You must only do checks in this method, and not consume or produce things as the recipe may not be processed if another requirement return false.
   * @param component The {@link MachineComponent} used by this requirement.
   * @param context A few useful info about the crafting process, and some utilities methods.
   * @return True if the requirement can be processed by this machine, false otherwise.
   */
  boolean test(C component, ICraftingContext context);

  /**
   * This method is called by the {@link IProcessor} when a recipe containing this requirement is found suitable to be processed.
   * Inside this method you must pass every {@link IRequirementList.RequirementFunction} that the processor must check or process.
   * @param list An {@link IRequirementList} used to collect every {@link IRequirementList.RequirementFunction} that the processor must check or process for this requirement.
   */
  void gatherRequirements(IRequirementList<C> list);

  default ResourceLocation getId() {
    return ModularMachineryReborn.getRequirementRegistrar().getKey(getType());
  }

  PositionedRequirement getPosition();

  default JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("actionType", getMode().name());
    json.add("position", getPosition().asJson());
    json.addProperty("type", getId() != null ? getId().toString() : "");
    json.addProperty("modified", isModified());
    return json;
  }

  @Nonnull
  Component getMissingComponentErrorMessage(IOType ioType);

  boolean isComponentValid(C m, ICraftingContext context);

  default boolean isModified() {
    return false;
  }

  default void setModified(boolean modified) {

  }

  default void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {

  }
}
