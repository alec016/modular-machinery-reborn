package es.degrassi.mmr.api.client;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;

public enum ExperienceButtonType implements StringRepresentable {
  EXTRACT_1(Icon.XP_EXTRACT_1),
  EXTRACT_10(Icon.XP_EXTRACT_10),
  EXTRACT_ALL(Icon.XP_EXTRACT_100),
  INSERT_1(Icon.XP_INSERT_1),
  INSERT_10(Icon.XP_INSERT_10),
  INSERT_ALL(Icon.XP_INSERT_100);

  private final Icon icon;
  ExperienceButtonType(Icon icon) {
    this.icon = icon;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public boolean isAll() {
    return this == EXTRACT_ALL || this == INSERT_ALL;
  }

  public boolean extract() {
    return switch (this) {
      case EXTRACT_1, EXTRACT_10, EXTRACT_ALL -> true;
      default -> false;
    };
  }

  public MutableComponent text() {
    return (switch (this) {
      case EXTRACT_1 -> ExperienceButtonTypeTexts.EXTRACT_1;
      case EXTRACT_10 -> ExperienceButtonTypeTexts.EXTRACT_10;
      case EXTRACT_ALL -> ExperienceButtonTypeTexts.EXTRACT_ALL;
      case INSERT_1 -> ExperienceButtonTypeTexts.INSERT_1;
      case INSERT_10 -> ExperienceButtonTypeTexts.INSERT_10;
      case INSERT_ALL -> ExperienceButtonTypeTexts.INSERT_ALL;
    }).text();
  }

  public static List<ExperienceButtonType> extractions() {
    return List.of(EXTRACT_1, EXTRACT_10, EXTRACT_ALL);
  }

  public static List<ExperienceButtonType> insertions() {
    return List.of(INSERT_1, INSERT_10, INSERT_ALL);
  }

  public Icon icon() {
    return this.icon;
  }

  public int getAmount(boolean extraction) {
    return switch (this) {
      case INSERT_1, EXTRACT_1 -> extraction ? 1 : -1;
      case INSERT_10, EXTRACT_10 -> extraction ? 10 : -10;
      default -> 0;
    };
  }
}
