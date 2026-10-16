package es.degrassi.mmr.api.network.data;

import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ItemResourceData extends ResourceData<ItemResource> {
  public ItemResourceData(short id, ItemResource value) {
    super(DataRegistration.ITEM_RESOURCE_DATA.get(), DefaultCodecs.ITEM_RESOURCE, id, value);
  }

  public ItemResourceData(short id, RegistryFriendlyByteBuf buf) {
    super(DataRegistration.ITEM_RESOURCE_DATA.get(), DefaultCodecs.ITEM_RESOURCE, id, buf);
  }
}
