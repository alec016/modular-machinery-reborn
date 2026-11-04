package es.degrassi.mmr.client.screen.widget.tabs;

import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum TabTexts implements LocalizationEnum {
  tooltipAuto("Auto %s"),
  tooltipAutoChange("Actual Mode: %s, Change to: %s"),
  tooltipEnabled("Enabled"),
  tooltipDisabled("Disabled"),
  tooltipOpenFill("Open Fill Panel"),
  tooltipCloseFill("Close Fill Panel"),
  fillDurability("Durability"),
  fillAmount("Amount"),
  fillCancel("Cancel"),
  fillConfirm("Fill"),
  fillSearch("Search"),
  fillSearchHintMod("@ to search by mod"),
  fillSearchHintTag("# to search by tag"),
  fillReasonEnergy("The energy storage is at maximum"),
  fillReasonExperience("The tank cannot hold more experience"),
  fillReasonFull("The hatch is full: nothing else can be added"),
  fillReasonCompiling("The interface is still compiling: try again in a moment"),
  compileHudTitle("Compiling…"),
  compileHud("%s (%s/%s)"),
  fillMax("Max: %s"),
  rowsCycleHint("Click to cycle: 4 / 7 / 10 / screen"),
  tooltipOpenSideConfig("Open Side Config"),
  tooltipCloseSideConfig("Close Side Config"),
  autoioHintSelect("Click a face to select it (green outline)"),
  autoioHintToggle("Click the selected face again to toggle its auto-IO (blue = input, orange = output)"),
  autoioDisableAll("Disable All"),
  autoioEnableAll("Enable All")
  ;

  private final String englishText;

  @Override
  public String getTranslationKey() {
    return "mmr.gui." + name();
  }
}
