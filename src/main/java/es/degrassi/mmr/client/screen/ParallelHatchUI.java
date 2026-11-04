package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import com.lowdragmc.lowdraglib2.gui.ui.utils.KeyState;
import es.degrassi.mmr.api.client.CoreActionType;
import es.degrassi.mmr.api.client.CoreActionTypeTexts;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import es.degrassi.mmr.common.network.client.CCoreButtonClickedPacked;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class ParallelHatchUI {

  private ParallelHatchUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, ParallelHatchEntity entity) {
    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT);

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.parallel_hatch"), 8, 6);

    int rowY = 33;
    IconButton.iconButton(panel, CoreActionType.REMOVE.icon(), 15, rowY,
        event -> action(entity, false, KeyState.isShiftDown(), KeyState.isCtrlDown()),
        () -> legend(CoreActionType.REMOVE));

    var cores = MMRMachineUI.syncedLabel(panel,
        () -> Component.literal(String.format("%s", entity.getCores())), 50, rowY + 1);
    cores.lss("width", 76).lss("height", 16);
    cores.textStyle(style -> style
        .textAlignHorizontal(Horizontal.CENTER)
        .textAlignVertical(Vertical.CENTER));

    IconButton.iconButton(panel, CoreActionType.ADD.icon(), 143, rowY,
        event -> action(entity, true, KeyState.isShiftDown(), KeyState.isCtrlDown()),
        () -> legend(CoreActionType.ADD));

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y);
    return MMRMachineUI.menu(panel, holder.player);
  }

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

  private static List<Component> legend(CoreActionType action) {
    var type = CoreActionTypeTexts.by(action);
    boolean shift = KeyState.isShiftDown();
    boolean control = KeyState.isCtrlDown();
    List<Component> lines = new ArrayList<>();
    lines.add(type.text(type.variant(shift, control)));
    lines.add(Component.translatable("%s %s",
        Component.translatable("modular_machinery_reborn.controller.shift").withStyle(ChatFormatting.AQUA),
        type.text(type.variant(true, false)).withStyle(ChatFormatting.GRAY)));
    lines.add(Component.translatable("%s %s",
        Component.translatable("modular_machinery_reborn.controller.control").withStyle(ChatFormatting.AQUA),
        type.text(type.variant(false, true)).withStyle(ChatFormatting.GRAY)));
    return lines;
  }
}
