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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreativeTabsRegistration {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularMachineryReborn.MODID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MODULAR_MACHINERY_REBORN_TAB = CREATIVE_TABS.register("modular_machinery_reborn", () -> CreativeModeTab.builder()
    .title(Component.translatable(rootLC("itemgroup.modular_machinery_reborn.group")))
    .icon(ItemRegistration.MODULARIUM.get()::getDefaultInstance)
    .displayItems((params, output) -> {
      ItemRegistration.getItems().forEach(entry -> {
        if (!(entry.get() instanceof ControllerItem))
          output.accept(entry.get());
      });
    }).build()
  );

  static {
    CREATIVE_TABS.register("controllers", () -> CreativeModeTab.builder()
        .title(Component.translatable(rootLC("itemgroup.modular_machinery_reborn.group.controller")))
        .icon(BlockRegistration.CONTROLLER::stack)
        .displayItems((params, output) -> {
          ModularMachineryReborn.MACHINES.keySet().forEach(id -> {
            if (id.equals(DynamicMachine.DUMMY.getRegistryName())) return;
            output.accept(ControllerItem.makeMachineItem(id));
          });
        })
        .withSearchBar()
        .withTabsBefore(MODULAR_MACHINERY_REBORN_TAB.getId())
        .build());
  }

  public static void register(final IEventBus bus) {
    CREATIVE_TABS.register(bus);
  }
}
