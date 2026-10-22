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
import es.degrassi.mmr.client.screen.MMRSceneFx;
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

/**
 * The GTCEu-style 3D preview of this component's block: the block is copied into a dummy world
 * and rendered by LDLib2's {@link Scene}. Hovering highlights the visible face under the cursor
 * (the scene's select outline) and shows that side's auto-IO state; a click toggles that side.
 * The preview can be rotated (drag) and zoomed (wheel) to reach the hidden faces, exactly like
 * the GTCEu machine preview.
 */
public class SideScene<T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>> extends Scene {
  private final AutoIoTab<T> tab;
  /** Step one of the click flow: the face highlighted by clicking, waiting for its toggle click. */
  @Nullable
  private Direction selectedFace;
  /** The machine's real surrounding blocks, shown as 0.5-opacity ghosts around the preview. */
  private final List<GhostBlock> ghosts = new ArrayList<>();
  /** The dummy world adapted for ghost tessellation (AO/light sampling). */
  private final BlockAndTintGetter region;
  private final ModelBlockRenderer ghostRenderer;
  private final BlockStateModelSet modelSet;

  /** One surrounding block: its preview position and real-world position, and its real state. */
  private record GhostBlock(BlockPos pos, BlockPos realPos, BlockState state, @Nullable BlockEntity entity) {
    private GhostBlock(BlockPos pos, BlockPos realPos, BlockState state) {
      this(pos, realPos, state, null);
    }
  }

  SideScene(AutoIoTab<T> tab) {
    this.tab = tab;
    // The component's block, copied into a tiny dummy world at the origin.
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
    // The scene's selection machinery paints a dark-red cube overlay on the selected block
    // (Scene.renderOverlay calls RenderUtils.renderBlockOverLay with r=0.6). The GT-style look
    // only wants the green facing border, so the overlay pass goes off while the selection
    // itself (and with it the border) stays intact.
    this.renderSelect = false;
    // Ghost support: only the component's block ever exists in the dummy world (the scene's
    // block filter stays core-only), so ghosts render but can never consume a ray-clip hit —
    // the scene's hover and clicks stay exclusive to the component's own six faces.
    this.region = world instanceof BlockAndTintGetter getter ? getter : new WrappedBlockAndTintGetter(world);
    var mc = Minecraft.getInstance();
    this.modelSet = mc.getModelManager().getBlockStateModelSet();
    this.ghostRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), false, mc.getBlockColors());
    setOnSelected((p, face) -> {
      if (p.equals(pos)) onFaceClicked(face);
      else onFaceClicked(null);
    });
    // A press that lands on the preview's background rather than on one of the six faces drops
    // the pending face selection; the scene-side block selection (facing border) is untouched.
    addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (getLastHoverPosFace() == null) {
        selectedFace = null;
      }
    });
    // Per-frame overlay (runs after the scene's translucent dispatch, camera still bound):
    // 0.5-alpha neighbor ghosts plus the colored enabled faces.
    this.overlay = this::renderOverlay;
    collectGhostNeighbors();
    // Ghost neighbors whose visuals live entirely in their BlockEntityRenderer (chests, banners,
    // heads...) are submitted as real BERs before the dispatch passes, so they draw like they do
    // in the world instead of as placeholder boxes.
    this.<WorldSceneRenderer>getRenderer()
        .setAfterBuiltinSubmit(this::submitGhostBlockEntities);
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      // Per-frame tooltip resolution; the face under the cursor makes the lines dynamic.
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
      lines.add(Component.translatable(tab.infoKey + ".change",
          Component.translatable("mmr.gui.tooltip.enabled." + mode.isEnabled()).withStyle(ChatFormatting.AQUA),
          Component.translatable("mmr.gui.tooltip.enabled." + mode.isDisabled()).withStyle(ChatFormatting.AQUA)));
      event.hoverTooltips = HoverTooltips.create(lines.toArray());
    });
  }

  /**
   * The two-step click flow: the first click on a face only selects it (the scene keeps its own
   * selection outline on that face for as long as the panel is open); clicking the already
   * selected face toggles that side's auto-IO on the server.
   */
  private void onFaceClicked(@Nullable Direction face) {
    if (face == null) {
      // A scene event with no face (click missed the component) resets the pending side
      // selection; the component's block itself keeps its scene-side selection.
      selectedFace = null;
      return;
    }
    if (selectedFace != face) {
      selectedFace = face;
      return;
    }
    toggleSide(face);
  }

  /** Toggles the already-selected face's world side on the server via the set-side RPC. */
  private void toggleSide(Direction face) {
    RelativeSide side = RelativeSide.fromDirections(tab.entity.getControllerFacing(), face);
    boolean turnOn = tab.entity.getConfig().getSideMode(side).isDisabled();
    // Runs client-side; the RPC flips the side on the server, which syncs it back.
    tab.sendEvent(tab.setSideRpc, side.name(), turnOn);
  }

  /**
   * Snapshots the six real blocks around the component into the ghost list. Ghosts keep their
   * real-world states (and therefore their true orientation); only non-air blocks get in, and
   * they are drawn per frame in {@link #renderOverlay} — never added to the dummy world.
   */
  private void collectGhostNeighbors() {
    // Client-side only in practice: the SideScene is built by the client-side open() call.
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
      // Preview-space position: the same offset around the origin block as around the
      // real component, so a ghost always sits on the face it belongs to.
      ghosts.add(new GhostBlock(new BlockPos(0, 0, 0).relative(dir), realPos, state, entity));
    }
  }

  /** Overlay hook: ghost neighbors first, then the auto-IO face colors on top. */
  private void renderOverlay(SceneRenderContext ctx) {
    MultiBufferSource.BufferSource bufferSource = ctx.bufferSource();
    drawGhostNeighbors(bufferSource);
    drawFaceTints(bufferSource);
  }

  private void drawGhostNeighbors(MultiBufferSource.BufferSource bufferSource) {
    if (ghosts.isEmpty()) {
      return;
    }
    // The ghost consumer's color multiplier is per-call state: machine ghosts carry their
    // in-world machine color (resolved on the real world position, where a colorable block
    // entity still exists) at the ghost's 0.5 opacity, matching the in-world look.
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
        // No baked block model and no BlockEntityRenderer either: paint a barely-inflated solid
        // box in their slot so the position stays readable (same machinery the Scene uses for
        // selection overlays).
        RenderUtils.renderBlockOverLay(new PoseStack(), bufferSource, ghost.pos(), 0.42f, 0.45f, 0.52f, 1.003f);
      }
      // Everything else (chests, banners, heads — EntityBlock ghosts) is submitted as a real
      // BlockEntity render in {@link #submitGhostBlockEntities}.
    }
  }

  /**
   * Real BlockEntityRenderer submission for the ghost neighbors whose visuals live entirely in
   * their BER. Ghosts are not block entities of the dummy world, so a throwaway BlockEntity is
   * instantiated per ghost for rendering only, wired to the scene's dummy world, and handed to
   * the vanilla dispatcher — the exact sequence of LDLib2's own {@code submitBlockEntities}
   * (prepare with the scene camera, extract the render state, submit translated to the block).
   * Runs during the scene's post-builtin-submit phase, so the normal dispatch passes drain the
   * submitted nodes.
   */
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

  /**
   * Auto-IO enabled faces: blue for the input variant, orange for the output variant — a
   * per-face indicator of which sides currently take part in the auto-IO, exactly the
   * color language the user asked for.
   */
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

  /** The block right next to the hovered face, like the old cell tooltip showed. */
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
