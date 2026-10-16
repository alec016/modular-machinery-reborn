package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class EnergyInputHatchEntity extends EnergyHatchEntity implements IAutoInputEntity<EnergyHandler> {

  public EnergyInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, EnergyHatchSize.TINY, IOType.INPUT);
  }

  public EnergyInputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Override
  public void tickAutoInput() {
    if (!getConfig().isEnabled()) return;
    long prevEnergy = this.energy;

    long transferCap = Math.min(this.size.transferLimit, this.getMaxEnergy() - this.energy);
    for (Direction face : Direction.values()) {
      if (!getConfig().canAutoIO(face)) continue;
      if (transferCap > 0) {
        int transferred = attemptFETransfer(face, convertDownEnergy(transferCap));
        transferCap -= transferred;
        this.energy += transferred;
      }
      if (transferCap <= 0) {
        break;
      }
    }

    if (prevEnergy != this.energy) {
      markForUpdate();
    }
  }

  private int attemptFETransfer(Direction face, int maxTransferLeft) {
    try (var root = Transaction.open(null)) {
      BlockPos at = this.getBlockPos().relative(face);

      int receivedEnergy = 0;
      BlockEntity te = level.getBlockEntity(at);
      if (te != null && !(te instanceof EnergyHatchEntity)) {
        var ce = getNeighbour(Capabilities.Energy.BLOCK, face);
        if (ce != null) {
          try(var tx = Transaction.open(root)) {
            receivedEnergy = ce.extract(maxTransferLeft, tx);
            tx.commit();
          }
        }
      }
      root.commit();
      return receivedEnergy;
    }
  }
}
