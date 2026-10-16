package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.entity.base.EntityBaseEntity;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDamagerEntity extends EntityBaseEntity {
  public EntityDamagerEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.ENTITY_DAMAGER.get(), pos, blockState, RequirementEntity.Action.CONSUME_HEALTH);
  }
}
