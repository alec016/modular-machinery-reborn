package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ItemDurabilityContainer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.container;
//
// import es.degrassi.mmr.client.ModularMachineryRebornClient;
// import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
// import es.degrassi.mmr.common.data.config.ItemBusConfig;
// import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
// import es.degrassi.mmr.common.registration.ContainerRegistration;
// import es.degrassi.mmr.common.manager.handler.ItemHandler;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.chat.Component;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.MenuProvider;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import net.minecraft.world.inventory.Slot;
// import org.jetbrains.annotations.NotNull;
//
// import java.util.concurrent.atomic.AtomicInteger;
//
// public class ItemDurabilityContainer extends ContainerBase<DurabilityHatchEntity> {
//
//   public static void open(ServerPlayer player, DurabilityHatchEntity machine) {
//     player.openMenu(new MenuProvider() {
//       @Override
//       public @NotNull Component getDisplayName() {
//         return Component.translatable("modular_machinery_reborn.gui.title.durability_hatch");
//       }
//
//       @Override
//       public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
//         return new ItemDurabilityContainer(id, inv, machine);
//       }
//     }, buf -> buf.writeBlockPos(machine.getBlockPos()));
//   }
//
//   public ItemDurabilityContainer(int id, Inventory playerInv, DurabilityHatchEntity entity) {
//     super(entity, playerInv.player, ContainerRegistration.ITEM_DURABILITY_HATCH.get(), id);
//   }
//
//   public ItemDurabilityContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
//     this(id, inv, ModularMachineryRebornClient.getClientSideItemDurabilityEntity(buffer.readBlockPos()));
//   }
//
//   @Override
//   public void init() {
//     super.init();
//     addInventorySlots(getEntity().getInventory(), getEntity().getSize(), new AtomicInteger(this.getFirstComponentSlotIndex()));
//   }
//
//   protected void addPlayerSlots(AtomicInteger slotIndex) {
//     int rows = (int) Math.ceil(getEntity().getSlots()*1d / getEntity().getSize().cols);
//     int yOffset = rows * 18 + 18;
//     int lastYOffset;
//     for (int i = 0; i < 9; i++) {
//       addSyncedSlot(new Slot(player.getInventory(), slotIndex.getAndIncrement(), 8 + i * 18, yOffset + 18 * 3 + 3));
//     }
//     for (int i = 0; i < 3; i++) {
//       for (int j = 0; j < 9; j++) {
//         lastYOffset = yOffset + i * 18;
//         addSyncedSlot(new Slot(player.getInventory(), slotIndex.getAndIncrement(), 8 + j * 18, lastYOffset));
//       }
//     }
//   }
//
//   protected void addInventorySlots(ItemHandler itemHandler, ItemDurabilityHatchSize size, AtomicInteger atomicInteger) {
//     int xOffset = ItemBusConfig.get().itemSlotXOffset.get();
//     int yOffset = ItemBusConfig.get().itemSlotYOffset.get();
//     int cols = size.cols;
//     int row = 0;
//     for (int s = 0, c = 0; s < size.slots; s++, c++) {
//       if (c >= cols) {
//         c = 0;
//         row++;
//       }
//       addSyncedSlot(new SlotItemComponent(itemHandler.getInventory().get(s), atomicInteger.getAndIncrement(), xOffset + c * 18, yOffset + row * 18));
//     }
//   }
// }
//
