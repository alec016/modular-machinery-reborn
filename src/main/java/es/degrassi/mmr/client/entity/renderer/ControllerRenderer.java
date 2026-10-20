package es.degrassi.mmr.client.entity.renderer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ControllerRenderer implements BlockEntityRenderer<MachineControllerEntity, ControllerRenderState> {
  public static final Map<BlockPos, StructureRenderer> renderers = Maps.newHashMap();
  private final BlockEntityRendererProvider.Context context;
  public ControllerRenderer(BlockEntityRendererProvider.Context context) {
    this.context = context;
  }

  @Override
  public ControllerRenderState createRenderState() {
    return new ControllerRenderState();
  }

  @Override
  public void extractRenderState(MachineControllerEntity blockEntity, ControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    ControllerRenderState.extractBase(blockEntity, state, breakProgress);
  }

  @Override
  public void submit(ControllerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    var machineControllerEntity = state.getEntity();
    if (machineControllerEntity.getLevel() == null) return;

    if (renderers.containsKey(machineControllerEntity.getBlockPos())) {
      Direction machineFacing = machineControllerEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
      StructureRenderer renderer = renderers.get(machineControllerEntity.getBlockPos());
      if (renderer.shouldRender()) {
        renderer.render(context, poseStack, collector, machineFacing, machineControllerEntity.getLevel(), machineControllerEntity.getBlockPos());
      } else {
        renderers.remove(machineControllerEntity.getBlockPos());
      }
    }
  }

  public static void add(DynamicMachine machine, BlockPos controllerPos) {
    renderers.put(controllerPos, new StructureRenderer(MMRConfig.get().structureRenderTime.get(), machine.getPattern()::getBlocks));
  }
}
