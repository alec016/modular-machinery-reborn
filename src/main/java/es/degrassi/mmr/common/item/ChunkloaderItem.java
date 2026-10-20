package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.BlockChunkloader;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import net.minecraft.resources.Identifier;

public class ChunkloaderItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private static final Identifier OVERLAY_TEXTURE = ModularMachineryReborn.rl("block/overlay_chunkloader");

  public ChunkloaderItem(BlockChunkloader block, Properties props) {
    super(block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, OVERLAY_TEXTURE)
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/chunkloader"))
    );
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return OVERLAY_TEXTURE;
  }
}
