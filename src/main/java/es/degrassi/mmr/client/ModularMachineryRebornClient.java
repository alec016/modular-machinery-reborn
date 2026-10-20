package es.degrassi.mmr.client;

import com.google.common.collect.Lists;
import com.mojang.math.Quadrant;
import es.degrassi.experiencelib.api.definition.BlockDefinition;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.IWrenchable;
import es.degrassi.mmr.api.entity.ColorableMachineEntity;
import es.degrassi.mmr.api.handler.FilterRendererRegistry;
import es.degrassi.mmr.api.handler.RegisterFilterRendererEvent;
import es.degrassi.mmr.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmr.client.entity.renderer.IWrenchableRenderer;
import es.degrassi.mmr.client.entity.renderer.StructureCheckerRenderer;
import es.degrassi.mmr.client.integration.athena.MMRAthenaModels;
import es.degrassi.mmr.client.integration.jei.MMRJeiClientIntegration;
import es.degrassi.mmr.client.item.MMRItemTintSource;
import es.degrassi.mmr.client.item.MMRItemTooltip;
import es.degrassi.mmr.client.item.MMRItemTooltipComponent;
import es.degrassi.mmr.client.model.InitBuiltInModels;
import es.degrassi.mmr.client.model.controller.ControllerModelLoader;
import es.degrassi.mmr.client.model.hatch.HatchModelLoader;
import es.degrassi.mmr.client.util.FluidRenderer;
import es.degrassi.mmr.common.block.base.BlockDynamicColor;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.util.Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@Mod(value = ModularMachineryReborn.MODID, dist = Dist.CLIENT)
public class ModularMachineryRebornClient {
  public static final ClientScheduler clientScheduler = new ClientScheduler();

  public static final StandaloneModelKey<BlockStateModel> NOPE_MODEL_KEY =
      new StandaloneModelKey<>(() -> "mmr_block_nope");
  public static final StandaloneModelKey<BlockStateModel> DEFAULT_CONTROLLER_KEY =
      new StandaloneModelKey<>(() -> "mmr_default_controller");
  public static final StandaloneModelKey<BlockStateModel> DEFAULT_HATCH_ALL_KEY =
      new StandaloneModelKey<>(() -> "mmr_default_hatch_all");
  public static final StandaloneModelKey<BlockStateModel> DEFAULT_HATCH_ORIENTABLE_KEY =
      new StandaloneModelKey<>(() -> "mmr_default_hatch_orientable");

  private static final Map<Identifier, StandaloneModelKey<BlockStateModel>> hatchModelKeys = new HashMap<>();
  private static final Map<Identifier, StandaloneModelKey<BlockStateModel>> configModelKeys = new HashMap<>();
  /** Same as the base keys, but rotated to match vanilla blockstate {@code facing} variants (see {@link #registerRotated}). */
  private static final Map<Identifier, Map<Direction, StandaloneModelKey<BlockStateModel>>> rotatedModelKeys = new HashMap<>();

  private static final List<Block> blockModelsToRegister = Lists.newLinkedList();
  private static final List<Item> itemModelsToRegister = Lists.newLinkedList();
  private final IEventBus bus;

  public ModularMachineryRebornClient(final IEventBus bus) {
    MMRAthenaModels.init();
    InitBuiltInModels.init();
    NeoForge.EVENT_BUS.register(clientScheduler);
    bus.register(this);
    this.bus = bus;
  }

