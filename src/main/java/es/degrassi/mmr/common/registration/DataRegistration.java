package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.network.DataType;
import es.degrassi.experiencelib.api.network.IData;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.controller.CorePopup;
import es.degrassi.mmr.api.network.data.CorePopupData;
import es.degrassi.mmr.api.network.data.FluidResourceData;
import es.degrassi.mmr.api.network.data.FluidStackData;
import es.degrassi.mmr.api.network.data.IOSideConfigData;
import es.degrassi.mmr.api.network.data.ItemResourceData;
import es.degrassi.mmr.api.network.data.ItemStackData;
import es.degrassi.mmr.api.network.syncable.CorePopupSyncable;
import es.degrassi.mmr.api.network.syncable.FluidStackSyncable;
import es.degrassi.mmr.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmr.api.network.syncable.ItemResourceSyncable;
import es.degrassi.mmr.api.network.syncable.ItemStackSyncable;
import es.degrassi.mmr.api.network.syncable.FluidResourceSyncable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

public class DataRegistration {
  private DataRegistration() {}
  public static final DeferredRegister<DataType<? extends IData<?>, ?>> DATAS =
      DeferredRegister.create(DataType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Supplier<DataType<ItemStackData, ItemStack>> ITEMSTACK_DATA = DATAS.register(rootLC("itemstack"),
      () -> DataType.create(ItemStack.class, ItemStackSyncable::create, ItemStackData::new));
  public static final Supplier<DataType<FluidStackData, FluidStack>> FLUIDSTACK_DATA = DATAS.register(rootLC("fluidstack"),
      () -> DataType.create(FluidStack.class, FluidStackSyncable::create, FluidStackData::new));
  public static final Supplier<DataType<IOSideConfigData, IOSideConfig>> IO_SIDE_CONFIG_DATA = DATAS.register(rootLC("io_side_condig"),
      () -> DataType.create(IOSideConfig.class, IOSideConfigSyncable::create, IOSideConfigData::readData));
  public static final Supplier<DataType<CorePopupData, CorePopup>> CORE_POPUP_DATA = DATAS.register(rootLC("core_popup"),
      () -> DataType.create(CorePopup.class, CorePopupSyncable::create, CorePopupData::new));

  // Resources
  public static final Supplier<DataType<FluidResourceData, FluidResource>> FLUID_RESOURCE_DATA = DATAS.register(rootLC("fluid_resource"),
      () -> DataType.create(FluidResource.class, FluidResourceSyncable::create, FluidResourceData::new));
  public static final Supplier<DataType<ItemResourceData, ItemResource>> ITEM_RESOURCE_DATA = DATAS.register(rootLC("item_resource"),
      () -> DataType.create(ItemResource.class, ItemResourceSyncable::create, ItemResourceData::new));


  public static void register(final IEventBus bus) {
    DATAS.register(bus);
  }
}
