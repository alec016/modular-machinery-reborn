package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.PartialBlockState;
import es.degrassi.mmr.client.ModularMachineryRebornClient;
import es.degrassi.mmr.client.util.RenderTypes;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.util.CycleTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class StructureRenderer {
  private final int time;
  private final long start;
  private final Map<Direction, Map<BlockPos, BlockIngredient>> blocksGetter = new HashMap<>();
  private final Map<Map<BlockPos, BlockIngredient>, CycleTimer> timers = new HashMap<>();

  public StructureRenderer(int time, Function<Direction, Map<BlockPos, BlockIngredient>> blocksGetter) {
    this.start = System.currentTimeMillis();
    final int configTagTime = MMRConfig.get().blockTagCycleTime.get();
    AtomicInteger maxTime = new AtomicInteger(time);
    for (var direction : Direction.values()) {
      if (direction.getAxis().isVertical()) continue;
      this.blocksGetter.put(direction, blocksGetter.apply(direction));
      Map<BlockPos, BlockIngredient> map = this.blocksGetter.get(direction);
      map.forEach((pos, ing) -> {
        int currentMax = maxTime.get();
        int possibleMax = Math.max(currentMax, ing.getAll().size() * configTagTime);
        if (possibleMax != currentMax) maxTime.set(possibleMax);
      });
      timers.put(map, new CycleTimer(() -> configTagTime, false));
    }
    this.time = maxTime.get();
  }

  public void render(BlockEntityRendererProvider.Context context, PoseStack matrix, SubmitNodeCollector collector, Direction direction, Level world, BlockPos machinePos) {
    Map<BlockPos, BlockIngredient> blocks = this.blocksGetter.get(direction);
    CycleTimer timer = this.timers.get(blocks);
    if (timer == null) {
      this.timers.put(blocks, new CycleTimer(() -> MMRConfig.get().blockTagCycleTime.get(), false));
      timer = timers.get(blocks);
    }
    timer.onDraw();
    CycleTimer finalTimer = timer;
    blocks.forEach((pos, ingredient) -> {
      matrix.pushPose();
      matrix.translate(pos.getX(), pos.getY(), pos.getZ());
      if (!(pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) && ingredient != BlockIngredient.ANY) {
        PartialBlockState state = finalTimer.get(ingredient.getAll());
        boolean isNot = ingredient.isNot();
        BlockPos blockPos = machinePos.offset(pos);
        if (state != null && state != PartialBlockState.ANY && !state.getBlockState().isAir()) {
          if (world.getBlockState(blockPos).isAir()) {
            matrix.pushPose();
            matrix.translate(0.1F, 0.1F, 0.1F);
            matrix.scale(0.8f, 0.8f, 0.8f);
            renderTransparentBlock(context, world, blockPos, state, matrix, collector);
            if (isNot) renderNope(world, blockPos, matrix, collector);
            matrix.popPose();
          } else if (!ingredient.test(new BlockInWorld(world, blockPos, false))) {
            matrix.pushPose();
            matrix.translate(-0.0005, -0.0005, -0.0005);
            matrix.scale(1.001F, 1.001F, 1.001F);
            renderNope(world, blockPos, matrix, collector);
            matrix.popPose();
          }
        }
      }
      matrix.popPose();
    });
  }

  private void renderTransparentBlock(BlockEntityRendererProvider.Context context, Level level, BlockPos pos, PartialBlockState state,
                                      PoseStack poseStack,
                                      SubmitNodeCollector collector) {
    if (!(level instanceof ClientLevel cl)) return;
    BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state.getBlockState());
    if (model != Minecraft.getInstance().getModelManager().getBlockStateModelSet().missingModel()) {
      collector.submitCustomGeometry(poseStack, RenderTypes.PHANTOM, (pose, builder) -> {
        renderAndCollectParts(cl, model, state.getBlockState(), pos, pose, builder);
      });
    }
  }

  private void renderNope(Level level, BlockPos pos, PoseStack matrix, SubmitNodeCollector collector) {
    if (!(level instanceof ClientLevel cl)) return;
    var model = Minecraft.getInstance().getModelManager().getStandaloneModel(ModularMachineryRebornClient.NOPE_MODEL_KEY);
    if (model != null && model != Minecraft.getInstance().getModelManager().getBlockStateModelSet().missingModel()) {
      collector.submitCustomGeometry(matrix, RenderTypes.NOPE, (pose, builder) -> {
        renderAndCollectParts(cl, model, Blocks.AIR.defaultBlockState(), pos, pose, builder);
      });
    }
  }

  private void renderAndCollectParts(ClientLevel cl, BlockStateModel model, BlockState state,
                                     BlockPos pos, PoseStack.Pose pose,
                                     VertexConsumer builder) {
    List<BlockStateModelPart> parts = new ArrayList<>();
    model.collectParts(cl, pos, state, RandomSource.create(), parts);
    parts.forEach(part -> {
      Arrays.stream(Direction.values())
          .flatMap(dir -> part.getQuads(dir).stream())
          .forEach(quad -> builder.putBakedQuad(pose, quad, new QuadInstance()));
      part.getQuads(null).forEach(quad -> builder.putBakedQuad(pose, quad, new QuadInstance()));
    });
  }

  public boolean shouldRender() {
    return System.currentTimeMillis() < this.start + this.time;
  }
}
