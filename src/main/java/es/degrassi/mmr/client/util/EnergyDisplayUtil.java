package es.degrassi.mmr.client.util;

import es.degrassi.mmr.common.data.config.EnergyHatchConfig;
import lombok.Getter;
import net.minecraft.util.Mth;

public class EnergyDisplayUtil {

  public static boolean displayFETooltip = true;
  public static boolean displayIC2EUTooltip = true;

  public static EnergyType type = EnergyType.FE;

  public static void loadFromConfig() {
    displayFETooltip = EnergyHatchConfig.get().energy_displayFETooltip.get();
    displayIC2EUTooltip = EnergyHatchConfig.get().energy_displayIC2EUTooltip.get();
    type = EnergyHatchConfig.get().energy_type.get();
  }

  public enum EnergyType {
    FE(1),
    IC2_EU(0.25F);

    private final double multiplier;
    @Getter
    private final String unlocalizedFormat;

    EnergyType(double multiplier) {
      this.multiplier = multiplier;
      this.unlocalizedFormat = "tooltip.energy.type." + name().toLowerCase();
    }

    public long formatEnergyForDisplay(long energy) {
      return Mth.lfloor(energy * multiplier);
    }

    public static EnergyType getType(String configValue) {
      EnergyType type = FE;
      try {
        type = EnergyType.valueOf(configValue);
      } catch (Exception ignored) {}

      return type;
    }
  }
}
