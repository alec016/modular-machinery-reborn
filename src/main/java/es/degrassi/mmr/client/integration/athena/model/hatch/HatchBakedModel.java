package es.degrassi.mmr.client.integration.athena.model.hatch;

import es.degrassi.mmr.client.integration.athena.model.MMRBakedModel;
import es.degrassi.mmr.client.integration.athena.model.MMRAthenaBlockModel;
import es.degrassi.mmr.client.integration.athena.utils.MMRMaterialMap;
import es.degrassi.mmr.client.model.hatch.DefaultHatchBakedModel;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class HatchBakedModel implements MMRBakedModel {

  public static final ModelProperty<HatchTextureData> TEXTURE_DATA = new ModelProperty<>();

  private final HatchBlockModel model;
  @Getter
  private final Int2ObjectMap<Material.Baked> textures;

  public HatchBakedModel(HatchBlockModel model, Function<Material, Material.Baked> function) {
    this.model = model;
    this.textures = this.model.getTextures(function);
  }

  @Override
  public MMRMaterialMap getMaterialMap() {
    return this.model.materials;
  }

  @Override
  public MMRAthenaBlockModel getModel() {
    return this.model;
  }

  @Override
  public boolean addOverlay(Direction direction, @Nullable BlockState state) {
    if (!MMRBakedModel.super.addOverlay(direction, state)) return false;
    return state == null || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
        || state.getValue(BlockStateProperties.HORIZONTAL_FACING) == direction;
  }

  @Override
  public void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state,
                           @NotNull RandomSource random, @NotNull List<BlockStateModelPart> parts) {
    ModelData modelData = level.getModelData(pos);
    HatchTextureData textureData = modelData.get(TEXTURE_DATA);

    if (!state.hasProperty(BlockMachineComponent.CONNECT_TEXTURES)
        || !state.getValue(BlockMachineComponent.CONNECT_TEXTURES)
        || (textureData != null && !textureData.hasDefaultTextures())) {
      DefaultHatchBakedModel.INSTANCE.collectParts(level, pos, state, random, parts);
      return;
    }

    MMRBakedModel.super.collectParts(level, pos, state, random, parts);
  }
}
