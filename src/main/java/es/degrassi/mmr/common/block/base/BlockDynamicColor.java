package es.degrassi.mmr.common.block.base;

import es.degrassi.mmr.common.data.Config;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockDynamicColor {
  default int getColorMultiplier(BlockState state, @Nullable BlockAndTintGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
    return Config.machineColor;
  }
}
