package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BlockEnergyHatch;
import es.degrassi.mmr.common.block.BlockEnergyInputHatch;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

@Getter
public class EnergyHatchItem extends ItemBlockMachineComponent implements ItemHatch {
  private final EnergyHatchSize type;
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");

  public EnergyHatchItem(BlockEnergyHatch block, EnergyHatchSize type) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_energy" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/energy" + fromBlock(block).getSerializedName() + "hatch_" + type.getSerializedName()))
    );
    this.type = type;
  }

  private static IOType fromBlock(Block block) {
    return block instanceof BlockEnergyInputHatch ? IOType.INPUT : IOType.OUTPUT;
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_energy" + fromBlock(this.getBlock()).getSerializedName() + "hatch_" + type.getSerializedName());
  }
}
