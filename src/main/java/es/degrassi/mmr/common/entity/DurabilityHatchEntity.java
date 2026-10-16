package es.degrassi.mmr.common.entity;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.api.network.syncable.IOSideConfigSyncable;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.DurabilityComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.UpdateValueListener;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.network.server.component.SUpdateItemComponentPacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@ParametersAreNonnullByDefault
public class DurabilityHatchEntity extends TileInventory implements MachineComponentEntity<DurabilityComponent>, ControllerAccessible, TextureableMachineEntity,
    IAutoInputEntity<ResourceHandler<ItemResource>>, ISyncableStuff, ITickEntity, IServerTickEntity,
    ISideConfigComponent<IOSideMode> {
  @Setter
  @Nullable private BlockPos controllerPos;
  private ItemDurabilityHatchSize size;

  @Setter
  private Identifier baseTexture;
  @Setter
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private final Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);

  private final IOSideConfig config;

  private final UpdateValueListener<ItemStack> listener;

  public DurabilityHatchEntity(BlockPos pos, BlockState blockState, ItemDurabilityHatchSize size) {
    super(EntityRegistration.ITEM_DURABILITY_HATCH.get(), pos, blockState, size.getSlotCount());
    this.size = size;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_durabilityhatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.config = IOSideConfig.Template.DEFAULT_ALL_DISABLED.build(this);
    this.config.setCallback(this::configChanged);
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
  }

  public DurabilityHatchEntity(BlockPos pos, BlockState blockState) {
    this(pos, blockState, ItemDurabilityHatchSize.TINY);
  }

  @Override
  public ItemHandler buildInventory(int slots, int stackSize) {
    return new ItemHandler(slots, Integer.MAX_VALUE, 0, stackSize);
  }

  @Nullable
  @Override
  public DurabilityComponent provideComponent() {
    return new DurabilityComponent(this.getInventory());
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.size = ItemDurabilityHatchSize.value(input.getString("busSize").orElseThrow());
    input.getLong("controllerPos").ifPresent(value -> this.controllerPos = BlockPos.of(value));

    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_durabilityhatch_" + size.getSerializedName());

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    input.readChild("config", this.config);
    this.inventory.setListener(listener);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);

    output.putString("busSize", this.size.getSerializedName());
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
    output.putChild("config", this.config);
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("all").build();
  }

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
    return (switch (size) {
      case TINY -> MachineHatchTypeRegistration.DURABILITY_HATCH_TINY;
      case SMALL -> MachineHatchTypeRegistration.DURABILITY_HATCH_SMALL;
      case NORMAL -> MachineHatchTypeRegistration.DURABILITY_HATCH_NORMAL;
      case BIG -> MachineHatchTypeRegistration.DURABILITY_HATCH_BIG;
    }).get();
  }

  @Override
  public void tickAutoInput() {
    if (!getConfig().isEnabled()) return;
    for (Direction side : inventory.getAccessibleSides()) {
      if (!getConfig().canAutoIO(side)) continue;
      var neighbour = getNeighbour(Capabilities.Item.BLOCK, side);
      if (neighbour == null) continue;

      moveStacks(neighbour, inventory, Integer.MAX_VALUE);
    }
  }

  protected void moveStacks(ResourceHandler<ItemResource> from, ResourceHandler<ItemResource> to, int maxAmount) {
    try (Transaction tx = Transaction.open(null)) {
      ResourceHandlerUtil.moveStacking(from, to, _ -> true, maxAmount, tx);
      tx.commit();
    }
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IOSideConfigSyncable.create(this::getConfig, this.config::set));
  }
}
