package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SUpdateEffectComponent(Optional<MobEffectInstance> effect, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateEffectComponent> TYPE = new Type<>(ModularMachineryReborn.rl("update_effect"));
  @Override
  public Type<SUpdateEffectComponent> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateEffectComponent> CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(MobEffectInstance.STREAM_CODEC),
      SUpdateEffectComponent::effect,
      BlockPos.STREAM_CODEC,
      SUpdateEffectComponent::pos,
      SUpdateEffectComponent::new
  );

  public static void handle(SUpdateEffectComponent packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof EffectDispenserEntity entity) {
          entity.getHandler().setData(packet.effect);
        }
      });
  }
}
