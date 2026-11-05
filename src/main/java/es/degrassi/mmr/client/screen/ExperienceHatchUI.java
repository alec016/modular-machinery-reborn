package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.mmr.api.client.ExperienceButtonType;
import es.degrassi.mmr.client.container.MMRBusSlot;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.gauges.ExperienceBarGauge;
import es.degrassi.mmr.client.screen.widget.tabs.AutoIoTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.AbstractFillTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.FillTabs;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.network.client.CExperienceButtonClickedPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public final class ExperienceHatchUI {

  private ExperienceHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, ExperienceHatchEntity hatch) {
    var tank = hatch.getTank();

    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUIEXPERIENCE, MMRMachineUI.PANEL_WIDTH, 166);

    new AutoIoTab<>(panel, hatch, MMRMachineUI.PANEL_WIDTH, 0, hatch.getMode().isInput());

    if (hatch.getMode().isInput()) {
      FillTabs.fillExperienceTab(panel, holder.player, hatch, AbstractFillTab.LEFT_TAB_X, 0,
          () -> Math.max(0L, hatch.getTank().getCapacityAsLong(0, ExperienceResource.EMPTY)
              - hatch.getTank().getAmountAsLong(0)));
    }

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.experience_hatch"), 8, 6);

    MMRMachineUI.add(panel, new ExperienceBarGauge(
            160,
            () -> tank.getAmountAsLong(0),
            () -> tank.getCapacityAsLong(0, ExperienceResource.EMPTY))
        .lss("width", 160)
        .lss("height", 16), 8, 20);

    MMRMachineUI.inputSlotAt(panel, 80, 47).bind(new MMRBusSlot(hatch.getCapabilityInventory(), 0));
    MMRMachineUI.outputSlotAt(panel, 98, 47).bind(new MMRBusSlot(hatch.getCapabilityInventory(), 1));

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

  private static void addExperienceButton(UIElement panel, ExperienceHatchEntity hatch,
                                          ExperienceButtonType type,
                                          int x, int y) {
    IconButton.iconButton(panel, type.icon(), x, y,
        event -> ClientPacketDistributor.sendToServer(
            new CExperienceButtonClickedPacket(hatch.getBlockPos(), type, type.extract())),
        () -> List.of(type.text()));
  }
}
