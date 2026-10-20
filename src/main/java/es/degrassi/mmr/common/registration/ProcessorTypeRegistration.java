package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.codec.RegistrarCodec;
import es.degrassi.mmr.api.crafting.IProcessor;
import es.degrassi.mmr.common.manager.crafting.MachineProcessor;
import es.degrassi.mmr.common.manager.crafting.ProcessorType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessorTypeRegistration {
  public static final DeferredRegister<ProcessorType<? extends IProcessor>> MACHINE_PROCESSORS =
      DeferredRegister.create(ProcessorType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Registry<ProcessorType<? extends IProcessor>> PROCESSOR_REGISTRY =
      MACHINE_PROCESSORS.makeRegistry(builder -> {});

  public static final NamedCodec<ProcessorType<?>> PROCESSOR = RegistrarCodec.of(PROCESSOR_REGISTRY, true);
  public static final Supplier<ProcessorType<MachineProcessor>> MACHINE_PROCESSOR =
      MACHINE_PROCESSORS.register(rootLC("machine"), () -> ProcessorType.create(MachineProcessor.Template.CODEC));


  public static void register(final IEventBus bus) {
    MACHINE_PROCESSORS.register(bus);
  }
}
