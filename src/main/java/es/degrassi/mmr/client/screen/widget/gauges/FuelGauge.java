package es.degrassi.mmr.client.screen.widget.gauges;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.LongSupplier;

public final class FuelGauge extends Gauge {

  private final LongSupplier fuel;
  private final LongSupplier maxFuel;

  public FuelGauge(LongSupplier fuel, LongSupplier maxFuel) {
    this.fuel = fuel;
    this.maxFuel = maxFuel;
  }

  @Override
  protected List<Component> tooltipLines() {
    return List.of(Component.translatable("tooltip.fuel_tank.tank",
        String.valueOf(fuel.getAsLong()), String.valueOf(maxFuel.getAsLong())));
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    GUIContext gui = clientContext(context);
    if (gui == null) {
      return;
    }
    int width = 14;
    int height = 14;
    gui.drawTexture(MMRMachineUI.sprite(ModularMachineryReborn.rl("textures/gui/empty_fuel.png"), 0, 0, width, height),
        getContentX(), getContentY(), width, height);
    long max = maxFuel.getAsLong();
    long value = fuel.getAsLong();
    if (value > 0 && max > 0) {
      int calcHeight = (int) (value * 1d / max * height);
      calcHeight = Mth.clamp(calcHeight, 0, height);
      int offset = height - calcHeight;
      gui.drawTexture(MMRMachineUI.sprite(ModularMachineryReborn.rl("textures/gui/filled_fuel.png"), 0, offset, width, calcHeight),
          getContentX() + 1, getContentY() + offset + 1, width, calcHeight);
    }
  }
}
