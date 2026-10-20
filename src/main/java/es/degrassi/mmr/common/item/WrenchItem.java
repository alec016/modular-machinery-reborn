package es.degrassi.mmr.common.item;

import es.degrassi.mmr.api.IWrenchable;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.common.registration.SoundRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item {;

  public WrenchItem(Properties props) {
    super(props.stacksTo(1));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (context.getLevel().isClientSide() || player == null)
      return super.useOn(context);
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Direction face = context.getClickedFace();
    BlockState state = level.getBlockState(pos);
    BlockEntity entity = level.getBlockEntity(pos);

    if (state.getBlock() instanceof IWrenchable wrenchable) {
      var facing = player.getDirection().getOpposite();
      var result = wrenchable.onWrenched(RelativeSide.fromDirections(facing, face), player);

      playSoundFromResult(level, pos, result);
      return fromResult(result);
    }

    if (entity instanceof IWrenchable wrenchable) {
      Direction facing = player.getDirection().getOpposite();
      var result = wrenchable.onWrenched(RelativeSide.fromDirections(facing, face), player);

      playSoundFromResult(level, pos, result);
      return fromResult(result);
    }

    return super.useOn(context);
  }

  private InteractionResult fromResult(IWrenchable.Result result) {
    return switch (result) {
      case SUCCESS -> InteractionResult.CONSUME;
      case FAIL -> InteractionResult.FAIL;
      default -> InteractionResult.PASS;
    };
  }

  public void playSoundFromResult(Level level, BlockPos at, IWrenchable.Result result) {
    switch (result){
      case SUCCESS -> level.playSound(null, at, SoundRegistration.WRENCH_SUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      case FAIL -> level.playSound(null, at, SoundRegistration.WRENCH_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      default -> {}
    }
  }
}
