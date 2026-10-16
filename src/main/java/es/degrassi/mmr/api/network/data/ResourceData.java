package es.degrassi.mmr.api.network.data;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.network.Data;
import es.degrassi.experiencelib.api.network.DataType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.transfer.resource.Resource;

public abstract class ResourceData<T extends Resource> extends Data<T> {
  private final NamedCodec<T> codec;
  protected ResourceData(DataType<? extends Data<T>, T> type, NamedCodec<T> codec, short id, T value) {
    super(type, id, value);
    this.codec = codec;
  }

  protected ResourceData(DataType<? extends Data<T>, T> type, NamedCodec<T> codec, short id, RegistryFriendlyByteBuf buf) {
    this(type, codec, id, codec.fromNetwork(buf));
  }

  @Override
  public void writeData(RegistryFriendlyByteBuf buffer) {
    super.writeData(buffer);
    codec.toNetwork(buffer, getValue());
  }
}
