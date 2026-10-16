package es.degrassi.mmr.common.util.sound;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.function.Function;

public record AmbientSound(SoundEvent sound, float volume, float pitch, SoundSource source, boolean loop, boolean attenuation, int delay, boolean relative) {

  public static final AmbientSound DEFAULT = makeDefault(SoundEvent.createVariableRangeEvent(Identifier.parse("")));

  public static final NamedCodec<AmbientSound> FULL_CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.SOUND_EVENT.fieldOf("sound").forGetter(AmbientSound::sound),
      NamedCodec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("volume", 1F).forGetter(AmbientSound::volume),
      NamedCodec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("pitch", 1F).forGetter(AmbientSound::pitch),
      NamedCodec.enumCodec(SoundSource.class).optionalFieldOf("source", SoundSource.BLOCKS).forGetter(AmbientSound::source),
      NamedCodec.BOOL.optionalFieldOf("loop", true).forGetter(AmbientSound::loop),
      NamedCodec.BOOL.optionalFieldOf("attenuation", true).forGetter(AmbientSound::attenuation),
      NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("delay", 0).forGetter(AmbientSound::delay),
      NamedCodec.BOOL.optionalFieldOf("relative", false).forGetter(AmbientSound::relative)
  ).apply(instance, AmbientSound::new), "Ambient sound");

  public static final NamedCodec<AmbientSound> CODEC = NamedCodec.either(FULL_CODEC, DefaultCodecs.SOUND_EVENT)
      .xmap(either -> either.map(Function.identity(), AmbientSound::makeDefault), Either::left, "Ambient sound");

  public static AmbientSound makeDefault(SoundEvent sound) {
    return new AmbientSound(sound, 1, 1, SoundSource.BLOCKS, true, true, 0, false);
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("sound", sound.location().toString());
    json.addProperty("volume", volume);
    json.addProperty("pitch", pitch);
    json.addProperty("source", source.getName().toLowerCase());
    json.addProperty("loop", loop);
    json.addProperty("attenuation", attenuation);
    json.addProperty("delay", delay);
    json.addProperty("relative", relative);
    return json;
  }

  public boolean isDefault() {
    return this == DEFAULT;
  }
}
