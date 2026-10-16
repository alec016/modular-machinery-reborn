package es.degrassi.mmr.api.capability;

import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.network.server.component.SUpdateEffectComponent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EffectHandler {
  private final EffectDispenserEntity entity;
  @Getter
  @Setter
  private boolean isApplyingEffect = false;
  @Getter
  @Setter
  private MobEffectInstance effect;
  public EffectHandler(EffectDispenserEntity entity) {
    this.entity = entity;
  }

  private EffectDispenserSize getSize() {
    return entity.getSize();
  }

  public void setData(Optional<MobEffectInstance> effect) {
    effect.ifPresentOrElse(ef -> {
      this.effect = ef;
      this.isApplyingEffect = true;
    }, () -> {
      this.isApplyingEffect = false;
      this.effect = null;
    });
  }

  public void applyEffect(MobEffectInstance effect, Predicate<Entity> filter) {
    this.setData(Optional.of(effect));
    if (getSize().interdimensional) {
      Stream.Builder<ServerLevel> levels = Stream.builder();
      entity.getLevel().getServer().getAllLevels().forEach(levels::add);
      levels
          .build()
          .map(level -> {
            List<LivingEntity> entities = Lists.newArrayList();
            level.getEntities()
                .getAll()
                .forEach(entity -> {
                  if (entity instanceof LivingEntity) entities.add(entity.asLivingEntity());
                });
            return entities;
          })
          .flatMap(List::stream)
          .forEach(entity -> entity.addEffect(effect));
      setChanged();
      return;
    }
    BlockPos machinePos = entity.getBlockPos();
    AABB bb = new AABB(machinePos).inflate(getSize().radius);
    entity.getLevel().getEntitiesOfClass(LivingEntity.class, bb, filter).stream()
        .filter(entity -> entity.distanceToSqr(machinePos.getX(), machinePos.getY(), machinePos.getZ()) < getSize().radius * getSize().radius)
        .forEach(entity -> entity.addEffect(effect));
    setChanged();
  }

  public void resetEffect() {
    setData(Optional.empty());
    setChanged();
  }

  public void setChanged() {
    entity.setChanged();
    if (entity.getLevel() instanceof ServerLevel sl) {
      PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(entity.getBlockPos()),
          new SUpdateEffectComponent(Optional.ofNullable(effect), entity.getBlockPos()));
    }
  }
}
