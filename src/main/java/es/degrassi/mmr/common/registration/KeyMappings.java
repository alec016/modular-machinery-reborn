package es.degrassi.mmr.common.registration;

import com.mojang.blaze3d.platform.InputConstants;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, value = Dist.CLIENT)
public abstract class KeyMappings {

  private KeyMappings() {}

  @SubscribeEvent
  public static void registerBindings(RegisterKeyMappingsEvent event) {
  }
}
