package es.degrassi.mmr.client.screen;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.network.client.CRedstoneButtonModeClickedPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * LDLib2 replacement for the old {@code RedstonePortContainer} + {@code RedstonePortScreen} pair.
 *
 * <p>176x166 nine-slice panel ({@code sprites/background.png}), the vanilla title caption at
 * (8, 6) and the legacy {@code EnumButton} — now centered under the caption instead of hugging the
 * panel's left edge: a {@code StringButton} whose label is the current mode (live, through the
 * entity's {@code StringSyncable}) clicking over to the next mode. The vanilla inventory caption
 * and grid fill the lower half, like the other machine screens. A click keeps the old behaviour — {@code entity.setMode(v)} client prediction plus the
 * real {@code CRedstoneButtonModeClickedPacket} — and re-arms the {@code mmr.gui.tooltip.button.enum.
 * cycle} hover tooltip with the next mode, exactly like the old {@code EnumButton#getTooltips}.
 */
public final class RedstonePortUI {

  private static final int BUTTON_WIDTH = 48;
  /** Centered in the standard panel (user preference; legacy parity put it at the left edge). */
  private static final int BUTTON_X = MMRMachineUI.centeredLeft(BUTTON_WIDTH);
  private static final int BUTTON_Y = 25;

  private RedstonePortUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, RedstonePortEntity entity) {
    UIElement panel = MMRMachineUI.panelNineSlice(
        MMRMachineUI.TEX_BACKGROUND, MMRMachineUI.PANEL_WIDTH, MMRMachineUI.PANEL_HEIGHT);

    MMRMachineUI.captionLabel(panel,
        Component.translatable("modular_machinery_reborn.gui.title.redstone_port"), 8, 6);

    Button modeButton = SpriteButton.spriteButton(
        panel, Component.empty(), BUTTON_X, BUTTON_Y, BUTTON_WIDTH, 20, null);
    // The EnumButton label lives inside the button and follows the synced mode value.
    Label modeText = new Label();
    modeText.bind(DataBindingBuilder.componentS2C(() -> modeName(entity.getMode())).build());
    modeText.layout(layout -> layout.widthPercent(100).heightPercent(100));
    modeText.textStyle(style -> style
        .textAlignHorizontal(Horizontal.CENTER)
        .textAlignVertical(Vertical.CENTER));
    modeButton.addChild(modeText);

    modeButton.setOnClick(event -> {
      IOType next = nextMode(entity.getMode());
      // Legacy prediction: flip the local value right away, then ask the server.
      entity.setMode(next);
      ClientPacketDistributor.sendToServer(new CRedstoneButtonModeClickedPacket(next, entity.getBlockPos()));
    });
    modeButton.addEventListener(com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents.MOUSE_ENTER,
        event -> modeButton.style(style -> style.tooltips(cycleTip(nextMode(entity.getMode())))));

    MMRMachineUI.inventoryLabel(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_LABEL_Y);
    MMRMachineUI.playerInventory(panel, MMRMachineUI.INVENTORY_X, MMRMachineUI.INVENTORY_Y);
    return MMRMachineUI.menu(panel, holder.player);
  }

  /** The legacy {@code EnumButton.getNextValue}: INPUT and OUTPUT cycle. */
  private static IOType nextMode(IOType current) {
    return current.isInput() ? IOType.OUTPUT : IOType.INPUT;
  }

  private static Component modeName(IOType mode) {
    return Component.translatable("mmr.gui.tooltip.redstone.button.mode." + mode.getSerializedName());
  }

  /** The legacy {@code EnumButton#getTooltips}: one line, the next mode's name. */
  private static Component cycleTip(IOType next) {
    return Component.translatable("mmr.gui.tooltip.button.enum.cycle", modeName(next));
  }
}
