package es.degrassi.mmr.common.integration;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum WailaTexts implements LocalizationEnum {
  tooltipAutoInput("Auto Input: %s"),
  tooltipAutoOutput("Auto Output: %s")
  ;

  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "gui." + name();
  }
}
