package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum StructureCreatorItemMode implements StringRepresentable {
  SINGLE, BOX, VEIN;

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

  public MutableComponent component() {
    return Component.translatable("modular_machinery_reborn.structure_creator.mode." + getSerializedName());
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
