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

/**
 * Port of the 1.21.1 {@code es.degrassi.mmreborn.client.integration.athena.model.hatch.HatchBakedModel}.
 *
 * <p>Runtime texture overrides arrive through {@code ModelData} exactly like before: block entities
 * still build {@code getModelData()} with {@link #TEXTURE_DATA}, and the 26.1 chunk renderer exposes
 * it via {@code IBlockGetterExtension#getModelData(BlockPos)} on the level passed to
 * {@code collectParts}.
 *
 * <p>When the hatch has non-default textures (or CTM is disabled) the model falls back to
 * {@code es.degrassi.mmr.client.model.hatch.DefaultHatchBakedModel}, as in 1.21.1.
 */
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

  /** 1.21.1 parity: hatches only expose the overlay on their horizontal facing (or when un-oriented). */
  @Override
  public boolean addOverlay(Direction direction, @Nullable BlockState state) {
    if (!MMRBakedModel.super.addOverlay(direction, state)) return false;
    return state == null || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
        || state.getValue(BlockStateProperties.HORIZONTAL_FACING) == direction;
  }

  @Override
  public void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state,
                           @NotNull RandomSource random, @NotNull List<BlockStateModelPart> parts) {
    ModelData modelData = level instanceof IBlockGetterExtension extension
        ? extension.getModelData(pos)
        : ModelData.EMPTY;
    HatchTextureData textureData = modelData.get(TEXTURE_DATA);

    // 1.21.1 parity: custom textures or CTM disabled route to the legacy DefaultHatchBakedModel.
    if (!state.getValue(BlockMachineComponent.CONNECT_TEXTURES)
        || (textureData != null && !textureData.hasDefaultTextures())) {
      DefaultHatchBakedModel.INSTANCE.collectParts(level, pos, state, random, parts);
      return;
    }

    MMRBakedModel.super.collectParts(level, pos, state, random, parts);
  }
}
