package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

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
    long prevEnergy = this.getEnergyHandler().getAmountAsLong();

    for (Direction face : Direction.values()) {
      if (!getConfig().canAutoIO(face)) continue;
      var ce = getNeighbour(Capabilities.Energy.BLOCK, face);
      if (ce == null) continue;
      attemptFETransfer(getEnergyHandler(), ce, Long.MAX_VALUE);
    }

    if (prevEnergy != this.getEnergyHandler().getAmountAsLong()) {
      markForUpdate();
    }
  }
}
