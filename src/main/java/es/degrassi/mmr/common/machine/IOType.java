package es.degrassi.mmr.common.machine;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;

@AutoCollectLang(ModularMachineryReborn.MODID)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum IOType implements StringRepresentable, LocalizationEnum {
  INPUT("Input"),
  OUTPUT("Output"),
  NONE("None");

  @Getter
  private final String englishText;

  public static final NamedCodec<IOType> CODEC = NamedCodec.enumCodec(IOType.class);

  public static IOType value(String mode) {
    return valueOf(mode.toUpperCase(Locale.ENGLISH));
  }

  @Nullable
  public static IOType getByString(String name) {
    for (IOType val : values()) {
      if (val.name().equalsIgnoreCase(name)) {
        return val;
      }
    }
    return null;
  }

  public boolean isInput() {
    return this == INPUT;
  }

  public boolean isOutput() {
    return this == OUTPUT;
  }

  public boolean isNone() {
    return this == NONE;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  @Override
  public String getTranslationKey() {
    return ModularMachineryReborn.MODID + ".requirement.mode." + getSerializedName();
  }
}
