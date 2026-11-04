package es.degrassi.mmr.common.block.base;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.client.screen.ItemBusUI;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRows;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public abstract class BaseItemBus extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable, BlockUIMenuType.BlockUI,
    IBlockUIState<VisibleRows>, SizedBlockHatch<ItemBusSize> {
  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof TileItemBus entity) {
      return ItemBusUI.create(holder, entity);
    }
    return null;
  }

  @Getter
  private final ItemBusSize size;
  protected BaseItemBus(Properties properties, ItemBusSize size) {
    super(properties);
    this.size = size;
  }

  @Override
  public VisibleRows fallback() {
    return VisibleRows.SMALL;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    tooltip.accept(
        getSize().getSlotCount() == 1 ?
            Component.translatable("tooltip.itembus.slot") :
            Component.translatable("tooltip.itembus.slots", getSize().getSlotCount())
    );
  }
}
