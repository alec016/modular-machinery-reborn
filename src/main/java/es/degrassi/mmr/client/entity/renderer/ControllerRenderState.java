package es.degrassi.mmr.client.entity.renderer;

import es.degrassi.mmr.common.entity.MachineControllerEntity;
import lombok.Getter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.jspecify.annotations.Nullable;

public class ControllerRenderState extends BlockEntityRenderState {
  @Getter
  private MachineControllerEntity entity;
  public static void extractBase(MachineControllerEntity blockEntity, ControllerRenderState state, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
    state.entity = blockEntity;
  }
}
