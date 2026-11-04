package es.degrassi.mmr.mixin;

import net.minecraft.nbt.NbtAccounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtAccounter.class)
public abstract class NbtAccounterMixin {
  @Inject(method = "create", at = @At("RETURN"), cancellable = true)
  private static void create(long quota, CallbackInfoReturnable<NbtAccounter> cir) {
    cir.setReturnValue(NbtAccounter.unlimitedHeap());
  }

  @Inject(method = "defaultQuota", at = @At("RETURN"), cancellable = true)
  private static void defaultQuota(CallbackInfoReturnable<NbtAccounter> cir) {
    cir.setReturnValue(NbtAccounter.unlimitedHeap());
  }

  @Inject(method = "uncompressedQuota", at = @At("RETURN"), cancellable = true)
  private static void uncompressedQuota(CallbackInfoReturnable<NbtAccounter> cir) {
    cir.setReturnValue(NbtAccounter.unlimitedHeap());
  }
}
