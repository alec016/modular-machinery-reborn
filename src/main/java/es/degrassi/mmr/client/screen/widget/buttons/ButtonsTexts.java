package es.degrassi.mmr.client.screen.widget.buttons;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum ButtonsTexts implements LocalizationEnum {
  rowsVisible("Max visible rows: %s"),
  rowsScreenMax("Screen max: %s rows"),
  ;

  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "mmr.gui.button." + name();
  }
}
