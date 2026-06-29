package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EmptyComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.EmptyRequirementType;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

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
