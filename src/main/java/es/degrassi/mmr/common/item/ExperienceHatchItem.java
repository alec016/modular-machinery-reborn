package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BlockExperienceHatch;
import es.degrassi.mmr.common.block.BlockExperienceInputHatch;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

@Getter
public class ExperienceHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final ExperienceHatchSize type;

  public ExperienceHatchItem(BlockExperienceHatch block, Properties props) {
    super(
        block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_experience" + fromBlock(block).getSerializedName() + "hatch_" + block.getSize().getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/experience" + fromBlock(block).getSerializedName() + "hatch_" + block.getSize().getSerializedName()))
    );
    this.type = block.getSize();
  }

  private static IOType fromBlock(Block block) {
    return block instanceof BlockExperienceInputHatch ? IOType.INPUT : IOType.OUTPUT;
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_experience" + fromBlock(getBlock()).getSerializedName() + "hatch_" + type.getSerializedName());
  }
}
