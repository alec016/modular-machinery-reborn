package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.manager.handler.SimpleEnergyHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class RequirementEnergy implements IRequirement<EnergyComponent, SimpleEnergyHandler> {
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
  public RequirementType<RequirementEnergy, EnergyComponent, SimpleEnergyHandler> getType() {
    return RequirementTypeRegistration.ENERGY.get();
  }

  @Override
  public ComponentType<SimpleEnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public boolean test(EnergyComponent component, ICraftingContext context) {
    SimpleEnergyHandler handler = component.getContainerProvider();
    return switch (mode) {
      case INPUT -> handler.getAmountAsLong() >= requirement;
      case OUTPUT -> handler.getCapacityAsLong() >= handler.getAmountAsLong() + requirement;
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
    try (var root = Transaction.open(null)) {
      int amount = (int) context.getIntegerModifiedValue(this.requirement, this);
      var handler = component.getContainerProvider();
      var tempMaxExtract = handler.getMaxExtract();
      handler.setMaxExtract(Long.MAX_VALUE);
      try (Transaction sub = Transaction.open(root)) {
        int canExtract = handler.extract(amount, sub);
        if (canExtract >= amount) {
          sub.commit();
          root.commit();
          handler.setMaxExtract(tempMaxExtract);
          return CraftingResult.success();
        }
        root.commit();
        handler.setMaxExtract(tempMaxExtract);
        return CraftingResult.error(Component.translatable("craftcheck.failure.energy.input", requirement, canExtract));
      }
    }
  }

  private CraftingResult processOutputs(EnergyComponent component, ICraftingContext context) {
    try (var root = Transaction.open(null)) {
      int amount = (int) context.getIntegerModifiedValue(this.requirement, this);
      var handler = component.getContainerProvider();
      var tempMaxInsert = handler.getMaxInsert();
      handler.setMaxInsert(Long.MAX_VALUE);
      try (var sub = Transaction.open(root)) {
        int canReceive = handler.insert(amount, sub);
        if (canReceive >= amount) {
          sub.commit();
          root.commit();
          handler.setMaxInsert(tempMaxInsert);
          return CraftingResult.success();
        }
        root.commit();
        handler.setMaxInsert(tempMaxInsert);
        return CraftingResult.error(Component.translatable(
            "craftcheck.failure.energy.output", requirement, handler.getCapacityAsLong() - handler.getAmountAsLong()
        ));
      }
    }
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

  @Override
  public @Nullable Component toControllerUI() {
    var amount = Component.empty();
    amount.append(Component.literal(NumberFormats.compact(requirement)))
        .append(" (")
        .append(Component.literal(NumberFormats.grouped(requirement)))
        .append(") ");
    return RequirementTexts.energy.text(amount, EnergyDisplayUtil.type.text());
  }
}
