package es.degrassi.mmr.client.entity.renderer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class StructureCheckerRenderer implements BlockEntityRenderer<StructureCheckerEntity, StructureCheckerRenderState> {
  public static final Map<Identifier, StructureRenderer> renderers = Maps.newHashMap();
  private final BlockEntityRendererProvider.Context context;
  public StructureCheckerRenderer(BlockEntityRendererProvider.Context context) {
    this.context = context;
  }

  @Override
  public StructureCheckerRenderState createRenderState() {
    return new StructureCheckerRenderState();
  }

  @Override
  public void submit(StructureCheckerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
  }

  public static void add(Identifier machine, Structure structure) {
    renderers.put(machine, new StructureRenderer(MMRConfig.get().structureRenderTime.get(), structure::getBlocks));
  }
}
