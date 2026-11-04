package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.uistate.UIState;
import es.degrassi.mmr.client.ClientUIStates;
import es.degrassi.mmr.api.save.UIStateSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record SUpdateUIStatePacket(ResourceKey<Level> dimension, Optional<UUID> player, BlockPos pos,
                                   Optional<UIState> value) implements CustomPacketPayload {
  public SUpdateUIStatePacket(ResourceKey<Level> dimension, @Nullable UUID player, BlockPos pos, @Nullable UIState value) {
    this(
      dimension,
      Optional.ofNullable(player),
      pos,
      Optional.ofNullable(value)
    );
  }
  public static final Type<SUpdateUIStatePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_ui_state"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateUIStatePacket> CODEC = StreamCodec.composite(
      ResourceKey.streamCodec(Registries.DIMENSION),
      SUpdateUIStatePacket::dimension,
      UIStateSavedData.OPTIONAL_UUID_CODEC.streamCodec(),
      SUpdateUIStatePacket::player,
      BlockPos.STREAM_CODEC,
      SUpdateUIStatePacket::pos,
      UIState.OPTIONAL_CODEC.streamCodec(),
      SUpdateUIStatePacket::value,
      SUpdateUIStatePacket::new
  );

  @Override
  public Type<SUpdateUIStatePacket> type() {
    return TYPE;
  }

  public static void handle(SUpdateUIStatePacket packet, IPayloadContext context) {
    ClientUIStates.register();
    if (packet.player.isEmpty() || packet.value.isEmpty()) {
      ClientUIStates.getInstance().remove(packet.dimension, packet.pos);
      return;
    }
    ClientUIStates.getInstance().putValue(packet.dimension, packet.player.get(), packet.pos, packet.value.get());
  }
}
