package es.degrassi.mmreborn.data.blockstate.builder;

import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.resources.ResourceLocation;

public class StateControllerBuilder extends MMRStateBuilder<StateControllerBuilder> {

  @Override
  protected ResourceLocation loader() {
    return ModularMachineryReborn.rl("controller");
  }
}
