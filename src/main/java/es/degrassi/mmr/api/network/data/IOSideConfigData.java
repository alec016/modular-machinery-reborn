package es.degrassi.mmr.api.network.data;

import es.degrassi.experiencelib.api.network.Data;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.common.registration.DataRegistration;
import es.degrassi.mmr.common.util.Color;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class IOSideConfigData extends Data<IOSideConfig> {

  public IOSideConfigData(Short id, IOSideConfig value) {
    super(DataRegistration.IO_SIDE_CONFIG_DATA.get(), id, value);
  }

  public static IOSideConfigData readData(short id, RegistryFriendlyByteBuf buffer) {
    Map<RelativeSide, IOSideMode> map = new HashMap<>();
    for(RelativeSide side : RelativeSide.values())
      map.put(side, buffer.readEnum(IOSideMode.class));
    return new IOSideConfigData(id, new IOSideConfig(null, map, true, Color.fromARGB(buffer.readVarInt())));
  }

  @Override
  public void writeData(RegistryFriendlyByteBuf buffer) {
    super.writeData(buffer);
    for(RelativeSide side : RelativeSide.values())
      buffer.writeEnum(getValue().getSideMode(side));
    buffer.writeVarInt(getValue().getColor().getARGB());
  }
}
