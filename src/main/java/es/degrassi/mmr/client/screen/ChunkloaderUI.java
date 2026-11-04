package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

public final class ChunkloaderUI {
  private ChunkloaderUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, ChunkloaderEntity hatch) {
    UIElement panel = InfoUIs.panel(3);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.chunkloader"));

    InfoUIs.line(panel, 0,
        () -> UITexts.radiusChunks.text(hatch.getChunkloader().getRadius()).getString(), null);
    InfoUIs.line(panel, 1,
        () -> hatch.getChunkloader().isActive() && !hatch.isUnloaded()
            ? UITexts.chunkActive.text().getString()
            : UITexts.chunkInactive.text().getString(), null);
    InfoUIs.line(panel, 2,
        () -> {
          var chunkloader = hatch.getChunkloader();
          if (!chunkloader.isActive() || chunkloader.getTempo() < 0) {
            return null;
          }
          return UITexts.chunkTempo.text(chunkloader.getTempo()).getString();
        }, null);

    InfoUIs.areaButton(panel, hatch.getBlockPos(), () -> {
      var chunk = ChunkPos.containing(hatch.getBlockPos());
      int extent = hatch.getChunkloader().getRadius() + 1;
      var level = hatch.getLevel();
      int minY = level != null ? level.getMinY() : -64;
      int maxY = level != null ? level.getMaxY() + 1 : 320;
      return new AABB(
          (chunk.x() - extent) << 4, minY, (chunk.z() - extent) << 4,
          (chunk.x() + extent + 1) << 4, maxY, (chunk.z() + extent + 1) << 4);
    });
    return MMRMachineUI.menu(panel, holder.player);
  }
}
