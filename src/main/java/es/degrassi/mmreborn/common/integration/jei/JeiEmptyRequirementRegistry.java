package es.degrassi.mmreborn.common.integration.jei;

import es.degrassi.mmreborn.api.integration.jei.RegisterJeiEmptyRequirementEvent;
import es.degrassi.mmreborn.common.util.EmptyRequirementType;
import es.degrassi.mmreborn.api.integration.jei.JeiConsumer;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class JeiEmptyRequirementRegistry {
  private JeiEmptyRequirementRegistry() {}

  private static Map<EmptyRequirementType, JeiConsumer> consumers;

  public static void init() {
    RegisterJeiEmptyRequirementEvent event = new RegisterJeiEmptyRequirementEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    consumers = event.getMap();
  }

  public static boolean hasJeiConsumer(EmptyRequirementType type) {
    return consumers.containsKey(type);
  }

  public static JeiConsumer getConsumer(EmptyRequirementType type) {
    return consumers.get(type);
  }
}
