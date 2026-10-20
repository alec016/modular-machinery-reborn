package es.degrassi.mmr.common.network;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.network.client.CBreakStructurePacket;
import es.degrassi.mmr.common.network.client.CChangeIOSideConfigPacket;
import es.degrassi.mmr.common.network.client.CCoreButtonClickedPacked;
import es.degrassi.mmr.common.network.client.CDynamicTooltipEventCallPacket;
import es.degrassi.mmr.common.network.client.CExperienceButtonClickedPacket;
import es.degrassi.mmr.common.network.client.CPlaceStructurePacket;
import es.degrassi.mmr.common.network.client.CRedstoneButtonModeClickedPacket;
import es.degrassi.mmr.common.network.client.CSetFilterSlotItemPacket;
import es.degrassi.mmr.common.network.client.CSetFilterSlotFluidPacket;
import es.degrassi.mmr.common.network.client.emi.FillRecipeC2SPacket;
import es.degrassi.mmr.common.network.server.SAddControllerRenderer;
import es.degrassi.mmr.common.network.server.SLootTablesPacket;
import es.degrassi.mmr.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmr.common.network.server.SOpenFilePacket;
import es.degrassi.mmr.common.network.server.SRemoveControllerRenderer;
import es.degrassi.mmr.common.network.server.SStopSoundInstancePacket;
import es.degrassi.mmr.common.network.server.SSyncDynamicTooltipsPacket;
import es.degrassi.mmr.common.network.server.SSyncMachinePacket;
import es.degrassi.mmr.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmr.common.network.server.SSyncTooltipsPacket;
import es.degrassi.mmr.common.network.server.SUpdateContainerPacket;
import es.degrassi.mmr.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmr.common.network.server.SUpdateFilterInvPacket;
import es.degrassi.mmr.common.network.server.SUpdateFluidFilterPacket;
import es.degrassi.mmr.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateCoresPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateEffectComponent;
import es.degrassi.mmr.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateExperienceComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateFluidComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateFuelComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateItemComponentPacket;
import es.degrassi.mmr.common.network.server.component.SUpdateSideConfigPacket;
import es.degrassi.mmr.common.util.Mods;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID)
public class PacketManager {
  private PacketManager() {}
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar(ModularMachineryReborn.MODID);
    // TO CLIENT
    registrar.playToClient(SLootTablesPacket.TYPE, SLootTablesPacket.CODEC, SLootTablesPacket::handle);
    registrar.playToClient(SOpenFilePacket.TYPE, SOpenFilePacket.CODEC, SOpenFilePacket::handle);
    registrar.playToClient(SMachineUpdatePacket.TYPE, SMachineUpdatePacket.CODEC, SMachineUpdatePacket::handle);
    registrar.playToClient(SUpdateEnergyComponentPacket.TYPE, SUpdateEnergyComponentPacket.CODEC, SUpdateEnergyComponentPacket::handle);
    registrar.playToClient(SUpdateSideConfigPacket.TYPE, SUpdateSideConfigPacket.CODEC, SUpdateSideConfigPacket::handle);
    registrar.playToClient(SUpdateExperienceComponentPacket.TYPE, SUpdateExperienceComponentPacket.CODEC, SUpdateExperienceComponentPacket::handle);
    registrar.playToClient(SUpdateFuelComponentPacket.TYPE, SUpdateFuelComponentPacket.CODEC, SUpdateFuelComponentPacket::handle);
    registrar.playToClient(SUpdateFluidComponentPacket.TYPE, SUpdateFluidComponentPacket.CODEC, SUpdateFluidComponentPacket::handle);
    registrar.playToClient(SUpdateItemComponentPacket.TYPE, SUpdateItemComponentPacket.CODEC, SUpdateItemComponentPacket::handle);
    registrar.playToClient(SUpdateCraftingStatusPacket.TYPE, SUpdateCraftingStatusPacket.CODEC, SUpdateCraftingStatusPacket::handle);
    registrar.playToClient(SUpdateMachineColorPacket.TYPE, SUpdateMachineColorPacket.CODEC, SUpdateMachineColorPacket::handle);
    registrar.playToClient(SUpdateMachineTexturePacket.TYPE, SUpdateMachineTexturePacket.CODEC, SUpdateMachineTexturePacket::handle);
    registrar.playToClient(SSyncMachinePacket.TYPE, SSyncMachinePacket.CODEC, SSyncMachinePacket::handle);
    registrar.playToClient(SSyncPauseStatePacket.TYPE, SSyncPauseStatePacket.CODEC, SSyncPauseStatePacket::handle);
    registrar.playToClient(SSyncTooltipsPacket.TYPE, SSyncTooltipsPacket.CODEC, SSyncTooltipsPacket::handle);
    registrar.playToClient(SSyncDynamicTooltipsPacket.TYPE, SSyncDynamicTooltipsPacket.CODEC, SSyncDynamicTooltipsPacket::handle);
    registrar.playToClient(SAddControllerRenderer.TYPE, SAddControllerRenderer.CODEC, SAddControllerRenderer::handle);
    registrar.playToClient(SRemoveControllerRenderer.TYPE, SRemoveControllerRenderer.CODEC, SRemoveControllerRenderer::handle);
    registrar.playToClient(SUpdateContainerPacket.TYPE, SUpdateContainerPacket.CODEC, SUpdateContainerPacket::handle);
    registrar.playToClient(SUpdateCoresPacket.TYPE, SUpdateCoresPacket.CODEC, SUpdateCoresPacket::handle);
    registrar.playToClient(SStopSoundInstancePacket.TYPE, SStopSoundInstancePacket.CODEC, SStopSoundInstancePacket::handle);
    registrar.playToClient(SUpdateEffectComponent.TYPE, SUpdateEffectComponent.CODEC, SUpdateEffectComponent::handle);
    registrar.playToClient(SUpdateFluidFilterPacket.TYPE, SUpdateFluidFilterPacket.CODEC, SUpdateFluidFilterPacket::handle);
    registrar.playToClient(SUpdateFilterInvPacket.TYPE, SUpdateFilterInvPacket.CODEC, SUpdateFilterInvPacket::handle);

