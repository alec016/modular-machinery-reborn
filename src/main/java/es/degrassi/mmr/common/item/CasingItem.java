package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.block.BlockCasing;
import lombok.Getter;

@Getter
public class CasingItem extends ItemBlockMachineComponent {

  public CasingItem(BlockCasing casing) {
    super(casing, new Properties());
  }
}
