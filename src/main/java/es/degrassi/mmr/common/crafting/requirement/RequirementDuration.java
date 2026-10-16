package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.DurationComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public class RequirementDuration implements IRequirement<DurationComponent, Void> {
  public static final NamedMapCodec<RequirementDuration> CODEC = NamedCodec.record(instance -> instance.group(
          NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(RequirementDuration::getTime),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, RequirementDuration::new),
      "Duration requirement"
  );
  public final int time;
  private final PositionedRequirement position;

  public RequirementDuration(int time, PositionedRequirement position) {
    this.position = position;
    this.time = time;
  }

  @Override
  public RequirementType<RequirementDuration, DurationComponent, Void> getType() {
    return RequirementTypeRegistration.SPEED.get();
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_DURATION.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(DurationComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<DurationComponent> list) {
    // If the {@link #test(DurationComponent, ICraftingContext)} success then this requirement is OK to proceed
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("time", time);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.duration");
  }

  @Override
  public boolean isComponentValid(DurationComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
