package es.degrassi.mmr.client.model.casing;

import es.degrassi.mmr.client.model.BasicUnbakedModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;

public class CasingModel implements BasicUnbakedModel {
  @Override
  public BlockStateModel bake(BlockState state, ModelBaker baker) {
    return new CasingBakedModel(material -> baker.materials().get(material, () -> "MMR: ?"));
  }
}
