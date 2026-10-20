package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.bus.api.IEventBus;

/**
 * Container registration for Modular Machinery Reborn.
 * <p>
 * With LDLib2, menu types are registered automatically via {@code BlockUIMenuType}.
 * Blocks implement {@code BlockUIMenuType.BlockUI} and provide their own {@code ModularUI}.
 * No manual MenuType registration is needed.
 * <p>
 * This class is kept for reference and to hold any future static menu type references if needed.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerRegistration {

  public static void register(IEventBus bus) {
    // LDLib2's BlockUIMenuType handles menu registration internally.
    // Blocks that have GUIs implement BlockUIMenuType.BlockUI and
    // call BlockUIMenuType.openUI(player, pos) to open their UI.
    // No manual MenuType registration is needed.
    ModularMachineryReborn.LOGGER.info("LDLib2 BlockUIMenuType handles container registration - no manual MenuTypes needed");
  }
}
