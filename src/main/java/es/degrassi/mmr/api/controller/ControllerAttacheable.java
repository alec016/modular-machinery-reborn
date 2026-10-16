package es.degrassi.mmr.api.controller;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface ControllerAttacheable {
  Set<BlockPos> getControllerPosSet();
}
