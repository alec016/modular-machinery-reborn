package es.degrassi.mmr.common.crafting.helper;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@Getter
public class FuelData extends DirectionalPositionedData {
  public static final Identifier defaultEmpty = ModularMachineryReborn.rl("textures/gui/empty_fuel.png");
  public static final Identifier defaultFilled = ModularMachineryReborn.rl("textures/gui/filled_fuel.png");

  public static final NamedCodec<FuelData> CODEC = NamedCodec.record(instance -> instance.group(
        NamedCodec.enumCodec(Direction.class).optionalFieldOf("direction", Direction.BOTTOM).forGetter(FuelData::direction),
        PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(FuelData::position),
        DefaultCodecs.IDENTIFIER.optionalFieldOf("emptyTexture", defaultEmpty).forGetter(FuelData::getEmptyTexture),
        DefaultCodecs.IDENTIFIER.optionalFieldOf("filledTexture", defaultFilled).forGetter(FuelData::getFillTexture)
    ).apply(instance, FuelData::new), "Directional Positioned Data");

  private final Identifier emptyTexture, fillTexture;

  public FuelData(Direction direction, PositionedRequirement position, Identifier emptyTexture, Identifier fillTexture) {
    super(direction, position);
    this.emptyTexture = emptyTexture;
    this.fillTexture = fillTexture;
  }

  public static final FuelData DEFAULT_FUEL = new FuelData(
      Direction.BOTTOM,
      new PositionedRequirement(0, 0),
      defaultEmpty,
      defaultFilled
  );

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("emptyTexture", emptyTexture.toString());
    json.addProperty("filledTexture", fillTexture.toString());
    return json;
  }
}
