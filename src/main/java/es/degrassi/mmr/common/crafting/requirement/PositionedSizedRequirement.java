package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;

public record PositionedSizedRequirement(int x, int y, int width, int height) {
  public static final NamedCodec<PositionedSizedRequirement> POSITION_SIZED_CODEC =
      NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("x").forGetter(PositionedSizedRequirement::x),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("y").forGetter(PositionedSizedRequirement::y),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("width").forGetter(PositionedSizedRequirement::width),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("height").forGetter(PositionedSizedRequirement::height)
  ).apply(instance, PositionedSizedRequirement::new), "Position Sized");

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("x", x);
    json.addProperty("y", y);
    json.addProperty("width", width);
    json.addProperty("height", height);
    return json;
  }
}
