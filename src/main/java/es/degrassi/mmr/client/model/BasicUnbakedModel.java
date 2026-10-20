package es.degrassi.mmr.client.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.level.block.state.BlockState;

public interface BasicUnbakedModel extends BlockStateModel.UnbakedRoot {
  @Override
  default void resolveDependencies(ResolvableModel.Resolver resolver) {
  }

  @Override
  default Object visualEqualityGroup(BlockState blockState) {
    return this;
  }
}
