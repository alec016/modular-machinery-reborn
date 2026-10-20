package es.degrassi.mmr.api.capability.config;

import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.common.network.server.component.SUpdateSideConfigPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import net.neoforged.neoforge.network.PacketDistributor;

public interface ISideConfigComponent<M extends SideConfig.SideMode> extends ControllerAccessible, IBlockEntityExtension {
  SideConfig<M> getConfig();

  default BlockEntity self() {
    return (BlockEntity) this;
  }

  default void configChanged(RelativeSide side, IOSideMode oldMode, IOSideMode newMode) {
    if (oldMode.isDisabled() != newMode.isDisabled()) {
      invalidateCapabilities();
    }
    // Side configs only reach clients through chunk-level block entity syncs by default, so a
    // side flipped while players have GUIs open (auto-IO configurator, wrench, ...) would leave
    // them reading the pre-change modes until a world reload. Every server-side mutation runs
    // through this callback, making it the single valid push point for the fresh modes.
    if (self().getLevel() instanceof ServerLevel serverLevel
        && this.getConfig() instanceof IOSideConfig sideConfig) {
      PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
          ChunkPos.containing(self().getBlockPos()),
          new SUpdateSideConfigPacket(self().getBlockPos(), SUpdateSideConfigPacket.maskOf(sideConfig)));
    }
  }
}
