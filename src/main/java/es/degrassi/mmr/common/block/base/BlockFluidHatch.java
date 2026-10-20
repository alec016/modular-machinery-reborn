package es.degrassi.mmr.common.block.base;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.FluidHatchUI;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class BlockFluidHatch extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable, BlockUIMenuType.BlockUI {

  @Override
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof FluidTankEntity entity) {
      return FluidHatchUI.create(holder, entity);
    }
    return null;
  }
  @Getter
  protected final FluidHatchSize size;
  public BlockFluidHatch(Properties id, FluidHatchSize size) {
    super(id);
    this.size = size;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    tooltip.accept(
      Component.translatable("tooltip.fluidhatch.tank.info", size.getSize()).withStyle(ChatFormatting.GRAY)
    );
  }

  @Override
  protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                 Player player, InteractionHand hand, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof FluidTankEntity fluidTank) {
      if (!stack.isEmpty()) {
        var access = ItemAccess.forStack(stack);
        Optional.ofNullable(access.getCapability(Capabilities.Fluid.ITEM)).ifPresent(_ -> {
          try (Transaction tx = Transaction.open(null)) {
            FluidHandler ft = fluidTank.getTank();
            var done = FluidUtil.interactWithFluidHandler(player, hand, pos, ft, tx);
          }
        });
      }
      if (player instanceof ServerPlayer serverPlayer) {
        BlockUIMenuType.openUI(serverPlayer, pos);
      }
      return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction side) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }
}
