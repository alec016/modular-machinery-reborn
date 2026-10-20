package es.degrassi.mmr.client.integration.athena.model;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import es.degrassi.mmr.client.integration.athena.utils.MMRAthenaQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

public interface MMRAthenaBlockModel extends AthenaBlockModel {
  List<MMRAthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState state, BlockPos pos,
                               Direction direction, Void ignored);
  Map<Direction, List<MMRAthenaQuad>> getDefaultQuads(Direction direction, Void ignored);

  default List<AthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState state, BlockPos pos,
                            Direction direction) {
    return List.of();
  }
  default Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction) {
    return Map.of();
  }
}
