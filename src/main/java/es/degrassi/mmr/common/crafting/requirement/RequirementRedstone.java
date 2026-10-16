package es.degrassi.mmr.common.crafting.requirement;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.RedstoneComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

@Getter
public class RequirementRedstone implements IRequirement<RedstoneComponent, Integer> {
  public static final NamedCodec<RequirementRedstone> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, 15).fieldOf("amount").forGetter(RequirementRedstone::getAmount),
      IOType.CODEC.optionalFieldOf("mode", IOType.INPUT).forGetter(IRequirement::getMode)
  ).apply(instance, RequirementRedstone::new), "Requirement Redstone");

  private final IOType mode;
  private final int amount;

  public RequirementRedstone(int amount, IOType mode) {
    this.amount = amount;
    this.mode = mode;
  }

  @Override
  public RequirementType<RequirementRedstone, RedstoneComponent, Integer> getType() {
    return RequirementTypeRegistration.REDSTONE.get();
  }

  @Override
  public ComponentType<Integer> getComponentType() {
    return ComponentRegistration.COMPONENT_REDSTONE.get();
  }

  @Override
  public boolean test(RedstoneComponent component, ICraftingContext context) {
    if (getMode().isInput()) {
      return component.getContainerProvider() >= this.amount;
    }
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<RedstoneComponent> list) {
    if (getMode().isInput()) {
      list.processEachTick(this::process);
    } else if (getMode().isOutput()) {
      list.processEachTick(
          (component, context) -> {
            component.setOutputAmount(this.amount);
            return CraftingResult.success();
          }
      );
    }
  }

  private CraftingResult process(RedstoneComponent component, ICraftingContext context) {
    return component.getContainerProvider() >= this.amount
        ? CraftingResult.success()
        : CraftingResult.error(Component.translatable("craftcheck.failure.redstone", this.amount, component.getContainerProvider()));
  }

  @Override
  public PositionedRequirement getPosition() {
    return new PositionedRequirement(0, 0);
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.redstone");
  }

  @Override
  public boolean isComponentValid(RedstoneComponent m, ICraftingContext context) {
    return getMode().equals(m.getMode());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.setItemIcon(Items.REDSTONE);
    info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.redstone." + getMode().getSerializedName(), getAmount()));
  }
}
