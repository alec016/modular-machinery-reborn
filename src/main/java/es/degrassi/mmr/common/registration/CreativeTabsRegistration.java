package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.item.ControllerItem;
import es.degrassi.mmr.common.machine.DynamicMachine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreativeTabsRegistration {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularMachineryReborn.MODID);

  public static final Supplier<CreativeModeTab> MODULAR_MACHINERY_REBORN_TAB = CREATIVE_TABS.register("modular_machinery_reborn", () -> CreativeModeTab.builder()
    .title(Component.translatable(rootLC("itemGroup.modular_machinery_reborn.group")))
    .icon(ItemRegistration.MODULARIUM.get()::getDefaultInstance)
    .displayItems((params, output) -> {
      ItemRegistration.getItems().forEach(entry -> {
        if (entry.get() instanceof ControllerItem) {
          ModularMachineryReborn.MACHINES.keySet().forEach(id -> {
            if (id.equals(DynamicMachine.DUMMY.getRegistryName())) return;
            output.accept(ControllerItem.makeMachineItem(id));
          });
        } else
          output.accept(entry.get());
      });
    }).build()
  );

  public static void register(final IEventBus bus) {
    CREATIVE_TABS.register(bus);
  }
}
