package es.degrassi.mmr.common.entity.base;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.EntityHandler;
import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.api.entity.EntityMenuHolder;
import es.degrassi.mmr.api.entity.MachineComponentEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.machine.component.EntityComponent;
import es.degrassi.mmr.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmr.common.registration.MachineHatchTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public abstract class EntityBaseEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<EntityComponent>,
    TextureableMachineEntity, ControllerAccessible, EntityMenuHolder {
  protected final EntityHandler handler;
  protected final IOType mode;

  @Setter
  private BlockPos controllerPos;
  @Setter
  private Identifier baseTexture;
  @Setter
  private Identifier overlayTexture;
  private Identifier defaultOverlayTexture;
  @Getter
  private static final Identifier defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");

  private final RequirementEntity.Action[] validActions;

  public EntityBaseEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState,
                          RequirementEntity.Action... validActions) {
    super(entityType, pos, blockState);
    if (validActions.length < 1) throw new IllegalArgumentException("Actions must be at least 1");
    this.handler = new EntityHandler(this);
    this.mode = validActions[0].getMode();
    this.validActions = validActions;
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_entity" + fromMode());
    this.overlayTexture = defaultOverlayTexture;
  }

  private String fromMode() {
    return switch (validActions[0]) {
      case ADD_HEALTH -> "healer";
      case CONSUME_HEALTH -> "damager";
      case KILL -> "killer";
      case SPAWN -> "spawner";
      case CHECK_AMOUNT, CHECK_HEALTH -> "detector";
    };
  }

  @Override
  public EntityComponent provideComponent() {
    return new EntityComponent(mode, handler, validActions);
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
  protected void loadAdditional(ValueInput nbt) {
    super.loadAdditional(nbt);
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_entity" + fromMode());

    this.baseTexture = nbt.getString("baseTexture").map(Identifier::parse).orElse(defaultBaseTexture);
    this.overlayTexture = nbt.getString("overlayTexture").map(Identifier::parse).orElse(defaultOverlayTexture);
    nbt.getLong("controllerPos").ifPresent(val -> controllerPos = BlockPos.of(val));

    nbt.getString("validActions").ifPresent(str -> {
      var actions = Arrays.stream(str.split(",")).map(String::trim).toArray(String[]::new);
      for (int i = 0; i < Math.min(actions.length, this.validActions.length); i++) {
        this.validActions[i] = RequirementEntity.Action.value(actions[i]);
      }
    });
  }

  @Override
  protected void saveAdditional(ValueOutput nbt) {
    super.saveAdditional(nbt);
    if (baseTexture != null)
      nbt.putString("baseTexture", baseTexture.toString());
    if (overlayTexture != null)
      nbt.putString("overlayTexture", overlayTexture.toString());
    if (controllerPos != null)
      nbt.putLong("controllerPos", controllerPos.asLong());

    var actionsStr = Arrays.stream(this.validActions)
        .map(Enum::name)
        .collect(Collectors.joining(","));
    nbt.putString("validActions", actionsStr);
  }

  @Override
  public MachineHatchType getHatchType() {
    return (switch (validActions[0]) {
      case CHECK_HEALTH, CHECK_AMOUNT -> MachineHatchTypeRegistration.ENTITY_DETECTOR;
      case KILL -> MachineHatchTypeRegistration.ENTITY_KILLER;
      case SPAWN -> MachineHatchTypeRegistration.ENTITY_SPAWNER;
      case ADD_HEALTH -> MachineHatchTypeRegistration.ENTITY_HEALER;
      case CONSUME_HEALTH -> MachineHatchTypeRegistration.ENTITY_DAMAGER;
    }).get();
  }
}
