package es.degrassi.mmr.client.requirement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public interface ItemRendering extends StackHolder {
  int RENDER_ICON = 1;
  int RENDER_AMOUNT = 2;
  int RENDER_REMAINDER = 8;

  int getWidth();
  int getHeight();

  default void drawStack(GuiGraphicsExtractor draw, int x, int y, int flags) {
    int xOff = (getWidth() - 16) / 2;
    int yOff = (getHeight() - 16) / 2;
    ItemStack stack = getItemStack();
    if ((flags & RENDER_ICON) != 0) {
      draw.fakeItem(stack, x + xOff, y + yOff);
      draw.itemDecorations(Minecraft.getInstance().font, stack, x + xOff, y + yOff, "");
    }
    if ((flags & RENDER_AMOUNT) != 0) {
      String count = "";
      if (getStack().map(s -> s.getCount(), f -> f.getAmount()) != 1) {
        count += getStack().map(s -> String.valueOf(s.getCount()), f -> String.valueOf(f.getAmount()));
      }
      if (!count.isEmpty()) {
        draw.text(Minecraft.getInstance().font, count, x + xOff, y + yOff, 0xFFFFFF);
      }
    }
  }
}
