package es.degrassi.mmr.client.screen.widget.tabs;

import com.lowdragmc.lowdraglib2.client.scene.SceneRenderContext;
import com.lowdragmc.lowdraglib2.client.scene.WorldSceneRenderer;
import com.lowdragmc.lowdraglib2.client.utils.RenderUtils;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scene;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.lowdraglib2.utils.data.BlockPosFace;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.WrappedBlockAndTintGetter;
import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.api.entity.ColorableMachineEntity;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.client.screen.util.MMRSceneFx;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SideScene<T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>> extends Scene {
  private final AutoIoTab<T> tab;
  @Nullable
  private Direction selectedFace;
  private final List<GhostBlock> ghosts = new ArrayList<>();
  private final BlockAndTintGetter region;
  private final ModelBlockRenderer ghostRenderer;
  private final BlockStateModelSet modelSet;

  private record GhostBlock(BlockPos pos, BlockPos realPos, BlockState state, @Nullable BlockEntity entity) { }

  SideScene(AutoIoTab<T> tab) {
    this.tab = tab;
    var world = new TrackedDummyWorld();
    var pos = new BlockPos(0, 0, 0);
    var entityState = tab.entity.getBlockState();
    world.addBlock(pos, new BlockInfo(entityState));

    var entity = entityState instanceof EntityBlock entityBlock
        ? entityBlock.newBlockEntity(pos, entityState)
        : null;

    if (entity != null) {
      entity.setLevel(world);
    }

    createScene(world);
    world.setBlockFilter(p -> {
      var isController = p.equals(pos);
      if (isController) return true;
      for (var side : Direction.values()) {
        var relative = pos.relative(side);
        if (p.equals(relative)) return true;
      }
      return false;
    });

    var be = world.getBlockEntity(pos);
    if (be != null) {
      if (be instanceof ColorableMachineEntity ce && tab.entity instanceof ColorableMachineEntity tce) {
        ce.setMachineColor(tce.getMachineColor());
      }
    }
    setRenderedCore(List.of(pos));
    setCameraYawAndPitch(-45f, 25f);
    this.renderSelect = false;
    this.region = new WrappedBlockAndTintGetter(world);
    var mc = Minecraft.getInstance();
    this.modelSet = mc.getModelManager().getBlockStateModelSet();
    this.ghostRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), false, mc.getBlockColors());
    setOnSelected((p, face) -> {
      if (p.equals(pos)) onFaceClicked(face);
      else onFaceClicked(null);
    });
    addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (getLastHoverPosFace() == null) {
        selectedFace = null;
      }
    });
    this.overlay = this::renderOverlay;
    collectGhostNeighbors();
    this.<WorldSceneRenderer>getRenderer()
        .setAfterBuiltinSubmit(this::submitGhostBlockEntities);
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      BlockPosFace hover = getLastHoverPosFace();
      if (hover == null) {
        return;
      }
      RelativeSide side = RelativeSide.fromDirections(tab.entity.getControllerFacing(), hover.facing());
      IOSideMode mode = tab.entity.getConfig().getSideMode(side);
      Component blockName = blockName(side);
      List<Component> lines = new ArrayList<>();
      lines.add(blockName == null
          ? side.getTranslationName()
          : Component.translatable("%s: %s", side.getTranslationName(), blockName));
      lines.add(tab.info());
      lines.add(
          TabTexts.tooltipAutoChange.text(
              (mode.isEnabled() ? TabTexts.tooltipEnabled : TabTexts.tooltipDisabled).text().withStyle(ChatFormatting.AQUA),
              (mode.isDisabled() ? TabTexts.tooltipEnabled : TabTexts.tooltipDisabled).text().withStyle(ChatFormatting.AQUA)
          )
      );
      event.hoverTooltips = HoverTooltips.create(lines.toArray());
    });
  }

  private void onFaceClicked(@Nullable Direction face) {
    if (face == null) {
      selectedFace = null;
      return;
    }
    if (selectedFace != face) {
      selectedFace = face;
      return;
    }
    toggleSide(face);
  }

  private void toggleSide(Direction face) {
    RelativeSide side = RelativeSide.fromDirections(tab.entity.getControllerFacing(), face);
    boolean turnOn = tab.entity.getConfig().getSideMode(side).isDisabled();
    // Runs client-side; the RPC flips the side on the server, which syncs it back.
    tab.sendEvent(tab.setSideRpc, side.name(), turnOn);
  }

  private void collectGhostNeighbors() {
    var level = tab.entity.getLevel();
    if (level == null) {
      return;
    }
    for (Direction dir : Direction.values()) {
      BlockPos realPos = tab.entity.getBlockPos().relative(dir);
      BlockState state = level.getBlockState(realPos);
      if (state.isAir()) {
        continue;
      }
      BlockEntity entity = null;
      if (state.hasBlockEntity()) {
        entity = tab.entity.getLevel().getBlockEntity(realPos);
      }
      BlockPos ghostPos = new BlockPos(0, 0, 0).relative(dir);
      ghosts.add(new GhostBlock(ghostPos, realPos, state, entity));
      // The neighbors have to live in the scene world: the model tints and the block entity colors
      // are resolved against the region, so a ghost without its block in the world renders with
      // the wrong colors (or not at all).
      if (dummyWorld != null) {
        dummyWorld.addBlock(ghostPos, new BlockInfo(state));
      }
    }
  }

  private void renderOverlay(SceneRenderContext ctx) {
    MultiBufferSource.BufferSource bufferSource = ctx.bufferSource();
    drawGhostNeighbors(bufferSource);
    drawFaceTints(bufferSource);
  }

  private void drawGhostNeighbors(MultiBufferSource.BufferSource bufferSource) {
    if (ghosts.isEmpty()) {
      return;
    }
    var buffer = MMRSceneFx.ghostBuffer(bufferSource);
    for (GhostBlock ghost : ghosts) {
      if (ghost.entity instanceof ColorableMachineEntity entity) {
        if (region.getBlockEntity(ghost.pos) instanceof ColorableMachineEntity sceneEntity) {
          sceneEntity.setMachineColor(entity.getMachineColor());
        }
      }
      if (ghost.state().getRenderShape() == RenderShape.MODEL) {
        MMRSceneFx.tesselateGhost(ghostRenderer, modelSet, region, buffer, ghost.pos(), ghost.state());
      } else if (!(ghost.state().getBlock() instanceof EntityBlock)) {
        RenderUtils.renderBlockOverLay(new PoseStack(), bufferSource, ghost.pos(), 0.42f, 0.45f, 0.52f, 1.003f);
      }
    }
  }

  private void submitGhostBlockEntities(SceneRenderContext ctx) {
    if (ghosts.isEmpty()) {
      return;
    }
    var beDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    var renderer = ctx.renderer();
    beDispatcher.prepare(new Vec3(renderer.getEyePos()));
    for (GhostBlock ghost : ghosts) {
      dummyWorld.setBlockAndUpdate(ghost.pos, ghost.state);
      if (!(ghost.state().getBlock() instanceof EntityBlock entityBlock)) {
        continue;
      }
      var be = dummyWorld.getBlockEntity(ghost.pos);
      if (be == null) be = entityBlock.newBlockEntity(ghost.pos(), ghost.state());
      if (be == null) {
        continue;
      }
      if (be.getLevel() == null) be.setLevel(dummyWorld);
      if (be instanceof MachineControllerEntity entity) {
        if (ghost.entity instanceof MachineControllerEntity controller) {
          entity.setMachine(controller.getFoundMachine().getRegistryName());
          entity.setFormed(true);
          entity.setChanged();
        }
      } else if (be instanceof ColorableMachineEntity entity) {
        if (ghost.entity instanceof ColorableMachineEntity ce) {
          entity.setMachineColor(ce.getMachineColor());
        }
      }
      var state = beDispatcher.tryExtractRenderState(be, ctx.partialTicks(), null, null);
      if (state == null) {
        continue;
      }
      ctx.poseStack().pushPose();
      ctx.poseStack().translate(ghost.pos().getX(), ghost.pos().getY(), ghost.pos().getZ());
      beDispatcher.submit(state, ctx.poseStack(), ctx.submitStorage(), ctx.cameraState());
      ctx.poseStack().popPose();
    }
  }

  private void drawFaceTints(MultiBufferSource.BufferSource bufferSource) {
    float[] tint = tab.isInput() ? MMRSceneFx.INPUT_TINT : MMRSceneFx.OUTPUT_TINT;
    var config = tab.entity.getConfig();
    var controllerFacing = tab.entity.getControllerFacing();
    for (Direction face : Direction.values()) {
      RelativeSide side = RelativeSide.fromDirections(controllerFacing, face);
      if (config.getSideMode(side).isEnabled()) {
        MMRSceneFx.drawFaceTint(bufferSource, new BlockPos(0, 0, 0), face,
            tint[0], tint[1], tint[2], MMRSceneFx.TINT_ALPHA);
      }
    }
  }

  @Nullable
  private Component blockName(RelativeSide side) {
    if (tab.entity.getLevel() == null) {
      return null;
    }
    var relative = tab.entity.getBlockPos().relative(side.getDirection(tab.entity.getControllerFacing()));
    if (tab.entity.getLevel().getBlockEntity(relative) instanceof MachineControllerEntity entity) {
      return entity.getFoundMachine().getName();
    }
    BlockState state = tab.entity.getLevel().getBlockState(relative);
    if (state.isAir()) {
      return null;
    }
    return state.getBlock().getName();
  }
}
