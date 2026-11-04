package es.degrassi.mmr.client.screen;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum UITexts implements LocalizationEnum {
  status("Status: %s"),
  applyingEffect("Applying effect"),
  noRecipe("No running recipe"),
  effectText("Effect: %s"),
  amplifier("Amplifier: %s"),
  duration("Duration: %s ticks"),
  radiusBlocks("Radius: %s blocks"),
  radiusChunks("Radius: %s chunks"),
  interdimensional("Works across dimensions"),
  chunkActive("Active"),
  chunkInactive("Inactive"),
  chunkTempo("Expires in %s ticks"),
  mode("Mode: %s"),
  acceptedActions("Accepted actions:"),
  boundController("Bound controller: X %s, Y %s, Z %s"),
  notBound("Not bound to a controller"),
  machineStatus("Machine status: %s"),
  boundMachine("Bound machine: %s"),
  noBoundMachine("No machine bound yet"),
  recipeRadiusNote("The scan radius is set by the machine's recipe requirements"),
  commandNote("Executes the commands set by the machine's recipe requirements"),
  commandSourceNote("Runs at this position with operator permission — the recipe provides the command"),
  heightText("Height: Y %s"),
  heightNote("The recipe compares this height"),
  dayTime("Day time: %s"),
  timeNote("The recipe compares this time"),
  biomeText("Biome: %s"),
  biomeId("Identifier: %s"),
  dimensionText("Dimension: %s"),
  dimensionId("Identifier: %s"),
  weatherText("Weather: %s"),
  weatherClear("Clear"),
  weatherRain("Rain"),
  weatherThunder("Thunder"),
  areaToggle("Toggle area preview"),
  actionCheckAmount("Check entity amount"),
  actionCheckHealth("Check entity health"),
  actionConsumeHealth("Damage entities"),
  actionAddHealth("Heal entities"),
  actionSpawn("Spawn entities"),
  actionKill("Kill entities"),
  breakPlaceTime("It will take up to %s ticks (%s seconds)")
  ;

  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "mmr.gui." + name();
  }
}
