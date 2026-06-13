package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.item.StructureCreatorItemMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

import static es.degrassi.mmreborn.ModularMachineryReborn.rootLC;

public class DataComponentRegistration {
  private DataComponentRegistration() {}
  public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<DataComponentType<ResourceLocation>> MACHINE_DATA =
      DATA_COMPONENTS.register(rootLC("machine"), () -> DataComponentType.<ResourceLocation>builder()
          .persistent(ResourceLocation.CODEC)
          .networkSynchronized(ResourceLocation.STREAM_CODEC)
          .build()
      );

  public static final Supplier<DataComponentType<ResourceLocation>> BASE_TEXTURE =
      DATA_COMPONENTS.register(rootLC("base_texture"), () -> DataComponentType.<ResourceLocation>builder()
          .persistent(ResourceLocation.CODEC)
          .networkSynchronized(ResourceLocation.STREAM_CODEC)
          .build()
      );

  public static final Supplier<DataComponentType<ResourceLocation>> OVERLAY_TEXTURE =
      DATA_COMPONENTS.register(rootLC("overlay_texture"), () -> DataComponentType.<ResourceLocation>builder()
          .persistent(ResourceLocation.CODEC)
          .networkSynchronized(ResourceLocation.STREAM_CODEC)
          .build()
      );

  public static final Supplier<DataComponentType<ResourceLocation>> DEFAULT_MODEL =
      DATA_COMPONENTS.register(rootLC("default_model"), () -> DataComponentType.<ResourceLocation>builder()
          .persistent(ResourceLocation.CODEC)
          .networkSynchronized(ResourceLocation.STREAM_CODEC)
          .build()
      );

  public static final Supplier<DataComponentType<List<BlockPos>>> STRUCTURE_CREATOR_DATA =
      DATA_COMPONENTS.register(rootLC("structure_creator"), () -> DataComponentType.<List<BlockPos>>builder()
          .persistent(BlockPos.CODEC.listOf())
          .networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()))
          .build()
      );

  public static final Supplier<DataComponentType<StructureCreatorItemMode>> STRUCTURE_CREATOR_MODE =
      DATA_COMPONENTS.register(
          rootLC("structure_creator_mode"), () -> DataComponentType.<StructureCreatorItemMode>builder()
              .persistent(StructureCreatorItemMode.CODEC.codec())
              .networkSynchronized(StructureCreatorItemMode.STREAM_CODEC)
              .build()
      );

  public static final Supplier<DataComponentType<BlockPos>> STRUCTURE_CREATOR_BOX =
      DATA_COMPONENTS.register(
          rootLC("structure_creator_selected_corner"), () -> DataComponentType.<BlockPos>builder()
              .persistent(BlockPos.CODEC)
              .networkSynchronized(BlockPos.STREAM_CODEC)
              .build()
      );

  public static final Supplier<DataComponentType<Boolean>> STRUCTURE_CREATOR_BOX_CURRENT =
      DATA_COMPONENTS.register(
          rootLC("structure_creator_box_current"), () -> DataComponentType.<Boolean>builder()
              .persistent(NamedCodec.BOOL.codec())
              .networkSynchronized(ByteBufCodecs.BOOL)
              .build()
      );

  public static void register(final IEventBus bus) {
    DATA_COMPONENTS.register(bus);
  }
}
