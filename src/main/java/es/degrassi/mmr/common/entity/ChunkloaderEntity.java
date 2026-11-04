package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.ChunkloadComponent;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmr.common.util.Chunkloader;
import es.degrassi.mmr.common.util.ChunkloaderList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ChunkloaderEntity extends BlockEntityRestrictedTick implements MachineComponentEntity<ChunkloadComponent>, TextureableMachineEntity,
    IServerTickEntity, EntityMenuHolder {
  private Identifier baseTexture;
  private Identifier overlayTexture;
  private static final Identifier defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_chunkloader");
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private final Chunkloader chunkloader;
  public ChunkloaderEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.CHUNKLOADER.get(), pos, blockState);
    chunkloader = new Chunkloader(this);
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
  }

  @Override
  public ChunkloadComponent provideComponent() {
    return new ChunkloadComponent(this);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    output.putChild("chunkloader", chunkloader);
    if (baseTexture != null)
      output.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      output.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    chunkloader.deserialize(input.childOrEmpty("chunkloader"));

    this.baseTexture = input.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = input.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
  }

  @Override
  public void setLevel(Level level) {
    super.setLevel(level);
    ChunkloaderList.add(this);
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    chunkloader.serverTick();
  }

  @Override
  public void onLoad() {
    super.onLoad();
    chunkloader.init();
  }

  @Getter
  private boolean unloaded = false;

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    this.unloaded = true;
  }

  @Override
  public void setRemoved() {
    chunkloader.onRemoved();
    super.setRemoved();
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
    return MachineHatchTypeRegistration.BIOME_READER.get();
  }

  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }
}
