package es.degrassi.mmr.api.controller;

import es.degrassi.mmr.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.Optional;

public interface ComponentMapper {
  Map<BlockPos, Optional<MachineComponent<?>>> getFoundComponentsMap();
}
