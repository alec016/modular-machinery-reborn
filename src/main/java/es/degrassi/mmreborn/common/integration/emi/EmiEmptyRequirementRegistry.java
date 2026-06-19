package es.degrassi.mmreborn.common.integration.emi;

import es.degrassi.mmreborn.api.integration.emi.RegisterEmiEmptyRequirementEvent;
import es.degrassi.mmreborn.api.integration.emi.EmiConsumer;
import es.degrassi.mmreborn.common.util.EmptyRequirementType;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class EmiEmptyRequirementRegistry {
  private EmiEmptyRequirementRegistry() {}

  private static Map<EmptyRequirementType, EmiConsumer> consumers;

  public static void init() {
    RegisterEmiEmptyRequirementEvent event = new RegisterEmiEmptyRequirementEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    consumers = event.getMap();
  }

  public static boolean hasEmiConsumer(EmptyRequirementType type) {
    return consumers.containsKey(type);
  }

  public static EmiConsumer getConsumer(EmptyRequirementType type) {
    return consumers.get(type);
  }
}
