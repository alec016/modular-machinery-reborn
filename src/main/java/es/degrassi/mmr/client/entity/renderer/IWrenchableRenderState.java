package es.degrassi.mmr.client.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class IWrenchableRenderState extends BlockEntityRenderState {
  protected Vec3 cameraPos;
  protected BlockEntity entity;
}
