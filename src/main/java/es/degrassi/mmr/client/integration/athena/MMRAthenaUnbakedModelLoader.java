package es.degrassi.mmr.client.integration.athena;

import com.google.gson.JsonObject;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import earth.terrarium.athena.impl.loading.AthenaResourceLoader;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class MMRAthenaUnbakedModelLoader extends AthenaUnbakedModelLoader {
  private final Identifier id;
  private final AthenaModelFactory factory;
  private final Function<Supplier<AthenaBlockModel>, BlockStateModel.UnbakedRoot> loader;

  public MMRAthenaUnbakedModelLoader(Identifier id, AthenaModelFactory factory, Function<Supplier<AthenaBlockModel>, BlockStateModel.UnbakedRoot> loader) {
    super(id, factory, loader);
    this.id = id;
    this.factory = factory;
    this.loader = loader;
  }

  public @Nullable BlockStateModel.UnbakedRoot loadModel(BlockState state) {
    var id = state.getBlock().builtInRegistryHolder().key().identifier();
    JsonObject json = AthenaResourceLoader.getData(this.id, id);
    return this.loadModel(json);
  }

  public BlockStateModel.UnbakedRoot loadModel(JsonObject json) {
    return json != null ? this.loader.apply(this.factory.create(json)) : null;
  }
}
