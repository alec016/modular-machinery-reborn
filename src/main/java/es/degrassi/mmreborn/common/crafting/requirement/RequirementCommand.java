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
import es.degrassi.mmreborn.common.machine.component.CommandComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

@Getter
@AllArgsConstructor
public class RequirementCommand implements IRequirement<CommandComponent, Void> {
  public static final NamedCodec<RequirementCommand> CODEC = NamedCodec.record(instance -> instance.group(
      Phase.CODEC.optionalFieldOf("phase", Phase.END).forGetter(RequirementCommand::getPhase),
      NamedCodec.STRING.fieldOf("command").forGetter(RequirementCommand::getCommand),
      NamedCodec.INT.optionalFieldOf("permission_level", 2).forGetter(RequirementCommand::getPermissionLevel),
      NamedCodec.BOOL.optionalFieldOf("log", false).forGetter(RequirementCommand::isLog)
  ).apply(instance, RequirementCommand::new), "Requirement Command");

  private final Phase phase;
  private final String command;
  private final int permissionLevel;
  private final boolean log;

  @Override
  public RequirementType<RequirementCommand, CommandComponent, Void> getType() {
    return RequirementTypeRegistration.COMMAND.get();
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_COMMAND.get();
  }

  @Override
  public IOType getMode() {
    return IOType.NONE;
  }

  @Override
  public boolean test(CommandComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<CommandComponent> list) {
    switch (phase) {
      case START -> list.processOnStart(this::process);
      case END -> list.processOnEnd(this::process);
      case EACH_TICK -> list.processEachTick(this::process);
    }
  }

  private CraftingResult process(CommandComponent component, ICraftingContext context) {
    component.sendCommand(this.command, this.permissionLevel, this.log);
    return CraftingResult.pass();
  }

  @Override
  public PositionedRequirement getPosition() {
    return new PositionedRequirement(0, 0);
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.command");
  }

  @Override
  public boolean isComponentValid(CommandComponent m, ICraftingContext context) {
    return true;
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.setItemIcon(Items.COMMAND_BLOCK);
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.command.info",
        Component.literal(command).withStyle(ChatFormatting.AQUA),
        switch (phase) {
          case START -> "start";
          case END -> "end";
          case EACH_TICK -> "each tick";
        }
    ));
  }

  public enum Phase {
    START,
    EACH_TICK,
    END;

    public static final NamedCodec<Phase> CODEC = NamedCodec.enumCodec(Phase.class);
  }
}
