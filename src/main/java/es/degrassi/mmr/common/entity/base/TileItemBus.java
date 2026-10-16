package es.degrassi.mmr.common.entity.base;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.entity.ItemInputBusEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.ItemComponent;
import es.degrassi.mmr.common.manager.handler.UpdateValueListener;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateItemComponentPacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class TileItemBus extends TileInventory implements MachineComponentEntity<ItemComponent>, ControllerAccessible, TextureableMachineEntity, ITickEntity,
    IServerTickEntity, ISyncableStuff, IAutoEntity<ResourceHandler<ItemResource>>, ISideConfigComponent<IOSideMode> {
  @Nullable private BlockPos controllerPos;
  private ItemBusSize size;
  private IOType ioType;

  private Identifier baseTexture;
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private final Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  private final IOSideConfig config;
  private final UpdateValueListener<ItemStack> listener;

  protected TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, ItemBusSize size, IOType ioType) {
    super(entityType, pos, blockState, size.getSlotCount(), size.stackSize);
    this.size = size;
    this.ioType = ioType;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_" + ioType.getSerializedName() + "bus_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.listener = new UpdateValueListener<>() {
      @Override
      public void onChange(int slot, ItemStack stack) {
        getControllerPosSet().forEach(p -> {
          if (getLevel() == null) return;
          if (getLevel().isClientSide()) return;
          if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
            controller.getProcessor().setMachineInventoryChanged();
          }
        });
        if (getLevel() instanceof ServerLevel l)
          PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
              new SUpdateItemComponentPacket(slot, inventory.getStack(slot), getBlockPos()));
      }

      @Override
      public void onChange() {
        for (int slot = 0; slot < inventory.size(); slot++) {
          onChange(slot, inventory.getStack(slot));
        }
      }
    };
    this.inventory.setListener(listener);
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);
  }

  @Nullable
  @Override
  public ItemComponent provideComponent() {
    return new ItemComponent(this.getInventory(), ioType);
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.size = ItemBusSize.value(input.getString("busSize").orElseThrow());
    this.ioType = IOType.getByString(input.getString("ioType").orElseThrow());
    input.getLong("controllerPos").ifPresent(v -> this.controllerPos = BlockPos.of(v));

    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_" + ioType.getSerializedName() + "bus_" + size.getSerializedName());

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);

    this.inventory.setListener(listener);
    input.readChild("config", config);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);

    output.putString("busSize", this.size.getSerializedName());
    if (ioType == null) {
      ioType = this instanceof ItemInputBusEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    output.putString("ioType", this.ioType.getSerializedName());
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
    output.putChild("config", this.config);
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("all").build();
  }

  @Override
  public HatchTextureData getTextureData(String mode) {
    return MachineComponentEntity.super.getTextureData(mode).derive(
        "bg_all",
        baseTexture,
        defaultBaseTexture,
        "ov_all",
        overlayTexture,
        defaultOverlayTexture,
        false
    );
  }

  @Override
  public Identifier getMachineBaseTexture() {
    return baseTexture;
  }

  @Override
  public Identifier getMachineOverlayTexture() {
    return overlayTexture;
  }

  @Override
  public void setMachineBaseTexture(Identifier newTexture) {
    setChanged();
    this.baseTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
          new SUpdateMachineTexturePacket(baseTexture, true, getBlockPos()));
    }
  }

  @Override
  public void setMachineOverlayTexture(Identifier newTexture) {
    setChanged();
    this.overlayTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, ChunkPos.containing(getBlockPos()),
          new SUpdateMachineTexturePacket(overlayTexture, false, getBlockPos()));
    }
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public MachineHatchType getHatchType() {
    return switch(ioType) {
      case INPUT -> (switch (size) {
        case TINY -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_TINY;
        case SMALL -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_BIG;
        case HUGE -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.ITEM_INPUT_BUS_LUDICROUS;
      }).get();
      case OUTPUT -> (switch(size) {
        case TINY -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_TINY;
        case SMALL -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_SMALL;
        case NORMAL -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_NORMAL;
        case REINFORCED -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_REINFORCED;
        case BIG -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_BIG;
        case HUGE -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_HUGE;
        case LUDICROUS -> MachineHatchTypeRegistration.ITEM_OUTPUT_BUS_LUDICROUS;
      }).get();
      default -> null;
    };
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IOSideConfigSyncable.create(this::getConfig, this.config::set));
  }

  protected void moveStacks(ResourceHandler<ItemResource> from, ResourceHandler<ItemResource> to, int maxAmount) {
    try (var root = Transaction.open(null)) {
      ResourceHandlerUtil.moveStacking(from, to, _ -> true, maxAmount, root);
      root.commit();
    }
  }
}
