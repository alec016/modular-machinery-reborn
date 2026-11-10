package es.degrassi.mmr.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmr.client.entity.renderer.HatchAreaRenderer;
import es.degrassi.mmr.client.entity.renderer.StructureCheckerRenderer;
import es.degrassi.mmr.client.entity.renderer.StructureCreatorRenderer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, value = Dist.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MMRClientEvents {
  @Getter
  private static RecipeMap clientRecipes = RecipeMap.EMPTY;

  @SubscribeEvent
  public static void submitGeometry(final SubmitCustomGeometryEvent event) {
    StructureCreatorRenderer.submitSelectedBlocks(event.getSubmitNodeCollector(), event.getPoseStack());
    HatchAreaRenderer.submit(event.getSubmitNodeCollector(), event.getPoseStack());
    ControllerRenderer.submit(event.getPoseStack(), event.getSubmitNodeCollector());
    StructureCheckerRenderer.submit(event.getPoseStack(), event.getSubmitNodeCollector());
  }

  @SubscribeEvent
  public static void receiveClientRecipes(final RecipesReceivedEvent event) {
    clientRecipes = event.getRecipeMap();
  }

  @SubscribeEvent
  public static void clientDisconnected(final ClientPlayerNetworkEvent.LoggingOut event) {
    clientRecipes = RecipeMap.EMPTY;
    ClientUIStates.getInstance().clear();
    HatchAreaRenderer.clear();
  }
}