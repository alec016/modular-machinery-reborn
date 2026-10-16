package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.machine.IOType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CRedstoneButtonModeClickedPacket(IOType mode, BlockPos pos) implements CustomPacketPayload {

  public static final Type<CRedstoneButtonModeClickedPacket> TYPE = new Type<>(ModularMachineryReborn.rl("redstone_button_mode_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CRedstoneButtonModeClickedPacket> CODEC = new StreamCodec<>() {
    @Override
    public CRedstoneButtonModeClickedPacket decode(RegistryFriendlyByteBuf buf) {
      return new CRedstoneButtonModeClickedPacket(
          buf.readEnum(IOType.class),
          buf.readBlockPos()
      );
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, CRedstoneButtonModeClickedPacket packet) {
      buf.writeEnum(packet.mode)
          .writeBlockPos(packet.pos);
    }
  };

  @Override
  public Type<CRedstoneButtonModeClickedPacket> type() {
    return TYPE;
  }

  public static void handle(CRedstoneButtonModeClickedPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player && player.level().getBlockEntity(packet.pos) instanceof RedstonePortEntity entity) {
      entity.setMode(packet.mode);
    }
  }
}
