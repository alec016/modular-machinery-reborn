package es.degrassi.mmr.client.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class IWrenchableRenderState extends BlockEntityRenderState {
  @Nullable
  protected Vec3 cameraPos;
  @Nullable
  protected BlockEntity entity;
}
