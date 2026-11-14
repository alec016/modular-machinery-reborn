package es.degrassi.mmr.client.integration.athena.model.controller;

import es.degrassi.mmr.client.integration.athena.model.MMRBakedModel;
import es.degrassi.mmr.client.integration.athena.model.MMRAthenaBlockModel;
import es.degrassi.mmr.client.integration.athena.utils.CtmUtils;
import es.degrassi.mmr.client.integration.athena.utils.MMRMaterialMap;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.ControllerOverlays;
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

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class ControllerBakedModel implements MMRBakedModel {

  public static final ModelProperty<ControllerData> DATA = new ModelProperty<>();

  /**
   * The first sprite of the overlays of the states: the ctm materials use the low
   * indexes (at most ten per face), the states are baked above them.
   */
  private static final int STATE_SPRITE = 128;

  private final ControllerBlockModel model;
  @Getter
  private final Int2ObjectMap<Material.Baked> textures;
  private final EnumMap<MachineStatus, Integer> stateSprites = new EnumMap<>(MachineStatus.class);

  public ControllerBakedModel(ControllerBlockModel model, Function<Material, Material.Baked> function) {
    this.model = model;
    Int2ObjectMap<Material.Baked> baked = this.model.getTextures(function);
    // the overlay of every state of the machine: the terminal of the controller shows
    // the screen and the lamp of the state
    int sprite = STATE_SPRITE;
    for (MachineStatus status : MachineStatus.values()) {
      Material material = CtmUtils.blockMat(ControllerOverlays.overlay(status).toString());
      baked.put(sprite, function.apply(material));
      this.stateSprites.put(status, sprite++);
    }
    this.textures = baked;
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
    ControllerData data = modelData.get(DATA);
    if (data == null) {
      data = new ControllerData(DynamicMachine.DUMMY, null, MachineStatus.MISSING_STRUCTURE);
    }

    if (data.hasCustomModel() || !BlockMachineComponent.connectsTextures(state)) {
      es.degrassi.mmr.client.model.controller.ControllerBakedModel.INSTANCE.collectParts(level, pos, state, random, parts);
      return;
    }

    // the terminal shows the state of the machine: the screen of the running machine
    // moves, the other ones are still and the lamp has the colour of the state
    Integer sprite = this.stateSprites.get(data.status());
    parts.add(new CTMPart(this, state, MMRBakedModel.buildGeometryKey(level, pos, state, getModel()),
        sprite == null ? OVERLAY_SPRITE : sprite));
  }
}
