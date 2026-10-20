package es.degrassi.mmr.client.integration.athena.model;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.neoforge.AthenaBakedModel;
import es.degrassi.mmr.client.integration.athena.model.casing.CasingBakedModel;
import es.degrassi.mmr.client.integration.athena.model.casing.CasingBlockModel;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerBakedModel;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerBlockModel;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchBakedModel;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchBlockModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public class MMRAthenaUnbakedModel implements BlockStateModel.UnbakedRoot {

  private final Supplier<AthenaBlockModel> model;

  public MMRAthenaUnbakedModel(Supplier<AthenaBlockModel> athenaBlockModelSupplier) {
    this.model = athenaBlockModelSupplier;
  }

  @Override
  public void resolveDependencies(ResolvableModel.Resolver resolver) {
  }

  @Override
  public BlockStateModel bake(BlockState blockState, ModelBaker baker) {
    Function<Material, Material.Baked> function = mat -> baker.materials().get(mat, () -> "athena_model");
    if (model.get() instanceof CasingBlockModel casingBlockModel) {
      return new CasingBakedModel(casingBlockModel, function);
    } else if (model.get() instanceof HatchBlockModel hatchBlockModel) {
      return new HatchBakedModel(hatchBlockModel, function);
    } else if (model.get() instanceof ControllerBlockModel controllerBlockModel) {
      return new ControllerBakedModel(controllerBlockModel, function);
    }
    return new AthenaBakedModel(model.get(), function);
  }

  @Override
  public Object visualEqualityGroup(BlockState blockState) {
    return this;
  }
}
