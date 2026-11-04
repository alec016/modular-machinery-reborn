package es.degrassi.mmr;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.api.crafting.IProcessor;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.handler.FilterConverterRegistry;
import es.degrassi.mmr.api.handler.RegisterFilterConversionEvent;
import es.degrassi.mmr.api.uistate.UIStateType;
import es.degrassi.mmr.api.uistate.UIStates;
import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.block.prop.ConfigLoaded;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.command.MMRCommand;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.crafting.modifier.RecipeModifierTargetEvent;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.data.config.DurabilityHatchConfig;
import es.degrassi.mmr.common.data.config.EffectDispenserConfig;
import es.degrassi.mmr.common.data.config.EnergyHatchConfig;
import es.degrassi.mmr.common.data.config.ExperienceHatchConfig;
import es.degrassi.mmr.common.data.config.FluidHatchConfig;
import es.degrassi.mmr.common.data.config.FuelTankConfig;
import es.degrassi.mmr.common.data.config.ItemBusConfig;
import es.degrassi.mmr.common.data.config.ParallelHatchConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.integration.theoneprobe.TOPInfoProvider;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.MachineJsonReloadListener;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.manager.crafting.ProcessorType;
import es.degrassi.mmr.common.network.client.SSyncUIStatesPacket;
import es.degrassi.mmr.common.network.server.SLootTablesPacket;
import es.degrassi.mmr.common.network.server.SSyncMachinePacket;
import es.degrassi.mmr.common.network.server.SSyncTooltipsPacket;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.EmptyRequirementTypeRegistration;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.registration.ProcessorTypeRegistration;
import es.degrassi.mmr.common.registration.RecipeRegistration;
import es.degrassi.mmr.common.registration.Registration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import es.degrassi.mmr.common.registration.UIStateRegistration;
import es.degrassi.mmr.common.uistate.ServerUIStates;
import es.degrassi.mmr.common.util.EmptyRequirementType;
import es.degrassi.mmr.common.util.LootTableHelper;
import es.degrassi.mmr.common.util.MMRLogger;
import es.degrassi.mmr.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.resource.ListenerKey;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Mod(ModularMachineryReborn.MODID)
public class ModularMachineryReborn {
  public static final String MODID = "modular_machinery_reborn";
  public static final Logger LOGGER = LogManager.getLogger("Modular Machinery Reborn");

  public static final BiMap<Identifier, DynamicMachine> MACHINES = HashBiMap.create();
  public static final BiMap<Identifier, BlockController> MACHINES_BLOCK = HashBiMap.create();
  public static final BiMap<Identifier, EnumMap<TooltipUse, List<Either<Identifier, Component>>>> MACHINE_EXTRA_TOOLTIPS = HashBiMap.create();
  public static final Set<MachineControllerEntity> CONTROLLERS = Sets.newHashSet();

  public ModularMachineryReborn(final ModContainer CONTAINER, final IEventBus MOD_BUS) {
    initConfigs(CONTAINER);

    addConfigLoaders();

    Registration.register(MOD_BUS);

    MOD_BUS.addListener(this::commonSetup);
    MOD_BUS.addListener(this::sendIMCMessages);

    MOD_BUS.addListener(this::registerCapabilities);
    MOD_BUS.addListener(this::reloadConfig);
    MOD_BUS.addListener(this::addToBlacklist);
    MOD_BUS.addListener(this::addFilters);

    final IEventBus GAME_BUS = NeoForge.EVENT_BUS;
    GAME_BUS.addListener(this::serverStarting);
    GAME_BUS.addListener(this::syncDatapacks);
    GAME_BUS.addListener(this::registerReloadListener);
    GAME_BUS.addListener(this::registerCommands);
    GAME_BUS.addListener(this::onReloadStart);
    GAME_BUS.addListener(this::onDatapackSync);
  }

