package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.registration.CreativeTabsRegistration;
import es.degrassi.mmr.common.util.MMRLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SSyncMachinePacket(DynamicMachine machine) implements CustomPacketPayload {
  public static final Type<SSyncMachinePacket> TYPE = new Type<>(ModularMachineryReborn.rl("sync_machine"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncMachinePacket> CODEC = new StreamCodec<>() {
    @Override
    public SSyncMachinePacket decode(RegistryFriendlyByteBuf buf) {
      DynamicMachine machine = DynamicMachine.CODEC.fromNetwork(buf);
      return new SSyncMachinePacket(machine);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SSyncMachinePacket packet) {
      DynamicMachine machine = packet.machine;
      try {
        DynamicMachine.CODEC.toNetwork(machine, buf);
      } catch (Exception e) {
        MMRLogger.INSTANCE.error("Encode error: ", e);
      }
    }
  };

  @Override
  public Type<SSyncMachinePacket> type() {
    return TYPE;
  }

  public static void handle(SSyncMachinePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      var machine = packet.machine;
      ModularMachineryReborn.MACHINES.put(machine.getRegistryName(), packet.machine);
      Minecraft mc = Minecraft.getInstance();
      CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(mc.player.connection.enabledFeatures(), mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get(), mc.level.registryAccess());
      CreativeTabsRegistration.MODULAR_MACHINERY_REBORN_TAB.get().buildContents(params);
    }
  }
}
