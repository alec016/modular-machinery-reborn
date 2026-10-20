package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.EmptyRequirementType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyRequirementTypeRegistration {

  public static final DeferredRegister<EmptyRequirementType> MACHINE_COMPONENTS =
      DeferredRegister.create(EmptyRequirementType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Registry<EmptyRequirementType> EMPTY_REQUIREMENT_REGISTRY =
      MACHINE_COMPONENTS.makeRegistry(builder -> {
  });

  public static final Supplier<EmptyRequirementType> ITEM =
      MACHINE_COMPONENTS.register(rootLC("item".toLowerCase(Locale.ENGLISH)),
          () -> EmptyRequirementType.create(
              36,
              0,
              16,
              16
          ));

  public static final Supplier<EmptyRequirementType> FLUID =
      MACHINE_COMPONENTS.register(rootLC("fluid".toLowerCase(Locale.ENGLISH)),
          () -> EmptyRequirementType.create(
              0,
              18,
              16,
              16
          ));

  public static final Supplier<EmptyRequirementType> ENERGY =
      MACHINE_COMPONENTS.register(rootLC("energy".toLowerCase(Locale.ENGLISH)),
          () -> EmptyRequirementType.create(
              18,
              54,
              16,
              52
          ));

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
