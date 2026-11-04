package es.degrassi.mmr.common.entity.base;

import es.degrassi.mmr.api.entity.ItemDroppeable;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
public abstract class TileInventory extends ColorableMachineComponentEntity implements ItemDroppeable {
  protected final ItemHandler inventory;
  private final int slots;

  protected TileInventory(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, int slots) {
    super(entityType, pos, blockState);
    this.inventory = buildInventory(slots);
    this.slots = slots;
  }

  protected TileInventory(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, int slots, int stackSize) {
    super(entityType, pos, blockState);
    this.inventory = buildInventory(slots, stackSize);
    this.slots = slots;
  }

  public ItemHandler buildInventory(int slots) {
    return buildInventory(slots, 64);
  }

  public abstract ItemHandler buildInventory(int slots, int slotLimit);

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    input.readChild("inventory", inventory);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    output.putChild("inventory", inventory);
  }

  @Override
  public void addDrops(List<ItemStack> drops) {
    this.inventory.forEach(stack -> drops.addAll(split(stack)));
  }
}
