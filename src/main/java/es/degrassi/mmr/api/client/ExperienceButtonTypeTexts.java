package es.degrassi.mmr.api.client;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

@RequiredArgsConstructor
@Getter
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum ExperienceButtonTypeTexts implements LocalizationEnum, StringRepresentable {
  EXTRACT_1("Extract 1 Level"),
  EXTRACT_10("Extract 10 Levels"),
  EXTRACT_ALL("Extract All Levels"),
  INSERT_1("Insert 1 Level"),
  INSERT_10("Insert 10 Levels"),
  INSERT_ALL("Insert All Levels");
  private final String englishText;

  public String getTranslationKey() {
    return "mmr.gui.tooltip.experience.button." + getSerializedName();
  }
  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
