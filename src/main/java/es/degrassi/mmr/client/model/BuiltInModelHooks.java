package es.degrassi.mmr.client.model;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BuiltInModelHooks {
  private static final Map<Identifier, UnbakedModel> builtInModels = new HashMap<>();

  public static void addBuiltInModel(Identifier id, UnbakedModel model) {
    if (builtInModels.put(id, model) != null) {
      throw new IllegalArgumentException("Duplicate built.in model ID: " + id);
    }
  }

  @Nullable
  public static UnbakedModel getBuiltInModel(Identifier id) {
    if (!ModularMachineryReborn.MODID.equals(id.getNamespace())) {
      return null;
    }
    return builtInModels.get(id);
  }
}
