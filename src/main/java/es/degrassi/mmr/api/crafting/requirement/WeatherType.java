package es.degrassi.mmr.api.crafting.requirement;

import es.degrassi.experiencelib.api.codec.NamedCodec;

import java.util.Locale;

public enum WeatherType {
  CLEAR,
  RAIN,
  SNOW,
  THUNDER;

  public static final NamedCodec<WeatherType> CODEC = NamedCodec.enumCodec(WeatherType.class);

  public static WeatherType value(String value) {
    return valueOf(value.toUpperCase(Locale.ROOT));
  }
}
