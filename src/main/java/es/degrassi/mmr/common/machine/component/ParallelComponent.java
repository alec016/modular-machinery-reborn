package es.degrassi.mmr.common.machine.component;

import com.google.gson.JsonObject;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.network.server.component.SUpdateCoresPacket;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ParallelComponent extends MachineComponent<Integer> {
  private int cores;
  private final ParallelHatchSize parallel;

  public ParallelComponent(ParallelHatchSize parallel) {
    super(IOType.NONE);
    this.parallel = parallel;
    this.cores = 1;
  }

  @Override
  public ComponentType<Integer> getComponentType() {
    return ComponentRegistration.COMPONENT_PARALLEL.get();
  }

  @Override
  @NotNull
  public Integer getContainerProvider() {
    return cores;
  }

  public Integer getMaxCores() {
    return parallel.max;
  }

  public void setCores(int cores, Level level, BlockPos pos) {
    this.cores = Math.min(cores, parallel.max);
    if (level instanceof ServerLevel sl)
      PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(pos), new SUpdateCoresPacket(cores, pos));
  }

  @Override
  public void asTag(ValueOutput output) {
    super.asTag(output);
    output.putInt("cores", cores);
    output.putInt("maxCores", parallel.max);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("cores", cores);
    json.addProperty("maxCores", parallel.max);
    return json;
  }

  @Override
  public <C extends MachineComponent<Integer>> boolean canMerge(C c) {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Integer>> C merge(C c) {
    return (C) this;
  }
}
