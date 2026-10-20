package es.degrassi.mmr.client.model.controller;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;

public class ControllerModelLoader implements UnbakedModelLoader<ControllerModelLoader.ControllerUnbakedModel> {

  public static final ControllerModelLoader INSTANCE = new ControllerModelLoader();

  @Override
  public ControllerUnbakedModel read(JsonObject json, JsonDeserializationContext deserializationContext) throws JsonParseException {
    return new ControllerUnbakedModel();
  }

  public static class ControllerUnbakedModel implements UnbakedModel {
    public BlockStateModel.UnbakedRoot bakeRoot(ModelBaker modelBaker) {
      return new ControllerUnbakedRoot();
    }
  }

  public static class ControllerUnbakedRoot implements BlockStateModel.UnbakedRoot {
    @Override
    public BlockStateModel bake(BlockState blockState, ModelBaker modelBakery) {
      return ControllerBakedModel.INSTANCE;
    }

    @Override
    public Object visualEqualityGroup(BlockState blockState) {
      return this;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {

    }
  }
}