  private static void initConfigs(final ModContainer container) {
    container.registerConfig(ModConfig.Type.STARTUP, MMRConfig.getSpec(), config("common"));
    container.registerConfig(ModConfig.Type.COMMON, EnergyHatchConfig.getSpec(), config("energy_hatch"));
    container.registerConfig(ModConfig.Type.COMMON, FluidHatchConfig.getSpec(), config("fluid_hatch"));
    container.registerConfig(ModConfig.Type.COMMON, ItemBusConfig.getSpec(), config("item_bus"));
    container.registerConfig(ModConfig.Type.COMMON, DurabilityHatchConfig.getSpec(), config("durability_hatch"));
    container.registerConfig(ModConfig.Type.COMMON, ExperienceHatchConfig.getSpec(), config("experience_hatch"));
    container.registerConfig(ModConfig.Type.COMMON, ParallelHatchConfig.getSpec(), config("parallel_hatch"));
    container.registerConfig(ModConfig.Type.COMMON, FuelTankConfig.getSpec(), config("fuel_tank"));
    container.registerConfig(ModConfig.Type.COMMON, EffectDispenserConfig.getSpec(), config("effect_dispenser"));
  }

  private static String config(String name) {
    return String.format("%s/base/%s.toml", MODID, name);
  }

  private static void addConfigLoaders() {
    ConfigLoaded.add(EnergyHatchSize.class, size -> {
      size.maxEnergy = EnergyHatchConfig.get().energySize(size);
      size.maxEnergy = MiscUtils.clamp(size.maxEnergy, 1, Long.MAX_VALUE);
      size.transferLimit = EnergyHatchConfig.get().energyLimit(size);
      size.transferLimit = MiscUtils.clamp(size.transferLimit, 1, Long.MAX_VALUE);
    });
    ConfigLoaded.add(FluidHatchSize.class, size -> size.size = FluidHatchConfig.get().fluidSize(size));
    ConfigLoaded.add(ItemBusSize.class, size -> {
      size.slots = ItemBusConfig.get().itemSize(size);
      size.cols = ItemBusConfig.get().itemCols(size);
      size.stackSize = ItemBusConfig.get().stackSize(size);
    });
    ConfigLoaded.add(ItemDurabilityHatchSize.class, size -> {
      size.slots = DurabilityHatchConfig.get().durabilitySize(size);
      size.cols = DurabilityHatchConfig.get().durabilityCols(size);
      size.stackSize = DurabilityHatchConfig.get().stackSize(size);
    });
    ConfigLoaded.add(ExperienceHatchSize.class, size -> size.capacity = ExperienceHatchConfig.get().experienceSize(size));
    ConfigLoaded.add(ParallelHatchSize.class, size -> size.max = ParallelHatchConfig.get().maxParallel(size));
    ConfigLoaded.add(FuelTankSize.class, size -> {
      size.burnTimeCapacity = FuelTankConfig.get().fuelCapacity(size);
      size.stackSize = FuelTankConfig.get().stackSize(size);
    });
    ConfigLoaded.add(EffectDispenserSize.class, size -> {
      size.radius = EffectDispenserConfig.get().radius(size);
      size.interdimensional = EffectDispenserConfig.get().interdimensional(size);
      size.showParticles = EffectDispenserConfig.get().showParticles(size);
    });
  }

