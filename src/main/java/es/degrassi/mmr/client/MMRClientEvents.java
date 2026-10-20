package es.degrassi.mmr.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.entity.renderer.StructureCreatorRenderer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, value = Dist.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MMRClientEvents {
  @SubscribeEvent
  public static void renderLevel(final RenderLevelStageEvent.AfterTranslucentParticles event) {
    StructureCreatorRenderer.renderSelectedBlocks(event.getPoseStack());
  }

  @SubscribeEvent
  public static void clientTick(ClientTickEvent.Post event) {
  }

  @Getter
  private static RecipeMap clientRecipes = RecipeMap.EMPTY;

  @SubscribeEvent
  public static void receiveClientRecipes(final RecipesReceivedEvent event) {
    clientRecipes = event.getRecipeMap();
  }

  @SubscribeEvent
  public static void clientDisconnected(final ClientPlayerNetworkEvent.LoggingOut event) {
    clientRecipes = RecipeMap.EMPTY;
  }
}
