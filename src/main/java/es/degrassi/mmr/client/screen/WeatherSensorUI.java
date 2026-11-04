package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.WeatherSensorEntity;
import net.minecraft.network.chat.Component;

public final class WeatherSensorUI {
  private WeatherSensorUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, WeatherSensorEntity hatch) {
    UIElement panel = InfoUIs.panel(2);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.weather_sensor"));

    InfoUIs.line(panel, 0,
        () -> {
          var level = hatch.getLevel();
          if (level == null) {
            return null;
          }
          if (level.isThundering()) {
            return UITexts.weatherThunder.text().getString();
          }
          if (level.isRaining()) {
            return UITexts.weatherRain.text().getString();
          }
          return UITexts.weatherClear.text().getString();
        }, null);
    return MMRMachineUI.menu(panel, holder.player);
  }
}
