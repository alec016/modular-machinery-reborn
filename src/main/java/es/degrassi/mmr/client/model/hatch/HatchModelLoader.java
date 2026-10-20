package es.degrassi.mmr.client.model.hatch;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;

public class HatchModelLoader implements UnbakedModelLoader<HatchModelLoader.HatchUnbakedModel> {

  public static final HatchModelLoader INSTANCE = new HatchModelLoader();

  @Override
  public HatchUnbakedModel read(JsonObject json, JsonDeserializationContext deserializationContext) throws JsonParseException {
    return new HatchUnbakedModel();
  }

  public static class HatchUnbakedModel implements UnbakedModel {
    public BlockStateModel.UnbakedRoot bakeRoot(ModelBaker modelBaker) {
      return new HatchUnbakedRoot();
    }
  }

  public static class HatchUnbakedRoot implements BlockStateModel.UnbakedRoot {
    @Override
    public BlockStateModel bake(BlockState blockState, ModelBaker modelBakery) {
      return DefaultHatchBakedModel.INSTANCE;
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
