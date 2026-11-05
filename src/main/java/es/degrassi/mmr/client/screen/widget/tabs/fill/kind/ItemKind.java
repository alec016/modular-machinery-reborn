package es.degrassi.mmr.client.screen.widget.tabs.fill.kind;

import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.ItemPick;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ItemKind extends Kind<ItemPick, ItemStack> {
  protected final Predicate<ItemStack> itemFilter;
  public ItemKind(int height, Predicate<ItemStack> filter) {
    this("item", height, filter);
  }
  public ItemKind(String id, int height, Predicate<ItemStack> filter) {
    super(id, height, true);
    this.itemFilter = filter;
  }

  protected List<ItemStack> displayItemStacks(BlockEntity entity, @Nullable Player player) {
    if (displayCache != null) {
      return displayCache;
    }
    var stacks = new ArrayList<ItemStack>();
    var level = entity.getLevel();
    if (level != null) {
      CreativeModeTabs.tryRebuildTabContents(
          level.enabledFeatures(), player != null && player.canUseGameMasterBlocks(),
          level.registryAccess());
      var seen = new HashSet<ItemResource>();
      for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
        if (tab.getType() != CreativeModeTab.Type.CATEGORY) {
          continue;
        }
        for (ItemStack stack : tab.getDisplayItems()) {
          if (stack.isEmpty() || !seen.add(ItemResource.of(stack))) {
            continue;
          }
          stacks.add(stack.copyWithCount(1));
        }
      }
    }
    displayCache = List.copyOf(stacks);
    return displayCache;
  }

  @Override
  public void candidates(List<ItemPick> result, boolean byMod, boolean byTag, String needle, BlockEntity entity, @Nullable Player player) {
    List<ItemStack> display = displayItemStacks(entity, player);
    if (byTag) {
      collectItemTagCandidates(needle, display, result);
    } else if (display.isEmpty()) {
      for (Item item : BuiltInRegistries.ITEM) {
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
        ItemStack template = new ItemStack(item);
        if (!template.isEmpty()
            && (itemFilter == null || itemFilter.test(template))
            && matches(byMod, template.getHoverName().getString(),
            BuiltInRegistries.ITEM.getKey(item), needle)) {
          result.add(ItemPick.of(template));
        }
      }
    } else {
      for (ItemStack template : display) {
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
        if ((itemFilter == null || itemFilter.test(template))
            && matches(byMod, template.getHoverName().getString(),
            BuiltInRegistries.ITEM.getKey(template.getItem()), needle)) {
          result.add(ItemPick.of(template));
        }
      }
    }
  }

  protected void collectItemTagCandidates(String needle, List<ItemStack> display, List<ItemPick> result) {
    Map<Item, ItemStack> preferred = new HashMap<>();
    for (ItemStack stack : display) {
      preferred.putIfAbsent(stack.getItem(), stack);
    }
    var seen = new LinkedHashSet<Item>();
    BuiltInRegistries.ITEM.getTags().forEach(named -> {
      if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) {
        return;
      }
      for (var holder : named.stream().toList()) {
        if (result.size() >= MAX_CANDIDATES) {
          return;
        }
        Item item = holder.value();
        if (!seen.add(item)) {
          continue;
        }
        ItemStack template = preferred.getOrDefault(item, ItemStack.EMPTY);
        if (template.isEmpty() || (itemFilter != null && !itemFilter.test(template))) {
          continue;
        }
        result.add(ItemPick.of(template));
      }
    });
    BuiltInRegistries.BLOCK.getTags().forEach(named -> {
      if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) {
        return;
      }
      for (var holder : named.stream().toList()) {
        if (result.size() >= MAX_CANDIDATES) {
          return;
        }
        ItemStack template = new ItemStack(holder.value().asItem());
        if (template.isEmpty()
            || !seen.add(template.getItem())
            || (itemFilter != null && !itemFilter.test(template))) {
          continue;
        }
        result.add(ItemPick.of(template));
      }
    });
  }
}
