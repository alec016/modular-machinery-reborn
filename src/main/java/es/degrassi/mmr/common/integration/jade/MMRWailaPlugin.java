package es.degrassi.mmr.common.integration.jade;

import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MMRWailaPlugin implements IWailaPlugin {
  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(DynamicMachineComponentProvider.INSTANCE, BlockMachineComponent.class);
    /*registration.usePickedResult(BlockRegistration.CONTROLLER.get());
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(registration::usePickedResult);*/
    registration.registerBlockComponent(MachineHatchComponentProvider.INSTANCE, BlockController.class);
  }

  @Override
  public void register(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(DynamicMachineServerDataProvider.INSTANCE, ColorableMachineComponentEntity.class);
    registration.registerBlockDataProvider(MachineHatchServerDataProvider.INSTANCE, ColorableMachineComponentEntity.class);
  }
}
