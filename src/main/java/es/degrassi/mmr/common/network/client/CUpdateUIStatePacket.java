package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.api.uistate.UIState;
import es.degrassi.mmr.api.uistate.UIStates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CUpdateUIStatePacket<S extends UIState>(BlockPos pos, S value) implements CustomPacketPayload {
  public static final Type<CUpdateUIStatePacket<?>> TYPE = new Type<>(ModularMachineryReborn.rl("update_ui_state_client"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CUpdateUIStatePacket<?>> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CUpdateUIStatePacket::pos,
      UIState.CODEC.streamCodec(),
      CUpdateUIStatePacket::value,
      CUpdateUIStatePacket::new
  );

  @Override
  public Type<CUpdateUIStatePacket<?>> type() {
    return TYPE;
  }

  @SuppressWarnings("unchecked")
  public static <S extends UIState> void handle(CUpdateUIStatePacket<S> packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      var blockState = player.level().getBlockState(packet.pos);
      if (blockState.getBlock() instanceof IBlockUIState<?> state) {
        UIStates.put(player, packet.pos, (IBlockUIState<S>) state, packet.value);
      }
    }
  }
}
