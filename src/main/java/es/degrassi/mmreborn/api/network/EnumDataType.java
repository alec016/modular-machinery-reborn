package es.degrassi.mmreborn.api.network;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.network.data.EnumData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDataType<T extends Enum<T>> extends DataType<EnumData<T>, T> {

    public static <T extends Enum<T>> EnumDataType<T> createEnum(
        BiFunction<Supplier<T>,
        Consumer<T>, ISyncable<EnumData<T>, T>> builder,
        TriFunction<Short, Class<T>, RegistryFriendlyByteBuf, EnumData<T>> reader
    ) {
        return new EnumDataType<>(builder, reader);
    }

    private final BiFunction<Supplier<T>, Consumer<T>, ISyncable<EnumData<T>, T>> builder;
    private final TriFunction<Short, Class<T>, RegistryFriendlyByteBuf, EnumData<T>> reader;

    /**
     * A constructor for {@link EnumDataType}.
     * Use {@link EnumDataType#create(Class, BiFunction, BiFunction)} instead.
     */
    private EnumDataType(BiFunction<Supplier<T>, Consumer<T>, ISyncable<EnumData<T>, T>> builder,
                         TriFunction<Short, Class<T>, RegistryFriendlyByteBuf, EnumData<T>> reader) {
        super(null, null, null);
        this.builder = builder;
        this.reader = reader;
    }

    /**
     * This can be used by addons to create {@link ISyncable} instance without directly referencing the class implementing {@link ISyncable}.
     * @param supplier A {@link Supplier}, used to get the synced object on server side.
     * @param consumer A {@link Consumer}, used to set the synced object on client side.
     * @return An instance of {@link ISyncable} that can be passed to the container using {@link ISyncableStuff#getStuffToSync(Consumer)} method.
     */
    public ISyncable<EnumData<T>, T> createSyncable(Supplier<T> supplier, Consumer<T> consumer) {
        return this.builder.apply(supplier, consumer);
    }

    /**
     * Used to create an {@link IData} instance for this type, using the {@link RegistryFriendlyByteBuf} sent by the server.
     * @param id The {@link IData} ID, previously read by {@link IData#readData(RegistryFriendlyByteBuf)}.
     * @param buffer The {@link RegistryFriendlyByteBuf} sent by the server.
     * @return An {@link IData} of this type, holding the synced object.
     */
    public EnumData<T> readData(short id, Class<T> type, RegistryFriendlyByteBuf buffer) {
        return this.reader.apply(id, type, buffer);
    }

    /**
     * A helper method to get the ID of this {@link EnumDataType}.
     * @return The ID of this {@link EnumDataType}, or null if it is not registered.
     */
    public ResourceLocation getId() {
        return ModularMachineryReborn.dataRegistrar().getKey(this);
    }
}
