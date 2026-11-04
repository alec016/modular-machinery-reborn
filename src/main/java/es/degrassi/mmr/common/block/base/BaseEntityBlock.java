package es.degrassi.mmr.common.block.base;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.EntityHatchUI;
import es.degrassi.mmr.common.entity.base.EntityBaseEntity;
import org.jspecify.annotations.Nullable;

public abstract class BaseEntityBlock extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BaseEntityBlock(Properties id) {
    super(id);
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof EntityBaseEntity entity) {
      return EntityHatchUI.create(holder, entity, holder.getDisplayName());
    }
    return null;
  }
}
