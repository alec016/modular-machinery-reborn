package es.degrassi.mmr.common.integration.kubejs.events;

import com.google.common.collect.Lists;
import dev.latvian.mods.kubejs.event.KubeEvent;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.common.integration.kubejs.builder.ExtraTooltipsBuilderJS;
import lombok.Getter;
import net.minecraft.resources.Identifier;

import java.util.List;

@Getter
public class ExtraTooltipsKubeEvent implements KubeEvent {
  private final List<ExtraTooltipsBuilderJS> builders = Lists.newArrayList();

  public ExtraTooltipsBuilderJS create(Identifier id, TooltipUse useOn) {
    ExtraTooltipsBuilderJS builder = new ExtraTooltipsBuilderJS(id, useOn);
    builders.add(builder);
    return builder;
  }
}
