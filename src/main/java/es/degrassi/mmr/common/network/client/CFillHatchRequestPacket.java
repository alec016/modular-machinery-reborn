package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.util.MMRPermissions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.transaction.Transaction;

// TODO: fi this with the updated version of FillTab
public record CFillHatchRequestPacket(
    BlockPos pos, Identifier kind, long amount, ItemStack item, FluidStack fluid, int durabilityRemaining)
    implements CustomPacketPayload {

  public static final int DURABILITY_UNUSED = -1;

  public static final Type<CFillHatchRequestPacket> TYPE = new Type<>(ModularMachineryReborn.rl("fill_hatch_request"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CFillHatchRequestPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CFillHatchRequestPacket::pos,
      Identifier.STREAM_CODEC,
      CFillHatchRequestPacket::kind,
      ByteBufCodecs.VAR_LONG,
      CFillHatchRequestPacket::amount,
      ItemStack.OPTIONAL_STREAM_CODEC,
      CFillHatchRequestPacket::item,
      FluidStack.OPTIONAL_STREAM_CODEC,
      CFillHatchRequestPacket::fluid,
      ByteBufCodecs.INT,
      CFillHatchRequestPacket::durabilityRemaining,
      CFillHatchRequestPacket::new
  );

  @Override
  public Type<CFillHatchRequestPacket> type() {
    return TYPE;
  }

  public static void handle(CFillHatchRequestPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> {
        if (!MMRPermissions.canFillHatch(player)) {
          return;
        }
        long amount = Mth.clamp(packet.amount, 0L, Long.MAX_VALUE);
        if (amount <= 0) {
          return;
        }
        var kind = packet.kind;
        if (kind.equals(FillTab.Kind.ITEM.id())) {
            if (player.level().getBlockEntity(packet.pos) instanceof TileInventory inventory) {
              if (inventory instanceof TileItemBus bus && !bus.getIoType().isInput()) {
                return;
              }
              // The grid may still be compiling: its slots do not exist yet.
              if (inventory.getInventory().isCompiling()) {
                return;
              }
              if (packet.item != null && !packet.item.isEmpty()) {
                if (inventory.getInventory().addToInputs(packet.item, Math.clamp(amount, 0, Integer.MAX_VALUE)) > 0) {
                  inventory.setChanged();
                }
              }
            }
        } else if (kind.equals(FillTab.Kind.FLUID.id())) {
            if (player.level().getBlockEntity(packet.pos) instanceof FluidTankEntity tank) {
              if (tank.getMode().isInput() && packet.fluid != null && !packet.fluid.isEmpty()) {
                if (tank.getTank().addToOutputs(packet.fluid, amount) > 0) {
                  tank.setChanged();
                }
              }
            }
          } else if (kind.equals(FillTab.Kind.ENERGY.id())) {
            if (player.level().getBlockEntity(packet.pos) instanceof EnergyHatchEntity energy
                && energy.getMode().isInput()) {
              long headroom = Math.max(0L, energy.getEnergyHandler().getCapacityAsLong()
                  - energy.getEnergyHandler().getAmountAsLong());
              energy.getEnergyHandler().set(energy.getEnergyHandler().getAmountAsLong() + Math.min(amount, headroom));
            }
          } else if (kind.equals(FillTab.Kind.EXPERIENCE.id())) {
            if (player.level().getBlockEntity(packet.pos) instanceof ExperienceHatchEntity hatch
                && hatch.getMode().isInput()) {
              try (var transaction = Transaction.openRoot()) {
                hatch.getTank().insert(0, amount, transaction);
                transaction.commit();
              }
            }
          }

      });
    }
  }
}
