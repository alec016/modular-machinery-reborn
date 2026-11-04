package es.degrassi.mmr.common.block.base;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.EnergyHatchUI;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class BlockEnergyHatch extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable,
    BlockUIMenuType.BlockUI, SizedBlockHatch<EnergyHatchSize> {

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof EnergyHatchEntity entity) {
      return EnergyHatchUI.create(holder, entity);
    }
    return null;
  }

  @Getter
  protected final EnergyHatchSize size;
  public BlockEnergyHatch(Properties id, EnergyHatchSize type) {
    super(id);
    this.size = type;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction side) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }
}