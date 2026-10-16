package es.degrassi.mmr.common.block.prop;

import es.degrassi.mmr.api.core.EnumRepresentable;
import es.degrassi.mmr.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.network.server.component.SUpdateFluidComponentPacket;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

public enum FluidHatchSize implements EnumRepresentable, ConfigLoaded {
  TINY(100),
  SMALL(400),
  NORMAL(1000),
  REINFORCED(2000),
  BIG(4500),
  HUGE(8000),
  LUDICROUS(16000),
  VACUUM(32000);

  @Getter
  public int size;

  public final int defaultConfigurationValue;

  FluidHatchSize(int defaultConfigurationValue) {
    this.defaultConfigurationValue = defaultConfigurationValue;
  }

  public static FluidHatchSize value(String value) {
    return switch(value.toUpperCase(Locale.ENGLISH)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "VACUUM" -> VACUUM;
      default -> TINY;
    };
  }

  public FluidHandler buildTank(BlockEntitySynchronized tileEntity, boolean canFill, boolean canDrain) {
    FluidHandler tank;
    tank = buildDefaultTank(tileEntity, canFill, canDrain);
    return tank;
  }

  private FluidHandler buildDefaultTank(BlockEntitySynchronized tileEntity, boolean canFill, boolean canDrain) {
    var handler = new FluidHandler(canFill ? new int[] { 0 } : new int[]{}, canDrain ? new int[] { 0 } : new int[]{}, this.size);
    handler.setListener((slot, fluidStack) -> {
      if (tileEntity.getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(tileEntity.getBlockPos()),
            new SUpdateFluidComponentPacket(slot, fluidStack, tileEntity.getBlockPos()));
    });
    return handler;
  }
}
