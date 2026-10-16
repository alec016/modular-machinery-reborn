package es.degrassi.mmr.common.network.client;

import es.degrassi.experiencelib.util.ExperienceUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.ExperienceButtonType;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public record CExperienceButtonClickedPacket(BlockPos entityPos, ExperienceButtonType amount, boolean extraction) implements CustomPacketPayload {
  public static final Type<CExperienceButtonClickedPacket> TYPE = new Type<>(ModularMachineryReborn.rl(
      "experience_button_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CExperienceButtonClickedPacket> CODEC = new StreamCodec<>() {
    @Override
    public CExperienceButtonClickedPacket decode(RegistryFriendlyByteBuf buffer) {
      return new CExperienceButtonClickedPacket(buffer.readBlockPos(), buffer.readEnum(ExperienceButtonType.class), buffer.readBoolean());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CExperienceButtonClickedPacket value) {
      buffer.writeBlockPos(value.entityPos);
      buffer.writeEnum(value.amount);
      buffer.writeBoolean(value.extraction);
    }
  };

  @Override
  public Type<CExperienceButtonClickedPacket> type() {
    return TYPE;
  }

  public static void handle(CExperienceButtonClickedPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player && player.level().getBlockEntity(packet.entityPos) instanceof ExperienceHatchEntity entity) {
      try (Transaction tx = Transaction.open(null)) {
        if (packet.amount.isAll())
          ExperienceUtils.addAllLevelToPlayer(entity.getTank(), packet.extraction, player, tx);
        else
          ExperienceUtils.addLevelToPlayer(entity.getTank(), packet.amount.getAmount(packet.extraction), player, tx);
        tx.commit();
      }
    }
  }
}
