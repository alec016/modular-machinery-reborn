package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.CoreActionType;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import es.degrassi.mmr.common.network.client.CCoreButtonClickedPacked;
import net.minecraft.ChatFormatting;
import com.lowdragmc.lowdraglib2.gui.ui.utils.KeyState;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * LDLib2 replacement for the old {@code ParallelHatchContainer} + {@code ParallelHatchScreen} pair.
 *
 * <p>Parity with 1.21.1: a 176x176 nine-slice panel ({@code sprites/background.png}, the legacy
 * {@code blitSprite(..., 176, 176)}), the vanilla-style title caption at (8, 6), and the {@code
 * GridLayout} row at y = 25: the {@code CoreActionButton} (16x16 icon on the 18x20 toolbar plate)
 * centered in the first cell (x = 16), the live core count spanning the middle columns (x = 50..
 * 126) and the add button centered in the last cell (x = 144). Click behaviour is the old {@code
 * ParallelHatchScreen#action}: shift = ±10, control = jump to max/min, plain click = ±1, sent
 * through the existing {@code CCoreButtonClickedPacked} packet.
 */
public final class ParallelHatchUI {

  private ParallelHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, ParallelHatchEntity entity) {
    UIElement panel = MMRMachineUI.panelNineSlice(
        ModularMachineryReborn.rl("textures/gui/sprites/background.png"),
        MMRMachineUI.PANEL_WIDTH,
        176
    );

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.parallel_hatch"), 8, 6);

    MMRWidgets.iconButton(panel, CoreActionType.REMOVE.icon(), 16, 25,
        event -> action(entity, false, KeyState.isShiftDown(), KeyState.isCtrlDown()),
        () -> legend(CoreActionType.REMOVE));

    var cores = MMRMachineUI.syncedLabel(panel,
        () -> Component.literal(String.format("%s", entity.getCores())), 50, 25);
    cores.lss("width", 76).lss("height", 16);
    cores.textStyle(style -> style
        .textAlignHorizontal(Horizontal.CENTER)
        .textAlignVertical(Vertical.CENTER));

    MMRWidgets.iconButton(panel, CoreActionType.ADD.icon(), 144, 25,
        event -> action(entity, true, KeyState.isShiftDown(), KeyState.isCtrlDown()),
        () -> legend(CoreActionType.ADD));

    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }

  /** 1.21.1 parity of {@code ParallelHatchScreen#action}: shift = 10, control = max/min, default = 1. */
  private static void action(ParallelHatchEntity entity, boolean isAdd, boolean shift, boolean control) {
    int amount = shift ? 10 : control ? -1 : 1;
    int minAmount = 1;
    int maxAmount = entity.getMaxCores();
    int toSet = entity.getCores();
    if (isAdd) {
      toSet = amount == -1 ? maxAmount : Math.min(maxAmount, toSet + amount);
    } else {
      toSet = amount == -1 ? minAmount : Math.max(minAmount, toSet - amount);
    }
    ClientPacketDistributor.sendToServer(new CCoreButtonClickedPacked(entity.getBlockPos(), toSet));
  }

  /**
   * The legacy {@code CoreActionButton} tooltip: the current modifier state first, then the
   * {@code [SHIFT]} and {@code [CTRL]} variants in gray after an aqua key label.
   */
  private static List<Component> legend(CoreActionType type) {
    boolean shift = KeyState.isShiftDown();
    boolean control = KeyState.isCtrlDown();
    List<Component> lines = new ArrayList<>();
    lines.add(type.component(shift, control));
    lines.add(Component.translatable("%s %s",
        Component.translatable("modular_machinery_reborn.controller.shift").withStyle(ChatFormatting.AQUA),
        type.component(true, false).copy().withStyle(ChatFormatting.GRAY)));
    lines.add(Component.translatable("%s %s",
        Component.translatable("modular_machinery_reborn.controller.control").withStyle(ChatFormatting.AQUA),
        type.component(false, true).copy().withStyle(ChatFormatting.GRAY)));
    return lines;
  }
}
