package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EnergyComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class RequirementEnergy implements IRequirement<EnergyComponent, IEnergyHandler> {
  public static final NamedMapCodec<RequirementEnergy> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.requirement),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, (amount, type, position) -> new RequirementEnergy(type, amount, position)), "EnergyRequirement");
  @Getter
  private final IOType mode;
  @Getter
  private final PositionedRequirement position;
  public final long requirement;

  public RequirementEnergy(IOType ioType, long requirement, PositionedRequirement position) {
    this.requirement = requirement;
    this.position = position;
    this.mode = ioType;
  }

  public long getRequiredEnergy() {
    return requirement;
  }

  @Override
  public RequirementType<RequirementEnergy, EnergyComponent, IEnergyHandler> getType() {
    return RequirementTypeRegistration.ENERGY.get();
  }

  @Override
  public ComponentType<IEnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public boolean test(EnergyComponent component, ICraftingContext context) {
    IEnergyHandler handler = component.getContainerProvider();
    return switch (mode) {
      case INPUT -> handler.getCurrentEnergy() >= requirement;
      case OUTPUT -> handler.getMaxEnergy() >= handler.getCurrentEnergy() + requirement;
      case NONE -> true;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<EnergyComponent> list) {
    if (mode.isInput()) {
      list.processOnStart(this::processInputs);
    } else {
      list.processOnEnd(this::processOutputs);
    }
  }

  private CraftingResult processInputs(EnergyComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.requirement, this);
    var handler = component.getContainerProvider();
    var tempExtract = handler.canExtract();
    handler.setCanExtract(true);
    int canExtract = handler.extractEnergy(amount, true);
    if(canExtract >= amount) {
      handler.extractEnergy(amount, false);
      handler.setCanExtract(tempExtract);
      return CraftingResult.success();
    }
    handler.setCanExtract(tempExtract);
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.energy.input", requirement, component.getContainerProvider().getCurrentEnergy()
    ));
  }

  private CraftingResult processOutputs(EnergyComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.requirement, this);
    var handler = component.getContainerProvider();
    var tempInsert = handler.canReceive();
    handler.setCanInsert(true);
    int canReceive = handler.receiveEnergy(amount, true);
    if(canReceive >= amount) {
      handler.receiveEnergy(amount, false);
      handler.setCanInsert(tempInsert);
      return CraftingResult.success();
    }
    handler.setCanInsert(tempInsert);
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.energy.output", requirement, component.getContainerProvider().getRemainingCapacity()
    ));
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("actionType", mode.name());
    json.addProperty("amount", requirement);
    return json;
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.energy.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(EnergyComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
