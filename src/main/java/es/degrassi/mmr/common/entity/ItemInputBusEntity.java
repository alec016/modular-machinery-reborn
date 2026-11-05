package es.degrassi.mmr.common.entity;

import es.degrassi.mmr.api.codec.BigStackCodecs;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.common.block.BlockInputBus;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.entity.base.TileItemBus;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemInputBusEntity extends TileItemBus implements IAutoInputEntity<ResourceHandler<ItemResource>>, IPickResultEntity {
  public ItemInputBusEntity(BlockPos pos, BlockState state) {
    this(pos, state, ((BlockInputBus) state.getBlock()).getSize());
  }

  public ItemInputBusEntity(BlockPos pos, BlockState state, ItemBusSize type) {
    super(EntityRegistration.ITEM_INPUT_BUS.get(), pos, state, type, IOType.INPUT);
  }

  @Override
  public ItemHandler buildInventory(int slots, int stackSize) {
    return new ItemHandler(slots, stackSize, stackSize, 0, Direction.values());
  }

  @Override
  public void tickAutoInput() {
    if (!getConfig().isEnabled()) return;
    for (Direction side : inventory.getAccessibleSides()) {
      if (!getConfig().canAutoIO(side)) continue;
      var neighbour = getNeighbour(Capabilities.Item.BLOCK, side);
      if (neighbour == null) continue;

      moveStacks(neighbour, inventory, Integer.MAX_VALUE);
    }
  }

  @Override
  public void decode(CompoundTag tag) {
    if (getInventory().isCompiling()) return;
    var amount = Math.clamp(tag.getLongOr("amount", 0L), 0, Integer.MAX_VALUE);
    var item = tag.read("item", BigStackCodecs.OPTIONAL.codec()).orElse(ItemStack.EMPTY);
    if (item.isEmpty()) return;
    if (getInventory().addToInputs(item, amount) > 0) setChanged();
  }
}
