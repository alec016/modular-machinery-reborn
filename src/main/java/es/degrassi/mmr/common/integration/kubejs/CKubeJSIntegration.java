package es.degrassi.mmr.common.integration.kubejs;

import es.degrassi.mmr.common.network.client.CDynamicTooltipEventCallPacket;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class CKubeJSIntegration {
  private CKubeJSIntegration() {}

  public static void collectDynamicTooltip(int windowId) {
    ClientPacketDistributor.sendToServer(new CDynamicTooltipEventCallPacket(windowId));
  }
}
