package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class EnergyHatchContainer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.container;
//
// import es.degrassi.mmr.client.ModularMachineryRebornClient;
// import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.chat.Component;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.MenuProvider;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import org.jetbrains.annotations.NotNull;
//
// import java.util.concurrent.atomic.AtomicInteger;
//
// public class EnergyHatchContainer extends ContainerBase<EnergyHatchEntity> {
//   public static void open(ServerPlayer player, EnergyHatchEntity machine) {
//     player.openMenu(new MenuProvider() {
//       @Override
//       public @NotNull Component getDisplayName() {
//         return Component.translatable("modular_machinery_reborn.gui.title.energy_hatch");
//       }
//
//       @Override
//       public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
//         return new EnergyHatchContainer(id, inv, machine);
//       }
//     }, buf -> buf.writeBlockPos(machine.getBlockPos()));
//   }
//
//   public EnergyHatchContainer(int id, Inventory playerInv, EnergyHatchEntity entity) {
//     super(entity, playerInv.player, null, id);
//   }
//
//   public EnergyHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
//     this(id, inv, ModularMachineryRebornClient.getClientSideEnergyHatchEntity(buffer.readBlockPos()));
//   }
//
//   @Override
//   public void init() {
//     super.init();
//     addSyncedSlot(new SlotItemComponent(
//         getEntity().getCapabilityInventory().getInventory().get(0),
//         new AtomicInteger(this.getFirstComponentSlotIndex()).getAndIncrement(),
//         35 + 8,
//         10 + 61/2 - 8
//     ));
//   }
// }
//
