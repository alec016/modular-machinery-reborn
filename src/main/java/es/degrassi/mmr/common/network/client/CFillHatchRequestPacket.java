package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.util.MMRPermissions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Fill command from the operator-only picker tab of the input hatch screens (fluid hatch, item
 * input bus, fuel tank, durability hatch, energy input hatch, experience input hatch).
 *
 * <p>Carries the fill {@link #kind}, the selected template stack ({@link ItemStack} or
 * {@link FluidStack}, one empty for the kindless energy/experience fills), the requested amount
 * and the per-stack durability the client wants the inserted items to carry ({@link
 * #durabilityRemaining}; only used by the durability hatch fill). The server answers with the
 * {@code gamemaster}/custom fill permission check ({@link MMRPermissions#canFillHatch}) and pours
 * the amount through the handler bypassing the external throughput limits, exactly like the
 * machine auto-input would do. The request never carries more than the client saw synced, and the
 * handlers cap the response again against filters and capacity, so a stale slider value can never
 * overfill anything.
 */
public record CFillHatchRequestPacket(
    BlockPos pos, byte kind, int amount, ItemStack item, FluidStack fluid, int durabilityRemaining)
    implements CustomPacketPayload {

  public static final byte KIND_ITEM = 0;
  public static final byte KIND_FLUID = 1;
  public static final byte KIND_ENERGY = 2;
  public static final byte KIND_EXPERIENCE = 3;
  /** Sentinel for the kinds that don't set per-stack damage in {@link #durabilityRemaining}. */
  public static final int DURABILITY_UNUSED = -1;

  public static final Type<CFillHatchRequestPacket> TYPE = new Type<>(es.degrassi.mmr.ModularMachineryReborn.rl(
      "fill_hatch_request"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CFillHatchRequestPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CFillHatchRequestPacket::pos,
      ByteBufCodecs.BYTE,
      CFillHatchRequestPacket::kind,
      ByteBufCodecs.INT,
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
        int amount = Mth.clamp(packet.amount, 0, Integer.MAX_VALUE);
        if (amount <= 0) {
          return;
        }
        switch (packet.kind) {
          case KIND_ITEM -> {
            // Item input bus, fuel tank and durability hatch; output buses stay untouched by the
            // tab, and the fill respects the handler filter (addToOutputs validates every slot).
            // The durability hatch fill arrives with the damage component already set by the
            // client (max durability - the requested remaining durability), so the same pour
            // covers it; the per-slot change listeners then sync the client copies and UI.
            if (player.level().getBlockEntity(packet.pos) instanceof TileInventory inventory) {
              if (inventory instanceof TileItemBus bus && !bus.getIoType().isInput()) {
                return;
              }
              if (packet.item != null && !packet.item.isEmpty()) {
                if (inventory.getInventory().addToOutputs(packet.item, amount) > 0) {
                  inventory.setChanged();
                }
              }
            }
          }
          case KIND_FLUID -> {
            if (player.level().getBlockEntity(packet.pos) instanceof FluidTankEntity tank) {
              if (tank.getMode().isInput() && packet.fluid != null && !packet.fluid.isEmpty()) {
                if (tank.getTank().addToOutputs(packet.fluid, amount) > 0) {
                  tank.setChanged();
                }
              }
            }
          }
          case KIND_ENERGY -> {
            // Direct buffer pour: the transfer limit does not apply to operator fills.
            if (player.level().getBlockEntity(packet.pos) instanceof EnergyHatchEntity energy
                && energy.getMode().isInput()) {
              energy.getEnergyHandler().set(energy.getEnergyHandler().getAmountAsLong() + amount);
            }
          }
          case KIND_EXPERIENCE -> {
            if (player.level().getBlockEntity(packet.pos) instanceof ExperienceHatchEntity hatch
                && hatch.getMode().isInput()) {
              // Insert is amount-based; commit fires the tank's change hook, which sends the
              // experience sync packet so the owner's UI bar refreshes right away.
              try (var transaction = Transaction.openRoot()) {
                hatch.getTank().insert(0, amount, transaction);
                transaction.commit();
              }
            }
          }
          default -> { }
        }
      });
    }
  }
}
