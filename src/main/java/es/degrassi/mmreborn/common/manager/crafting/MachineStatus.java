package es.degrassi.mmreborn.common.manager.crafting;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * An enum of all possible states the machine can be. The machine status is hold by the CraftingManager attached to the
 * MachineTile and is synced automatically when changed.
 */
public enum MachineStatus implements StringRepresentable {
  /**
   * The machine is searching for a valid multiblock structure.
   */
  MISSING_STRUCTURE,
  /**
   * The machine search for a recipe it can process.
   */
  IDLE,
  /**
   * The machine is processing a recipe.
   */
  RUNNING,
  /**
   * The machine encountered an error when processing a recipe, usually either a missing input or not space for output.
   */
  ERRORED,
  /**
   * The machine is paused.
   */
  PAUSED;

  public static final NamedCodec<MachineStatus> CODEC = NamedCodec.enumCodec(MachineStatus.class);

  public boolean isMissingStructure() {
    return this == MISSING_STRUCTURE;
  }

  public boolean isIdle() {
    return this == IDLE;
  }

  public boolean isErrored() {
    return this == ERRORED;
  }

  public boolean isCrafting() {
    return this == RUNNING;
  }

  public boolean isPaused() {
    return this == PAUSED;
  }

  public static MachineStatus value(String string) {
    return valueOf(string.toUpperCase(Locale.ENGLISH));
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase(Locale.ENGLISH);
  }

  @NotNull
  @Override
  public String getSerializedName() {
    return toString();
  }

  public Component getUnlocalizedDescription() {
    return Component.translatable("gui.controller.status." + getSerializedName());
  }
}
