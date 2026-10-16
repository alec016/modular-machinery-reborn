package es.degrassi.mmr.common.util.sound;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.experiencelib.api.codec.NamedRecordCodec;
import es.degrassi.mmr.api.PartialBlockState;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

@SuppressWarnings("deprecation")
public class MMRSoundType extends SoundType {

  public static final MMRSoundType DEFAULT = new MMRSoundType(new PartialBlockState(Blocks.IRON_BLOCK));

  public static final NamedCodec<MMRSoundType> FROM_STATE = PartialBlockState.CODEC.xmap(MMRSoundType::new, type -> type.defaultBlock, "Sound type");

  public static final NamedMapCodec<MMRSoundType> FROM_PARTS = NamedCodec.record(cmSoundTypeInstance ->
      cmSoundTypeInstance.group(
          NamedCodec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(SoundType::getVolume),
          NamedCodec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(SoundType::getPitch),
          partCodec("break", SoundType::getBreakSound),
          partCodec("step", SoundType::getStepSound),
          partCodec("place", SoundType::getPlaceSound),
          partCodec("hit", SoundType::getHitSound),
          partCodec("fall", SoundType::getFallSound)
      ).apply(cmSoundTypeInstance, MMRSoundType::new), "Sound type"
  );

  public static final NamedCodec<MMRSoundType> CODEC = NamedCodec.either(FROM_STATE, FROM_PARTS, "Interaction sounds").xmap(
      either -> either.map(Function.identity(), Function.identity()),
      Either::right,
      "Interaction sounds"
  );

  private final PartialBlockState defaultBlock;

  public MMRSoundType(float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
    super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    this.defaultBlock = PartialBlockState.AIR;
  }

  public MMRSoundType(PartialBlockState state) {
    super(1.0F, 1.0F, state.getBlockState().getSoundType().getBreakSound(), state.getBlockState().getSoundType().getStepSound(), state.getBlockState().getSoundType().getPlaceSound(), state.getBlockState().getSoundType().getHitSound(), state.getBlockState().getSoundType().getFallSound());
    this.defaultBlock = state;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("volume", this.volume);
    json.addProperty("pitch", this.pitch);
    json.addProperty("break", getBreakSound().location().toString());
    json.addProperty("step", getStepSound().location().toString());
    json.addProperty("place", getPlaceSound().location().toString());
    json.addProperty("hit", getHitSound().location().toString());
    json.addProperty("fall", getFallSound().location().toString());
    return json;
  }

  private static NamedRecordCodec<MMRSoundType, SoundEvent> partCodec(String field, Function<SoundType, SoundEvent> typeToSound) {
    return NamedCodec.either(PartialBlockState.CODEC, DefaultCodecs.SOUND_EVENT, StringUtils.capitalize(field) + " Sound").xmap(
        either -> either.map(state -> typeToSound.apply(state.getBlockState().getSoundType()), Function.identity()),
        Either::right,
        StringUtils.capitalize(field) + " Sound"
    ).optionalFieldOf(field, typeToSound.apply(DEFAULT)).forGetter(typeToSound::apply);
  }
}
