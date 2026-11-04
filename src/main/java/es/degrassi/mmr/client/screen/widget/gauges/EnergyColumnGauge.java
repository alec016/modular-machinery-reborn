package es.degrassi.mmr.client.screen.widget.gauges;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.LongSupplier;

public final class EnergyColumnGauge extends Gauge {

  private final LongSupplier current;
  private final LongSupplier max;

  public EnergyColumnGauge(LongSupplier current, LongSupplier max) {
    this.current = current;
    this.max = max;
  }

  @Override
  protected List<Component> tooltipLines() {
    long currentEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(current.getAsLong());
    long maxEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(max.getAsLong());
    return List.of(
        GaugesTexts.energyHatchCharge.text(
            NumberFormats.compact(currentEnergy),
            NumberFormats.compact(maxEnergy),
            EnergyDisplayUtil.type.text()
        ),
        GaugesTexts.energyHatchCharge.text(
            NumberFormats.grouped(currentEnergy),
            NumberFormats.grouped(maxEnergy),
            EnergyDisplayUtil.type.text()
        )
    );
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    GUIContext gui = clientContext(context);
    if (gui == null) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    gui.drawTexture(MMRMachineUI.sprite(MMRMachineUI.TEX_TANK_BACKING, 0, 0,
            TANK_BACKING_WIDTH, TANK_BACKING_HEIGHT),
        x - 1, y - 1, TANK_BACKING_WIDTH, TANK_BACKING_HEIGHT);
    float percFilled = max.getAsLong() <= 0 ? 0f
        : Mth.clamp(current.getAsLong() / (float) max.getAsLong(), 0f, 1f);
    int pxFilled = Mth.ceil(percFilled * TANK_HEIGHT);
    if (pxFilled <= 0) {
      return;
    }
    pxFilled = Math.min(pxFilled, TANK_HEIGHT);
    gui.drawTexture(MMRMachineUI.sprite(MMRMachineUI.TEX_GUIBAR, 196, TANK_HEIGHT - pxFilled, TANK_WIDTH, pxFilled),
        x, y + TANK_HEIGHT - pxFilled, TANK_WIDTH, pxFilled);
  }
}
