package es.degrassi.mmr.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {
  private Utils() {}
  public static final RandomSource RAND = RandomSource.create(42L);
  private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,###");
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

  public static Vec3 vec3dFromBlockPos(BlockPos pos) {
    return new Vec3(pos.getX(), pos.getY(), pos.getZ());
  }

  public static boolean isResourceNameValid(String identifier) {
    var result = Identifier.tryParse(identifier);
    return result != null;
  }

  public static String format(int number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(int number) {
    return format(number) + "%";
  }

  public static String decimalFormat(int number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(int number) {
    return decimalFormat(number) + "%";
  }

  public static String format(long number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(long number) {
    return format(number) + "%";
  }

  public static String decimalFormat(long number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(long number) {
    return decimalFormat(number) + "%";
  }

  public static String format(double number) {
    return NUMBER_FORMAT.format(number);
  }
  public static String formatWithPercentage(double number) {
    return format(number) + "%";
  }

  public static String decimalFormat(double number) {
    return DECIMAL_FORMAT.format(number).replaceAll(",", ".");
  }
  public static String decimalFormatWithPercentage(double number) {
    return decimalFormat(number) + "%";
  }

  public static long clamp(long value, long min, long max) {
    return value < min ? min : Math.min(value, max);
  }

  public static boolean shouldRunPeriodicCheck(boolean immediate, long gameTime, long lastGameTime, long offset, long period) {
    if (lastGameTime == gameTime)
      return false;
    if (immediate)
      return true;
    long roundGameTime = gameTime % period;
    long roundOffset = offset % period;
    return (roundGameTime + roundOffset) % period == 0;
  }
}
