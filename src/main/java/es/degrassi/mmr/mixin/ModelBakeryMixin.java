package es.degrassi.mmr.mixin;

import es.degrassi.mmr.client.model.BuiltInModelHooks;
import net.minecraft.client.resources.model.ModelBakery.ModelBakerImpl;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakerImpl.class)
public abstract class ModelBakeryMixin {

  @Inject(at = @At("HEAD"), method="getModel", cancellable = true)
  private void getModelHook(Identifier id, CallbackInfoReturnable<UnbakedModel> cir) {
    var model = BuiltInModelHooks.getBuiltInModel(id);
    if (model != null) {
      cir.setReturnValue(model);
    }
  }
}
