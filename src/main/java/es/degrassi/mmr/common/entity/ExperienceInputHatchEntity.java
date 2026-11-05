package es.degrassi.mmr.common.entity;

import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ExperienceInputHatchEntity extends ExperienceHatchEntity implements IAutoInputEntity<IExperienceHandler>
    , IPickResultEntity {

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state, ExperienceHatchSize.TINY, IOType.INPUT);
  }

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state, ExperienceHatchSize size) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Override
  public void tickAutoInput() {
    if (!this.getConfig().isEnabled()) return;
    long prevXp = this.getTank().getAmountAsLong(0);
    for (Direction face : Direction.values()) {
      if (!getConfig().canAutoIO(face)) continue;
      var ce = getNeighbour(ExperienceLibCapabilities.EXPERIENCE.block(), face);
      if (ce == null) continue;
      attemptXPTransfer(ce, getTank(), Long.MAX_VALUE);
    }

    if (prevXp != this.getTank().getAmountAsLong(0)) {
      markForUpdate();
    }
  }

  @Override
  public void decode(CompoundTag tag) {
    var amount = tag.getLongOr("amount", 0L);
    try (Transaction root = Transaction.openRoot()) {
      getTank().insertRecipe(0, amount, root);
      root.commit();
    }
  }
}
