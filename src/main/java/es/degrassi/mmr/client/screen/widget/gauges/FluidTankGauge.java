package es.degrassi.mmr.client.screen.widget.gauges;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.ArrayList;
import java.util.List;

public final class FluidTankGauge extends Gauge {

  private final FluidHandler tank;
  private final int index;

  public FluidTankGauge(FluidHandler tank, int index) {
    this.tank = tank;
    this.index = index;
  }

  @Override
  protected List<Component> tooltipLines() {
    List<Component> text = new ArrayList<>();
    FluidStack content = tank.getStack(index);
    long capacity = tank.getCapacityAsLong(index, FluidResource.EMPTY);
    int amount;
    if (content.getAmount() <= 0) {
      text.add(GaugesTexts.fluidHatchEmpty.text());
      amount = 0;
    } else {
      text.add(content.getHoverName());
      amount = content.getAmount();
    }
    // Above a thousand the amount is shown in buckets, so the number stays readable.
    text.add(GaugesTexts.fluidHatchTankBuckets.text(
        NumberFormats.fluidAmount(amount), NumberFormats.fluidAmount(capacity)));
    text.add(GaugesTexts.fluidHatchTank.text(
        NumberFormats.grouped(amount), NumberFormats.grouped(capacity)));
    return text;
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    GUIContext gui = clientContext(context);
    if (gui == null) {
      return;
    }
    gui.drawTexture(MMRMachineUI.sprite(MMRMachineUI.TEX_TANK_BACKING, 0, 0,
            TANK_BACKING_WIDTH, TANK_BACKING_HEIGHT),
        (int) getContentX() - 1, (int) getContentY() - 1, TANK_BACKING_WIDTH, TANK_BACKING_HEIGHT);
    FluidStack fluid = tank.getStack(index);
    long capacity = tank.getCapacityAsLong(index, FluidResource.EMPTY);
    if (fluid.getAmount() <= 0 || capacity <= 0) {
      return;
    }
    int scaledAmount = (int) (fluid.getAmount() * TANK_HEIGHT / capacity);
    scaledAmount = Mth.clamp(scaledAmount, 1, TANK_HEIGHT);
    DrawerHelperClient.drawFluidForGui(gui, fluid,
        getContentX(), getContentY() + TANK_HEIGHT - scaledAmount, TANK_WIDTH, scaledAmount, -1);
  }
}
