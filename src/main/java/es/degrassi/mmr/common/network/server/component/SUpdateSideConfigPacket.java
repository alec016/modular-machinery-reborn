package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Pushes a side config's modes from the server to every player tracking the block. It is fired by
 * {@link ISideConfigComponent#configChanged} on every server-side {@code setSideMode} mutation
 * (auto-IO configurator flips, wrench writes, ...). Without it a side flip never reached the
 * clients: only the chunk-level block entity sync carried side configs, so GUI tooltips, scene
 * face colors and click predictions kept the pre-change data until a world reload.
 */
public record SUpdateSideConfigPacket(BlockPos pos, int modes) implements CustomPacketPayload {

  public static final Type<SUpdateSideConfigPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_side_config"));
  @Override
  public Type<SUpdateSideConfigPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateSideConfigPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SUpdateSideConfigPacket::pos,
      ByteBufCodecs.VAR_INT,
      SUpdateSideConfigPacket::modes,
      SUpdateSideConfigPacket::new
  );

  /** One bit per relative side: bit {@code i} = side {@code RelativeSide.values()[i]} enabled. */
  public static int maskOf(IOSideConfig config) {
    RelativeSide[] sides = RelativeSide.values();
    int mask = 0;
    for (int i = 0; i < sides.length; i++) {
      if (config.getSideMode(sides[i]).isEnabled()) {
        mask |= 1 << i;
      }
    }
    return mask;
  }

  /** Applies a received mask; client-side write, so the config change callback stays silent. */
  public static void apply(IOSideConfig config, int mask) {
    RelativeSide[] sides = RelativeSide.values();
    for (int i = 0; i < sides.length; i++) {
      IOSideMode mode = (mask >> i & 1) == 1 ? IOSideMode.ENABLED : IOSideMode.DISABLED;
      if (config.getSideMode(sides[i]) != mode) {
        config.setSideMode(sides[i], mode);
      }
    }
  }

  public static void handle(SUpdateSideConfigPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof ISideConfigComponent<?> component
            && component.getConfig() instanceof IOSideConfig config) {
          apply(config, packet.modes);
        }
      });
    }
  }
}
