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

public class RequirementEnergyPerTick implements IRequirement<EnergyComponent, IEnergyHandler> {
  public static final NamedMapCodec<RequirementEnergyPerTick> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.requirementPerTick),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, (amount, type, position) -> new RequirementEnergyPerTick(type, amount, position)), "EnergyRequirement");
  @Getter
  private final IOType mode;
  @Getter
  private final PositionedRequirement position;
  public final long requirementPerTick;

  public RequirementEnergyPerTick(IOType ioType, long requirementPerTick, PositionedRequirement position) {
    this.requirementPerTick = requirementPerTick;
    this.position = position;
    this.mode = ioType;
  }

  public long getRequiredEnergyPerTick() {
    return requirementPerTick;
  }

  @Override
  public RequirementType<RequirementEnergyPerTick, EnergyComponent, IEnergyHandler> getType() {
    return RequirementTypeRegistration.ENERGY_PER_TICK.get();
  }

  @Override
  public ComponentType<IEnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public boolean test(EnergyComponent component, ICraftingContext context) {
    IEnergyHandler handler = component.getContainerProvider();
    return switch (mode) {
      case INPUT -> handler.getCurrentEnergy() >= requirementPerTick;
      case OUTPUT -> handler.getMaxEnergy() >= handler.getCurrentEnergy() + requirementPerTick;
      case NONE -> true;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<EnergyComponent> list) {
    if (mode.isInput()) {
      list.processEachTick(this::processInputs);
    } else {
      list.processEachTick(this::processOutputs);
    }
  }

  private CraftingResult processInputs(EnergyComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.requirementPerTick, this);
    var handler = component.getContainerProvider();
    var tempExtract = handler.canExtract();
    handler.setCanExtract(true);
    int canExtract = handler.extractEnergy(amount, true);
    if(canExtract == amount) {
      handler.extractEnergy(amount, false);
      handler.setCanExtract(tempExtract);
      return CraftingResult.success();
    }
    handler.setCanExtract(tempExtract);
    return CraftingResult.error(Component.translatable("craftcheck.failure.energy.input", requirementPerTick, canExtract));
  }

  private CraftingResult processOutputs(EnergyComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.requirementPerTick, this);
    var handler = component.getContainerProvider();
    var tempInsert = handler.canReceive();
    handler.setCanInsert(true);
    int canReceive = handler.receiveEnergy(amount, true);
    if(canReceive == amount) {
      handler.receiveEnergy(amount, false);
      handler.setCanInsert(tempInsert);
      return CraftingResult.success();
    }
    handler.setCanInsert(tempInsert);
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.energy.output", requirementPerTick, component.getContainerProvider().getRemainingCapacity()
    ));
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("actionType", mode.name());
    json.addProperty("amount", requirementPerTick);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.energy.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(EnergyComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
