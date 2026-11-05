package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.common.util.MMRPermissions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CFillHatchRequestPacket(BlockPos pos, CompoundTag data) implements CustomPacketPayload {

  public static final int DURABILITY_UNUSED = -1;

  public static final Type<CFillHatchRequestPacket> TYPE = new Type<>(ModularMachineryReborn.rl("fill_hatch_request"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CFillHatchRequestPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CFillHatchRequestPacket::pos,
      ByteBufCodecs.COMPOUND_TAG,
      CFillHatchRequestPacket::data,
      CFillHatchRequestPacket::new
  );

  @Override
  public Type<CFillHatchRequestPacket> type() {
    return TYPE;
  }

  public static void handle(CFillHatchRequestPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> {
        if (!MMRPermissions.canFillHatch(player)) return;
        var entity = player.level().getBlockEntity(packet.pos);
        if (entity instanceof IPickResultEntity pickEntity) {
          pickEntity.decode(packet.data);
        }
      });
    }
  }
}
