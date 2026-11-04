package es.degrassi.mmr.client.util;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
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

  @AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
  public enum EnergyType implements LocalizationEnum {
    FE(1, "FE"),
    IC2_EU(0.25F, "EU");

    private final double multiplier;
    @Getter
    private final String englishText;

    EnergyType(double multiplier, String englishText) {
      this.multiplier = multiplier;
      this.englishText = englishText;
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

    @Override
    public String getTranslationKey() {
      return "tooltip.energy.type." + name();
    }
  }
}
