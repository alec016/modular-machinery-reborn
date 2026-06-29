package es.degrassi.mmreborn.common.integration.kubejs;

import com.google.common.collect.Maps;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.integration.kubejs.events.DynamicTooltipKubeEvent;
import es.degrassi.mmreborn.common.integration.kubejs.events.FunctionKubeEvent;
import es.degrassi.mmreborn.common.integration.kubejs.events.MachineKubeEvent;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineLocation;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KubeJSIntegration {
  private KubeJSIntegration() {}
  public static MachineLocation getMachineLocation(Resource resource, String packName, ResourceLocation id) {
    try(PackResources pack = resource.source()) {
      if(pack instanceof KubeFileResourcePack)
        return MachineLocation.fromKubeJS(id, packName);
      else if(pack instanceof VirtualDataPack)
        return MachineLocation.fromKubeJSScript(id, packName);
      return MachineLocation.fromDefault(id, packName);
    }
  }

  public static Map<ResourceLocation, DynamicMachine> collectMachines() {
    ScriptType.SERVER.console.info("Collecting Modular Machinery Reborn machines from JS scripts.");

    MachineKubeEvent event = new MachineKubeEvent();
    MMRKubeJSPlugin.MACHINES.post(event);

    Map<ResourceLocation, DynamicMachine> machines = Maps.newHashMap();
    AtomicReference<ResourceLocation> machineId = new AtomicReference<>();
    try {
      event.getBuilders().forEach(builder -> {
        machineId.set(builder.getId());
        MMRLogger.INSTANCE.info("Parsing machine {} in js script", machineId.get().toString());
        DynamicMachine machine = builder.build();
        MMRLogger.INSTANCE.info("Successfully parsed machine from script: {}", machineId.get().toString());
        if (MMRConfig.get().logDebugStructure.get()) {
          MMRLogger.INSTANCE.debug("Machine: {}", machine.asJson().toString());
        }
        machines.put(machine.getRegistryName(), machine);
      });
    } catch (Exception e) {
      ScriptType.SERVER.console.error("Couldn't build machine: " + machineId.get(), e);
    }
    ScriptType.SERVER.console.infof("Successfully added %s Modular Machines ", event.getBuilders().size());
    return machines;
  }

  public static CraftingResult sendFunctionRequirementEvent(String id, ICraftingContext context, List<String> args) {
    if(!MMRKubeJSPlugin.FUNCTIONS.hasListeners(id))
      return CraftingResult.error(Component.translatable("craftcheck.failure.function.no_listener", id));
    EventResult result = MMRKubeJSPlugin.FUNCTIONS.post(new FunctionKubeEvent(context, args), id);
    if(result.interruptTrue() || result.interruptDefault() || result.pass())
      return CraftingResult.success();
    else if(result.value() instanceof Component error)
      return CraftingResult.error(error);
    else if(result.value() instanceof CharSequence charSequence)
      return CraftingResult.error(Component.literal(charSequence.toString()));
    else
      return CraftingResult.error(Component.translatable("craftcheck.failure.function.interrupt"));
  }

  public static Component sendDynamicTooltipEvent(ResourceLocation id) {
    if (!MMRKubeJSPlugin.DYNAMIC_TOOLTIPS.hasListeners(id)) return Component.literal(id.getPath());
    Component toReturn;
    EventResult result = MMRKubeJSPlugin.DYNAMIC_TOOLTIPS.post(new DynamicTooltipKubeEvent(), id);
    if (result.value() instanceof Component comp) {
      toReturn = comp;
    } else if (result.value() instanceof CharSequence charSequence) {
      toReturn = Component.literal(charSequence.toString());
    } else {
      toReturn = Component.literal(id.getPath());
    }
    return toReturn;
  }

  public static void logError(Throwable error) {
    ScriptType.SERVER.console.error("Error while processing function requirement: ", error);
  }
}
