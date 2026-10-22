package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.entity.CommandExecutionerEntity;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CommandComponent extends MachineComponent<Void> {

  private static final CommandSource COMMAND_SOURCE_LOG = new CommandSource() {

    @Override
    public void sendSystemMessage(Component component) {
      ModularMachineryReborn.LOGGER.info(component.getString());
    }

    @Override
    public boolean acceptsSuccess() {return false;}

    @Override
    public boolean acceptsFailure() {return true;}

    @Override
    public boolean shouldInformAdmins() {return true;}
  };

  private static final CommandSource COMMAND_SOURCE_NO_LOG = new CommandSource() {
    @Override
    public void sendSystemMessage(Component component) {
      ModularMachineryReborn.LOGGER.info(component.getString());
    }

    @Override
    public boolean acceptsSuccess() {return false;}

    @Override
    public boolean acceptsFailure() {return true;}

    @Override
    public boolean shouldInformAdmins() {return false;}
  };

  private final CommandExecutionerEntity entity;
  public CommandComponent(CommandExecutionerEntity entity) {
    super(IOType.NONE);
    this.entity = entity;
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_COMMAND.get();
  }

  @Override
  public @Nullable Void getContainerProvider() {
    return new Void(null);
  }

  @Override
  public <C extends MachineComponent<Void>> boolean canMerge(C c) {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Void>> C merge(C c) {
    return (C) this;
  }

  public void sendCommand(String command, int permissionLevel, boolean log) {
    if (entity.getLevel().getServer() == null) return;
    CommandSourceStack source = new CommandSourceStack(
        log ? COMMAND_SOURCE_LOG : COMMAND_SOURCE_NO_LOG,
        Vec3.atCenterOf(entity.getBlockPos()),
        getMachineRotation(),
        (ServerLevel) entity.getLevel(),
        LevelBasedPermissionSet.forLevel(PermissionLevel.byId(permissionLevel)),
        "Modular Machinery Reborn",
        entity.getController().getFoundMachine().getName(),
        entity.getLevel().getServer(),
        null
    );
    entity.getLevel().getServer().getCommands().performPrefixedCommand(source, command);
  }

  private Vec2 getMachineRotation() {
    Direction facing = entity.getController().getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    return new Vec2(0, facing.toYRot() - 180);
  }
}
