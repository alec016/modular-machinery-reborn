package es.degrassi.mmr.mixin;

import es.degrassi.mmr.api.controller.IMultiblockController;
import es.degrassi.mmr.api.controller.MMRWorldSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {

  @Shadow
  @Final
  private Thread thread;

  @Unique
  private @Nullable ChunkAccess mmr$maybeGetChunkAsync(int chunkX, int chunkZ) {
    if (this.isClientSide()) return null;
    if (Thread.currentThread() == this.thread) return null;
    if (!MMRWorldSavedData.isThreadService()) return null;
    if (!this.getChunkSource().hasChunk(chunkX, chunkZ)) return null;

    return this.getChunkSource().getChunkNow(chunkX, chunkZ);
  }

  @Inject(method = "getBlockEntity", at = @At(value = "HEAD"), cancellable = true)
  private void mmr$getBlockEntityOffThread(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
    ChunkAccess chunk = mmr$maybeGetChunkAsync(pos.getX() >> 4, pos.getZ() >> 4);
    if (chunk instanceof LevelChunk levelChunk) {
      cir.setReturnValue(levelChunk.getBlockEntities().get(pos));
    }
  }

  @Inject(method = "getBlockState", at = @At(value = "HEAD"), cancellable = true)
  private void mmr$getBlockStateOffThread(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
    ChunkAccess chunk = mmr$maybeGetChunkAsync(pos.getX() >> 4, pos.getZ() >> 4);
    if (chunk != null) {
      cir.setReturnValue(chunk.getBlockState(pos));
    }
  }

  @SuppressWarnings("ConstantValue")
  @Inject(method = "markAndNotifyBlock",
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/world/level/Level;updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V",
          remap = true),
      remap = false)
  private void mmr$updateChunkMultiblocks(BlockPos pos, LevelChunk chunk,
                                            BlockState oldState, BlockState newState, int flags, int recursionLeft,
                                            CallbackInfo ci) {
    if (!(((Object) this) instanceof ServerLevel serverLevel)) return;

    MMRWorldSavedData mwsd = MMRWorldSavedData.getOrCreate(serverLevel);
    Set<IMultiblockController> defensiveCopy = new HashSet<>(mwsd.getControllersInChunk(chunk.getPos()));
    for (IMultiblockController structure : defensiveCopy) {
      if (structure.getController() == null || !structure.self().isFormed()) {
        // skip for unloaded/unformed multiblocks
        continue;
      }
      if (structure.isPosInCache(pos)) {
        serverLevel.getServer().executeBlocking(() -> structure.onBlockStateChanged(pos, newState));
      }
    }
  }
}
