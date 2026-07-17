package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.capability.config.IOSideConfig;
import es.degrassi.mmreborn.api.controller.CorePopup;
import es.degrassi.mmreborn.api.network.DataType;
import es.degrassi.mmreborn.api.network.EnumDataType;
import es.degrassi.mmreborn.api.network.IData;
import es.degrassi.mmreborn.api.network.data.BooleanData;
import es.degrassi.mmreborn.api.network.data.ComponentData;
import es.degrassi.mmreborn.api.network.data.CorePopupData;
import es.degrassi.mmreborn.api.network.data.DoubleData;
import es.degrassi.mmreborn.api.network.data.EnumData;
import es.degrassi.mmreborn.api.network.data.FloatData;
import es.degrassi.mmreborn.api.network.data.FluidStackData;
import es.degrassi.mmreborn.api.network.data.IOSideConfigData;
import es.degrassi.mmreborn.api.network.data.IntegerData;
import es.degrassi.mmreborn.api.network.data.ItemStackData;
import es.degrassi.mmreborn.api.network.data.LongData;
import es.degrassi.mmreborn.api.network.data.NbtData;
import es.degrassi.mmreborn.api.network.data.ResourceLocationData;
import es.degrassi.mmreborn.api.network.data.StringData;
import es.degrassi.mmreborn.api.network.syncable.BooleanSyncable;
import es.degrassi.mmreborn.api.network.syncable.ComponentSyncable;
import es.degrassi.mmreborn.api.network.syncable.CorePopupSyncable;
import es.degrassi.mmreborn.api.network.syncable.DoubleSyncable;
import es.degrassi.mmreborn.api.network.syncable.EnumSyncable;
import es.degrassi.mmreborn.api.network.syncable.FloatSyncable;
import es.degrassi.mmreborn.api.network.syncable.FluidStackSyncable;
import es.degrassi.mmreborn.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.ItemStackSyncable;
import es.degrassi.mmreborn.api.network.syncable.LongSyncable;
import es.degrassi.mmreborn.api.network.syncable.NbtSyncable;
import es.degrassi.mmreborn.api.network.syncable.ResourceLocationSyncable;
import es.degrassi.mmreborn.api.network.syncable.StringSyncable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static es.degrassi.mmreborn.ModularMachineryReborn.rootLC;

@SuppressWarnings("unchecked")
public class DataRegistration {
  private DataRegistration() {}
  public static final DeferredRegister<DataType<? extends IData<?>, ?>> DATAS =
      DeferredRegister.create(DataType.REGISTRY_KEY, ModularMachineryReborn.MODID);
  public static final Registry<DataType<? extends IData<?>, ?>> DATA_REGISTRY = DATAS.makeRegistry(builder -> {});

  public static final Supplier<DataType<BooleanData, Boolean>> BOOLEAN_DATA = DATAS.register(rootLC("boolean"),
      () -> DataType.create(Boolean.class, BooleanSyncable::create, BooleanData::new));
  public static final Supplier<DataType<IntegerData, Integer>> INTEGER_DATA = DATAS.register(rootLC("integer"),
      () -> DataType.create(Integer.class, IntegerSyncable::create, IntegerData::new));
  public static final Supplier<DataType<DoubleData, Double>> DOUBLE_DATA = DATAS.register(rootLC("double"),
      () -> DataType.create(Double.class, DoubleSyncable::create, DoubleData::new));
  public static final Supplier<DataType<FloatData, Float>> FLOAT_DATA = DATAS.register(rootLC("float"),
      () -> DataType.create(Float.class, FloatSyncable::create, FloatData::new));
  public static final Supplier<DataType<ItemStackData, ItemStack>> ITEMSTACK_DATA = DATAS.register(rootLC("itemstack"),
      () -> DataType.create(ItemStack.class, ItemStackSyncable::create, ItemStackData::new));
  public static final Supplier<DataType<FluidStackData, FluidStack>> FLUIDSTACK_DATA = DATAS.register(rootLC("fluidstack"),
      () -> DataType.create(FluidStack.class, FluidStackSyncable::create, FluidStackData::new));
  public static final Supplier<DataType<StringData, String>> STRING_DATA = DATAS.register(rootLC("string"),
      () -> DataType.create(String.class, StringSyncable::create, StringData::new));
  public static final Supplier<DataType<LongData, Long>> LONG_DATA = DATAS.register(rootLC("long"),
      () -> DataType.create(Long.class, LongSyncable::create, LongData::new));
  public static final Supplier<DataType<NbtData, CompoundTag>> NBT_DATA = DATAS.register(rootLC("nbt"),
      () -> DataType.create(CompoundTag.class, NbtSyncable::create, NbtData::new));
  public static final Supplier<DataType<ResourceLocationData, ResourceLocation>> RESOURCE_LOCATION_DATA = DATAS.register(rootLC("rl"),
      () -> DataType.create(ResourceLocation.class, ResourceLocationSyncable::create, ResourceLocationData::new));
  public static final Supplier<DataType<IOSideConfigData, IOSideConfig>> IO_SIDE_CONFIG_DATA = DATAS.register(rootLC("io_side_condig"),
      () -> DataType.create(IOSideConfig.class, IOSideConfigSyncable::create, IOSideConfigData::readData));

  public static final Supplier<DataType<ComponentData, Component>> COMPONENT_DATA = DATAS.register(rootLC("component"),
      () -> DataType.create(Component.class, ComponentSyncable::create, ComponentData::new));
  public static final Supplier<EnumDataType<?>> ENUM_DATA = (Supplier<EnumDataType<?>>) (Object) DATAS.register(rootLC("enum"),
      () -> EnumDataType.createEnum(EnumSyncable::create, EnumData::readData));

  public static final Supplier<DataType<CorePopupData, CorePopup>> CORE_POPUP_DATA = DATAS.register(rootLC("core_popup"),
      () -> DataType.create(CorePopup.class, CorePopupSyncable::create, CorePopupData::new));


  public static void register(final IEventBus bus) {
    DATAS.register(bus);
  }
}
