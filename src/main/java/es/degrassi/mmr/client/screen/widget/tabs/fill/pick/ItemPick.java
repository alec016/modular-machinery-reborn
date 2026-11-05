package es.degrassi.mmr.client.screen.widget.tabs.fill.pick;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

public record ItemPick(ItemStack stack, ItemResource resource) implements Pick {
  public static ItemPick of(ItemStack stack) {
    return new ItemPick(stack, ItemResource.of(stack));
  }

  @Override
  public void addLines(List<Component> lines) {
    lines.add(stack.getHoverName());
  }

  @Override
  public void draw(GUIContext gui, int x, int y) {
    DrawerHelperClient.drawItemStack(gui, stack, x, y, 0);
  }
}
