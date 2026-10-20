package es.degrassi.mmr.common.item;

import lombok.Getter;
import net.minecraft.world.level.block.Block;

@Getter
public class CasingItem extends ItemBlockMachineComponent {

  public CasingItem(Block block, Properties props) {
    super(block, props);
  }
}
