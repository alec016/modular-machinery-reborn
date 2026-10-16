package es.degrassi.mmr.api.capability;

import com.google.common.base.Suppliers;
import es.degrassi.mmr.common.entity.base.EntityBaseEntity;
import es.degrassi.mmr.common.util.MMRDamageSource;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityHandler {
  private final Supplier<MMRDamageSource> damageSource;
  private final EntityBaseEntity delegate;

  public EntityHandler(EntityBaseEntity entity) {
    this.damageSource = Suppliers.memoize(() -> new MMRDamageSource(entity.getController(), entity));
    this.delegate = entity;
  }

  public int getEntitiesInRadius(int radius, Predicate<Entity> filter) {
    BlockPos pos = delegate.getBlockPos();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    return delegate.getLevel()
        .getEntitiesOfClass(Entity.class, bb, entity -> entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius && filter.test(entity))
        .size();
  }

  public double getEntitiesInRadiusHealth(int radius, Predicate<Entity> filter) {
    BlockPos pos = delegate.getBlockPos();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    return delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .stream()
        .mapToDouble(LivingEntity::getHealth)
        .sum();
  }

  public boolean canHealEntitiesInRadius(int radius, Predicate<Entity> filter, int amount) {
    BlockPos pos = delegate.getBlockPos();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    return delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .stream()
        .mapToDouble(entity -> entity.getMaxHealth() - entity.getHealth())
        .sum() >= amount;
  }

  public boolean canHurtEntitiesInRadius(int radius, Predicate<Entity> filter, int amount) {
    BlockPos pos = delegate.getBlockPos();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    return delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .stream()
        .mapToDouble(LivingEntity::getHealth)
        .sum() >= amount;
  }

  public void removeEntitiesHealth(int radius, Predicate<Entity> filter, int amount) {
    BlockPos pos = delegate.getBlockPos();
    AtomicInteger toRemove = new AtomicInteger(amount);
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .forEach(entity -> {
          int maxRemove = Math.min((int)entity.getHealth(), toRemove.get());
          entity.hurt(this.damageSource.get(), maxRemove);
          toRemove.addAndGet(-maxRemove);
        });
  }

  public void addEntitiesHealth(int radius, Predicate<Entity> filter, int amount) {
    BlockPos pos = delegate.getBlockPos();
    AtomicReference<Float> toAdd = new AtomicReference<>(amount * 1f);
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .forEach(entity -> {
          float maxAdd = Math.min(entity.getMaxHealth() - entity.getHealth(), toAdd.get());
          entity.heal(maxAdd);
          toAdd.set(toAdd.get() - maxAdd);
        });
  }

  public void killEntities(int radius, Predicate<Entity> filter, int amount) {
    BlockPos pos = delegate.getBlockPos();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    delegate.getLevel()
        .getEntitiesOfClass(LivingEntity.class, bb, entity -> filter.test(entity) && entity.distanceToSqr(Utils.vec3dFromBlockPos(pos)) <= radius * radius)
        .stream()
        .limit(amount)
        .forEach(entity -> entity.hurt(this.damageSource.get(), Float.MAX_VALUE));
  }

  public boolean spawnEntities(int radius, int amount, EntityType<?> type) {
    BlockPos pos = delegate.getBlockPos();
    RandomSource rand = delegate.getLevel().getRandom();
    AABB bb = new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    for (int i = 0; i < amount; i++) {
      BlockPos toSpawnPos;
      int x = rand.nextIntBetweenInclusive((int) bb.minX, (int) bb.maxX);
      int y = rand.nextIntBetweenInclusive((int) bb.minY, (int) bb.maxY);
      int z = rand.nextIntBetweenInclusive((int) bb.minZ, (int) bb.maxZ);
      toSpawnPos = new BlockPos(x, y, z);
      if(!delegate.getLevel().getBlockState(toSpawnPos).isAir()) {
        i--;
        continue;
      };
      type.spawn(
          (ServerLevel) delegate.getLevel(),
          toSpawnPos,
          EntitySpawnReason.SPAWNER
      );
    }
    return true;
  }
}
