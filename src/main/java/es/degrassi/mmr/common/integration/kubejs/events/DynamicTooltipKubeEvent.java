package es.degrassi.mmr.common.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;

public class DynamicTooltipKubeEvent implements KubeEvent {
  @Override
  public Object defaultExitValue(Context cx) {
    return Component.empty();
  }
}
