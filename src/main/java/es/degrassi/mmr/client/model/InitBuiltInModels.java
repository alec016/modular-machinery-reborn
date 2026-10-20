package es.degrassi.mmr.client.model;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public final class InitBuiltInModels {
  private InitBuiltInModels() {}

  public static void init() {
  }

  private static <T extends UnbakedModel> void addBuiltInModel(String id, Supplier<T> modelFactory) {
    BuiltInModelHooks.addBuiltInModel(ModularMachineryReborn.rl(id), modelFactory.get());
  }
}
