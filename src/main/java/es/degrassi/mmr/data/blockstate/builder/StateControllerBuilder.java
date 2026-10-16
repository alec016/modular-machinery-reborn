package es.degrassi.mmr.data.blockstate.builder;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;

public class StateControllerBuilder extends MMRStateBuilder<StateControllerBuilder> {

  @Override
  protected Identifier loader() {
    return ModularMachineryReborn.rl("controller");
  }
}
