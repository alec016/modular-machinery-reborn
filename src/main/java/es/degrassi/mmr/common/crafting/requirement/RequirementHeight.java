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
import es.degrassi.mmr.common.machine.component.HeightComponent;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import es.degrassi.mmr.common.util.IntRange;
import lombok.Getter;
import net.minecraft.network.chat.Component;

public class RequirementHeight implements IRequirement<HeightComponent, IntRange> {
  public static final NamedCodec<RequirementHeight> CODEC = NamedCodec.record(instance -> instance.group(
      IntRange.CODEC.fieldOf("range").forGetter(RequirementHeight::height)
  ).apply(instance, RequirementHeight::new), "Height Requirement");

  private final IntRange height;
  @Getter
  private final PositionedRequirement position;

  public RequirementHeight(IntRange height) {
    this.height = height;
    this.position = new PositionedRequirement(0, 0);
  }

  public IntRange height() {
    return height;
  }

  @Override
  public RequirementType<RequirementHeight, HeightComponent, IntRange> getType() {
    return RequirementTypeRegistration.HEIGHT.get();
  }

  @Override
  public ComponentType<IntRange> getComponentType() {
    return ComponentRegistration.COMPONENT_HEIGHT.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(HeightComponent component, ICraftingContext context) {
    return this.height.contains(context.getMachineTile().getBlockPos().getY());
  }

  @Override
  public void gatherRequirements(IRequirementList<HeightComponent> list) {
    list.worldCondition(this::check);
  }

  private CraftingResult check(HeightComponent component, ICraftingContext context) {
    int height = context.getMachineTile().getBlockPos().getY();
    if (this.height.contains(height))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.height",
        this.height.toFormattedString(),
        height
    ));
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.height");
  }

  @Override
  public boolean isComponentValid(HeightComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.height",
        height().toFormattedString()
    ));
    info.setItemIcon(BlockRegistration.HEIGHT_METER.asItem());
  }
}
