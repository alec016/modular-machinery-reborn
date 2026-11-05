package es.degrassi.mmr.client.screen.widget.tabs.fill;

import es.degrassi.mmr.api.codec.BigStackCodecs;
import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.ItemKind;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.ItemPick;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemFillTab extends AbstractFillTab<ItemHandler, ItemStack, ItemPick> {
  protected ItemFillTab(@Nullable Player player, BlockEntity entity, @Nullable Supplier<ItemHandler> handler, @Nullable Predicate<ItemStack> filter) {
    super(player, entity, new ItemKind(FILL_OPEN_HEIGHT, filter), handler, null, filter);
  }

  @Override
  protected CompoundTag encodePick() {
    var tag = super.encodePick();
    if (selected != null) {
      var item = selected.stack().copy();
      tag.store("item", BigStackCodecs.OPTIONAL.codec(), item);
    }
    return tag;
  }

  @Override
  protected boolean isCompiling() {
    if (handler != null && handler.get() != null && handler.get().isCompiling()) {
      return true;
    }
    return entity instanceof TileInventory inventory && inventory.getInventory().isCompiling();
  }

  @Override
  protected long currentMax() {
    if (selected == null) {
      return 0L;
    }
    return handler == null || handler.get() == null ? 0L : handler.get().getSpaceForItem(selected.stack());
  }

  @Override
  protected void additionalFields() {}

  @Override
  protected void updateActive(boolean disable) {}

  @Override
  protected boolean isFull() {
    return handler != null && handler.get() != null && handler.get().isFull();
  }
}
