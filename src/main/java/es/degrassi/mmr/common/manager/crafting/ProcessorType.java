package es.degrassi.mmr.common.manager.crafting;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.crafting.IProcessor;
import es.degrassi.mmr.api.crafting.IProcessorTemplate;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Used for registering custom {@link ProcessorType}.
 * All instances of this class must be created and registered using {@link Registry} for Fabric or {@link DeferredRegister} for Forge or Architectury.
 * @param <T> The {@link IProcessor} handled by this {@link ProcessorType}.
 */
@Getter
public class ProcessorType<T extends IProcessor> {

  /**
   * The {@link ResourceKey} pointing to the {@link ProcessorType} vanilla registry.
   * Can be used to create a {@link DeferredRegister} for registering your {@link ProcessorType}.
   */
  public static final ResourceKey<Registry<ProcessorType<? extends IProcessor>>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("processor_type"));

  /**
   * A factory method used to create new {@link ProcessorType}.
   * @param codec A {@link NamedCodec} used to parse the specified {@link IProcessorTemplate} from the machine json and send it to the client.
   * @param <T> The {@link IProcessor} handled by this {@link ProcessorType}.
   */
  public static <T extends IProcessor> ProcessorType<T> create(NamedCodec<? extends IProcessorTemplate<T>> codec) {
    return new ProcessorType<>(codec);
  }

  /**
   * -- GETTER --
   *
   * @return A {@link NamedCodec} used to parse the {@link IProcessorTemplate} from the machine json and send it to the clients.
   */
  private final NamedCodec<? extends IProcessorTemplate<T>> codec;

  /**
   * A constructor for {@link ProcessorType}.
   * Use {@link ProcessorType#create(NamedCodec)} instead.
   */
  private ProcessorType(NamedCodec<? extends IProcessorTemplate<T>> codec) {
    this.codec = codec;
  }

  /**
   * A helper method to get the ID of this {@link ProcessorType}.
   * @return The ID of this {@link ProcessorType}, or null if it is not registered.
   */
  public Identifier getId() {
    return ModularMachineryReborn.processorRegistrar().getKey(this);
  }
}
