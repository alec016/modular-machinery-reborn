package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.BiomeReaderEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public final class BiomeReaderUI {
  private BiomeReaderUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, BiomeReaderEntity hatch) {
    UIElement panel = InfoUIs.panel(2);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.biome_reader"));

    InfoUIs.line(panel, 0,
        () -> biomeKey(hatch)
            .map(key -> Component.translatable("biome." + key.getNamespace() + "." + key.getPath()).getString())
            .orElse(null), null);
    InfoUIs.line(panel, 1,
        () -> biomeKey(hatch).map(key -> UITexts.biomeId.text(key.toString()).getString())
            .orElse(null), null);
    return MMRMachineUI.menu(panel, holder.player);
  }

  private static Optional<Identifier> biomeKey(BiomeReaderEntity hatch) {
    var level = hatch.getLevel();
    if (level == null) {
      return Optional.empty();
    }
    return level.getBiome(hatch.getBlockPos())
        .unwrapKey()
        .map(ResourceKey::identifier);
  }
}
