package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.client.screen.ItemDurabilityUI;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRows;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.block.base.SizedBlockHatch;
import es.degrassi.mmr.common.block.base.TooltipAppendable;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BlockDurabilityHatch extends BlockMachineComponent
    implements BlockTickEntity, TooltipAppendable, BlockUIMenuType.BlockUI, IBlockUIState<VisibleRows>, SizedBlockHatch<ItemDurabilityHatchSize> {

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof DurabilityHatchEntity entity) {
      return ItemDurabilityUI.create(holder, entity);
    }
    return null;
  }

  @Getter
  protected final ItemDurabilityHatchSize size;
  public BlockDurabilityHatch(Properties id, ItemDurabilityHatchSize size) {
    super(id);
    this.size = size;
  }

  @Override
  protected MapCodec<BlockDurabilityHatch> codec() {
    return BlockCodecs.DURABILITY.mapCodec();
  }

  @Override
  public VisibleRows fallback() {
    return VisibleRows.SMALL;
  }

  @Override
  public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag pTooltipFlag) {
    tooltip.accept(
      size.getSlotCount() == 1 ?
        Component.translatable("tooltip.itembus.slot") :
        Component.translatable("tooltip.itembus.slots", size.getSlotCount())
    );
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new DurabilityHatchEntity(blockPos, blockState, this.size);
  }
}
