package es.degrassi.mmr.common.entity;

import es.degrassi.experiencelib.api.network.ISyncable;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.syncable.IntegerSyncable;
import es.degrassi.experiencelib.api.network.syncable.StringSyncable;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.RedstoneComponent;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
@Setter
public class RedstonePortEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<RedstoneComponent>, TextureableMachineEntity, ControllerAccessible,
    ISyncableStuff {
  private Identifier baseTexture;
  private Identifier overlayTexture;
  @Nullable
  private BlockPos controllerPos;
  private static final Identifier defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_redstone_port");
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private IOType mode;
  private int outputAmount = 0;
  public RedstonePortEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.REDSTONE_PORT.get(), pos, blockState);
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
    this.mode = IOType.INPUT;
  }

  @Override
  public RedstoneComponent provideComponent() {
    return new RedstoneComponent(this);
  }

  public void setOutputAmount(int outputAmount) {
    this.outputAmount = outputAmount;
    getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    for(Direction direction : Direction.values()) {
      getLevel().updateNeighborsAt(getBlockPos().relative(direction), getBlockState().getBlock());
    }
  }

  @Override
  public Identifier getMachineBaseTexture() {
    return baseTexture;
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
  public Identifier getMachineOverlayTexture() {
    return overlayTexture;
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

  @Override
  public MachineHatchType getHatchType() {
    return MachineHatchTypeRegistration.REDSTONE_PORT.get();
  }

  @Override
  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("all").build();
  }

  @Override
  public HatchTextureData getTextureData(@NotNull String mode) {
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
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    input.getLong("controllerPos").ifPresent(value -> this.controllerPos = BlockPos.of(value));

    this.mode = input.getString("mode").map(IOType::value).orElse(IOType.INPUT);

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    this.outputAmount = input.getIntOr("outputAmount", 0);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    output.putString("mode", mode.getSerializedName());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
    output.putInt("outputAmount", this.outputAmount);
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(StringSyncable.create(mode::getSerializedName, s -> setMode(IOType.value(s))));
    container.accept(IntegerSyncable.create(this::getOutputAmount, this::setOutputAmount));
  }

  public void setMode(IOType mode) {
    this.mode = mode;
    setChanged();
  }
}
