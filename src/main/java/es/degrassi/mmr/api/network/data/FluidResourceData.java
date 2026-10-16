package es.degrassi.mmr.api.network.data;

import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class FluidResourceData extends ResourceData<FluidResource> {
  public FluidResourceData(short id, FluidResource value) {
    super(DataRegistration.FLUID_RESOURCE_DATA.get(), DefaultCodecs.FLUID_RESOURCE, id, value);
  }

  public FluidResourceData(short id, RegistryFriendlyByteBuf buf) {
    super(DataRegistration.FLUID_RESOURCE_DATA.get(), DefaultCodecs.FLUID_RESOURCE, id, buf);
  }
}
