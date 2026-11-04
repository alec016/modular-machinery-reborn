package es.degrassi.mmr.client.screen.widget.gauges;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum GaugesTexts implements LocalizationEnum {
  energyHatchCharge("%s / %s %s"),
  experienceTooltip("%s / %s"),
  fluidHatchTank("%smB / %smB"),
  fluidHatchTankBuckets("%s B / %s B"),
  fluidHatchEmpty("Empty"),
  fuelTankTicks("%s / %s ticks"),
  fuelTankSeconds("%s / %s seconds")
  ;

  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "mmr.gui.element." + name();
  }
}
