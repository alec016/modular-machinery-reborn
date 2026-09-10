package es.degrassi.mmr;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ModularMachineryReborn.MODID)
public class ModularMachineryReborn {
  public static final String MODID = "modular_machinery_reborn";
  private static final Logger LOGGER = LogUtils.getLogger();
  public ModularMachineryReborn(IEventBus modEventBus, ModContainer modContainer) {
  }
}
