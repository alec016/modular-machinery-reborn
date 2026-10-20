package es.degrassi.mmr.common.item;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.block.BlockEntityDetector;
import es.degrassi.mmr.common.block.BlockEntityKiller;
import es.degrassi.mmr.common.block.BlockEntitySpawner;
import es.degrassi.mmr.common.block.BlockEntityHealer;
import es.degrassi.mmr.common.block.BlockEntityDamager;
import es.degrassi.mmr.common.registration.DataComponentRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class EntityItem extends ItemBlockMachineComponent implements ItemHatch {
  private static final Identifier BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  public EntityItem(BaseEntityBlock block, Properties props) {
    super(
        block,
        props
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryReborn.rl("block/overlay_entity" + fromBlock(block)))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryReborn.rl("default/hatches/entity_" + fromBlock(block)))
    );
  }

  private static String fromBlock(Block block) {
    return switch (block) {
      case BlockEntityDetector $ -> "detector";
      case BlockEntitySpawner $ -> "spawner";
      case BlockEntityKiller $ -> "killer";
      case BlockEntityDamager $ -> "damager";
      case BlockEntityHealer $ -> "healer";
      default -> "";
    };
  }

  @Override
  public Identifier getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public Identifier getDefaultOverlayTexture() {
    return ModularMachineryReborn.rl("block/overlay_entity" + fromBlock(getBlock()));
  }
}
