package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.TimeComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class RequirementTime implements IRequirement<TimeComponent, IntRange> {
  public static final NamedCodec<RequirementTime> CODEC = NamedCodec.record(instance -> instance.group(
      IntRange.CODEC.fieldOf("range").forGetter(RequirementTime::time)
  ).apply(instance, RequirementTime::new), "Time Requirement");

  private final IntRange time;
  @Getter
  private final PositionedRequirement position;

  public RequirementTime(IntRange time) {
    this.time = time;
    this.position = new PositionedRequirement(0, 0);
  }

  public IntRange time() {
    return time;
  }

  @Override
  public RequirementType<RequirementTime, TimeComponent, IntRange> getType() {
    return RequirementTypeRegistration.TIME.get();
  }

  @Override
  public ComponentType<IntRange> getComponentType() {
    return ComponentRegistration.COMPONENT_TIME.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(TimeComponent component, ICraftingContext context) {
    long time = context.getMachineTile().getLevel().dimensionType().hasFixedTime()
        ? context.getMachineTile().getLevel().getDayTime()
        : context.getMachineTile().getLevel().getDayTime() % 24000L;
    return this.time.contains((int) time);
  }

  @Override
  public void gatherRequirements(IRequirementList<TimeComponent> list) {
    list.worldCondition(this::check);
  }

  private CraftingResult check(TimeComponent component, ICraftingContext context) {
    long time = context.getMachineTile().getLevel().dimensionType().hasFixedTime()
        ? context.getMachineTile().getLevel().getDayTime()
        : context.getMachineTile().getLevel().getDayTime() % 24000L;
    if(this.time.contains((int) time))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.time",
        this.time.toFormattedString(),
        time
    ));
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.time");
  }

  @Override
  public boolean isComponentValid(TimeComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.time",
        time().toFormattedString()
    ));
    info.setItemIcon(Items.CLOCK);
  }
}
