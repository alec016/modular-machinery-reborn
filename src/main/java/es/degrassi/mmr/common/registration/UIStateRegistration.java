package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.uistate.UIStateType;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRows;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UIStateRegistration {
  private static final DeferredRegister<UIStateType<?>> DR = DeferredRegister.create(UIStateType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Registry<UIStateType<?>> REGISTRY = DR.makeRegistry(builder -> {});

  public static final Supplier<UIStateType<VisibleRows>> VISIBLE_ROWS = DR.register("visible_rows", () -> UIStateType.create(VisibleRows.CODEC));

  public static void register(IEventBus bus) {
    DR.register(bus);
  }
}
