package es.degrassi.mmreborn.common.crafting.requirement.entity;

import es.degrassi.mmreborn.api.capability.EntityHandler;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EntityComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Getter
public abstract class RequirementEntity implements IRequirement<EntityComponent, EntityHandler> {
  private final IOType mode;
  private final Action action;
  private final int amount;
  private final int radius;

  public RequirementEntity(Action action, int amount, int radius) {
    this(action.validMode, action, amount, radius);
  }

  protected RequirementEntity(IOType mode, Action action, int amount, int radius) {
    this.mode = mode;
    this.action = action;
    this.amount = amount;
    this.radius = radius;
  }

  @Override
  public ComponentType<EntityHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENTITY.get();
  }

  @Override
  public PositionedRequirement getPosition() {
    return new PositionedRequirement(0, 0);
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return action.missingComponent();
  }

  @Override
  public boolean isComponentValid(EntityComponent m, ICraftingContext context) {
    return this.mode.equals(m.getIOType()) && m.isValidAction(action);
  }

  @Override
  @MustBeInvokedByOverriders
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.entity." + this.action.toString().toLowerCase(Locale.ENGLISH) + ".info", this.amount, this.radius));
  }

  public enum Action {
    CHECK_AMOUNT(IOType.NONE, Component.translatable("component.missing.entity.detector")),
    CHECK_HEALTH(IOType.NONE, Component.translatable("component.missing.entity.detector")),
    SPAWN(IOType.OUTPUT, Component.translatable("component.missing.entity.spawner")),
    ADD_HEALTH(IOType.OUTPUT, Component.translatable("component.missing.entity.healer")),
    CONSUME_HEALTH(IOType.INPUT, Component.translatable("component.missing.entity.damager")),
    KILL(IOType.INPUT, Component.translatable("component.missing.entity.killer"));

    private final IOType validMode;
    private final Component message;

    Action(IOType mode, Component message) {
      this.validMode = mode;
      this.message = message;
    }

    public Component missingComponent() {
      return this.message;
    }

    public IOType getMode() {
      return validMode;
    }

    public static final NamedCodec<Action> CODEC = NamedCodec.enumCodec(Action.class);

    public static Action value(String action) {
      return valueOf(action.toUpperCase(Locale.ENGLISH));
    }
  }
}
