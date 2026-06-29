package es.degrassi.mmreborn.common.machine;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFunction;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.integration.kubejs.KubeJSIntegration;
import es.degrassi.mmreborn.common.util.CustomJsonReloadListener;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.Map;

public class MachineJsonReloadListener extends CustomJsonReloadListener {

  private static final String MAIN_PACKNAME = "main";
  public static IContext context;

  public MachineJsonReloadListener() {
    super(MMRConfig.get().machineDirectory.get());
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
    MMRLogger.INSTANCE.info("Reading Modular Machinery Reborn Machines...");

    ModularMachineryReborn.MACHINES.clear();

    context = this.getContext();

    map.forEach((id, json) -> {
      MachineLocation location = getMachineLocation(resourceManager, id);

      MMRLogger.INSTANCE.info("Parsing machine json: {} in datapack: {}", id, location.getPackName());

      //Check if the content of the json file is a json object
      if(!json.isJsonObject()) {
        MMRLogger.INSTANCE.error("Bad machine JSON: {} must be a json object and not an array or primitive, skipping...", id);
        return;
      }

      //If there is already a machine with same id: error and skip
      if(ModularMachineryReborn.MACHINES.containsKey(id)) {
        MMRLogger.INSTANCE.error("A machine with id: {} already exists, skipping...", id);
        return;
      }

      //Read the file as a CustomMachine
      DataResult<DynamicMachine> result = DynamicMachine.CODEC.read(JsonOps.INSTANCE, json);
      if (result.result().isPresent()) {
        DynamicMachine machine = result.result().get();
        machine.setRegistryName(id);
        ModularMachineryReborn.MACHINES.put(machine.getRegistryName(), machine);
        MMRLogger.INSTANCE.info("Successfully parsed machine json: {}", machine.getRegistryName());
        if (MMRConfig.get().logDebugStructure.get()) {
          MMRLogger.INSTANCE.debug("Machine: {}", machine.asJson().toString());
        }
      } else if(result.error().isPresent())
        MMRLogger.INSTANCE.error("Error while parsing machine json: {}, skipping...\n{}", id, result.error().get().message());
    });

    if(ModList.get().isLoaded("kubejs")) {
      MMRLogger.INSTANCE.info("Collecting machines with kubeJS.");
      Map<ResourceLocation, DynamicMachine> kubejsMachines = KubeJSIntegration.collectMachines();
      if(!kubejsMachines.isEmpty())
        MMRLogger.INSTANCE.info("Successfully added {} machines with kubejs", kubejsMachines.size());
      else
        MMRLogger.INSTANCE.info("No machines found with kubejs");
      ModularMachineryReborn.MACHINES.putAll(kubejsMachines);
    }
    context = null;

    MMRLogger.INSTANCE.info("Finished creating {} modular machines.", ModularMachineryReborn.MACHINES.keySet().size());
    RequirementFunction.errors.clear();
  }

  private MachineLocation getMachineLocation(ResourceManager resourceManager, ResourceLocation id) {
    ResourceLocation path = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "machines/" + id.getPath() + ".json");
    try {
      Resource res = resourceManager.getResourceOrThrow(path);
      String packName = res.sourcePackId();
      if(packName.equals(MAIN_PACKNAME))
        return MachineLocation.fromDefault(id, packName);
      else if(packName.contains("KubeJS") && ModList.get().isLoaded("kubejs"))
        return KubeJSIntegration.getMachineLocation(res, packName, id);
      else {
        try(PackResources pack = res.source()) {
          if(pack instanceof FilePackResources)
            return MachineLocation.fromDatapackZip(id, packName);
          else if(pack instanceof PathPackResources)
            return MachineLocation.fromDatapack(id, packName);
        }
      }
    } catch (IOException ignored) {}
    return MachineLocation.fromDefault(id, MAIN_PACKNAME);
  }
}