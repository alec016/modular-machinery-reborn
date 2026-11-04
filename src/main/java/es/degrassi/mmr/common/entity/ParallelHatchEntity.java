package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.ParallelComponent;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@Setter
@ParametersAreNonnullByDefault
public class ParallelHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<ParallelComponent>, ControllerAccessible,
    TextureableMachineEntity, EntityMenuHolder {
  @Nullable
  private BlockPos controllerPos;
  protected final ParallelHatchSize size;
  private ParallelComponent component;
  private Identifier baseTexture;
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  public ParallelHatchEntity(BlockPos pos, BlockState state, ParallelHatchSize size) {
    super(EntityRegistration.PARALLEL_HATCH.get(), pos, state);
    this.size = size;
    this.component = new ParallelComponent(size);
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_parallel_hatch_" + size.getSerializedName());
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
  }

  public ParallelHatchEntity(BlockPos pos, BlockState state) {
    this(pos, state, ParallelHatchSize.BASIC);
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  public void setCores(int cores) {
    this.component.setCores(cores, getLevel(), getBlockPos());
    if (getController() != null)
      getController().getProcessor().updateActiveCores(cores);
  }

  public int getCores() {
    return this.component.getContainerProvider();
  }

  public int getMaxCores() {
    return size.max;
  }

  @Override
  public ParallelComponent provideComponent() {
    return component;
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.component = new ParallelComponent(size);
    input.getLong("controllerPos").ifPresent(value -> this.controllerPos = BlockPos.of(value));
    input.getInt("cores").ifPresent(this::setCores);
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_parallel_hatch_" + size.getSerializedName());

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    output.putString("size", size.getSerializedName());
    if (controllerPos != null)
      output.putLong("controllerPos", controllerPos.asLong());
    output.putInt("cores", component.getContainerProvider());
    output.putInt("maxCores", getMaxCores());
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("orientable").build();
  }

  @Override
  public HatchTextureData getTextureData(String mode) {
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
    return (switch (size) {
      case BASIC -> MachineHatchTypeRegistration.PARALLEL_HATCH_BASIC;
      case ADVANCED -> MachineHatchTypeRegistration.PARALLEL_HATCH_ADVANCED;
      case MEDIUM -> MachineHatchTypeRegistration.PARALLEL_HATCH_MEDIUM;
      case ULTIMATE -> MachineHatchTypeRegistration.PARALLEL_HATCH_ULTIMATE;
      case MAX -> MachineHatchTypeRegistration.PARALLEL_HATCH_MAX;
    }).get();
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }
}
