package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ParallelHatchContainer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.container;
//
// import es.degrassi.mmr.client.ModularMachineryRebornClient;
// import es.degrassi.mmr.common.entity.ParallelHatchEntity;
// import es.degrassi.mmr.common.registration.ContainerRegistration;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.chat.Component;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.MenuProvider;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import org.jetbrains.annotations.NotNull;
//
// public class ParallelHatchContainer extends ContainerBase<ParallelHatchEntity> {
//   public static void open(ServerPlayer player, ParallelHatchEntity machine) {
//     player.openMenu(new MenuProvider() {
//       @Override
//       public @NotNull Component getDisplayName() {
//         return Component.translatable("modular_machinery_reborn.gui.title.parallel_hatch");
//       }
//
//       @Override
//       public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
//         return new ParallelHatchContainer(id, inv, machine);
//       }
//     }, buf -> buf.writeBlockPos(machine.getBlockPos()));
//   }
//
//   public ParallelHatchContainer(int id, Inventory playerInv, ParallelHatchEntity entity) {
//     super(entity, playerInv.player, ContainerRegistration.PARALLEL_HATCH.get(), id);
//   }
//
//   public ParallelHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
//     this(id, inv, ModularMachineryRebornClient.getClientSideParallelHatchEntity(buffer.readBlockPos()));
//   }
// }
//
