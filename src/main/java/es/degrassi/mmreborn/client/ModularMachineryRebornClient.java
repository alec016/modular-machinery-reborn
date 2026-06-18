package es.degrassi.mmreborn.client;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.IWrenchable;
import es.degrassi.mmreborn.api.handler.FilterRendererRegistry;
import es.degrassi.mmreborn.api.handler.RegisterFilterRendererEvent;
import es.degrassi.mmreborn.client.entity.renderer.ControllerRenderer;
import es.degrassi.mmreborn.client.entity.renderer.IWrenchableRenderer;
import es.degrassi.mmreborn.client.entity.renderer.StructureCheckerRenderer;
import es.degrassi.mmreborn.client.integration.athena.MMRAthenaModels;
import es.degrassi.mmreborn.client.integration.emi.MMREmiClientIntegration;
import es.degrassi.mmreborn.client.integration.jei.MMRJeiClientIntegration;
import es.degrassi.mmreborn.client.item.MMRItemTooltip;
import es.degrassi.mmreborn.client.item.MMRItemTooltipComponent;
import es.degrassi.mmreborn.client.model.InitBuiltInModels;
import es.degrassi.mmreborn.client.model.controller.ControllerModelLoader;
import es.degrassi.mmreborn.client.model.hatch.HatchModelLoader;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.EnergyHatchScreen;
import es.degrassi.mmreborn.client.screen.ExperienceHatchScreen;
import es.degrassi.mmreborn.client.screen.FluidHatchScreen;
import es.degrassi.mmreborn.client.screen.FuelTankScreen;
import es.degrassi.mmreborn.client.screen.ItemBusScreen;
import es.degrassi.mmreborn.client.screen.ItemDurabilityScreen;
import es.degrassi.mmreborn.client.screen.ParallelHatchScreen;
import es.degrassi.mmreborn.client.screen.RedstonePortScreen;
import es.degrassi.mmreborn.client.util.FluidRenderer;
import es.degrassi.mmreborn.common.block.BlockDynamicColor;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.DurabilityHatchEntity;
import es.degrassi.mmreborn.common.entity.FuelTankEntity;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.ParallelHatchEntity;
import es.degrassi.mmreborn.common.entity.RedstonePortEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.item.ItemDynamicColor;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
@Mod(value = ModularMachineryReborn.MODID, dist = Dist.CLIENT)
public class ModularMachineryRebornClient {
  public static final ClientScheduler clientScheduler = new ClientScheduler();
  private static Map<ModelResourceLocation, BakedModel> models;
  private static final List<Block> blockModelsToRegister = Lists.newLinkedList();
  private static final List<Item> itemModelsToRegister = Lists.newLinkedList();
  private final IEventBus bus;

  public ModularMachineryRebornClient(final IEventBus bus) {
    MMRAthenaModels.init();
    InitBuiltInModels.init();
    NeoForge.EVENT_BUS.register(clientScheduler);
    NeoForge.EVENT_BUS.addListener(this::onBlockHighlightEvent);
    bus.register(this);
    this.bus = bus;
  }

  public void onBlockHighlightEvent(RenderHighlightEvent.Block event) {
    //IWrenchableRenderer.renderBlockHighlight(event.getPoseStack(), event.getCamera(), event.getTarget(),event.getMultiBufferSource(), event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
  }

