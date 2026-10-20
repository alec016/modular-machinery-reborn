package es.degrassi.mmr.common.block;

import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.WeatherSensorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockWeatherSensor extends BlockMachineComponent {
  public BlockWeatherSensor(Properties id) {
    super(id);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new WeatherSensorEntity(pos, state);
  }

}
