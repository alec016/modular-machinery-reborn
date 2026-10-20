package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemOutputBusEntity extends TileItemBus implements IAutoOutputEntity<ResourceHandler<ItemResource>> {

  public ItemOutputBusEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ITEM_OUTPUT_BUS.get(), pos, state, ItemBusSize.TINY, IOType.OUTPUT);
  }

  public ItemOutputBusEntity(BlockPos pos, BlockState state, ItemBusSize type) {
    super(EntityRegistration.ITEM_OUTPUT_BUS.get(), pos, state, type, IOType.OUTPUT);
  }

  @Override
  public ItemHandler buildInventory(int slots, int stackSize) {
    // Output bus: nothing is insertable from outside, every slot is extractable
    // (legacy: inSlots = none, outSlots = all slots, slotLimit = stackSize).
    return new ItemHandler(slots, stackSize, 0, stackSize, Direction.values());
  }

  @Override
  public void tickAutoOutput() {
    if (!getConfig().isEnabled()) return;
    for (Direction side : inventory.getAccessibleSides()) {
      if (!getConfig().canAutoIO(side)) continue;
      var neighbour = getNeighbour(Capabilities.Item.BLOCK, side);
      if (neighbour == null) continue;

      moveStacks(inventory, neighbour, Integer.MAX_VALUE);
    }
  }
}
