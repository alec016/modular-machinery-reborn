package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.concurrent.atomic.AtomicInteger;

public class FluidHatchContainer extends ContainerBase<FluidTankEntity> {

  public static void open(ServerPlayer player, FluidTankEntity machine) {
    player.openMenu(new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return Component.translatable("modular_machinery_reborn.gui.title.fluid_hatch");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FluidHatchContainer(id, inv, machine);
      }
    }, buf -> buf.writeBlockPos(machine.getBlockPos()));
  }

  public FluidHatchContainer(int id, Inventory playerInv, FluidTankEntity entity) {
    super(entity, playerInv.player, ContainerRegistration.FLUID_HATCH.get(), id);
  }

  public FluidHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideFluidHatchEntity(buffer.readBlockPos()));
  }

  @Override
  public void init() {
    super.init();
    var index = new AtomicInteger(this.getFirstComponentSlotIndex());
    addSyncedSlot(new SlotItemComponent(
        getEntity().getCapabilityInventory().getInventory().get(0),
        index.getAndIncrement(),
        35 + 8,
        10 + 61/2 - 8
    ));
    addSyncedSlot(new FilterSlotComponent<>(
        getEntity(),
        getEntity().getFilterInventory().getInventory().get(0),
        index.getAndIncrement(),
        35 + 8 + 20,
        10 + 61/2 - 8
    ));
  }
}
