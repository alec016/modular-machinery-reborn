package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.gauges.ExperienceBarGauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.mmr.api.client.ExperienceButtonType;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.network.client.CExperienceButtonClickedPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * LDLib2 replacement for the old {@code ExperienceHatchContainer} + {@code ExperienceHatchScreen} pair.
 *
 * <p>Parity with 1.21.1: 176x166 {@code guiexperience.png} panel, the 160x16 level bar at (8, 20)
 * (the legacy {@code ExperienceWidget}: level figure over a 3px black track with the green progress
 * line), the capability slot at (80, 47) and the {@code ItemOrIconButton} rows at y = 45 — insert
 * side from x = 8 stepping right by 18, extract side from x = 151 stepping left by 18. Each button
 * fires the real {@code CExperienceButtonClickedPacket} like the old screen did.
 */
public final class ExperienceHatchUI {

  private ExperienceHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, ExperienceHatchEntity hatch) {
    var tank = hatch.getTank();

    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUIEXPERIENCE, MMRMachineUI.PANEL_WIDTH, 166);

    // Legacy right tab: the GTCEu-style 3D auto-IO configurator for this hatch's io orientation.
    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    // Operator-only fill tab, hanging left: pours XP points straight into the tank (input hatches).
    if (hatch.getMode().isInput()) {
      FillTab.fillExperienceTab(panel, holder.player, hatch, FillTab.LEFT_TAB_X, 0,
          () -> (int) Math.max(0L, Math.min(hatch.getTank().getCapacityAsLong(0, ExperienceResource.EMPTY)
              - hatch.getTank().getAmountAsLong(0), Integer.MAX_VALUE)));
    }

    // Vanilla-style title above the legacy bar: the (8, 6) caption spot the old screen left blank.
    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.experience_hatch"), 8, 6);

    // The legacy ExperienceWidget spot: (leftPos + 8, topPos + 20), width = imageWidth - 16.
    MMRMachineUI.add(panel, new ExperienceBarGauge(
            160,
            () -> tank.getAmountAsLong(0),
            () -> tank.getCapacityAsLong(0, ExperienceResource.EMPTY))
        .lss("width", 160)
        .lss("height", 16), 8, 20);

    // The legacy container slot position: (176 / 2 - 8, 47).
    MMRMachineUI.slotAt(panel, 80, 47).bind(hatch.getCapabilityInventory(), 0);

    if (hatch instanceof ExperienceInputHatchEntity input) {
      int x = 8;
      for (var type : ExperienceButtonType.insertions()) {
        addExperienceButton(panel, input, type, x, 45);
        x += 18;
      }
    } else if (hatch instanceof ExperienceOutputHatchEntity output) {
      int x = MMRMachineUI.PANEL_WIDTH - 8 - 17;
      for (var type : List.copyOf(ExperienceButtonType.extractions()).reversed()) {
        addExperienceButton(panel, output, type, x, 45);
        x -= 18;
      }
    }

    MMRMachineUI.inventoryLabel(panel, 8, 72);
    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }

  /**
   * One legacy {@code ExperienceButton}: an {@code ItemOrIconButton} (16x16 icon on the 18x20
   * toolbar plate) firing the old {@code CExperienceButtonClickedPacket(pos, type, extract)}.
   */
  private static void addExperienceButton(UIElement panel, ExperienceHatchEntity hatch,
                                          ExperienceButtonType type,
                                          int x, int y) {
    IconButton.iconButton(panel, type.icon(), x, y,
        event -> ClientPacketDistributor.sendToServer(
            new CExperienceButtonClickedPacket(hatch.getBlockPos(), type, type.extract())),
        () -> List.of(type.component()));
  }
}
