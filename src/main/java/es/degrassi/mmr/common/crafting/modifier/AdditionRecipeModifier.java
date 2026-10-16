package es.degrassi.mmr.common.crafting.modifier;

import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AdditionRecipeModifier<
    R extends IRequirement<C, T>,
    C extends MachineComponent<T>,
    T
    > extends RecipeModifier<R, C, T> {

  public AdditionRecipeModifier(RequirementType<R, C, T> requirementType, IOType mode, float modifier,
                                float chance, float max, float min) {
    super(requirementType, mode, modifier, chance, max, min);
  }

  @Override
  public float apply(float original) {
    return Mth.clamp(original + this.modifier, this.min, this.max);
  }

  @Override
  public Component getDefaultTooltip() {
    if (requirementType == RequirementTypeRegistration.SPEED.get()
        || requirementType == RequirementTypeRegistration.LOOT_TABLE.get()
        || requirementType == RequirementTypeRegistration.DURABILITY.get()
    )
      return Component.translatable("mmr.recipe.modifier." +  getTargetValue() + "." + getOperation(), modifier);
    return Component.translatable("mmr.recipe.modifier." + getTargetValue() + "." + getOperation(), modifier,
        getMode().getSerializedName(), chance);
  }

  @Override
  public OPERATION getOperation() {
    return OPERATION.ADDITION;
  }
}