  @SubscribeEvent
  public void registerBlockEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    EntityRegistration.ENTITY_TYPE.getEntries().forEach(holder -> {
      if (holder.getDelegate().value().create(BlockPos.ZERO,
          holder.value().getValidBlocks().iterator().next().defaultBlockState()) instanceof IWrenchable aw && aw.shouldAddRender())
        event.registerBlockEntityRenderer(holder.get(), IWrenchableRenderer::new);
    });
    event.registerBlockEntityRenderer(EntityRegistration.CONTROLLER.get(), ControllerRenderer::new);
    event.registerBlockEntityRenderer(EntityRegistration.STRUCTURE_CHECKER.get(), StructureCheckerRenderer::new);
  }

  @SubscribeEvent
  public void registerModelLoader(final ModelEvent.RegisterGeometryLoaders event) {
    event.register(ModularMachineryReborn.rl("controller"), ControllerModelLoader.INSTANCE);
    event.register(ModularMachineryReborn.rl("hatch"), HatchModelLoader.INSTANCE);
  }

  @SubscribeEvent
  public void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
    event.register(MMRItemTooltipComponent.class, c -> new MMRItemTooltip(c.getItem(), c.getComponent(), c.isCompleted()));
  }

  @SubscribeEvent
  public void onBackingCompleted(final ModelEvent.BakingCompleted event) {
    models = event.getModels();
  }

  public static Map<ModelResourceLocation, BakedModel> getAllModels() {
    return models;
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

  @SubscribeEvent
  public void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
    BlockRegistration.BLOCKS
        .getEntries()
        .stream()
        .map(DeferredHolder::value)
        .filter(b -> b instanceof BlockDynamicColor)
        .forEach(block -> event.register(ModularMachineryRebornClient::blockColor, block));
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(block -> event.register(ModularMachineryRebornClient::blockColor, block));
  }

  @SubscribeEvent
  public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    ItemRegistration.ITEMS
        .getEntries()
        .stream()
        .map(DeferredHolder::value)
        .filter(item -> item instanceof ItemDynamicColor)
        .forEach(item -> event.register(ModularMachineryRebornClient::itemColor, item));
    ModularMachineryReborn.MACHINES_BLOCK.values().forEach(block -> event.register(ModularMachineryRebornClient::itemColor, block));
  }

  public static int blockColor(BlockState state, @Nullable  BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
    if (level == null || pos == null)
      return 0;
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
    return Config.machineColor;
  }

  public static int itemColor(ItemStack stack, int tintIndex) {
    if (stack.getItem() instanceof ItemDynamicColor colorableItem) {
      return colorableItem.getColorFromItemstack(stack, tintIndex);
    }
    return Config.machineColor;
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public void onModelRegister(ModelEvent.RegisterAdditional event) {
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("block/nope")));
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("default/controller")));
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("default/hatch_all")));
    event.register(ModelResourceLocation.standalone(ModularMachineryReborn.rl("default/hatch_orientable")));
    Minecraft.getInstance().getResourceManager().listResources("models/default/hatches", s -> s.getPath().endsWith(".json")).forEach((rl, resource) -> {
      ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().substring(7).replace(".json", ""));
      event.register(ModelResourceLocation.standalone(modelRL));
    });
    for (String folder : MMRConfig.get().modelFolders.get()) {
      Minecraft.getInstance().getResourceManager().listResources("models/" + folder, s -> s.getPath().endsWith(".json")).forEach((rl, resource) -> {
        ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().substring(7).replace(".json", ""));
        event.register(ModelResourceLocation.standalone(modelRL));
      });
    }
    for (Block block : blockModelsToRegister) {
      event.register(ModelResourceLocation.standalone(Objects.requireNonNull(Holder.direct(block).getKey()).location()));
      itemModelsToRegister.add(block.asItem());
    }
    for (Item item : itemModelsToRegister) {
      String name = item.getClass().getSimpleName().toLowerCase();
      if (item instanceof BlockItem) {
        name = ((BlockItem) item).getBlock().getClass().getSimpleName().toLowerCase();
      }
      event.register(new ModelResourceLocation(ModularMachineryReborn.rl(name), "inventory"));
    }
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public void clientSetup(final FMLClientSetupEvent event) {
    if (Mods.isEMILoaded()) {
      new MMREmiClientIntegration(bus);
    } else if (Mods.isJEILoaded()) {
      new MMRJeiClientIntegration(bus);
    }
    FilterRendererRegistry.init();
  }

  @SubscribeEvent
  public void registerFilterRender(final RegisterFilterRendererEvent event) {
    BuiltInRegistries.FLUID.forEach(fluid -> {
      if (!fluid.isSource(fluid.defaultFluidState())) return;
      event.register(fluid, (guiGraphics, x, y) -> {
        FluidRenderer.renderFluid(
            guiGraphics.pose(),
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
    event.register(ContainerRegistration.CONTROLLER.get(), ControllerScreen::new);
    event.register(ContainerRegistration.ENERGY_HATCH.get(), EnergyHatchScreen::new);
    event.register(ContainerRegistration.FLUID_HATCH.get(), FluidHatchScreen::new);
    event.register(ContainerRegistration.ITEM_BUS.get(), ItemBusScreen::new);
    event.register(ContainerRegistration.ITEM_DURABILITY_HATCH.get(), ItemDurabilityScreen::new);
    event.register(ContainerRegistration.EXPERIENCE_HATCH.get(), ExperienceHatchScreen::new);
    event.register(ContainerRegistration.PARALLEL_HATCH.get(), ParallelHatchScreen::new);
    event.register(ContainerRegistration.FUEL_TANK.get(), FuelTankScreen::new);
    event.register(ContainerRegistration.REDSTONE_PORT.get(), RedstonePortScreen::new);
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
