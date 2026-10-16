package es.degrassi.mmr.common.crafting.requirement;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.codec.RegistrarCodec;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.EmptyComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import es.degrassi.mmr.common.util.EmptyRequirementType;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public class RequirementEmpty implements IRequirement<EmptyComponent, Void> {
  public static final NamedCodec<RequirementEmpty> CODEC = NamedCodec.record(instance -> instance.group(
          RegistrarCodec.EMPTY_REQUIREMENT_TYPE.fieldOf("empty_type").forGetter(RequirementEmpty::getRequirementType),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, RequirementEmpty::new),
      "RequirementEmpty");

  private final EmptyRequirementType requirementType;
  private final PositionedRequirement position;

  public RequirementEmpty(EmptyRequirementType requirementType, PositionedRequirement position) {
    this.requirementType = requirementType;
    this.position = position;
  }

  @Override
  public RequirementType<RequirementEmpty, EmptyComponent, Void> getType() {
    return RequirementTypeRegistration.EMPTY.get();
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_EMPTY.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(EmptyComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<EmptyComponent> list) {

  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.empty();
  }

  @Override
  public boolean isComponentValid(EmptyComponent m, ICraftingContext context) {
    return true;
  }
}
