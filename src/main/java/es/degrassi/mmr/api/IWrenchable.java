package es.degrassi.mmr.api;

import es.degrassi.mmr.api.capability.config.RelativeSide;
import net.minecraft.world.entity.player.Player;

public interface IWrenchable {
  /**
   * Called when clicked on a block face
   * @param side side clicked
   */
  Result onWrenched(RelativeSide side, Player player);

  default boolean shouldAddRender() {
    return true;
  }

  enum Result{
    SUCCESS,
    FAIL,
    NONE
  }
}
