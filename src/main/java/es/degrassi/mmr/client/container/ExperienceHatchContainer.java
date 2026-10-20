package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ExperienceHatchContainer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.container;
//
// import es.degrassi.mmr.client.ModularMachineryRebornClient;
// import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
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
// public class ExperienceHatchContainer extends ContainerBase<ExperienceHatchEntity> {
//   public static void open(ServerPlayer player, ExperienceHatchEntity machine) {
//     player.openMenu(new MenuProvider() {
//       @Override
//       public @NotNull Component getDisplayName() {
//         return machine.getBlockState().getBlock().getName();
//       }
//
//       @Override
//       public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
//         return new ExperienceHatchContainer(machine, player, id);
//       }
//     }, buf -> buf.writeBlockPos(machine.getBlockPos()));
//   }
//   public ExperienceHatchContainer(ExperienceHatchEntity entity, Player player, int containerId) {
//     super(entity, player, null, containerId);
//   }
//
//   public ExperienceHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
//     this(ModularMachineryRebornClient.getClientSideExperienceHatchEntity(buffer.readBlockPos()), inv.player, id);
//   }
//
//   @Override
//   public void init() {
//     super.init();
//     addSyncedSlot(new SlotItemComponent(
//         getEntity().getCapabilityInventory().getInventory().get(0),
//         new AtomicInteger(this.getFirstComponentSlotIndex()).getAndIncrement(),
//         176 / 2 - 8,
//         47
//     ));
//   }
// }
//
