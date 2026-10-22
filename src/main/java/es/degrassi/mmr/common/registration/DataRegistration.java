package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.network.DataType;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.controller.CorePopup;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataRegistration {
  public static final DeferredRegister<DataType<?>> DATAS = DeferredRegister.create(DataType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Supplier<DataType<IOSideConfig>> IO_SIDE_CONFIG_DATA = DATAS.register(rootLC("io_side_condig"),
      () -> DataType.create(IOSideConfig.class, IOSideConfig.STREAM_CODEC.cast(), IOSideConfig::copy));
  public static final Supplier<DataType<CorePopup>> CORE_POPUP_DATA = DATAS.register(rootLC("core_popup"),
      () -> DataType.create(CorePopup.class, CorePopup.STREAM_CODEC.cast(), CorePopup::copy));

  public static final Supplier<DataType<MachineStatus>> MACHINE_STATUS_DATA = DATAS.register(rootLC("machine_status"),
      () -> DataType.createEnum(MachineStatus.class));
  public static final Supplier<DataType<IOType>> IO_TYPE_DATA = DATAS.register(rootLC("io_type"),
      () -> DataType.createEnum(IOType.class));


  public static void register(final IEventBus bus) {
    DATAS.register(bus);
  }
}
