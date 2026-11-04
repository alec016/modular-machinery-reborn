package es.degrassi.mmr.client.model.hatch;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.ModularMachineryRebornClient;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchBakedModel;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultHatchBakedModel implements DynamicBlockStateModel {

  public static final DefaultHatchBakedModel INSTANCE = new DefaultHatchBakedModel();

  private DefaultHatchBakedModel() {
  }

  private @Nullable BlockStateModel delegate(@Nullable BlockAndTintGetter level, BlockPos pos, @Nullable BlockState state) {
    ModelData modelData = level instanceof IBlockGetterExtension extension
        ? extension.getModelData(pos)
        : ModelData.EMPTY;
    Identifier modelRL = ModularMachineryReborn.rl("default/hatch_all");
    if (modelData != ModelData.EMPTY) {
      HatchTextureData textureData = modelData.get(HatchBakedModel.TEXTURE_DATA);
      if (textureData != null && textureData.defaultModel() != null) {
        modelRL = textureData.defaultModel();
      }
    }
    Direction facing = state != null && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
        ? state.getValue(BlockStateProperties.HORIZONTAL_FACING)
        : null;
    return ModularMachineryRebornClient.standaloneModel(modelRL, facing);
  }

  @Override
  public void collectParts(@Nullable BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull RandomSource random, @NotNull List<BlockStateModelPart> output) {
    BlockStateModel delegate = delegate(level, pos, state);
    if (delegate != null) {
      delegate.collectParts(random, output);
    }
  }

  @Override
  public @NotNull Material.Baked particleMaterial() {
    BlockStateModel delegate = delegate(null, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    return delegate != null
        ? delegate.particleMaterial()
        : Minecraft.getInstance().getModelManager()
            .getBlockStateModelSet()
            .getParticleMaterial(Blocks.AIR.defaultBlockState());
  }

  @Override
  public int materialFlags() {
    BlockStateModel delegate = delegate(null, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    return delegate != null ? delegate.materialFlags() : 0;
  }
}
