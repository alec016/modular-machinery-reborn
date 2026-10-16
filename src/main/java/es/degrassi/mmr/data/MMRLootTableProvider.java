package es.degrassi.mmr.data;

import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.registration.BlockRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MMRLootTableProvider extends LootTableProvider {
  public MMRLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
    super(output, Set.of(), List.of(
        new SubProviderEntry(
            LootSubProvider::new,
            LootTable.DEFAULT_PARAM_SET
        )
    ), provider);
  }

  static class LootSubProvider extends BlockLootSubProvider {
    public LootSubProvider(HolderLookup.Provider provider) {
      super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
      return BlockRegistration.BLOCKS.getEntries()
          .stream()
          .map(DeferredHolder::value)
          .map(e -> (Block) e)
          .toList();
    }

    @Override
    protected void generate() {
      getKnownBlocks().forEach(block -> {
        if (!(block instanceof BlockController b)) {
          dropSelf(block);
        } else {
          add(block, LootTable.lootTable()
              .withPool(
                  this.applyExplosionCondition(b,
                      LootPool.lootPool()
                          .setRolls(ConstantValue.exactly(1.0F))
                          .add(LootItem.lootTableItem(b)))
                          .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))
              )
          );
        }
      });
    }
  }
}
