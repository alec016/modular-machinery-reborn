package es.degrassi.mmr.common.util;

import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

@Getter
public class Chunkloader implements ValueIOSerializable {
  private final ChunkloaderEntity entity;
  private int radius;
  private int tempo = -1;
  private boolean active;

  public Chunkloader(ChunkloaderEntity entity, int radius, boolean active) {
    this.active = active;
    this.radius = radius;
    this.entity = entity;
  }

  public Chunkloader(ChunkloaderEntity entity) {
    this(entity, 1, false);
  }

  private static final TicketType MACHINE_CHUNKLOADER = Registry.register(BuiltInRegistries.TICKET_TYPE, "chunkloader", new TicketType(0L, TicketType.FLAG_PERSIST));

  public void setActive(ServerLevel level, int radius) {
    if(this.active)
      this.setInactive(level);

    this.active = true;
    this.radius = radius;

    BlockPos machinePos = getEntity().getBlockPos();
    ChunkPos chunk = ChunkPos.containing(machinePos);
    level.setChunkForced(chunk.getRegionX(), chunk.getRegionZ(), true);
    level.getChunkSource().addTicketAndLoadWithRadius(MACHINE_CHUNKLOADER, chunk, radius + 1);
  }

  public void setActiveWithTempo(ServerLevel level, int radius, int tempo) {
    this.tempo = Math.max(this.tempo, tempo);
    if(!this.active || this.radius < radius)
      this.setActive(level, radius);
  }

  public void setInactive(ServerLevel level) {
    this.active = false;

    BlockPos machinePos = getEntity().getBlockPos();
    ChunkPos chunk = ChunkPos.containing(machinePos);
    if(ChunkloaderList.findInSameChunk(getEntity()).isEmpty())
      level.setChunkForced(chunk.getRegionX(), chunk.getRegionZ(), false);
    level.getChunkSource().removeTicketWithRadius(MACHINE_CHUNKLOADER, chunk, this.radius + 1);
  }

  public void serverTick() {
    if(this.tempo >= 0 && this.tempo-- == 0)
      this.setInactive((ServerLevel) getEntity().getLevel());
  }

  public void onRemoved() {
    if(getEntity().getLevel() instanceof ServerLevel level && !getEntity().isUnloaded())
      this.setInactive(level);
  }

  public void init() {
    if (this.active && getEntity().getLevel() instanceof ServerLevel level) {
      ChunkPos pos = ChunkPos.containing(getEntity().getBlockPos());
      if(level.getChunk(pos.getRegionX(), pos.getRegionZ(), ChunkStatus.EMPTY, false) instanceof LevelChunk)
        this.setActive(level, this.radius);
      else
        TaskDelayer.enqueue(1, () -> this.setActive(level, this.radius));
    }
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putBoolean("active", this.active);
    output.putInt("radius", this.radius);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.active = input.getBooleanOr("active", false);
    input.getInt("radius").ifPresent(radius -> this.radius = radius);
  }
}
