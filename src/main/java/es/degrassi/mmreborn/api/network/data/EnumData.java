package es.degrassi.mmreborn.api.network.data;

import es.degrassi.mmreborn.api.network.Data;
import es.degrassi.mmreborn.api.network.DataType;
import es.degrassi.mmreborn.common.registration.DataRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;

@SuppressWarnings("unchecked")
public class EnumData<T extends Enum<T>> extends Data<T> {
  public EnumData(short id, T value) {
    super((DataType<? extends Data<T>, T>) DataRegistration.ENUM_DATA.get(), id, value);
  }

  public static <T extends Enum<T>> EnumData<T> readData(short id, Class<T> type, RegistryFriendlyByteBuf buffer) {
    return new EnumData<>(id, buffer.readEnum(type));
  }

  @Override
  public void writeData(RegistryFriendlyByteBuf buffer) {
    super.writeData(buffer);
    buffer.writeUtf(getValue().getClass().getName());
    buffer.writeEnum(getValue());
  }
}
