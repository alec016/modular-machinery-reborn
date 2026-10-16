package es.degrassi.mmr.client.integration.athena;

import earth.terrarium.athena.api.client.models.FactoryManager;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.integration.athena.model.casing.CasingBlockModel;
import es.degrassi.mmr.client.integration.athena.model.controller.ControllerBlockModel;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchBlockModel;

public class MMRAthenaModels {
  public static void init() {
    FactoryManager.register(ModularMachineryReborn.rl("casing"), CasingBlockModel.FACTORY);
    FactoryManager.register(ModularMachineryReborn.rl("hatch"), HatchBlockModel.FACTORY);
    FactoryManager.register(ModularMachineryReborn.rl("controller"), ControllerBlockModel.FACTORY);
  }
}
