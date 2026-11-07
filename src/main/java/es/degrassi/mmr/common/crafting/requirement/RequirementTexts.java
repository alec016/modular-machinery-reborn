package es.degrassi.mmr.common.crafting.requirement;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;

@Getter
@RequiredArgsConstructor
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum RequirementTexts implements LocalizationEnum {
  or(" or "),
  and(" and "),
  chance("%s chance of %s"),
  notConsumed("%s is not consumed"),
  list("* %s"),
  durability("Restores %s durability of %s"),
  durabilityTick("Restores %s durability per tick of %s"),
  item("%s of %s"),
  fluid("%smB of %s"),
  fluidTick("%smBx per tick of %s"),
  effect("Giving %s for %s ticks per tick"),
  effectFilter(" to %s"),
  energy("Producing %s%s"),
  energyTick("Producing %s%s per tick")
  ;
  public static final Component newLine = Component.literal("\n");
  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "gui.controller.requirement." + name();
  }
}
