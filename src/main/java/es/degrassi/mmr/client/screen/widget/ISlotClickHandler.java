package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public interface ISlotClickHandler {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.client.container.SlotItemComponent;
// import net.minecraft.world.item.ItemStack;
// import org.jetbrains.annotations.Nullable;
//
// import java.util.function.Supplier;
//
// public interface ISlotClickHandler {
//
//   void onClick(Supplier<@Nullable SlotItemComponent> slotProvider, int button, boolean hasShiftDown, ItemStack heldItem);
// }
//
