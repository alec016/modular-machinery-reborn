package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FluidOutputHatchEntity extends FluidTankEntity implements IAutoOutputEntity<ResourceHandler<FluidResource>> {

  public FluidOutputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.FLUID_OUTPUT_HATCH.get(), pos, state, FluidHatchSize.TINY, IOType.OUTPUT);
  }

  public FluidOutputHatchEntity(BlockPos pos, BlockState state, FluidHatchSize size) {
    super(EntityRegistration.FLUID_OUTPUT_HATCH.get(), pos, state, size, IOType.OUTPUT);
  }

  public void tickAutoOutput() {
    if (!this.getConfig().isEnabled()) return;
    for (Direction side : getTank().getAccessibleSides()) {
      try (Transaction root = Transaction.openRoot()) {
        if (!getConfig().canAutoIO(side)) continue;
        if (getTank().isEmpty()) continue;
        ResourceHandler<FluidResource> neighbour = getNeighbour(Capabilities.Fluid.BLOCK, side);
        if (neighbour == null)
          continue;
        ResourceHandlerUtil.moveStacking(getTank(), neighbour, _ -> true, Integer.MAX_VALUE, root);
        root.commit();
      }
    }
  }
}
