package es.degrassi.mmr.common.util;

import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

/**
 * Permission gates for the operator-only machine tools (the input hatch fill tab).
 *
 * <p>{@link Player#permissions()} answers the authoritative per-profile permission set on the server
 * (levels plus any custom provider grants configured by the server admin) and the synced copy on the
 * client, so one shared check drives both the UI build and the server-side payload validation.
 */
public final class MMRPermissions {

  /**
   * Custom "fill hatch" permission: a server admin can grant it to a non-operator profile through
   * the per-profile permission configuration, granting the fill tab without gamemaster rights.
   */
  public static final Permission FILL_HATCH = Permission.Atom.create("modular_machinery_reborn/fill_hatch");

  private MMRPermissions() {}

  /** Gamemaster operators, ad higher profiles, or profiles granted the custom fill permission. */
  public static boolean canFillHatch(Player player) {
    PermissionSet permissions = player.permissions();
    return permissions.hasPermission(Permissions.COMMANDS_GAMEMASTER)
        || permissions.hasPermission(FILL_HATCH);
  }
}
