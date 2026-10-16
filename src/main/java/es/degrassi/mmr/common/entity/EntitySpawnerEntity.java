package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.entity.base.EntityBaseEntity;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EntitySpawnerEntity extends EntityBaseEntity {
  public EntitySpawnerEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.ENTITY_SPAWNER.get(), pos, blockState, RequirementEntity.Action.SPAWN);
  }
}
