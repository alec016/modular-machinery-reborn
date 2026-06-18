package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.api.IWrenchable;
import es.degrassi.mmreborn.api.capability.config.IOSideConfig;
import es.degrassi.mmreborn.api.capability.config.ISideConfigComponent;
import es.degrassi.mmreborn.api.capability.config.RelativeSide;
import es.degrassi.mmreborn.api.controller.ControllerAttacheable;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ColorableMachineComponentEntity extends BlockEntitySynchronized implements ColorableMachineEntity, IWrenchable, ControllerAttacheable {
  private int definedColor = Config.machineColor;
  @Getter
  protected final Set<BlockPos> controllerPosSet = new HashSet<>();

  public ColorableMachineComponentEntity(BlockPos pos, BlockState blockState) {
    this(EntityRegistration.COLORABLE_MACHINE.get(), pos, blockState);
  }

  public ColorableMachineComponentEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public boolean shouldAuto() {
    if (this instanceof ISideConfigComponent<?> entity && entity instanceof IAutoEntity<?> && entity.getConfig() instanceof IOSideConfig config) {
      for (RelativeSide side : RelativeSide.values()) {
        if (config.getSideMode(side).isEnabled())
          return true;
      }
    }
    return false;
  }

  @Override
  public int getMachineColor() {
    return definedColor;
  }

  @Override
  public void setMachineColor(int newColor) {
    setChanged();
    this.definedColor = newColor;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
        new SUpdateMachineColorPacket(newColor, getBlockPos()));
    }
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    if (this instanceof FiltereableEntity<?, ?> entity) {
      entity.deserializeFilter(nbt.getCompound(FiltereableEntity.FILTER_KEY), pRegistries);
    }
    if (nbt.contains("casingColor")) {
      definedColor = nbt.getInt("casingColor");
      return;
    }
    definedColor = Config.machineColor;
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    if (this instanceof FiltereableEntity<?, ?> entity) {
      nbt.put(FiltereableEntity.FILTER_KEY, entity.serializeFilter(pRegistries));
    }
    nbt.putInt("casingColor", this.definedColor);
  }

  @Override
  public boolean triggerEvent(int id, int type) {
    if (id == 1) {
      if (getLevel() != null && getLevel().isClientSide())
        scheduleRenderUpdate();
      return true;
    }
    return false;
  }

  public void scheduleRenderUpdate() {
    if (getLevel() != null) {
      if (getLevel().isClientSide()) {
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1 << 3);
      } else {
        getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
      }
    }
  }

  @Override
  public Result onWrenched(RelativeSide side, Player player) {
    if (!(this instanceof ISideConfigComponent<?> configEntity)) return Result.NONE;
    var oldMode = configEntity.getConfig().getSideMode(side);
    configEntity.getConfig().setNext(side);
    var newMode = configEntity.getConfig().getSideMode(side);
    setChanged();
    player.sendSystemMessage(
        Component.translatable("mmr.wrench.side_mode.change",
          side.getTranslationName().copy().withStyle(ChatFormatting.AQUA),
          oldMode.title().copy().withStyle(ChatFormatting.RED),
          newMode.title().copy().withStyle(ChatFormatting.GREEN)
        )
    );
    getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
    return Result.SUCCESS;
  }
}
