package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockOutputBus;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@Getter
public class OutputBusItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final ItemBusSize type;

  public OutputBusItem(BlockOutputBus block, Properties props) {
    super(
        block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_outputbus_" + block.getSize().getSerializedName()))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/outputbus_" + block.getSize().getSerializedName()))
    );
    this.type = block.getSize();
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_outputbus_" + type.getSerializedName());
  }
}
