package es.degrassi.mmr.data.loot;

import com.google.common.collect.ImmutableMap;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.registration.BlockRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BlockDropProvider extends BlockLootSubProvider {
  private final Map<Block, Function<Block, LootTable.Builder>> overrides = createOverrides();

  public BlockDropProvider(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return BuiltInRegistries.BLOCK
        .stream()
        .filter(entry -> {
          var lootTable = entry.getLootTable().orElse(null);
          return lootTable != null && lootTable.identifier().getNamespace().equals(ModularMachineryReborn.MODID);
        })
        .toList();
  }

  @NotNull
  private ImmutableMap<Block, Function<Block, LootTable.Builder>> createOverrides() {
    return ImmutableMap.<Block, Function<Block, LootTable.Builder>>builder()
        .put(BlockRegistration.CONTROLLER.block(), block -> LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block))
                .when(ExplosionCondition.survivesExplosion())
                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))
            ))
        .build();
  }

  @Override
  protected void generate() {
    for (var block : getKnownBlocks()) {
      add(block, overrides.getOrDefault(block, this::defaultBuilder).apply(block));
    }
  }

  private LootTable.Builder defaultBuilder(Block block) {
    LootPool.Builder pool = LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(LootItem.lootTableItem(block))
        .when(ExplosionCondition.survivesExplosion());

    return LootTable.lootTable().withPool(pool);
  }
}
