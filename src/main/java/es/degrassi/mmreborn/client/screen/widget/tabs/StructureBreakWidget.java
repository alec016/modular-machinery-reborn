package es.degrassi.mmreborn.client.screen.widget.tabs;

import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.popup.ConfirmationPopup;
import es.degrassi.mmreborn.client.screen.widget.ItemOrIconButton;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.network.client.CBreakStructurePacket;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class StructureBreakWidget extends TopTabWidget {
  private final ControllerScreen parentScreen;

  private final ResourceLocation machine;
  private final BlockPos controllerPos;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_breaker_button");
  private final Component tooltipTime, time;

  public StructureBreakWidget(ControllerScreen parentScreen, ResourceLocation machine, BlockPos controllerPos) {
    super(0, 0, new ItemOrIconButton(5, 5, Items.DIAMOND_PICKAXE, button -> {}));
    this.parentScreen = parentScreen;
    this.machine = machine;
    this.controllerPos = controllerPos;
    int positions = parentScreen.getMenu().getEntity().getFoundMachine().getPattern().getBlocks(Direction.NORTH).size();
    this.tooltipTime = Component.translatable("mmr.gui.structure.time.tooltip", MMRConfig.get().delayOperation.get());
    this.time = Component.translatable("mmr.gui.structure.time", Utils.decimalFormat(positions / 20f), positions);
  }

  @Override
  public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);
    guiGraphics.renderTooltip(
        Minecraft.getInstance().font,
        List.of(
            component.getVisualOrderText(),
            tooltipTime.getVisualOrderText()
        ),
        x,
        y
    );
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    parentScreen.setFocused(this);
    parentScreen.getMenu().getEntity().setLastFocus(1);
    parentScreen.openPopup(new ConfirmationPopup<>(
        parentScreen,
        180,
        96,
        () -> PacketDistributor.sendToServer(new CBreakStructurePacket(machine, controllerPos))
    )
        .text(
            Component.translatable("mmr.gui.structure.break"),
            time,
            Component.empty()
        ), "popup");
  }
}
