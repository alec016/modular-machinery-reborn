package es.degrassi.mmr.client.integration.athena.model.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelAttributes;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.TintProvider;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.CtmState;
import es.degrassi.mmr.client.integration.athena.utils.MMRMaterialMap;
import es.degrassi.mmr.client.integration.athena.model.MMRAthenaBlockModel;
import es.degrassi.mmr.client.integration.athena.utils.CtmUtils;
import es.degrassi.mmr.client.integration.athena.utils.MMRAthenaQuad;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class ControllerBlockModel implements MMRAthenaBlockModel {
  public static final AthenaModelFactory FACTORY = new Factory();
  final MMRMaterialMap materials;
  private final BiPredicate<BlockState, BlockState> connectTo;
  @Getter
  private final AthenaModelAttributes attributes;

  public ControllerBlockModel(MMRMaterialMap materials, BiPredicate<BlockState, BlockState> connectTo,
                         AthenaModelAttributes attributes) {
    this.materials = materials;
    this.connectTo = connectTo;
    this.attributes = attributes;
  }
  @Override
  public List<MMRAthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState state, BlockPos pos, Direction direction, Void ignored) {
    if (CtmUtils.checkRelative(level, state, pos, direction)) return List.of();

    final CtmState ctm = CtmState.from(level, state, pos, direction, CtmUtils.check(level, state, pos, direction,
        (other, self) -> connectTo.test(other, self) && BlockMachineComponent.connectsTextures(other) && BlockMachineComponent.connectsTextures(self)));

    if (ctm.allTrue()) return List.of(MMRAthenaQuad.withSprite(materials.getTexture(direction, 1), 4));

    List<MMRAthenaQuad> quads = Lists.newArrayList();

    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.up(), ctm.left(), ctm.upLeft(), 0, 0.5f, 1f, 0.5f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.up(), ctm.right(), ctm.upRight(), 0.5f, 1f, 1f, 0.5f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.down(), ctm.left(), ctm.downLeft(), 0, 0.5f, 0.5f, 0f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.down(), ctm.right(), ctm.downRight(), 0.5f, 1f, 0.5f, 0f, 4));

    if (
        materials.getTexture(direction, 6) != 0 &&
        materials.getTexture(direction, 7) != 0 &&
        materials.getTexture(direction, 8) != 0 &&
        materials.getTexture(direction, 9) != 0
    ) {
      var quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.up(), ctm.left(), ctm.upLeft(), 0, 0.5f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.up(), ctm.right(), ctm.upRight(), 0.5f, 1f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.down(), ctm.left(), ctm.downLeft(), 0, 0.5f, 0.5f, 0f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.down(), ctm.right(), ctm.downRight(), 0.5f, 1f, 0.5f, 0f, -1);
      quads.add(quad);
    }

    return quads;
  }

  @Override
  public Map<Direction, List<MMRAthenaQuad>> getDefaultQuads(Direction direction, Void ignored) {
    if (direction == null) return Map.of();
    Map<Direction, List<MMRAthenaQuad>> defaultQuads = Maps.newHashMap();

    var quads = defaultQuads.computeIfAbsent(direction, key -> Lists.newArrayList());

    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.0F, 0.5F, 1.0F, 0.5F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.5F, 1.0F, 1.0F, 0.5F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.0F, 0.5F, 0.5F, 0.0F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.5F, 1.0F, 0.5F, 0.0F, 4));

    if (
        materials.getTexture(direction, 6) != 0 &&
        materials.getTexture(direction, 7) != 0 &&
        materials.getTexture(direction, 8) != 0 &&
        materials.getTexture(direction, 9) != 0
    ) {
      var quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0, 0.5f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0.5f, 1f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0, 0.5f, 0.5f, 0f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0.5f, 1f, 0.5f, 0f, -1);
      quads.add(quad);
    }

    return defaultQuads;
  }

  @Override
  public Int2ObjectMap<Material.Baked> getTextures(Function<Material, Material.Baked> function) {
    return this.materials.getTextures(function);
  }

  public static class Factory implements AthenaModelFactory {
    private Factory() {
    }

    public Supplier<AthenaBlockModel> create(JsonObject json) {
      MMRMaterialMap materials = CtmUtils.tryParse(GsonHelper.getAsJsonObject(json, "ctm_textures"), Factory::parseDefaultMaterials);
      if (materials == null) {
        materials = CtmUtils.tryParse(GsonHelper.getAsJsonObject(json, "ctm_textures"), Factory::parseMaterials);
      }

      if (materials == null) {
        throw new JsonSyntaxException("Expected either ctm_textures to have 5-6 entries for all textures or have " +
            "directional textures for each direction or to have some directions and a default textures object.");
      } else {
        BiPredicate<BlockState, BlockState> conditions = CtmUtils.parseCondition(json);
        MMRMaterialMap finalMaterials = materials;
        return () -> new ControllerBlockModel(finalMaterials, conditions, new AthenaModelAttributes(new TintProvider.Index(4)));
      }
    }

    private static MMRMaterialMap parseMaterials(JsonObject json) {
      MMRMaterialMap materials = new MMRMaterialMap();

      for(Direction direction : Direction.values()) {
        if (GsonHelper.isStringValue(json, direction.getSerializedName())) {
          materials.put(direction, CtmUtils.blockMat(GsonHelper.getAsString(json, direction.getSerializedName())));
        } else {
          Int2ObjectMap<Material> directionMaterials = CtmUtils.parseCtmMaterials(GsonHelper.getAsJsonObject(json, direction.getSerializedName(), GsonHelper.getAsJsonObject(json, "default")));
          materials.put(direction, directionMaterials);
        }
      }

      return materials;
    }

    private static MMRMaterialMap parseDefaultMaterials(JsonObject json) {
      Int2ObjectMap<Material> materials = CtmUtils.parseCtmMaterials(json);
      MMRMaterialMap connectedTextureMap = new MMRMaterialMap();

      for(Direction direction : Direction.values()) {
        connectedTextureMap.put(direction, materials);
      }

      return connectedTextureMap;
    }
  }
}
