package es.degrassi.mmr.common.integration.kubejs.events;


import com.google.common.collect.Lists;
import dev.latvian.mods.kubejs.event.KubeEvent;
import es.degrassi.mmr.common.integration.kubejs.builder.MachineBuilderJS;
import lombok.Getter;
import net.minecraft.resources.Identifier;

import java.util.List;

@Getter
public class MachineKubeEvent implements KubeEvent {
  private final List<MachineBuilderJS> builders = Lists.newArrayList();

  public MachineBuilderJS create(Identifier id) {
    MachineBuilderJS builder = new MachineBuilderJS(id);
    builders.add(builder);
    return builder;
  }
}