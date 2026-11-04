package es.degrassi.mmr.common.entity;

import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.CommandComponent;
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
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class CommandExecutionerEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<CommandComponent>, TextureableMachineEntity,
    ControllerAccessible, ISyncableStuff, EntityMenuHolder {
  private Identifier baseTexture;
  private Identifier overlayTexture;
  @Nullable
  private BlockPos controllerPos;
  private static final Identifier defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_command_executioner");
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  public CommandExecutionerEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.COMMAND_EXECUTIONER.get(), pos, blockState);
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
    return MachineHatchTypeRegistration.COMMAND_EXECUTIONER.get();
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
  public @Nullable CommandComponent provideComponent() {
    return new CommandComponent(this);
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
    input.getLong("controllerPos").ifPresent(value -> controllerPos = BlockPos.of(value));

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
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
  public void getStuffToSync(SyncableList container) {
  }
}
