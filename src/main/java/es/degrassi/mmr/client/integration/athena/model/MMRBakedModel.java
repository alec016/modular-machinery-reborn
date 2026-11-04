package es.degrassi.mmr.client.integration.athena.model;

import earth.terrarium.athena.api.client.neoforge.WrappedGetter;
import earth.terrarium.athena.api.client.models.TintProvider;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.NullableEnumMap;
import es.degrassi.mmr.client.integration.athena.utils.CtmUtils;
import es.degrassi.mmr.client.integration.athena.utils.MMRMaterialMap;
import es.degrassi.mmr.client.integration.athena.utils.MMRAthenaQuad;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MMRBakedModel extends BlockStateModel {
  Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

  Int2ObjectMap<Material.Baked> getTextures();

  MMRMaterialMap getMaterialMap();

  MMRAthenaBlockModel getModel();

  default List<BakedQuad> bakeQuads(List<MMRAthenaQuad> quads, Direction direction) {
    List<BakedQuad> bakedQuads = new ArrayList<>(quads.size());
    for (MMRAthenaQuad quad : quads) {
      Material.Baked material = getTextures().get(quad.sprite());
      if (material == null) continue;
      bakedQuads.addAll(CtmUtils.bakeQuad(quad, direction, material, getTint(quad)));
    }
    return bakedQuads;
  }

  default boolean addOverlay(Direction direction, @Nullable BlockState state) {
    MMRMaterialMap map = getMaterialMap();
    return map != null && map.getTexture(direction, 5) != 0;
  }

  static NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> buildGeometryKey(
      BlockAndTintGetter level, BlockPos pos, BlockState state, MMRAthenaBlockModel model) {
    AppearanceAndTintGetter getter = new WrappedGetter(level);
    final NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> quads = new NullableEnumMap<>(Direction.class);
    Map<Direction, List<MMRAthenaQuad>> nonCullQuads = new HashMap<>();
    for (Direction direction : DIRECTIONS) {
      List<MMRAthenaQuad> culledQuads = new ArrayList<>();
      List<MMRAthenaQuad> unculledQuads = new ArrayList<>();
      for (MMRAthenaQuad quad : model.getQuads(getter, state, pos, direction, null)) {
        if (quad.cull()) {
          culledQuads.add(quad);
        } else {
          unculledQuads.add(quad);
        }
      }
      quads.put(direction, Map.of(direction, culledQuads));
      nonCullQuads.put(direction, unculledQuads);
    }
    quads.put(null, nonCullQuads);
    return quads;
  }

  @Override
  default void collectParts(@NotNull RandomSource random, @NotNull List<BlockStateModelPart> parts) {
    parts.add(new DefaultPart(this));
  }

  @Override
  default void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @NotNull RandomSource random, @NotNull List<BlockStateModelPart> parts) {
    parts.add(new CTMPart(this, state, buildGeometryKey(level, pos, state, getModel())));
  }

  @Override
  default @NotNull Material.Baked particleMaterial() {
    return particle(getTextures());
  }

  @Override
  default @BakedQuad.MaterialFlags int materialFlags() {
    return materialFlags(getTextures());
  }

  static Material.Baked particle(@NotNull Int2ObjectMap<Material.Baked> materials) {
    if (materials.containsKey(0)) return materials.get(0);
    return new Material.Baked(
        Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).missingSprite(),
        false
    );
  }

  static @BakedQuad.MaterialFlags int materialFlags(@NotNull Int2ObjectMap<Material.Baked> materials) {
    int flags = 0;
    for (Material.Baked material : materials.values()) {
      var contents = material.sprite().contents();
      if (material.forceTranslucent() || contents.computeTransparency(0f, 0f, 1f, 1f).hasTranslucent()) {
        flags |= BakedQuad.FLAG_TRANSLUCENT;
      }
      if (contents.isAnimated()) {
        flags |= BakedQuad.FLAG_ANIMATED;
      }
    }
    return flags;
  }

  static TintProvider getTint(MMRAthenaQuad quad) {
    return quad.tint() == -1 ? null : new TintProvider.Index(quad.tint());
  }

  final class CTMPart implements BlockStateModelPart {
    private final MMRBakedModel model;
    private final Map<Direction, List<BakedQuad>> cache = new HashMap<>();
    private final NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> geometry;
    private final BlockState state;

    public CTMPart(MMRBakedModel model, BlockState state,
                   NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> geometry) {
      this.model = model;
      this.geometry = geometry;
      this.state = state;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable Direction side) {
      List<BakedQuad> cached = cache.get(side);
      if (cached != null) return cached;

      List<BakedQuad> baked = new ArrayList<>();
      Map<Direction, List<MMRAthenaQuad>> values = geometry.getOrDefault(side, Map.of());
      values.forEach((dir, quadList) -> {
        baked.addAll(model.bakeQuads(quadList, dir));
        if (model.addOverlay(dir, state)) {
          baked.addAll(model.bakeQuads(List.of(MMRAthenaQuad.withSprite(5, -0.0001F, -1)), dir));
        }
      });
      cache.put(side, baked);
      return baked;
    }

    @Override
    public boolean useAmbientOcclusion() {
      return true;
    }

    @Override
    public @NotNull Material.Baked particleMaterial() {
      return MMRBakedModel.particle(model.getTextures());
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
      return MMRBakedModel.materialFlags(model.getTextures());
    }
  }

  final class DefaultPart implements BlockStateModelPart {
    private final MMRBakedModel model;
    private final Map<Direction, List<BakedQuad>> cache = new HashMap<>();

    public DefaultPart(MMRBakedModel model) {
      this.model = model;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable Direction side) {
      List<BakedQuad> cached = cache.get(side);
      if (cached != null) return cached;

      List<BakedQuad> baked = new ArrayList<>();
      model.getModel().getDefaultQuads(side, null).forEach((dir, quadList) -> {
        baked.addAll(model.bakeQuads(quadList, dir));
        if (model.addOverlay(dir, null)) {
          baked.addAll(model.bakeQuads(List.of(MMRAthenaQuad.withSprite(5, -0.0001F, -1)), dir));
        }
      });
      cache.put(side, baked);
      return baked;
    }

    @Override
    public boolean useAmbientOcclusion() {
      return true;
    }

    @Override
    public @NotNull Material.Baked particleMaterial() {
      return MMRBakedModel.particle(model.getTextures());
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
      return MMRBakedModel.materialFlags(model.getTextures());
    }
  }
}
