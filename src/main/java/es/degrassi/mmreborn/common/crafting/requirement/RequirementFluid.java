package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.FluidIngredient;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.FluidComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RequirementFluid implements IRequirement<FluidComponent> {
  public static final NamedMapCodec<RequirementFluid> CODEC = NamedCodec.record(instance -> instance.group(
      FluidIngredient.CODEC.fieldOf("fluid").forGetter(req -> req.ingredient),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
      NamedCodec.INT.optionalFieldOf("amount").forGetter(req -> Optional.of(req.amount)),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, (fluid, mode, amount, position) -> new RequirementFluid(mode, fluid, amount.orElse(1000), position)), "FluidRequirement");

  @Getter
  private final PositionedRequirement position;
  @Getter
  private final IOType mode;
  public final HybridFluid required;
  public final int amount;
  private final FluidIngredient ingredient;

  public RequirementFluid(IOType ioType, FluidIngredient fluid, int amount, PositionedRequirement position) {
    this.ingredient = fluid;
    this.required = new HybridFluid(new FluidStack(fluid.getAll().getFirst(), amount));
    this.amount = amount;
    this.position = position;
    this.mode = ioType;
  }

  @Override
  public RequirementType<RequirementFluid> getType() {
    return RequirementTypeRegistration.FLUID.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_FLUID.get();
  }

  @Override
  public boolean test(FluidComponent component, ICraftingContext context) {
    HybridTank handler = component.getContainerProvider();
    return switch (getMode()) {
      case INPUT -> {
        FluidStack drained = handler.drain(this.required.copy().asFluidStack(), IFluidHandler.FluidAction.SIMULATE);
        yield drained.getAmount() == required.getAmount();
      }
      case OUTPUT -> {
        int amount = (int) context.getIntegerModifiedValue(this.amount, this);
        int filled = component.getContainerProvider().fill(required.asFluidStack().copyWithAmount(amount), IFluidHandler.FluidAction.SIMULATE);
        yield filled == amount;
      }
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<FluidComponent> list) {
    switch (getMode()) {
      case INPUT -> list.processOnStart(this::processInput);
      case OUTPUT -> list.processOnEnd(this::processOutput);
    }
  }

  private CraftingResult processInput(FluidComponent component, ICraftingContext context) {
    int amount = (int) context.getIntegerModifiedValue(this.amount, this);
    if (!required.asFluidStack().is(component.getContainerProvider().getFluid().getFluid()))
      errorInput(amount, component.getContainerProvider().getFluid(),
          component.getContainerProvider().getFluidAmount());
    int toDrain = amount;
    FluidStack fluid = required.asFluidStack().copyWithAmount(toDrain);
    int canDrain = component.getContainerProvider().getFluidAmount();
    if (canDrain > 0) {
      canDrain = Math.min(canDrain, toDrain);
      component.getContainerProvider().drain(fluid.copyWithAmount(canDrain), IFluidHandler.FluidAction.EXECUTE);
      toDrain -= canDrain;
      if (toDrain == 0)
        return CraftingResult.success();
    }
    return errorInput(amount, component.getContainerProvider().getFluid(), component.getContainerProvider().getFluidAmount());
  }

  private CraftingResult errorInput(int amount, FluidStack found, int amountFound) {
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.fluid.input",
        amount, required.asFluidStack().getHoverName(),
        amountFound, found.getHoverName()
    ));
  }

  private CraftingResult errorOutput(FluidStack found) {
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.fluid.output.fluid",
        required.asFluidStack().getHoverName(),
        found.getHoverName()
    ));
  }

  private CraftingResult errorOutput(int amount, int requiredSpace) {
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.fluid.output.space",
        requiredSpace,
        amount
    ));
  }

  private CraftingResult processOutput(FluidComponent component, ICraftingContext context) {
    HybridTank handler = component.getContainerProvider();
    if (!handler.isEmpty() && !handler.getFluid().is(required.asFluidStack().getFluid()))
      return errorOutput(handler.getFluid());
    int amount = (int) context.getIntegerModifiedValue(this.amount, this);
    int canFill = handler.getSpace();
    if (canFill >= amount) {
      handler.fill(required.asFluidStack().copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE);
      return CraftingResult.success();
    }
    return errorOutput(canFill, amount);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("fluid", required.asFluidStack().getHoverName().getString());
    json.addProperty("amount", required.asFluidStack().getAmount());
    return json;
  }

  @Override
  public RequirementFluid deepCopyModified(List<RecipeModifier> modifiers) {
    int amount = Math.round(RecipeModifier.applyModifiers(modifiers, new RecipeRequirement<>(this), this.amount, false));
    return new RequirementFluid(this.getMode(), new FluidIngredient(required.asFluidStack().getFluid()), amount, getPosition());
  }

  @Override
  public RequirementFluid deepCopy() {
    return new RequirementFluid(this.getMode(), new FluidIngredient(required.asFluidStack().getFluid()), amount, getPosition());
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.fluid.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(FluidComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
