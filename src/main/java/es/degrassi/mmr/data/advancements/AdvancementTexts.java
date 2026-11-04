package es.degrassi.mmr.data.advancements;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;

import java.util.Locale;

@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum AdvancementTexts implements LocalizationEnum {
  Root("Modular Machinery Reborn"),
  RootDesc("When processing needs more infrastructure."),
  Controller("Making Multiblocks"),
  ControllerDesc("Craft a Multiblock Controller"),
  Generated("%s"),
  GeneratedDesc("Craft %s"),
  Casings("All Casings"),
  CasingsDesc("Get at least one of each casing type"),
  ItemBus("Item Buses"),
  ItemBusDesc("Craft any Item Bus"),
  FluidHatch("Fluid Hatches"),
  FluidHatchDesc("Craft any Fluid Hatch")
  ;
  @Getter
  private final String englishText;
  AdvancementTexts(String english) {
    this.englishText = english;
  }

  @Override
  public String getTranslationKey() {
    var suffix = name().toLowerCase(Locale.ENGLISH).endsWith("desc")
        ? name().replace("desc", ".desc").replace("Desc", ".desc")
        : name();
    return "advancement.mmr.%s".formatted(suffix);
  }
}
