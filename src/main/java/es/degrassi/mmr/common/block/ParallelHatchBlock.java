package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.ParallelHatchUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.TooltipAppendable;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ParallelHatchBlock extends BlockMachineComponent implements TooltipAppendable, BlockUIMenuType.BlockUI {

  @Override
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof ParallelHatchEntity entity) {
      return ParallelHatchUI.create(holder, entity);
    }
    return null;
  }
  @Getter
  protected final ParallelHatchSize size;
  public ParallelHatchBlock(Properties id, ParallelHatchSize type) {
    super(id);
    this.size = type;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING,
        context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    tooltip.accept(
        Component.translatable(
            "tooltip.parallelhatch.size",
            size.max
        ).withStyle(ChatFormatting.GRAY)
    );
  }

  @Override
  protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (level.isClientSide()) return InteractionResult.SUCCESS;
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ParallelHatchEntity entity) {
      if (player instanceof ServerPlayer sp) {
        BlockUIMenuType.openUI(sp, pos);
      }
      return InteractionResult.SUCCESS;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ParallelHatchEntity(blockPos, blockState, size);
  }

  @Override
  public void onBlockExploded(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion) {
    if (level.getBlockEntity(pos) instanceof ParallelHatchEntity entity) {
      if (level instanceof ServerLevel) {
        if (entity.getController() != null) {
          var controller = entity.getController();
          super.onBlockExploded(state, level, pos, explosion);
          controller.getComponentManager().updateComponents();
          controller.getProcessor().updateActiveCores(1);
        }
      } else {
        super.onBlockExploded(state, level, pos, explosion);
      }
    } else {
      super.onBlockExploded(state, level, pos, explosion);
    }
  }

  @Override
  protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (level.getBlockEntity(pos) instanceof ParallelHatchEntity entity) {
      if (level instanceof ServerLevel) {
        if (entity.getController() != null) {
          var controller = entity.getController();
          super.onRemove(state, level, pos, newState, movedByPiston);
          controller.getComponentManager().updateComponents();
          controller.getProcessor().updateActiveCores(1);
        } else {
          super.onRemove(state, level, pos, newState, movedByPiston);
        }
      } else {
        super.onRemove(state, level, pos, newState, movedByPiston);
      }
    } else {
      super.onRemove(state, level, pos, newState, movedByPiston);
    }
  }


  @Override
  public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
    if (level.getBlockEntity(pos) instanceof ParallelHatchEntity entity) {
      if (level instanceof ServerLevel) {
        if (entity.getController() != null) {
          var controller = entity.getController();
          var toReturn = super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
          controller.getComponentManager().updateComponents();
          controller.getProcessor().updateActiveCores(1);
          return toReturn;
        }
      }
    }
    return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
  }

  @Override
  public void onDestroyedByPushReaction(BlockState state, Level level, BlockPos pos, Direction pushDirection, FluidState fluid) {
    if (level.getBlockEntity(pos) instanceof ParallelHatchEntity entity) {
      if (level instanceof ServerLevel) {
        if (entity.getController() != null) {
          var controller = entity.getController();
          super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
          controller.getComponentManager().updateComponents();
          controller.getProcessor().updateActiveCores(1);
          return;
        }
      }
    }
    super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
  }
}
