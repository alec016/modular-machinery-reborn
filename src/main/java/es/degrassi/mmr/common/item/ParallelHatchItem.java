package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockParallelHatch;
import es.degrassi.mmr.common.block.prop.ParallelHatchSize;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@Getter
public class ParallelHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final ParallelHatchSize type;

  public ParallelHatchItem(BlockParallelHatch block, Properties props) {
    super(block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_parallel_hatch_" + block.getSize().getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/parallel_hatch_" + block.getSize().getSerializedName()))
    );
    this.type = block.getSize();
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_parallel_hatch_" + type.getSerializedName());
  }
}
