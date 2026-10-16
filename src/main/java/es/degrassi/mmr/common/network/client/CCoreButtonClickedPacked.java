package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CCoreButtonClickedPacked(BlockPos entityPos, int cores) implements CustomPacketPayload {
  public static final Type<CCoreButtonClickedPacked> TYPE = new Type<>(ModularMachineryReborn.rl(
      "core_button_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CCoreButtonClickedPacked> CODEC = new StreamCodec<>() {
    @Override
    public CCoreButtonClickedPacked decode(RegistryFriendlyByteBuf buffer) {
      return new CCoreButtonClickedPacked(buffer.readBlockPos(), buffer.readInt());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CCoreButtonClickedPacked value) {
      buffer.writeBlockPos(value.entityPos);
      buffer.writeInt(value.cores);
    }
  };

  @Override
  public Type<CCoreButtonClickedPacked> type() {
    return TYPE;
  }

  public static void handle(CCoreButtonClickedPacked packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player && player.level().getBlockEntity(packet.entityPos) instanceof ParallelHatchEntity entity) {
      entity.setCores(packet.cores);
    }
  }
}
