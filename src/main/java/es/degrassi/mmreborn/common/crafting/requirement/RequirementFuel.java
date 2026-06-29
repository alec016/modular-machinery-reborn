package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.capability.IFuelHandler;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.helper.FuelData;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.FuelComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;

public class RequirementFuel implements IRequirement<FuelComponent, IFuelHandler> {
  public static final NamedMapCodec<RequirementFuel> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.required),
      FuelData.CODEC.optionalFieldOf("fuel_data", FuelData.DEFAULT_FUEL).forGetter(RequirementFuel::displayData)
  ).apply(instance, RequirementFuel::new), "FuelRequirement");

  @Getter
  private final PositionedRequirement position;
  private final FuelData fuelData;
  public final long required;

  public RequirementFuel(long amount, FuelData fuelData) {
    this.required = amount;
    this.fuelData = fuelData;
    this.position = fuelData.position();
  }

  @Override
  public ComponentType<IFuelHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_FUEL.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public RequirementType<RequirementFuel, FuelComponent, IFuelHandler> getType() {
    return RequirementTypeRegistration.FUEL.get();
  }

  @Override
  public boolean test(FuelComponent component, ICraftingContext context) {
    long amount = context.getIntegerModifiedValue(this.required, this);
    return component.canStartRecipe(amount);
  }

  @Override
  public void gatherRequirements(IRequirementList<FuelComponent> list) {
    list.processEachTick(this::processTick);
  }

  private CraftingResult processTick(FuelComponent component, ICraftingContext context) {
    long amount = context.getIntegerModifiedValue(this.required, this);
    if (component.getContainerProvider().burn(amount))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable("craftcheck.failure.fuel", amount, component.getContainerProvider().getFuel()));
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("amount", required);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.fuel");
  }

  @Override
  public boolean isComponentValid(FuelComponent m, ICraftingContext context) {
    return true;
  }

  public FuelData displayData() {
    return fuelData;
  }
}
