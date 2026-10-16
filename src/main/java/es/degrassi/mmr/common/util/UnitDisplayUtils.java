package es.degrassi.mmr.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class UnitDisplayUtils {
  private static final Unit IGNORED_UNIT = new Unit() {

    @Override
    public Component appendTo(Object existing, boolean isShort, boolean spaceBetweenSymbol, boolean singular) {
      return TextComponentUtil.build(existing);
    }

    @Override
    public Object getSymbol(boolean singular) {
      return null;
    }
  };

  public static Component getDisplay(double value, int decimalPlaces) {
    return getDisplayBase(value, IGNORED_UNIT, decimalPlaces, true, false);
  }

  public static double roundDecimals(double d, int decimalPlaces) {
    double multiplier = Math.pow(10, decimalPlaces);
    long j = (long) (d * multiplier);
    return j / multiplier;
  }

  public static double roundDecimals(boolean negative, double d, int decimalPlaces) {
    return negative ? roundDecimals(-d, decimalPlaces) : roundDecimals(d, decimalPlaces);
  }

  private static Component getDisplayBase(double value, UnitDisplayUtils.Unit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol) {
    if (value == 0) {
      return unit.appendTo(value, isShort, spaceBetweenSymbol, false);
    }
    boolean singular = Mth.equal(value, 1);
    boolean negative = value < 0;
    if (negative) {
      value = Math.abs(value);
    }
    for (int i = 0; i < UnitDisplayUtils.MeasurementUnit.values().length; i++) {
      UnitDisplayUtils.MeasurementUnit lowerMeasure = UnitDisplayUtils.MeasurementUnit.values()[i];
      if ((i == 0 && lowerMeasure.below(value)) ||
          i + 1 >= MeasurementUnit.values().length ||
          (lowerMeasure.aboveEqual(value) && UnitDisplayUtils.MeasurementUnit.values()[i + 1].below(value))) {
        //First element and it is below it (no more unit abbreviations before),
        // or last element (no more unit abbreviations past),
        // or we are within the bounds between this one and the next one
        return lowerMeasure.getDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative, singular);
      }
    }
    //Fallback, should never be reached as should have been captured by the check in the loop
    return UnitDisplayUtils.MeasurementUnit.values()[UnitDisplayUtils.MeasurementUnit.values().length - 1].getDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative, singular);
  }

  private interface Unit {

    default Component appendTo(Object existing, boolean isShort, boolean spaceBetweenSymbol, boolean singular) {
      if (isShort) {
        if (spaceBetweenSymbol) {
          return TextComponentUtil.build(existing + " ", getSymbol(singular));
        }
        return TextComponentUtil.build(existing, getSymbol(singular));
      }
      return TextComponentUtil.build(existing);
    }

    Object getSymbol(boolean singular);
  }



  /**
   * Metric system of measurement.
   */
  public enum MeasurementUnit {
    FEMTO("Femto", "f", 0.000_000_000_000_001D),
    PICO("Pico", "p", 0.000_000_000_001D),
    NANO("Nano", "n", 0.000_000_001D),
    MICRO("Micro", "µ", 0.000_001D),
    MILLI("Milli", "m", 0.001D),
    BASE("", "", 1),
    KILO("Kilo", "k", 1_000D),
    MEGA("Mega", "M", 1_000_000D),
    GIGA("Giga", "G", 1_000_000_000D),
    TERA("Tera", "T", 1_000_000_000_000D),
    PETA("Peta", "P", 1_000_000_000_000_000D),
    EXA("Exa", "E", 1_000_000_000_000_000_000D),
    ZETTA("Zetta", "Z", 1_000_000_000_000_000_000_000D),
    YOTTA("Yotta", "Y", 1_000_000_000_000_000_000_000_000D);

    /**
     * long name for the unit
     */
    private final String name;

    /**
     * short unit version of the unit
     */
    private final String symbol;

    /**
     * Point by which a number is considered to be of this unit
     */
    private final double value;

    MeasurementUnit(String name, String symbol, double value) {
      this.name = name;
      this.symbol = symbol;
      this.value = value;
    }

    public String getName(boolean isShort) {
      if (isShort) {
        return symbol;
      }
      return name;
    }

    public double process(double d) {
      return d / value;
    }

    public boolean aboveEqual(double d) {
      return d >= value;
    }

    public boolean below(double d) {
      return d < value;
    }

    private Component getDisplay(double value, UnitDisplayUtils.Unit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol, boolean negative, boolean singular) {
      double rounded = roundDecimals(negative, process(value), decimalPlaces);
      String name = getName(isShort);
      if (spaceBetweenSymbol || !isShort) {
        name = " " + name;
      }
      //Note: We handle the space between symbols above
      return unit.appendTo(rounded + name, isShort, false, singular);
    }
  }
}
