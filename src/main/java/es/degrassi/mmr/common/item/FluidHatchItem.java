package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BlockFluidHatch;
import es.degrassi.mmr.common.block.BlockFluidInputHatch;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

@Getter
public class FluidHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final FluidHatchSize type;

  public FluidHatchItem(BlockFluidHatch block, Properties props) {
    super(
        block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_fluid" + fromBlock(block).getSerializedName() + "hatch_" + block.getSize().getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/fluid" + fromBlock(block).getSerializedName() + "hatch_" + block.getSize().getSerializedName()))
    );
    this.type = block.getSize();
  }

  private static IOType fromBlock(Block block) {
    return block instanceof BlockFluidInputHatch ? IOType.INPUT : IOType.OUTPUT;
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_fluid" + fromBlock(getBlock()).getSerializedName() + "hatch_" + type.getSerializedName());
  }
}
