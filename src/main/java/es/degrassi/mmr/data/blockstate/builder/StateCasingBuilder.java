package es.degrassi.mmr.data.blockstate.builder;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;

public class StateCasingBuilder extends MMRStateBuilder<StateCasingBuilder> {

  @Override
  protected Identifier loader() {
    return ModularMachineryReborn.rl("casing");
  }
}
