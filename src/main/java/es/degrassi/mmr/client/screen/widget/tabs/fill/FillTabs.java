package es.degrassi.mmr.client.screen.widget.tabs.fill;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.Kind;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.util.MMRPermissions;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
public final class FillTabs {
  /**
   * Size is defined in {@link AbstractFillTab#AbstractFillTab(Player, BlockEntity, Kind, Supplier, LongSupplier, Predicate)}
   */
  public static void fillItemTab(UIElement parent, Player player, BlockEntity entity, int x, int y,
                                 @Nullable Supplier<ItemHandler> handler, @Nullable Predicate<ItemStack> filter) {
    if (!MMRPermissions.canFillHatch(player)) return;
    MMRMachineUI.add(parent, new ItemFillTab(player, entity, handler, filter), x, y);
  }
  
  /**
   * Size is defined in {@link AbstractFillTab#AbstractFillTab(Player, BlockEntity, Kind, Supplier, LongSupplier, Predicate)}
   */
  public static void fillDurabilityTab(UIElement parent, Player player, BlockEntity entity,
                                       int x, int y, @Nullable Supplier<ItemHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) return;
    MMRMachineUI.add(parent, new DurabilityFillTab(player, entity, handler, stack -> stack.isDamageableItem() && stack.getMaxDamage() > 0), x, y);
  }

  /**
   * Size is defined in {@link AbstractFillTab#AbstractFillTab(Player, BlockEntity, Kind, Supplier, LongSupplier, Predicate)}
   */
  public static void fillFluidTab(UIElement parent, Player player, BlockEntity entity, int x, int y,
                                  @Nullable Supplier<FluidHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) return;
    MMRMachineUI.add(parent, new FluidFillTab(player, entity, handler), x, y);
  }

  /**
   * Size is defined in {@link AbstractFillTab#AbstractFillTab(Player, BlockEntity, Kind, Supplier, LongSupplier, Predicate)}
   */
  public static void fillEnergyTab(UIElement parent, Player player, BlockEntity entity, int x, int y,
                                   LongSupplier space) {
    if (!MMRPermissions.canFillHatch(player)) return;
    MMRMachineUI.add(parent, new EnergyFillTab(player, entity, space), x, y);
  }

  /**
   * Size is defined in {@link AbstractFillTab#AbstractFillTab(Player, BlockEntity, Kind, Supplier, LongSupplier, Predicate)}
   */
  public static void fillExperienceTab(UIElement parent, Player player, BlockEntity entity, int x, int y,
                                   LongSupplier space) {
    if (!MMRPermissions.canFillHatch(player)) return;
    MMRMachineUI.add(parent, new ExperienceFillTab(player, entity, space), x, y);
  }
}
