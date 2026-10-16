package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class RequirementEnergyPerTick implements IRequirement<EnergyComponent, EnergyHandler> {
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
  public RequirementType<RequirementEnergyPerTick, EnergyComponent, EnergyHandler> getType() {
    return RequirementTypeRegistration.ENERGY_PER_TICK.get();
  }

  @Override
  public ComponentType<EnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public boolean test(EnergyComponent component, ICraftingContext context) {
    EnergyHandler handler = component.getContainerProvider();
    assert handler != null;
    return switch (mode) {
      case INPUT -> handler.getAmountAsLong() >= requirementPerTick;
      case OUTPUT -> handler.getCapacityAsLong() >= handler.getAmountAsLong() + requirementPerTick;
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
    try (var root = Transaction.open(null)) {
      int amount = (int) context.getPerTickIntegerModifiedValue(this.requirementPerTick, this);
      var handler = component.getContainerProvider();
      try (var sub = Transaction.open(root)) {
        int canExtract = handler.extract(amount, sub);
        if (canExtract == amount) {
          sub.commit();
          root.commit();
          return CraftingResult.success();
        }
        return CraftingResult.error(Component.translatable("craftcheck.failure.energy.input", requirementPerTick, canExtract));
      }
    }
  }

  private CraftingResult processOutputs(EnergyComponent component, ICraftingContext context) {
    try (var root = Transaction.open(null)) {
      int amount = (int) context.getPerTickIntegerModifiedValue(this.requirementPerTick, this);
      var handler = component.getContainerProvider();
      try (var sub = Transaction.open(root)) {
        int canReceive = handler.insert(amount, sub);
        if (canReceive == amount) {
          sub.commit();
          root.commit();
          return CraftingResult.success();
        }
        root.commit();
        return CraftingResult.error(Component.translatable(
            "craftcheck.failure.energy.output", requirementPerTick, handler.getCapacityAsLong() - handler.getAmountAsLong()
        ));
      }
    }
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
