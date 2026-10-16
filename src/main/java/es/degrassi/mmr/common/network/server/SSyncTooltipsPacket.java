package es.degrassi.mmr.common.network.server;

import com.mojang.datafixers.util.Either;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.util.MMRLogger;
import es.degrassi.mmr.common.util.TextComponentUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.EnumMap;
import java.util.List;

public record SSyncTooltipsPacket(Identifier machineId,
                                  EnumMap<TooltipUse, List<Either<Identifier, Component>>> tooltips) implements CustomPacketPayload {
  public static final Type<SSyncTooltipsPacket> TYPE = new Type<>(ModularMachineryReborn.rl("sync_tooltips"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncTooltipsPacket> CODEC = new StreamCodec<>() {
    @Override
    public SSyncTooltipsPacket decode(RegistryFriendlyByteBuf buf) {
      var rl = buf.readIdentifier();
      EnumMap<TooltipUse, List<Either<Identifier, Component>>> tooltips = new EnumMap<>(TooltipUse.class);
      var map = buf.readMap(TooltipUse.CODEC::fromNetwork, NamedCodec.either(DefaultCodecs.IDENTIFIER,
          TextComponentUtils.CODEC).listOf()::fromNetwork);
      tooltips.putAll(map);
      return new SSyncTooltipsPacket(rl, tooltips);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SSyncTooltipsPacket packet) {
      try {
        buf.writeIdentifier(packet.machineId);
        buf.writeMap(
            packet.tooltips,
            TooltipUse.CODEC::toNetwork,
            NamedCodec.either(DefaultCodecs.IDENTIFIER, TextComponentUtils.CODEC).listOf()::toNetwork
        );
      } catch (Exception e) {
        MMRLogger.INSTANCE.error("Encode error: ", e);
      }
    }
  };

  @Override
  public Type<SSyncTooltipsPacket> type() {
    return TYPE;
  }

  public static void handle(SSyncTooltipsPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.put(packet.machineId, packet.tooltips);
    }
  }
}
