package es.degrassi.mmr.common.data;

import es.degrassi.mmr.common.util.MMRLogger;

public class Config {
  private Config() {}
  public static int machineColor;
  public static int chanceColor;

  static {
    load();
  }

  public static void load() {
    machineColor = toInt(MMRConfig.get().general_casing_color.get(), 0xFF4900);
    chanceColor = toInt(MMRConfig.get().chance_color.get(), 0xFFFFFF);
  }

  public static int toInt(String color, int defaultColor) {
    try {
      color = color.length() == 6 ? "FF" + color : color;
      if (!color.startsWith("#")) color = "#" + color;
      return Long.decode(color).intValue();
    } catch (Exception exc) {
      MMRLogger.INSTANCE.error("Color defined in is not a hex color: {}", color);
      MMRLogger.INSTANCE.error("Using default color instead...");
    }
    return defaultColor;
  }
}
