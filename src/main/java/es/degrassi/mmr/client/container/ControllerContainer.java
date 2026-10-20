package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ControllerContainer {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.container;
//
// import es.degrassi.experiencelib.api.network.syncable.IntegerSyncable;
// import es.degrassi.mmr.client.ModularMachineryRebornClient;
// import es.degrassi.mmr.common.entity.MachineControllerEntity;
// import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
// import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
// import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.MenuProvider;
// import net.minecraft.world.entity.player.Inventory;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.inventory.AbstractContainerMenu;
// import net.minecraft.world.inventory.ContainerInput;
//
// import java.util.List;
//
// public class ControllerContainer extends ContainerBase<MachineControllerEntity> {
//   private int corePage = 1;
//   private final Int2ObjectMap<List<MachineProcessorCore>> pages = new Int2ObjectArrayMap<>();
//   public Int2ObjectMap<Component> dynamicTooltips = new Int2ObjectArrayMap<>();
//
//   public static void open(ServerPlayer player, MachineControllerEntity machine) {
//     player.openMenu(new MenuProvider() {
//       @Override
//       public Component getDisplayName() {
//         return Component.translatable("modular_machinery_reborn.gui.title.controller");
//       }
//
//       @Override
//       public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
//         return new ControllerContainer(id, inv, machine);
//       }
//     }, buf -> buf.writeBlockPos(machine.getBlockPos()));
//   }
//
//   public ControllerContainer(int id, Inventory playerInv, MachineControllerEntity entity) {
//     super(entity, playerInv.player, null, id);
//   }
//
//   public ControllerContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
//     this(id, inv, ModularMachineryRebornClient.getClientSideMachineControllerEntity(buffer.readBlockPos()));
//   }
//
//   @Override
//   public void clicked(int slotId, int button, ContainerInput containerInput, Player player) {
//     super.clicked(slotId, button, containerInput, player);
//   }
//
//   @Override
//   public void init() {
//     super.init();
//     stuffToSync.add(IntegerSyncable.create(() -> corePage, i -> corePage = i));
//   }
//
//   public List<MachineProcessorCore> getPage() {
//     return entity.getPages().get(corePage);
//   }
//
//   public int getCurrentCorePage() {
//     return corePage;
//   }
//
//   public int getPagesNumber() {
//     return entity.getPages().size();
//   }
//
//   public void setPage(int corePage) {
//     this.corePage = corePage;
//   }
//
//   public Identifier getId() {
//     return getEntity().getFoundMachine().getRegistryName();
//   }
//
//   public void setTooltips(Int2ObjectArrayMap<Component> tooltips) {
//     this.dynamicTooltips = tooltips;
//   }
// }
//
