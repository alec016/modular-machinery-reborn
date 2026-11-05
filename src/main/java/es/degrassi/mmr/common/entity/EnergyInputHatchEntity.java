package es.degrassi.mmr.common.entity;

import com.google.common.primitives.Ints;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class EnergyInputHatchEntity extends EnergyHatchEntity implements IAutoInputEntity<EnergyHandler>, IPickResultEntity {

  public EnergyInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, EnergyHatchSize.TINY, IOType.INPUT);
  }

  public EnergyInputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Override
  public void tickAutoInput() {
    if (!getConfig().isEnabled()) return;
    long prevEnergy = this.getEnergyHandler().getAmountAsLong();

    for (Direction face : Direction.values()) {
      if (!getConfig().canAutoIO(face)) continue;
      var ce = getNeighbour(Capabilities.Energy.BLOCK, face);
      if (ce == null) continue;
      attemptFETransfer(ce, getEnergyHandler(), Long.MAX_VALUE);
    }

    if (prevEnergy != this.getEnergyHandler().getAmountAsLong()) {
      markForUpdate();
    }
  }

  @Override
  public void decode(CompoundTag tag) {
    var amount = tag.getLongOr("amount", 0L);
    try (Transaction root = Transaction.openRoot()) {
      while (amount > 0) {
        int subAmount = Ints.saturatedCast(Math.min(amount, Integer.MAX_VALUE));
        amount -= subAmount;
        try (var subtx = Transaction.open(root)) {
          getEnergyHandler().insert(subAmount, subtx);
          subtx.commit();
        }
      }
      root.commit();
    }
  }
}
