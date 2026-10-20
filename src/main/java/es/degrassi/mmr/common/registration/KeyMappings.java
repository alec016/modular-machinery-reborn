package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, value = Dist.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyMappings {

  @SubscribeEvent
  public static void registerBindings(RegisterKeyMappingsEvent event) {
  }
}
