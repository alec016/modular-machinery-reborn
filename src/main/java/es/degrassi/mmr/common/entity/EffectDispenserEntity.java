package es.degrassi.mmr.common.entity;

import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.EffectHandler;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.IServerTickEntity;
import es.degrassi.mmr.api.entity.ITickEntity;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.EffectComponent;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.registration.EntityRegistration;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@Setter
@ParametersAreNonnullByDefault
public class EffectDispenserEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<EffectComponent>,
    ControllerAccessible, TextureableMachineEntity, ISyncableStuff, ITickEntity, IServerTickEntity, EntityMenuHolder {
  @Getter
  private BlockPos controllerPos;
  @Getter
  private Identifier baseTexture;
  @Getter
  private Identifier overlayTexture;
  @Getter
  private static final Identifier defaultOverlayTexture = ModularMachineryReborn.rl("block/effect_dispenser");
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  @Getter
  private final EffectDispenserSize size;
  @Getter
  private final EffectHandler handler;

  public EffectDispenserEntity(BlockPos pos, BlockState blockState, EffectDispenserSize size) {
    super(EntityRegistration.EFFECT_DISPENSER.get(), pos, blockState);
    this.size = size;
    this.handler = new EffectHandler(this);
  }

  public EffectDispenserEntity(BlockPos pos, BlockState state) {
    this(pos, state, EffectDispenserSize.SMALL);
  }

  @Override
  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    if (getController() == null || getController().getStatus() != MachineStatus.RUNNING) {
      if (getEffect().isPresent())
        this.getHandler().resetEffect();
    }
  }

  @Override
  public @Nullable EffectComponent provideComponent() {
    return new EffectComponent(handler);
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
      case SMALL -> MachineHatchTypeRegistration.EFFECT_DISPENSER_SMALL;
      case MEDIUM -> MachineHatchTypeRegistration.EFFECT_DISPENSER_MEDIUM;
      case BIG -> MachineHatchTypeRegistration.EFFECT_DISPENSER_BIG;
    }).get();
  }

  @Override
  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  protected void loadAdditional(ValueInput nbt) {
    super.loadAdditional(nbt);
    nbt.getLong("controllerPos").ifPresent(val -> controllerPos = BlockPos.of(val));

    this.baseTexture = nbt.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = nbt.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
  }

  @Override
  protected void saveAdditional(ValueOutput nbt) {
    super.saveAdditional(nbt);

    nbt.putString("busSize", this.size.getSerializedName());
    if (controllerPos != null)
      nbt.putLong("controllerPos", controllerPos.asLong());
    if (baseTexture != null)
      nbt.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      nbt.putString("overlayTexture", overlayTexture.toString());
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
  public void getStuffToSync(SyncableList container) {
    container.syncBool(handler::isApplyingEffect, handler::setApplyingEffect);
  }

  public boolean isApplyingEffect() {
    return handler.isApplyingEffect();
  }

  public Optional<MobEffectInstance> getEffect() {
    return Optional.ofNullable(handler.getEffect());
  }
}
