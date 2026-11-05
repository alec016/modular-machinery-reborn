package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FluidInputHatchEntity extends FluidTankEntity implements IAutoInputEntity<ResourceHandler<FluidResource>>, IPickResultEntity {

  public FluidInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state, FluidHatchSize.TINY, IOType.INPUT);
  }

  public FluidInputHatchEntity(BlockPos pos, BlockState state, FluidHatchSize size) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  public void tickAutoInput() {
    if (!this.getConfig().isEnabled()) return;
    for (Direction side : getTank().getAccessibleSides()) {
      try (Transaction root = Transaction.openRoot()) {
        if (!getConfig().canAutoIO(side)) continue;
        ResourceHandler<FluidResource> neighbour = getNeighbour(Capabilities.Fluid.BLOCK, side);
        if (neighbour == null) continue;
        ResourceHandlerUtil.moveStacking(neighbour, getTank(), _ -> true, Integer.MAX_VALUE, root);
        root.commit();
      }
    }
  }

  @Override
  public void decode(CompoundTag tag) {
    var amount = tag.getLongOr("amount", 0L);
    var fluid = tag.read("fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
    if (fluid.isEmpty()) return;
    if (getTank().addToOutputs(fluid, amount) > 0) setChanged();
  }
}
