package es.degrassi.mmr.common.entity.base;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import es.degrassi.experiencelib.api.network.ISyncableHandler;
import es.degrassi.experiencelib.api.network.ISyncableStuff;
import es.degrassi.experiencelib.api.network.Syncable;
import es.degrassi.experiencelib.api.network.SyncableList;
import es.degrassi.mmr.api.entity.InitializableEntity;
import es.degrassi.mmr.common.util.IEntitySynchronizable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ParametersAreNonnullByDefault
public class BlockEntitySynchronized extends BlockEntity implements IEntitySynchronizable, ISyncableHandler, InitializableEntity {
  private static final String REQUEST_UPDATE_KEY = "requestModelUpdate";
  private static final String IN_STRUCTURE_KEY = "inStructure";
  private boolean requestModelUpdate = false;
  private boolean inStructure = false;
  protected final List<Syncable<?>> stuffToSync = new ArrayList<>();
  protected final SyncableList list = new SyncableList(stuffToSync);
  @Persisted
  @DescSynced
  private boolean initialized = false;

  public BlockEntitySynchronized(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
    super(type, pos, blockState);
  }

  public void init() {
    if (isInitialized()) return;
    if (this instanceof ISyncableStuff stuff) {
      stuff.getStuffToSync(list);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    init();
  }

  @Override
  public void setChanged() {
    super.setChanged();
    broadcastChanges();
  }

  @Override
  protected void loadAdditional(ValueInput nbt) {
    super.loadAdditional(nbt);
    stuffToSync.clear();
    if (this instanceof ISyncableStuff stuff) {
      stuff.getStuffToSync(list);
    }
    requestModelUpdate = nbt.getBooleanOr(REQUEST_UPDATE_KEY, false);
    inStructure = nbt.getBooleanOr(IN_STRUCTURE_KEY, false);
  }

  @Override
  protected void saveAdditional(ValueOutput nbt) {
    super.saveAdditional(nbt);
    nbt.putBoolean(REQUEST_UPDATE_KEY, requestModelUpdate);
    nbt.putBoolean(IN_STRUCTURE_KEY, inStructure);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
    return saveCustomOnly(pRegistries);
  }

  @Override
  public final ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public void markForUpdate() {
    if (requestModelUpdate) requestModelDataUpdate();
    setRequestModelUpdate(false);
    getLevel().setBlockAndUpdate(getBlockPos(), getBlockState());
    setChanged();
  }

  @Override
  public BlockEntity getEntity() {
    return this;
  }
}
