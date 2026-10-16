package es.degrassi.mmr.common.util.sound;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;

public record Sounds(
    AmbientSound ambientSound,
    MMRSoundType interaction
) {
  public static final Sounds DEFAULT = new Sounds(AmbientSound.DEFAULT, MMRSoundType.DEFAULT);

  public static final NamedCodec<Sounds> CODEC = NamedCodec.record(soundInstance -> soundInstance.group(
      AmbientSound.CODEC.optionalFieldOf("ambient", AmbientSound.DEFAULT).forGetter(Sounds::ambientSound),
      MMRSoundType.CODEC.optionalFieldOf("interaction", MMRSoundType.DEFAULT).forGetter(Sounds::interaction)
  ).apply(soundInstance, Sounds::new), "Sounds codec");

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.add("interaction", interaction.asJson());
    json.add("ambient", ambientSound.asJson());
    return json;
  }
}
