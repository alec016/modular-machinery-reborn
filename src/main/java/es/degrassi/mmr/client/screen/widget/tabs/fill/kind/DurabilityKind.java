package es.degrassi.mmr.client.screen.widget.tabs.fill.kind;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class DurabilityKind extends ItemKind {
  public DurabilityKind(int height, Predicate<ItemStack> filter) {
    super("durability", height, filter);
  }
}
