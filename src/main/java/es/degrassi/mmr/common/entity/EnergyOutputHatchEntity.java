package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class EnergyOutputHatchEntity extends EnergyHatchEntity implements IAutoOutputEntity<EnergyHandler> {

  public EnergyOutputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_OUTPUT_HATCH.get(), pos, state, EnergyHatchSize.TINY, IOType.OUTPUT);
  }

  public EnergyOutputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_OUTPUT_HATCH.get(), pos, state, size, IOType.OUTPUT);
  }

  @Override
  public void tickAutoOutput() {
    if (!getConfig().isEnabled()) return;
    long prevEnergy = this.energy;

    long transferCap = Math.min(this.size.transferLimit, this.energy);
    for (Direction face : Direction.values()) {
      if (!getConfig().canAutoIO(face)) continue;
      if (transferCap > 0) {
        int transferred = attemptFETransfer(face, convertDownEnergy(transferCap));
        transferCap -= transferred;
        this.energy -= transferred;
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
    try (Transaction root = Transaction.open(null)) {
      BlockPos at = this.getBlockPos().relative(face);

      int receivedEnergy = 0;
      BlockEntity te = level.getBlockEntity(at);
      if (te != null && !(te instanceof EnergyHatchEntity)) {
        var ce = getNeighbour(Capabilities.Energy.BLOCK, face);
        if (ce != null) {
          try (Transaction tx = Transaction.open(root)) {
            receivedEnergy = ce.insert(maxTransferLeft, tx);
            tx.commit();
          }
        }
      }
      root.commit();
      return receivedEnergy;
    }
  }
}
