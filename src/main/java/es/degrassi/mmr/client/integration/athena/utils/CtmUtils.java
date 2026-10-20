package es.degrassi.mmr.client.integration.athena.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.Quadrant;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.models.TintProvider;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.CtmState;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.GeometryUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class CtmUtils {
  private static final BiPredicate<BlockState, BlockState> FALSE = (selfState, otherState) -> false;
  private static final BiPredicate<BlockState, BlockState> STATE = (selfState, otherState) -> selfState == otherState;
  private static final BiPredicate<BlockState, BlockState> IS = (selfState, otherState) -> selfState.is(otherState.getBlock());

  public CtmUtils() {
  }

  public static int getTexture(boolean first, boolean second, boolean firstSecond) {
    if (first && second) {
      return firstSecond ? 1 : 2;
    } else {
      return first ? 3 : (second ? 4 : 0);
    }
  }

  public static int getTexture(boolean first, boolean second, boolean firstSecond, int offset) {
    var texture = getTexture(first, second, firstSecond);
    if (offset == 0 || texture == 1) return texture;
    if (texture == 0) return 6;
    return texture + offset;
  }

  public static Int2ObjectMap<Material> parseCtmMaterials(JsonObject json) {
    Int2ObjectMap<Material> materials = new Int2ObjectArrayMap<>();

    materials.put(0, blockMat(GsonHelper.getAsString(json, "particle")));
    materials.put(1, blockMat(GsonHelper.getAsString(json, "empty")));
    materials.put(2, blockMat(GsonHelper.getAsString(json, "center")));
    materials.put(3, blockMat(GsonHelper.getAsString(json, "vertical")));
    materials.put(4, blockMat(GsonHelper.getAsString(json, "horizontal")));

    if (json.has("overlay"))
      materials.put(5, blockMat(GsonHelper.getAsString(json, "overlay")));

    if (json.has("ov_particle"))
      materials.put(6, blockMat(GsonHelper.getAsString(json, "ov_particle")));

    if (json.has("ov_center"))
      materials.put(7, blockMat(GsonHelper.getAsString(json, "ov_center")));

    if (json.has("ov_vertical"))
      materials.put(8, blockMat(GsonHelper.getAsString(json, "ov_vertical")));

    if (json.has("ov_horizontal"))
      materials.put(9, blockMat(GsonHelper.getAsString(json, "ov_horizontal")));

    return materials;
  }

  public static Material blockMat(String id) {
    return new Material(Identifier.parse(id));
  }

  public static <I, O> O tryParse(I input, Function<I, O> parser) {
    try {
      return parser.apply(input);
    } catch (Exception var3) {
      return null;
    }
  }

  public static BiPredicate<BlockState, BlockState> parseCondition(JsonObject json) {
    if (!json.has("connect_to")) {
      return STATE;
    } else {
      JsonElement var2 = json.get("connect_to");
      if (var2 instanceof JsonObject jsonObject) {
        return parseConditionInternal(jsonObject);
      } else {
        return FALSE;
      }
    }
  }

  private static BiPredicate<BlockState, BlockState> parseConditionInternal(JsonObject json) {
    BiPredicate<BlockState, BlockState> var10000;
    switch (GsonHelper.getAsString(json, "type", "")) {
      case "not" -> var10000 = parseNotCondition(json);
      case "and" -> var10000 = parseListCondition(json, BiPredicate::and);
      case "or" -> var10000 = parseListCondition(json, BiPredicate::or);
      case "xor" -> var10000 = parseXorCondition(json);
      case "state" -> var10000 = parseStateCondition(json);
      case "tag" -> var10000 = parseTagCondition(json);
      case "sameBlock" -> var10000 = IS;
      case "sameState" -> var10000 = STATE;
      default -> var10000 = FALSE;
    }

    return var10000;
  }

  private static BiPredicate<BlockState, BlockState> parseListCondition(JsonObject json, BinaryOperator<BiPredicate<BlockState, BlockState>> mapper) {
    List<BiPredicate<BlockState, BlockState>> conditions = unwrapConditions(json).stream().map(CtmUtils::parseConditionInternal).toList();
    if (conditions.isEmpty()) {
      return FALSE;
    } else {
      return conditions.size() == 1 ? conditions.getFirst() : conditions.stream().reduce(mapper).orElseThrow();
    }
  }

  private static BiPredicate<BlockState, BlockState> parseXorCondition(JsonObject json) {
    List<JsonObject> conditionsJson = unwrapConditions(json);
    if (conditionsJson.size() != 2) {
      return FALSE;
    } else {
      BiPredicate<BlockState, BlockState> first = parseConditionInternal(conditionsJson.get(0));
      BiPredicate<BlockState, BlockState> second = parseConditionInternal(conditionsJson.get(1));
      return (selfState, otherState) -> first.test(selfState, otherState) ^ second.test(selfState, otherState);
    }
  }

  private static List<JsonObject> unwrapConditions(JsonObject json) {
    List<JsonObject> conditionList = new ArrayList<>();
    JsonElement var3 = json.get("conditions");
    if (var3 instanceof JsonArray) {
      for(JsonElement jsonElement : (JsonArray)var3) {
        if (jsonElement instanceof JsonObject jsonObject) {
          conditionList.add(jsonObject);
        }
      }
    }

    return conditionList;
  }

  private static BiPredicate<BlockState, BlockState> parseNotCondition(JsonObject json) {
    JsonElement var2 = json.get("condition");
    if (var2 instanceof JsonObject jsonObject) {
      return parseConditionInternal(jsonObject).negate();
    } else {
      return (selfState, otherState) -> false;
    }
  }

  private static BiPredicate<BlockState, BlockState> parseStateCondition(JsonObject json) {
    Optional<Identifier> var10000 = Optional.ofNullable(GsonHelper.getAsString(json, "block", null)).map(Identifier::tryParse);
    DefaultedRegistry<Block> var10001 = BuiltInRegistries.BLOCK;
    Objects.requireNonNull(var10001);
    Optional<Block> blockOpt = var10000.flatMap(var10001::getOptional);
    if (blockOpt.isEmpty()) {
      return FALSE;
    } else {
      Block block = blockOpt.get();
      if (!json.has("properties")) {
        return (selfState, otherState) -> otherState.is(block);
      } else {
        JsonElement propertiesElem = json.get("properties");
        if (!propertiesElem.isJsonObject()) {
          return FALSE;
        } else {
          Map<Property<?>, Object> properties = new HashMap<>();

          for(Map.Entry<String, JsonElement> jsonEntry : propertiesElem.getAsJsonObject().asMap().entrySet()) {
            Property<?> property = block.getStateDefinition().getProperty(jsonEntry.getKey());
            if (property != null && GsonHelper.isStringValue(jsonEntry.getValue())) {
              property.getValue((jsonEntry.getValue()).getAsString()).ifPresent((value) -> properties.put(property, value));
            }
          }

          return (selfState, otherState) -> {
            if (!otherState.is(block)) {
              return false;
            } else {
              for(Map.Entry<Property<?>, Object> propertyTestValue : properties.entrySet()) {
                if (otherState.getValue(propertyTestValue.getKey()) != propertyTestValue.getValue()) {
                  return false;
                }
              }

              return true;
            }
          };
        }
      }
    }
  }

  private static BiPredicate<BlockState, BlockState> parseTagCondition(JsonObject json) {
    TagKey<Block> tag = TagKey.create(Registries.BLOCK, Objects.requireNonNull(Identifier.tryParse(json.get("tag").getAsString())));
    return (selfState, otherState) -> otherState.is(tag);
  }

  public static CtmState.ConnectionCheck check(AppearanceAndTintGetter level, BlockState state, BlockPos pos, Direction direction, BiPredicate<BlockState, BlockState> predicate) {
    return (fromPos, fromState, fromAppearance) -> predicate.test(level.getAppearance(state, pos, direction, fromState, fromPos), fromAppearance);
  }

  public static boolean checkRelative(AppearanceAndTintGetter level, BlockState state, BlockPos pos, Direction direction) {
    BlockPos relativePos = pos.relative(direction);
    BlockState otherState = level.getBlockState(relativePos);
    BlockState stateAppearance = level.getAppearance(state, pos, direction, otherState, relativePos);
    BlockState otherStateAppearance = level.getAppearance(otherState, relativePos, direction.getOpposite(), state, pos);
    return !stateAppearance.isAir() && otherStateAppearance.is(stateAppearance.getBlock());
  }

  public static MMRAthenaQuad athenaQuadWithState(int offset, MMRMaterialMap map, Direction direction, boolean first,
                                     boolean second, boolean firstSecond, float left, float right, float top, float bottom, int tint) {
    return athenaQuadWithState(offset, map, direction, first, second, firstSecond, left, right, top, bottom, 0f, tint);
  }

  public static MMRAthenaQuad athenaQuadWithState(int offset, MMRMaterialMap map, Direction direction, boolean first,
                                     boolean second, boolean firstSecond, float left, float right, float top, float bottom, float depth, int tint) {
    int tex = getTexture(first, second, firstSecond, offset);
    int texture = map.getTexture(direction, tex);
    return new MMRAthenaQuad(texture, left, right, top, bottom, net.minecraft.world.level.block.Rotation.NONE, depth, true, tex <= 1 ? 4 : tint);
  }

  public static AthenaQuad toAthenaQuad(MMRAthenaQuad quad) {
    return new AthenaQuad(quad.sprite(), quad.left(), quad.right(), quad.top(), quad.bottom(), quad.rotation(), quad.depth(), quad.cull());
  }

  public static TintProvider getTintProvider(MMRAthenaQuad quad) {
    if (quad.tint() == -1) return null;
    return new TintProvider.Index(quad.tint());
  }

  public static Vector3f getStartPos(MMRAthenaQuad quad, Direction direction) {
    return switch (direction) {
      case NORTH -> new Vector3f((1 - quad.right()) * 16f, quad.top() * 16f, quad.depth() * 16f);
      case SOUTH -> new Vector3f(quad.left() * 16f, quad.top() * 16f, (1 - quad.depth()) * 16f);
      case WEST -> new Vector3f(quad.depth() * 16f, quad.top() * 16f, quad.left() * 16f);
      case EAST -> new Vector3f((1 - quad.depth()) * 16f, quad.top() * 16f, (1 - quad.right()) * 16f);
      case DOWN -> new Vector3f(quad.left() * 16f, quad.depth() * 16f, quad.top() * 16f);
      case UP -> new Vector3f(quad.left() * 16f, (1 - quad.depth()) * 16f, (1 - quad.bottom()) * 16f);
    };
  }

  public static Vector3f getEndPos(MMRAthenaQuad quad, Direction direction) {
    return switch (direction) {
      case NORTH -> new Vector3f((1 - quad.left()) * 16f, quad.bottom() * 16f, quad.depth() * 16f);
      case SOUTH -> new Vector3f(quad.right() * 16f, quad.bottom() * 16f, (1 - quad.depth()) * 16f);
      case WEST -> new Vector3f(quad.depth() * 16f, quad.bottom() * 16f, quad.right() * 16f);
      case EAST -> new Vector3f((1 - quad.depth()) * 16f, quad.bottom() * 16f, (1 - quad.left()) * 16f);
      case DOWN -> new Vector3f(quad.right() * 16f, quad.depth() * 16f, quad.bottom() * 16f);
      case UP -> new Vector3f(quad.right() * 16f, quad.depth() * 16f, (1 - quad.top()) * 16f);
    };
  }

  /**
   * Parity port of the 1.21.1 {@code CtmUtils#bakeQuad}: a single {@link org.joml.Vector3f} slice of a
   * {@code MMRAthenaQuad} baked through the same vertex selection the vanilla element bake used
   * ({@code FaceInfo.fromFacing(direction.getOpposite())} of the element, {@code shade = false},
   * per-quad {@code BlockFaceUV} rotation) on top of the 26.1 {@code CuboidFace}/
   * {@code BakedQuad.MaterialInfo} primitives.
   *
   * <p>Deliberately NOT delegating to {@code ForgeAthenaUtils#bakeQuad}: Athena 4.7.3 computes
   * start/end positions and vertex order differently (its CTM UVs end up 180° rotated compared to
   * what MMR shipped on 1.21.1).
   */
  public static List<BakedQuad> bakeQuad(MMRAthenaQuad quad, Direction direction, Material.Baked material,
                                         TintProvider tint) {
    final TextureAtlasSprite sprite = material.sprite();
    final Vector3f start = getStartPos(quad, direction);
    final Vector3f end = getEndPos(quad, direction);
    final FaceInfo faceInfo = FaceInfo.fromFacing(direction.getOpposite());

    final var pos1 = faceInfo.getVertexInfo(0).select(start, end).div(16f);
    final var pos2 = faceInfo.getVertexInfo(1).select(start, end).div(16f);
    final var pos3 = faceInfo.getVertexInfo(2).select(start, end).div(16f);
    final var pos4 = faceInfo.getVertexInfo(3).select(start, end).div(16f);
    final var normalDirection = findClosestDirection(pos1, pos2, pos3);

    final float[] uvs = MMRAthenaBlockElementFace.getUVs(start, end, direction);
    final CuboidFace.UVs cuboidUVs = new CuboidFace.UVs(uvs[0], uvs[1], uvs[2], uvs[3]);
    final Quadrant quadrant = switch (quad.rotation()) {
      case NONE -> Quadrant.R0;
      case CLOCKWISE_90 -> Quadrant.R90;
      case CLOCKWISE_180 -> Quadrant.R180;
      case COUNTERCLOCKWISE_90 -> Quadrant.R270;
    };

    final Transparency transparency = material.forceTranslucent() ? Transparency.TRANSLUCENT : computeTransparency(sprite, cuboidUVs);
    final int tintIndex = tint instanceof TintProvider.Index(int index) ? index : -1;
    final BakedQuad.MaterialInfo materialInfo = BakedQuad.MaterialInfo.of(
        material,
        transparency,
        tintIndex,
        false,
        0,
        true
    );
    final BakedColors colors = tint instanceof TintProvider.Static(var color) ? new BakedColors.PerQuad(color) : BakedColors.DEFAULT;

    final BakedQuad bakedQuad = new BakedQuad(
        pos1, pos2, pos3, pos4,
        packUV(cuboidUVs, quadrant, sprite, 0),
        packUV(cuboidUVs, quadrant, sprite, 1),
        packUV(cuboidUVs, quadrant, sprite, 2),
        packUV(cuboidUVs, quadrant, sprite, 3),
        Objects.requireNonNullElse(normalDirection, Direction.UP),
        materialInfo,
        BakedNormals.UNSPECIFIED,
        colors
    );
    return List.of(bakedQuad);
  }

  private static Transparency computeTransparency(TextureAtlasSprite sprite, CuboidFace.UVs uvs) {
    return sprite.contents().computeTransparency(
        Math.min(uvs.minU(), uvs.maxU()) / 16.0F,
        Math.min(uvs.minV(), uvs.maxV()) / 16.0F,
        Math.max(uvs.minU(), uvs.maxU()) / 16.0F,
        Math.max(uvs.minV(), uvs.maxV()) / 16.0F
    );
  }

  private static long packUV(CuboidFace.UVs uvs, Quadrant rotation, TextureAtlasSprite sprite, int index) {
    var u = CuboidFace.getU(uvs, rotation, index);
    var v = CuboidFace.getV(uvs, rotation, index);
    return UVPair.pack(sprite.getU(u), sprite.getV(v));
  }

  private static Direction findClosestDirection(Vector3f pos1, Vector3f pos2, Vector3f pos3) {
    Vector3f normal = new Vector3f();
    GeometryUtils.normal(pos1, pos2, pos3, normal);
    if (!normal.isFinite()) {
      return null;
    }
    Direction best = null;
    float closestProduct = 0.0F;
    for (Direction candidate : Direction.values()) {
      float product = normal.dot(candidate.getUnitVec3f());
      if (product >= 0.0F && product > closestProduct) {
        closestProduct = product;
        best = candidate;
      }
    }
    return best;
  }
}
