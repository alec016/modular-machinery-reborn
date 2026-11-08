package es.degrassi.mmr.common.registration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Registration {
  public static final ContextKeySet MODULAR_MACHINERY_LOOT_PARAMETER_SET = new ContextKeySet.Builder()
      .optional(LootContextParams.ORIGIN)
      .optional(LootContextParams.BLOCK_ENTITY)
      .build();

  public static void register(final IEventBus bus) {
    DataComponentRegistration.register(bus);
    TicketTypeRegistration.register(bus);
    UIStateRegistration.register(bus);
    ComponentRegistration.register(bus);
    MachineHatchTypeRegistration.register(bus);
    DataRegistration.register(bus);
    ProcessorTypeRegistration.register(bus);
    EmptyRequirementTypeRegistration.register(bus);
    RequirementTypeRegistration.register(bus);
    BlockRegistration.register(bus);
    ItemRegistration.register(bus);
    EntityRegistration.register(bus);
    RecipeRegistration.register(bus);
    CreativeTabsRegistration.register(bus);
    SoundRegistration.SOUND_REG.register(bus);
  }
}
