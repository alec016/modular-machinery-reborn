package es.degrassi.mmr.common.item;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import es.degrassi.experiencelib.data.localization.AutoCollectLang;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@AutoCollectLang(value = ModularMachineryReborn.MODID, lang = "en_us")
public enum StructureCreatorItemMode implements StringRepresentable, LocalizationEnum {
  SINGLE("Single"), BOX("Box"), VEIN("Proximity (Vein)");

  @Getter
  private final String englishText;

  public static final NamedCodec<StructureCreatorItemMode> CODEC = NamedCodec.enumCodec(StructureCreatorItemMode.class);
  public static final StreamCodec<RegistryFriendlyByteBuf, StructureCreatorItemMode> STREAM_CODEC = CODEC.streamCodec();

  public boolean isSingle() {
    return this == SINGLE;
  }

  public boolean isBox() {
    return this == BOX;
  }

  public boolean isVein() {
    return this == VEIN;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  @Override
  public String getTranslationKey() {
    return "modular_machinery_reborn.structure_creator.mode." + getSerializedName();
  }
}
