package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.TicketType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TicketTypeRegistration {
  private static final DeferredRegister<TicketType> DR = DeferredRegister.create(Registries.TICKET_TYPE, ModularMachineryReborn.MODID);

  public static final DeferredHolder<TicketType, TicketType> MACHINE_CHUNKLOADER = ticketType("chunkloader", 0L, 15);

  public static void register(IEventBus bus) {
    DR.register(bus);
  }

  static DeferredHolder<TicketType,  TicketType> ticketType(String name, long timeout, int flags) {
    return DR.register(name, () -> new TicketType(timeout, flags));
  }
}
