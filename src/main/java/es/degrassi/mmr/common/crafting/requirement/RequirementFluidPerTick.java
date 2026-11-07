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
import es.degrassi.mmr.common.machine.component.FluidComponent;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackContentsFactory;
import org.jspecify.annotations.Nullable;

@Getter
public class RequirementFluidPerTick implements IRequirement<FluidComponent, FluidHandler> {
  public static final NamedMapCodec<RequirementFluidPerTick> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.of(SizedFluidIngredient.CODEC).fieldOf("fluid").forGetter(req -> req.ingredient),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, (fluid, mode, position) -> new RequirementFluidPerTick(mode, fluid, position)), "FluidRequirement");

  private final PositionedRequirement position;
  private final IOType mode;
  private final SizedFluidIngredient ingredient;
  private final Component uiComponent;

  public RequirementFluidPerTick(IOType ioType, SizedFluidIngredient fluid, PositionedRequirement position) {
    this.ingredient = fluid;
    this.position = position;
    this.mode = ioType;
    var stackTexts = ingredient.ingredient().display().resolve(
            ContextMap.EMPTY,
            FluidStackContentsFactory.INSTANCE
        ).map(FluidStack::getHoverName)
        .toList();
    var text = Component.empty();
    for (int i = 0; i < stackTexts.size(); i++) {
      text.append(stackTexts.get(i));
      if (i != stackTexts.size() - 1) text.append(RequirementTexts.or.text());
    }
    this.uiComponent = RequirementTexts.fluidTick.text(ingredient.amount(), text);
  }

  @Override
  public RequirementType<RequirementFluidPerTick, FluidComponent, FluidHandler> getType() {
    return RequirementTypeRegistration.FLUID_PER_TICK.get();
  }

  @Override
  public ComponentType<FluidHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_FLUID.get();
  }

  @Override
  public boolean test(FluidComponent component, ICraftingContext context) {
    FluidHandler handler = component.getContainerProvider();
    return switch (getMode()) {
      case INPUT -> {
        int amount = (int) context.getPerTickIntegerModifiedValue(this.ingredient.amount(), this);
        yield handler.getIngredientAmount(this.ingredient.ingredient()) >= amount;
      }
      case OUTPUT -> {
        int amount = (int) context.getPerTickIntegerModifiedValue(this.ingredient.amount(), this);
        yield handler.getSpaceForFluid(this.output()) >= amount;
      }
      case NONE -> true;
    };
  }

  private FluidStack output() {
    return this.ingredient
        .ingredient()
        .display()
        .resolve(ContextMap.EMPTY, FluidStackContentsFactory.INSTANCE)
        .toList()
        .getFirst()
        .copyWithAmount(ingredient.amount());
  }

  @Override
  public void gatherRequirements(IRequirementList<FluidComponent> list) {
    switch (getMode()) {
      case INPUT -> list.processEachTick(this::processInput);
      case OUTPUT -> list.processEachTick(this::processOutput);
    }
  }

  private CraftingResult processInput(FluidComponent component, ICraftingContext context) {
    int amount = (int) context.getIntegerModifiedValue(this.ingredient.amount(), this);
    int maxExtract = component.getContainerProvider().getFluidAmount(ingredient.ingredient());

    if (maxExtract >= amount) {
      component.removeFromInputs(this.ingredient.ingredient(), amount);
      return CraftingResult.success();
    }

    return errorInput(amount, component.getContainerProvider().getFluids(),
        component.getContainerProvider().getFluidAmount(ingredient.ingredient()));
  }

  public Component ingredients() {
    return this.ingredient
        .ingredient()
        .display()
        .resolve(ContextMap.EMPTY, FluidStackContentsFactory.INSTANCE)
        .map(FluidStack::getHoverName)
        .collect(Component::empty, (a, b) -> a.append(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.or").append(b)), MutableComponent::append);
  }

  private CraftingResult errorInput(int amount, FluidIngredient found, int amountFound) {
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.fluid.input",
        amount, ingredients(),
        amountFound, String.valueOf(found)
    ));
  }

  private CraftingResult errorOutput(FluidIngredient found) {
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.fluid.output.fluid",
        ingredients(),
        found.toString()
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
    FluidHandler handler = component.getContainerProvider();
    var output = output();
    if (!handler.isEmpty() && !handler.contains(ingredient.ingredient()))
      return errorOutput(handler.getFluids());
    int amount = (int) context.getIntegerModifiedValue(this.ingredient.amount(), this);
    int canFill = Math.clamp(handler.getSpaceForFluid(output), 0, Integer.MAX_VALUE);
    if (canFill >= amount) {
      component.addToOutputs(output.copyWithAmount(amount));
      return CraftingResult.success();
    }
    return errorOutput(canFill, amount);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("fluid", ingredient.toString());
    json.addProperty("amount", ingredient.amount());
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.fluid.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(FluidComponent m, ICraftingContext context) {
    if (getMode().isInput()) {
      if (m.getContainerProvider().isEmpty()) return false;
    } else {
      if (m.getContainerProvider().isEmpty()) return true;
    }
    return m.getContainerProvider().contains(ingredient.ingredient());
  }

  @Override
  public @Nullable Component toControllerUI() {
    return this.uiComponent;
  }
}
