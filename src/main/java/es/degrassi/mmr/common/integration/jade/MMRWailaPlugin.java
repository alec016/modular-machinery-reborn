package es.degrassi.mmr.common.integration.jade;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.registration.BlockRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import java.util.Optional;

@WailaPlugin
public class MMRWailaPlugin implements IWailaPlugin {
  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(DynamicMachineComponentProvider.INSTANCE, BlockController.class);
    registration.registerBlockComponent(MachineHatchComponentProvider.INSTANCE, BlockMachineComponent.class);
  }

  @Override
  public void register(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(DynamicMachineServerDataProvider.INSTANCE, MachineControllerEntity.class);
    var blockOperations = registration.blockOperations();
    blockOperations.pick(ResourceKey.create(Registries.BLOCK, BlockRegistration.CONTROLLER.id()));
    ModularMachineryReborn.MACHINES_BLOCK.values()
        .forEach(block ->
            Optional.of(BuiltInRegistries.BLOCK.getKey(block))
                .ifPresent(id -> blockOperations.pick(ResourceKey.create(Registries.BLOCK, id)))
        );
    registration.registerBlockDataProvider(MachineHatchServerDataProvider.INSTANCE, ColorableMachineComponentEntity.class);
  }
}
