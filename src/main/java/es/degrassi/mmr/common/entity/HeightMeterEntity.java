package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.HeightComponent;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
@Setter
public class HeightMeterEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<HeightComponent>, ControllerAccessible,
    TextureableMachineEntity, EntityMenuHolder {
  @Nullable
  private BlockPos controllerPos;

  private Identifier baseTexture;
  private Identifier overlayTexture;
  @Getter
  private static final Identifier defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_height_meter");
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  public HeightMeterEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.HEIGHT_METER.get(), pos, blockState);
    this.overlayTexture = defaultOverlayTexture;
  }

  @Override
  public HeightComponent provideComponent() {
    return new HeightComponent();
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    input.getLong("controllerPos").ifPresent(value -> this.controllerPos = BlockPos.of(value));
    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("orientable").build();
  }

  @Override
  public HatchTextureData getTextureData(@NotNull String mode) {
    return MachineComponentEntity.super.getTextureData(mode).derive(
        "bg_all",
        baseTexture,
        defaultBaseTexture,
        "ov_front",
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
    return MachineHatchTypeRegistration.HEIGHT_METER.get();
  }
}
