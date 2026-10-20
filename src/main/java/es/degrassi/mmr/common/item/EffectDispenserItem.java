package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockEffectDispenser;
import es.degrassi.mmr.common.block.prop.EffectDispenserSize;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import net.minecraft.resources.Identifier;

public class EffectDispenserItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final EffectDispenserSize type;

  public EffectDispenserItem(BlockEffectDispenser block, Properties props) {
    super(
        block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_effectdispenser_" + block.getSize().getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/effect_dispenser_" + block.getSize().getSerializedName()))
    );
    this.type = block.getSize();
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_effectdispenser_" + type.getSerializedName());
  }
}
