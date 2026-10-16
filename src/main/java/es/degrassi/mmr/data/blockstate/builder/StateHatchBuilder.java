package es.degrassi.mmr.data.blockstate.builder;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;

public class StateHatchBuilder extends MMRStateBuilder<StateHatchBuilder> {

  @Override
  protected Identifier loader() {
    return ModularMachineryReborn.rl("hatch");
  }
}
