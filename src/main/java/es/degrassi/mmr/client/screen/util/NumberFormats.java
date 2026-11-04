package es.degrassi.mmr.client.screen.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Number display helpers for UI surfaces that deal with long amounts (fill tab tooltips, gauges,
 * slot counts).
 *
 * <p>All methods share the same locale rules: the decimal and grouping separators come from the
 * active language, so a compact number and its grouped counterpart always look the same (English
 * uses {@code 1,234.5}, Spanish uses {@code 1.234,5}).</p>
 */
public final class NumberFormats {
  /** The suffixes every mod uses: the powers of a thousand, like Minecraft's own number format. */
  private static final String[] SUFFIXES = {"", "k", "M", "G", "T", "P", "E"};
  private static final long[] POWERS = {
      1L,
      1_000L,
      1_000_000L,
      1_000_000_000L,
      1_000_000_000_000L,
      1_000_000_000_000_000L,
      1_000_000_000_000_000_000L
  };

  private NumberFormats() {}

  /** The separators of the active language. */
  public static DecimalFormatSymbols symbols() {
    return DecimalFormatSymbols.getInstance(Locale.getDefault());
  }

  /** The exact number with the grouping of the active language: {@code 1,234,567}. */
  public static String grouped(long value) {
    var symbols = symbols();
    var format = new DecimalFormat("#,##0", symbols);
    return format.format(value);
  }

  /**
   * Compact single-letter-suffix form: {@code 1_234} → {@code 1.23K}, {@code 1_500_000_000} →
   * {@code 1.5B}, exact multiples stay integer ({@code 12_000_000_000} → {@code 12B}). The two
   * decimals are floored, never rounded up ({@code 999_999_999_999} → {@code 999.99B}), so the
   * mantissa can never overflow into {@code 1000B}. Values below 1000 come back as plain digits.
   * The decimal separator follows the active language.
   */
  public static String compact(long value) {
    if (value < 0) {
      return "-" + compact(-value);
    }
    int step = Math.min((Long.toString(value).length() - 1) / 3, SUFFIXES.length - 1);
    if (step <= 0) {
      return grouped(value);
    }
    long pow = POWERS[step];
    long whole = value / pow;
    long hundredths = (value % pow) / (pow / 100); // floored 0..99, no rounding carry
    var symbols = symbols();
    String text;
    if (hundredths == 0) {
      text = whole + SUFFIXES[step];
    } else {
      var format = new DecimalFormat("0.00", symbols);
      format.setRoundingMode(java.math.RoundingMode.DOWN);
      format.setMaximumFractionDigits(2);
      format.setMinimumFractionDigits(0);
      text = format.format((double) value / pow) + SUFFIXES[step];
    }
    return text;
  }

  /**
   * Fluid amounts: buckets of 1000 mB above a thousand, so the number stays readable
   * ({@code 1500} mB → {@code 1.5 Bucket}).
   */
  public static String fluidAmount(long milliBucket) {
    if (milliBucket < 1000L) {
      return grouped(milliBucket);
    }
    return compact(milliBucket / 1000L);
  }
}
