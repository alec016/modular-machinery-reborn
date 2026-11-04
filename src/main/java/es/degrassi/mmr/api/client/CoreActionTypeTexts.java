package es.degrassi.mmr.api.client;

import es.degrassi.experiencelib.api.datagen.Variant;
import es.degrassi.experiencelib.api.datagen.VariantLocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;

import java.util.Locale;

@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum CoreActionTypeTexts implements VariantLocalizationEnum<CoreActionTypeTexts.CoreActionTypeVariant, CoreActionTypeTexts> {
  ADD, REMOVE;
  @Getter
  private final CoreActionTypeVariant[] variants = CoreActionTypeVariant.values();

  public static CoreActionTypeTexts by(CoreActionType type) {
    return switch (type) {
      case ADD -> ADD;
      case REMOVE -> REMOVE;
    };
  }

  public CoreActionTypeVariant variant(boolean shift, boolean control) {
    return shift ? CoreActionTypeVariant.SHIFT : control ? CoreActionTypeVariant.CONTROL : CoreActionTypeVariant.SINGLE;
  }

  public String getTranslationKey(CoreActionTypeVariant variant) {
    return "mmr.gui.tooltip.core.action.button.%s.%s".formatted(variant.name(), name().toLowerCase(Locale.ENGLISH));
  }

  public enum CoreActionTypeVariant implements Variant<CoreActionTypeTexts, CoreActionTypeTexts.CoreActionTypeVariant> {
    SINGLE,
    SHIFT,
    CONTROL;

    @Override
    public String text(CoreActionTypeTexts type) {
      return switch (type) {
        case ADD -> "Add " + switch (this) {
          case SHIFT -> "10 active cores";
          case SINGLE -> "1 active core";
          case CONTROL -> "All active cores";
        };
        case REMOVE -> "Remove " + switch (this) {
          case SHIFT -> "10 active cores";
          case SINGLE -> "1 active core";
          case CONTROL -> "All active cores but 1";
        };
      };
    }
  }
}
