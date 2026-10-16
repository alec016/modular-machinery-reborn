package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;

public record PositionedRequirement(int x, int y) {
  public static final NamedCodec<PositionedRequirement> POSITION_CODEC =
      NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("x").forGetter(PositionedRequirement::x),
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("y").forGetter(PositionedRequirement::y)
  ).apply(instance, PositionedRequirement::new), "Position");

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("x", x);
    json.addProperty("y", y);
    return json;
  }
}
