package es.degrassi.mmr.client.integration.athena.model.controller;

import earth.terrarium.athena.api.client.utils.NullableEnumMap;
import es.degrassi.mmr.client.integration.athena.utils.MMRAthenaQuad;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.MachineModelLocation;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record ControllerData(
    DynamicMachine machine,
    @Nullable NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> data,
    MachineStatus status
) {
  public boolean hasCustomModel() {
    return modelLocation() != MachineModelLocation.DEFAULT;
  }

  public MachineModelLocation modelLocation() {
    return machine.getControllerModel(status);
  }

  public boolean hasData() {
    return data != null;
  }
}
