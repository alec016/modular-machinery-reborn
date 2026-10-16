package es.degrassi.mmr.common.crafting.helper;

import com.google.gson.JsonObject;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;

public class DirectionalPositionedData {
  private final Direction direction;
  private final PositionedRequirement position;

  public DirectionalPositionedData(Direction direction, PositionedRequirement position) {
    this.direction = direction;
    this.position = position;
  }

  public Direction direction() {
    return this.direction;
  }

  public PositionedRequirement position() {
    return this.position;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("direction", direction.toString());
    json.add("position", position.asJson());
    return json;
  }
}