  @SubscribeEvent
  public void registerBlockEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    EntityRegistration.DR.getEntries().forEach(holder -> {
      if (holder.getDelegate().value().create(BlockPos.ZERO,
          holder.value().getValidBlocks().iterator().next().defaultBlockState()) instanceof IWrenchable aw && aw.shouldAddRender())
        event.registerBlockEntityRenderer(holder.get(), IWrenchableRenderer::new);
    });
    event.registerBlockEntityRenderer(EntityRegistration.CONTROLLER.get(), ControllerRenderer::new);
    event.registerBlockEntityRenderer(EntityRegistration.STRUCTURE_CHECKER.get(), StructureCheckerRenderer::new);
  }

  @SubscribeEvent
  public void registerModelLoader(final ModelEvent.RegisterLoaders event) {
    event.register(ModularMachineryReborn.rl("controller"), ControllerModelLoader.INSTANCE);
    event.register(ModularMachineryReborn.rl("hatch"), HatchModelLoader.INSTANCE);
  }

  @SubscribeEvent
  public void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
    event.register(MMRItemTooltipComponent.class, c -> new MMRItemTooltip(c.getItem(), c.getComponent(), c.isCompleted()));
  }

  @SubscribeEvent
  public void onBackingCompleted(final ModelEvent.BakingCompleted event) {
    // Models are now accessed via ModelManager.getStandaloneModel(key)
    // No need to store them in a map anymore
  }

  public static Map<Identifier, BlockStateModel> getAllModels() {
    return Map.of();
  }

  @Nullable
  public static BlockStateModel getStandaloneModel(StandaloneModelKey<BlockStateModel> key) {
    return Minecraft.getInstance().getModelManager().getStandaloneModel(key);
  }

  /**
   * The 1.21.1 fallback flow ({@code DefaultHatchBakedModel} / model loaders) rebaked the default chain
   * per render, rotating it to the machine facing with quaternions. The 26.1 standalone pipeline bakes
   * models once, so every default/config model additionally registers the three horizontal rotations the
   * legacy code applied (same quaternions as vanilla {@code y: 90/180/270} blockstate variants).
   */
  private static void registerRotated(ModelEvent.RegisterStandalone event, Identifier modelRL) {
    EnumMap<Direction, StandaloneModelKey<BlockStateModel>> rotated = new EnumMap<>(Direction.class);
    registerRotated(event, rotated, modelRL, Direction.EAST, Quadrant.R90);
    registerRotated(event, rotated, modelRL, Direction.SOUTH, Quadrant.R180);
    registerRotated(event, rotated, modelRL, Direction.WEST, Quadrant.R270);
    rotatedModelKeys.put(modelRL, rotated);
  }

  private static void registerRotated(ModelEvent.RegisterStandalone event,
                                      Map<Direction, StandaloneModelKey<BlockStateModel>> rotated,
                                      Identifier modelRL, Direction facing, Quadrant angle) {
    StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(() -> "mmr_rot_" + angle + "_" + modelRL);
    event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(modelRL,
        BlockModelRotation.get(Quadrant.fromXYZAngles(Quadrant.R0, angle, Quadrant.R0))));
    rotated.put(facing, key);
  }

  /**
   * The unbaked-key registry for one default/config model, preferring the facing-rotated variant
   * baked by {@link #registerRotated}. {@code facing == null} (or NORTH) returns the unrotated key.
   */
  @Nullable
  public static StandaloneModelKey<BlockStateModel> modelKey(Identifier modelRL, @Nullable Direction facing) {
    StandaloneModelKey<BlockStateModel> base = baseModelKey(modelRL);
    if (base == null) return null;
    if (facing == null || facing == Direction.NORTH || !rotatedModelKeys.containsKey(modelRL)) return base;
    StandaloneModelKey<BlockStateModel> key = rotatedModelKeys.get(modelRL).get(facing);
    return key != null ? key : base;
  }

  @Nullable
  private static StandaloneModelKey<BlockStateModel> baseModelKey(Identifier modelRL) {
    if (ModularMachineryReborn.rl("default/hatch_all").equals(modelRL)) return DEFAULT_HATCH_ALL_KEY;
    if (ModularMachineryReborn.rl("default/hatch_orientable").equals(modelRL)) return DEFAULT_HATCH_ORIENTABLE_KEY;
    if (ModularMachineryReborn.rl("default/controller").equals(modelRL)) return DEFAULT_CONTROLLER_KEY;
    StandaloneModelKey<BlockStateModel> key = hatchModelKeys.get(modelRL);
    if (key != null) return key;
    return configModelKeys.get(modelRL);
  }

  /** Resolved block-state model for one default/config model key, or {@code null} before bake/when unregistered. */
  @Nullable
  public static BlockStateModel standaloneModel(Identifier modelRL, @Nullable Direction facing) {
    return getStandaloneModel(modelKey(modelRL, facing));
  }

  public static boolean shouldAddParticles(RandomSource r) {
    return switch (Minecraft.getInstance().options.particles().get()) {
      case ALL -> true;
      case DECREASED -> r.nextBoolean();
      case MINIMAL -> false;
    };
  }

  public static void createParticle(ParticleOptions particle, BlockPos pos) {
    Minecraft.getInstance().particleEngine.createParticle(
        particle,
        pos.getX(),
        pos.getY() + 0.5,
        pos.getZ(),
        0.0, 0.0, 0.0
    );
  }

  private record MMRBlockTintSource(int tintIndex) implements BlockTintSource {
    @Override
    public int color(BlockState blockState) {
      return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
      return blockColor(state, level, pos, tintIndex);
    }
  }

  private List<BlockTintSource> createTintSources() {
    return IntStream.range(0, 5)
        .<BlockTintSource>mapToObj(MMRBlockTintSource::new)
        .toList();
  }

  @SubscribeEvent
  public void registerBlockColors(final RegisterColorHandlersEvent.BlockTintSources event) {
    List<Block> toRegister = new ArrayList<>();
    BlockRegistration.BLOCKS
        .stream()
        .map(BlockDefinition::block)
        .filter(b -> b instanceof BlockDynamicColor)
        .forEach(toRegister::add);
    toRegister.addAll(ModularMachineryReborn.MACHINES_BLOCK.values());
    event.register(createTintSources(), toRegister.toArray(Block[]::new));
  }

  @SubscribeEvent
  public void registerItemColors(final RegisterColorHandlersEvent.ItemTintSources event) {
    event.register(MMRItemTintSource.ID, MMRItemTintSource.MAP_CODEC);
  }

  public static int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
    switch (tintIndex) {
      case 1 -> level.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER);
      case 2 -> level.getBlockTint(pos, BiomeColors.GRASS_COLOR_RESOLVER);
      case 3 -> level.getBlockTint(pos, BiomeColors.FOLIAGE_COLOR_RESOLVER);
      case 4 -> {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof ColorableMachineEntity machineTile) {
          return machineTile.getMachineColor();
        }
      }
    }
    return -1;
  }

  @SubscribeEvent
  public void onModelRegister(ModelEvent.RegisterStandalone event) {
    Identifier nopeRl = ModularMachineryReborn.rl("block/nope");
    event.register(NOPE_MODEL_KEY, SimpleUnbakedStandaloneModel.blockStateModel(nopeRl));

    Identifier defaultControllerRl = ModularMachineryReborn.rl("default/controller");
    event.register(DEFAULT_CONTROLLER_KEY, SimpleUnbakedStandaloneModel.blockStateModel(defaultControllerRl));
    registerRotated(event, defaultControllerRl);

    Identifier defaultHatchAllRl = ModularMachineryReborn.rl("default/hatch_all");
    event.register(DEFAULT_HATCH_ALL_KEY, SimpleUnbakedStandaloneModel.blockStateModel(defaultHatchAllRl));

    Identifier defaultHatchOrientableRl = ModularMachineryReborn.rl("default/hatch_orientable");
    event.register(DEFAULT_HATCH_ORIENTABLE_KEY, SimpleUnbakedStandaloneModel.blockStateModel(defaultHatchOrientableRl));
    registerRotated(event, ModularMachineryReborn.rl("default/hatch_all"));
    registerRotated(event, defaultHatchOrientableRl);

    Minecraft.getInstance().getResourceManager().listResources("models/default/hatches", s -> s.getPath().endsWith(".json")).forEach((rl, resource) -> {
      Identifier modelRL = Identifier.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().substring(7).replace(".json", ""));
      StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(() -> "mmr_hatch_" + modelRL);
      hatchModelKeys.put(modelRL, key);
      event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(modelRL));
      registerRotated(event, modelRL);
    });

    for (String folder : MMRConfig.get().modelFolders.get()) {
      Minecraft.getInstance().getResourceManager().listResources("models/" + folder, s -> s.getPath().endsWith(".json")).forEach((rl, resource) -> {
        Identifier modelRL = Identifier.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().substring(7).replace(".json", ""));
        StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(() -> "mmr_config_" + modelRL);
        configModelKeys.put(modelRL, key);
        event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(modelRL));
        registerRotated(event, modelRL);
      });
    }

    for (Block block : blockModelsToRegister) {
      StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(() -> "mmr_block_" + block);
      event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(
          Identifier.fromNamespaceAndPath(ModularMachineryReborn.MODID, block.getClass().getSimpleName().toLowerCase())
      ));
      itemModelsToRegister.add(block.asItem());
    }

    for (Item item : itemModelsToRegister) {
      String name = item.getClass().getSimpleName().toLowerCase();
      if (item instanceof BlockItem blockItem) {
        name = blockItem.getBlock().getClass().getSimpleName().toLowerCase();
      }
      StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(() -> "mmr_item_" + item);
      event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(
          Identifier.fromNamespaceAndPath(ModularMachineryReborn.MODID, name)
      ));
    }
  }

  @SubscribeEvent
  public void clientSetup(final FMLClientSetupEvent event) {
    if (Mods.isJEILoaded()) {
      new MMRJeiClientIntegration(bus);
    }
    FilterRendererRegistry.init();
  }

  @SubscribeEvent
  public void registerFilterRender(final RegisterFilterRendererEvent event) {
    BuiltInRegistries.FLUID.forEach(fluid -> {
      if (!fluid.isSource(fluid.defaultFluidState())) return;
      event.register(fluid, (GuiGraphicsExtractor, x, y) -> {
        FluidRenderer.renderFluid(
            x, y,
            16, 16,
            new FluidStack(fluid, 1000),
            1000
        );
      });
    });
  }

  public void registerBlockModel(Block block) {
    blockModelsToRegister.add(block);
  }

  public void registerItemModel(Item item) {
    itemModelsToRegister.add(item);
  }

  @SubscribeEvent
  public void registerMenuScreens(final RegisterMenuScreensEvent event) {
    /*event.register(ContainerRegistration.CONTROLLER.get(), ControllerScreen::new);
    event.register(ContainerRegistration.ENERGY_HATCH.get(), EnergyHatchScreen::new);
    event.register(ContainerRegistration.FLUID_HATCH.get(), FluidHatchScreen::new);
    event.register(ContainerRegistration.ITEM_BUS.get(), ItemBusScreen::new);
    event.register(ContainerRegistration.ITEM_DURABILITY_HATCH.get(), ItemDurabilityScreen::new);
    event.register(ContainerRegistration.EXPERIENCE_HATCH.get(), ExperienceHatchScreen::new);
    event.register(ContainerRegistration.PARALLEL_HATCH.get(), ParallelHatchScreen::new);
    event.register(ContainerRegistration.FUEL_TANK.get(), FuelTankScreen::new);
    event.register(ContainerRegistration.REDSTONE_PORT.get(), RedstonePortScreen::new);*/
  }

  public static MachineControllerEntity getClientSideMachineControllerEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof MachineControllerEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Controller container without clicking on a Controller block");
  }

  public static EnergyHatchEntity getClientSideEnergyHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof EnergyHatchEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Energy Hatch container without clicking on a Energy Hatch block");
  }

  public static FluidTankEntity getClientSideFluidHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof FluidTankEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Fluid Hatch container without clicking on a Fluid Hatch block");
  }

  public static TileItemBus getClientSideItemBusEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof TileItemBus controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Item Bus container without clicking on a Item Bus block");
  }

  public static ExperienceHatchEntity getClientSideExperienceHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof ExperienceHatchEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Experience Hatch container without clicking on a Experience Hatch block");
  }

  public static ParallelHatchEntity getClientSideParallelHatchEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof ParallelHatchEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Parallel Hatch container without clicking on a Parallel Hatch block");
  }

  public static DurabilityHatchEntity getClientSideItemDurabilityEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof DurabilityHatchEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Durability Hatch container without clicking on a Durability Hatch block");
  }

  public static FuelTankEntity getClientSideFuelTankEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof FuelTankEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Fuel Tank container without clicking on a Fuel Tank block");
  }

  public static RedstonePortEntity getClientSideRedstonePortEntity(BlockPos pos) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof RedstonePortEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Redstone Port container without clicking on a Redstone Port block");
  }
}