  private void sendIMCMessages(final InterModEnqueueEvent event) {
    if (ModList.get().isLoaded("theoneprobe"))
      InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPInfoProvider::new);
  }

  private void serverStarting(final ServerStartingEvent event) {
    LootTableHelper.generate(event.getServer());
  }

  private void syncDatapacks(final OnDatapackSyncEvent event) {
    if (event.getPlayer() != null)
      syncData(event.getPlayer());
    else {
      LootTableHelper.generate(event.getPlayerList().getServer());
      event.getPlayerList().getPlayers().forEach(this::syncData);
    }
  }

  public void syncData(ServerPlayer player) {
    MACHINES.forEach((id, machine) -> PacketDistributor.sendToPlayer(player, new SSyncMachinePacket(machine)));
    PacketDistributor.sendToPlayer(player, new SLootTablesPacket(LootTableHelper.getLoots()));
    MACHINE_EXTRA_TOOLTIPS.forEach((id, tooltips) -> PacketDistributor.sendToPlayer(player, new SSyncTooltipsPacket(id, tooltips)));
    PacketDistributor.sendToPlayer(player, new SSyncUIStatesPacket(UIStates.exportAll(player)));
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    MMRLogger.init();

    Config.load();
    ConfigLoaded.load();
    EnergyDisplayUtil.loadFromConfig();
    ServerUIStates.init();

    RecipeModifierTargetEvent.init();
    FilterConverterRegistry.init();
  }

  private void addFilters(final RegisterFilterConversionEvent event) {
    BuiltInRegistries.FLUID.forEach(fluid -> {
      if (!fluid.isSource(fluid.defaultFluidState())) return;
      if (fluid.isSame(Fluids.EMPTY)) {
        event.register(Items.BUCKET::getDefaultInstance, () -> FluidResource.EMPTY);
        return;
      }
      event.register(fluid.getBucket()::getDefaultInstance, () -> FluidResource.of(fluid));
    });
  }

  private void addToBlacklist(RecipeModifierTargetEvent.Blacklist event) {
    event.register(RequirementTypeRegistration.DIMENSION);
    event.register(RequirementTypeRegistration.BIOME);
    event.register(RequirementTypeRegistration.WEATHER);
    event.register(RequirementTypeRegistration.TIME);
    event.register(RequirementTypeRegistration.CHUNKLOAD);
    event.register(RequirementTypeRegistration.FUNCTION);
    event.register(RequirementTypeRegistration.CHECK_ENTITY);
    event.register(RequirementTypeRegistration.KILL_ENTITY);
    event.register(RequirementTypeRegistration.HEATH_ENTITY);
    event.register(RequirementTypeRegistration.SPAWN_ENTITY);
    event.register(RequirementTypeRegistration.COMMAND);
    event.register(RequirementTypeRegistration.EMPTY);
    event.register(RequirementTypeRegistration.HEIGHT);
    event.register(RequirementTypeRegistration.REDSTONE);
    event.register(RequirementTypeRegistration.STRUCTURE);
    event.register(RequirementTypeRegistration.EFFECT);
  }

  private void reloadConfig(final ModConfigEvent.Reloading event) {
    if (event.getConfig().getSpec() == MMRConfig.getSpec()) {
      MMRLogger.setDebugLevel(MMRConfig.get().debugLevel.get().getLevel());
      Config.load();
      ConfigLoaded.load();
      EnergyDisplayUtil.loadFromConfig();
    }
  }

  private void registerCapabilities(final RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        EntityRegistration.ITEM_INPUT_BUS.get(),
        (be, side) -> side == null || be.getInventory().getAccessibleSides().contains(side) ? be.getInventory() : null
    );
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        EntityRegistration.ITEM_OUTPUT_BUS.get(),
        (be, side) -> side == null || be.getInventory().getAccessibleSides().contains(side) ? be.getInventory() : null
    );
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        EntityRegistration.ITEM_DURABILITY_HATCH.get(),
        (be, side) -> side == null || be.getInventory().getAccessibleSides().contains(side) ? be.getInventory() : null
    );
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        EntityRegistration.FUEL_TANK.get(),
        (be, side) -> side == null || be.getInventory().getAccessibleSides().contains(side) ? be.getInventory() : null
    );
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        EntityRegistration.FLUID_INPUT_HATCH.get(),
        (be, side) -> side == null || be.getTank().getAccessibleSides().contains(side) ? be.getTank() : null
    );
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        EntityRegistration.FLUID_OUTPUT_HATCH.get(),
        (be, side) -> side == null || be.getTank().getAccessibleSides().contains(side) ? be.getTank() : null
    );
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        EntityRegistration.ENERGY_INPUT_HATCH.get(),
        (be, side) -> be.getEnergyHandler()
    );
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        EntityRegistration.ENERGY_OUTPUT_HATCH.get(),
        (be, side) -> be.getEnergyHandler()
    );
    event.registerBlockEntity(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        EntityRegistration.EXPERIENCE_INPUT_HATCH.get(),
        (be, side) -> be.getTank()
    );
    event.registerBlockEntity(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        EntityRegistration.EXPERIENCE_OUTPUT_HATCH.get(),
        (be, side) -> be.getTank()
    );
  }

  private void registerReloadListener(final AddServerReloadListenersEvent event) {
    event.addRetainedListener(
        ListenerKey.create(ModularMachineryReborn.rl("machine_reload")),
        new MachineJsonReloadListener()
    );
  }

  @Contract("_ -> new")
  public static @NotNull Identifier rl(String path) {
    return Identifier.fromNamespaceAndPath(MODID, path.toLowerCase(Locale.ROOT));
  }

  public static String rootLC(String s) {
    return s.toLowerCase(Locale.ROOT);
  }

  public static String rootUC(String s) {
    return s.toUpperCase(Locale.ROOT);
  }

  private void registerCommands(final RegisterCommandsEvent event) {
    event.getDispatcher().register(MMRCommand.register("modularmachineryreborn"));
    event.getDispatcher().register(MMRCommand.register("modularmachinery"));
    event.getDispatcher().register(MMRCommand.register("modular_machinery_reborn"));
    event.getDispatcher().register(MMRCommand.register("modular_machinery"));
    event.getDispatcher().register(MMRCommand.register("mmr"));
    event.getDispatcher().register(MMRCommand.register("mm"));
  }

  private void onReloadStart(final CommandEvent event) {
    if (event.getParseResults().getReader().getString().equals("reload") && event.getParseResults().getContext().getSource().permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS))) {
      MMRLogger.reset();
      ConfigLoaded.load();
      Config.load();
      EnergyDisplayUtil.loadFromConfig();
      if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
        MMRCommand.reloadMachines(player.level().getServer(), player);
        CONTROLLERS.forEach(controller -> {
          if (controller.getStatus().isMissingStructure()) return;
          controller.getProcessor().reset();
          controller.getProcessor().cores().forEach(MachineProcessorCore::reload);
        });
      }
    }
  }

  private void onDatapackSync(final OnDatapackSyncEvent event) {
    event.sendRecipes(RecipeRegistration.RECIPE_TYPE.get());
  }

  public static MinecraftServer getMinecraftServer() {
    return ServerLifecycleHooks.getCurrentServer();
  }

  public static boolean isClientSide() {
    return FMLEnvironment.getDist().isClient();
  }

  public static boolean canGetServerLevel() {
    if (isClientSide()) {
      return Minecraft.getInstance().level != null;
    }
    var server = getMinecraftServer();
    return server != null &&
        !(server.isStopped() || server.isShutdown() || !server.isRunning() || server.isCurrentlySaving());
  }

  public static Registry<ProcessorType<? extends IProcessor>> processorRegistrar() {
    return ProcessorTypeRegistration.PROCESSOR_REGISTRY;
  }

  public static Registry<RequirementType<? extends IRequirement<?, ?>, ?, ?>> getRequirementRegistrar() {
    return RequirementTypeRegistration.REQUIREMENTS_REGISTRY;
  }

  public static Registry<ComponentType<?>> getComponentRegistrar() {
    return ComponentRegistration.COMPONENTS_REGISTRY;
  }

  public static Registry<MachineHatchType> getMachineHatchTypeRegistrar() {
    return MachineHatchTypeRegistration.MachineHatchType_REGISTRY;
  }

  public static Registry<EmptyRequirementType> getEmptyRequirementTypeRegistrar() {
    return EmptyRequirementTypeRegistration.EMPTY_REQUIREMENT_REGISTRY;
  }

  public static Registry<UIStateType<?>> getUISateRegistrar() {
    return UIStateRegistration.REGISTRY;
  }
}
