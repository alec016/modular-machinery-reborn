package es.degrassi.mmr.client.entity.renderer;

import com.lowdragmc.lowdraglib2.utils.RayTraceHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.api.IWrenchable;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class IWrenchableRenderer implements BlockEntityRenderer<BlockEntity, IWrenchableRenderState> {
  private final BlockEntityRendererProvider.Context context;

  public IWrenchableRenderer(BlockEntityRendererProvider.Context context) {
    this.context = context;
  }

  @Override
  public IWrenchableRenderState createRenderState() {
    return new IWrenchableRenderState();
  }

  @Override
  public void extractRenderState(BlockEntity blockEntity, IWrenchableRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
    state.entity = blockEntity;
    state.cameraPos = cameraPosition;
  }

  @Override
  public void submit(IWrenchableRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                     CameraRenderState cameraState) {
    var player = Minecraft.getInstance().player;
    if (player == null) return;
    if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemRegistration.WRENCH.holder())) return;
    var entity = state.entity;
    if (entity == null || entity.getLevel() == null) return;
    if (!(entity instanceof IWrenchable)) return;
    if (!(entity instanceof ISideConfigComponent<?> component)) return;
    if (!(component.getConfig() instanceof IOSideConfig config)) return;
    var blockHit = RayTraceHelper.rayTraceRange(entity.getLevel(), player, player.blockInteractionRange());
    if (!entity.getBlockPos().equals(blockHit.getBlockPos())) return;
    var face = blockHit.getDirection();
    var facing = Minecraft.getInstance().player.getDirection();
    byMode(config.getSideMode(RelativeSide.fromDirections(facing.getOpposite(), face))).getBlitter().blitWorld(poseStack, collector, face, OverlayTexture.NO_OVERLAY);
  }

  private static Icon byMode(IOSideMode mode) {
    return mode.isEnabled() ? Icon.AUTO_EXPORT_ON : Icon.AUTO_EXPORT_OFF;
  }
}
