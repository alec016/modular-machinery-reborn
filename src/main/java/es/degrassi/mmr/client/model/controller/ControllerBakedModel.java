package es.degrassi.mmr.client.model.controller;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.ModularMachineryRebornClient;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerData;
import es.degrassi.mmr.common.util.MachineModelLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Port of the 1.21.1 {@code es.degrassi.mmreborn.client.model.controller.ControllerBakedModel},
 * the fallback the controller routes to when CTM is disabled or the machine defines custom
 * controller models (see {@code MachineModelLocation}) — in 1.21.1 the model was resolved per
 * {@code ModelData} (state / standalone / MRL with properties) and rotated to the machine facing.
 *
 * <p>26.1: the runtime source is the controller entity's
 * {@code es.degrassi.mmr.client.integration.athena.model.controller.ControllerData} (the same
 * {@code DynamicMachine#getControllerModel(status)} source), resolved against the standalone
 * bake registry — the default chain plus the machine's config-folder models, including their
 * facing rotations ({@link ModularMachineryRebornClient#modelKey}). Locations that parse to a
 * {@link BlockState} render through the baked block-state model set as before.
 */
public class ControllerBakedModel implements DynamicBlockStateModel {

  public static final ControllerBakedModel INSTANCE = new ControllerBakedModel();

  private ControllerBakedModel() {
  }

  private static final Identifier DEFAULT_CONTROLLER_RL = ModularMachineryReborn.rl("default/controller");

  private @Nullable BlockStateModel delegate(@Nullable BlockAndTintGetter level, BlockPos pos, @Nullable BlockState state) {
    ModelData modelData = level != null && level instanceof IBlockGetterExtension extension
        ? extension.getModelData(pos)
        : ModelData.EMPTY;
    ControllerData data = modelData != ModelData.EMPTY
        ? modelData.get(es.degrassi.mmr.client.integration.athena.model.controller.ControllerBakedModel.DATA)
        : null;
    MachineModelLocation loc = data != null ? data.modelLocation() : MachineModelLocation.DEFAULT;
    if (loc == null) loc = MachineModelLocation.DEFAULT;

    if (loc.getState() != null) {
      return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(loc.getState());
    }

    Identifier modelRL = loc.getLoc() != null ? loc.getLoc() : DEFAULT_CONTROLLER_RL;
    Direction facing = state != null && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
        ? state.getValue(BlockStateProperties.HORIZONTAL_FACING)
        : null;
    BlockStateModel delegate = ModularMachineryRebornClient.standaloneModel(modelRL, facing);
    if (delegate == null) {
      delegate = ModularMachineryRebornClient.standaloneModel(DEFAULT_CONTROLLER_RL, facing);
    }
    return delegate;
  }

  @Override
  public void collectParts(@Nullable BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull RandomSource random, @NotNull List<BlockStateModelPart> output) {
    BlockStateModel delegate = delegate(level, pos, state);
    if (delegate != null) {
      delegate.collectParts(random, output);
    }
  }

  @Override
  public @NotNull Material.Baked particleMaterial() {
    BlockStateModel delegate = delegate(null, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    return delegate != null
        ? delegate.particleMaterial()
        : Minecraft.getInstance().getModelManager()
            .getBlockStateModelSet()
            .getParticleMaterial(Blocks.AIR.defaultBlockState());
  }

  @Override
  public int materialFlags() {
    BlockStateModel delegate = delegate(null, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    return delegate != null ? delegate.materialFlags() : 0;
  }
}
