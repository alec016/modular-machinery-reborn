package es.degrassi.mmr.common.block.base;

import com.google.common.primitives.Ints;
import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmr.client.screen.ExperienceHatchUI;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public abstract class BlockExperienceHatch extends BlockMachineComponent implements BlockTickEntity,
    TooltipAppendable, BlockUIMenuType.BlockUI, SizedBlockHatch<ExperienceHatchSize> {

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof ExperienceHatchEntity entity) {
      return ExperienceHatchUI.create(holder, entity);
    }
    return null;
  }
  @Getter
  protected final ExperienceHatchSize size;

  public BlockExperienceHatch(Properties id, ExperienceHatchSize size) {
    super(id);
    this.size = size;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    tooltip.accept(
        Component.translatable("tooltip.experiencehatch.tank.info", size.getCapacity()).withStyle(ChatFormatting.GRAY)
    );
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction side) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }

  @Override
  public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
    if (level.isClientSide()) return super.getExpDrop(state, level, pos, blockEntity, breaker, tool);
    if (level.getBlockEntity(pos) instanceof ExperienceHatchEntity entity) {
      IExperienceHandler tank = entity.getTank();
      if (tank.getAmountAsLong(0) > 0) {
        try (Transaction tx = Transaction.open(null)) {
          for (int i = 0; i < tank.size(); i++) {
            try (Transaction real = Transaction.open(tx)) {
              int extracted = Ints.saturatedCast(tank.extractRecipe(i, Integer.MAX_VALUE, real));
              if (extracted <= 0) continue;
              popExperience((ServerLevel) level, pos, extracted);
              real.commit();
              return extracted;
            }
          }
          tx.commit();
          return 0;
        }
      }
    }
    return super.getExpDrop(state, level, pos, blockEntity, breaker, tool);
  }
}
