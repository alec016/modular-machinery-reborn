package es.degrassi.mmr.mixin;

import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.FactoryManager;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.integration.athena.model.MMRAthenaUnbakedModel;
import es.degrassi.mmr.client.integration.athena.MMRAthenaUnbakedModelLoader;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(FactoryManager.class)
public abstract class AthenaFactoryManagerMixin {
  @Shadow @Final private static Map<Identifier, AthenaUnbakedModelLoader> LOADERS;

  @Inject(
      at = @At(
          value = "INVOKE",
          target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
      ),
      method = "register",
      cancellable = true
  )
  private static void register(Identifier type, AthenaModelFactory factory, CallbackInfo ci) {
    if (type.getNamespace().equals(ModularMachineryReborn.MODID)) {
      LOADERS.put(type, new MMRAthenaUnbakedModelLoader(type, factory, MMRAthenaUnbakedModel::new));
      ci.cancel();
    }
  }
}
