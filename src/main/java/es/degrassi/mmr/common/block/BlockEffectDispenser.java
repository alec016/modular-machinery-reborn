package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.ModularMachineryRebornClient;
import es.degrassi.mmr.client.screen.EffectDispenserUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.block.base.SizedBlockHatch;
import es.degrassi.mmr.common.block.base.TooltipAppendable;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static es.degrassi.mmr.client.ModularMachineryRebornClient.createParticle;

public class BlockEffectDispenser extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable,
    BlockUIMenuType.BlockUI, SizedBlockHatch<EffectDispenserSize> {
  @Getter
  private final EffectDispenserSize size;
  public BlockEffectDispenser(Properties id, EffectDispenserSize size) {
    super(id);
    this.size = size;
  }

  @Override
  protected MapCodec<BlockEffectDispenser> codec() {
    return BlockCodecs.EFFECT_DISPENSER.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof EffectDispenserEntity entity) {
      return EffectDispenserUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    super.animateTick(state, level, pos, random);
    if (!ModularMachineryRebornClient.shouldAddParticles(random)) return;
    if (size.interdimensional || !size.showParticles) return;
    if (level.getBlockEntity(pos) instanceof EffectDispenserEntity entity) {
      entity.getEffect().ifPresent(effect -> {
        var particle = effect.getParticleOptions();
        AABB bb = new AABB(pos).inflate(size.radius)
            .setMaxY(pos.getY())
            .setMinY(pos.getY());
        for (double i = bb.minX; i <= bb.maxX; i += 1) {
          for (double j = bb.minY; j <= bb.maxY; j+= 1) {
            for (double k = bb.minZ; k <= bb.maxZ; k += 1) {
              createParticle(particle, new BlockPos((int)i, (int)j, (int)k));
            }
          }
        }
      });
    }
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level pLevel, BlockPos pPos, Direction direction) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EffectDispenserEntity(blockPos, blockState, size);
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                              TooltipDisplay display, Consumer<Component> builder,
                              TooltipFlag tooltipFlag) {
    if (size.interdimensional) {
      builder.accept(Component.translatable("tooltip.effectdispenser.interdimensional").withStyle(ChatFormatting.GRAY));
    } else {
      builder.accept(Component.translatable("tooltip.effectdispenser.radius", size.radius).withStyle(ChatFormatting.GRAY));
    }
  }
}