    // TO SERVER
    registrar.playToServer(CPlaceStructurePacket.TYPE, CPlaceStructurePacket.CODEC, CPlaceStructurePacket::handle);
    registrar.playToServer(CBreakStructurePacket.TYPE, CBreakStructurePacket.CODEC, CBreakStructurePacket::handle);
    registrar.playToServer(CExperienceButtonClickedPacket.TYPE, CExperienceButtonClickedPacket.CODEC, CExperienceButtonClickedPacket::handle);
    registrar.playToServer(CCoreButtonClickedPacked.TYPE, CCoreButtonClickedPacked.CODEC, CCoreButtonClickedPacked::handle);
    registrar.playToServer(CRedstoneButtonModeClickedPacket.TYPE, CRedstoneButtonModeClickedPacket.CODEC, CRedstoneButtonModeClickedPacket::handle);
    registrar.playToServer(CChangeIOSideConfigPacket.TYPE, CChangeIOSideConfigPacket.CODEC, CChangeIOSideConfigPacket::handle);
    registrar.playToServer(CDynamicTooltipEventCallPacket.TYPE, CDynamicTooltipEventCallPacket.CODEC, CDynamicTooltipEventCallPacket::handle);
    registrar.playToServer(CSetFilterSlotItemPacket.TYPE, CSetFilterSlotItemPacket.CODEC, CSetFilterSlotItemPacket::handle);
    registrar.playToServer(CSetFilterSlotFluidPacket.TYPE, CSetFilterSlotFluidPacket.CODEC, CSetFilterSlotFluidPacket::handle);

    // JEI/Recipe Transfer packet
    if (Mods.isJEILoaded()) {
      registrar.playToServer(FillRecipeC2SPacket.TYPE, FillRecipeC2SPacket.CODEC, FillRecipeC2SPacket::handle);
    }
  }
}
