package es.degrassi.mmr.common.util;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.resources.Identifier;

/**
 * The appearance of the controller of a multiblock, one overlay per state of the
 * machine.
 *
 * <p>The overlay of the controller is the picture of its terminal: the screen with the
 * lines of data, and the little lamp of the console. The running machine shows the
 * animated overlay, the other states show a still picture with the lamp of the colour
 * of the state.
 */
public final class ControllerOverlays {
  private ControllerOverlays() {
  }

  /**
   * The overlay of the controller for a state of the machine.
   */
  public static Identifier overlay(MachineStatus status) {
    return switch (status) {
      case MISSING_STRUCTURE -> ModularMachineryReborn.rl("block/overlay_controller_off");
      case IDLE -> ModularMachineryReborn.rl("block/overlay_controller_idle");
      case RUNNING -> ModularMachineryReborn.rl("block/overlay_controller");
      case ERRORED -> ModularMachineryReborn.rl("block/overlay_controller_error");
      case PAUSED -> ModularMachineryReborn.rl("block/overlay_controller_paused");
    };
  }

  /**
   * The name of the default model of the controller for a state of the machine.
   */
  public static String modelName(MachineStatus status) {
    return switch (status) {
      case MISSING_STRUCTURE -> "controller_off";
      case IDLE -> "controller_idle";
      case RUNNING -> "controller";
      case ERRORED -> "controller_error";
      case PAUSED -> "controller_paused";
    };
  }

  /**
   * The identifier of the default model of the controller for a state of the machine.
   */
  public static Identifier model(MachineStatus status) {
    return ModularMachineryReborn.rl("default/" + modelName(status));
  }
}
