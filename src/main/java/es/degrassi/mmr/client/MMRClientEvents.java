package es.degrassi.mmr.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.entity.renderer.StructureCreatorRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, value = Dist.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MMRClientEvents {
  @SubscribeEvent
  public static void renderLevel(final RenderLevelStageEvent.AfterTranslucentFeatures event) {
    StructureCreatorRenderer.renderSelectedBlocks(event.getPoseStack());
  }

  @SubscribeEvent
  public static void clientTick(ClientTickEvent.Post event) {
  }
}
