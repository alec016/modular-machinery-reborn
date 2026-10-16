package es.degrassi.mmr.common.integration.kubejs.builder;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import lombok.AllArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

@AllArgsConstructor
public class ExtraTooltipsBuilderJS {
  private final Identifier id;
  private final TooltipUse useOn;
  private final List<Either<Identifier, Component>> components = Lists.newArrayList();

  public ExtraTooltipsBuilderJS add(Component extra) {
    components.add(Either.right(extra));
    return this;
  }

  public ExtraTooltipsBuilderJS addDynamic(String id) {
    try {
      components.add(Either.left(Identifier.parse(id)));
    } catch(Exception ignored) {
      components.add(Either.left(ModularMachineryReborn.rl(id)));
    }
    return this;
  }

  public int size() {
    return components.size();
  }

  @HideFromJS
  public Identifier getId() {
    return id;
  }

  @HideFromJS
  public TooltipUse getUseOn() {
    return useOn;
  }

  @HideFromJS
  public final List<Either<Identifier, Component>> build() {
    return components;
  }
}
