package es.degrassi.mmr.client.requirement;

import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public interface ChanceRendering extends Position {
  Minecraft CLIENT = Minecraft.getInstance();
  float getChance();
  IOType getActionType();

  default void drawChance(GuiGraphicsExtractor graphics, boolean top) {
    Component component = Component.empty();
    String c = Utils.decimalFormat(getChance() * 100);
    if (getChance() > 0 && getChance() < 1)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance", c, "%");
    else if (getChance() == 0)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance.nc");
    int xOff = (getWidth() - 16) / 2 + 2;
    int yOff = (getHeight() - 16) / 2;
    int modifier = top ? -1 : 1;
    yOff *= modifier;
    int modified = top ? 1 : 2;
    yOff += modified;
    renderAmount(graphics, xOff, yOff, top, component);
  }

  private static void renderAmount(GuiGraphicsExtractor graphics, int x, int y, boolean top, Component amount) {
    graphics.pose().popMatrix();
    graphics.pose().scale(0.75f, 0.75f);
    int tx = x + 17 - Math.min(14, CLIENT.font.width(amount));
    y += top ? 0 : CLIENT.font.lineHeight * 3/2;
    graphics.text(CLIENT.font, amount, tx, y, Config.chanceColor);
    graphics.pose().pushMatrix();
  }

  default void addChanceTooltips(List<Component> list) {
    String chance = Utils.decimalFormat(getChance() * 100);
    if (getChance() > 0 && getChance() < 1)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + getActionType().name().toLowerCase(Locale.ROOT), chance, "%"));
    else if (getChance() == 0)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
    else if (getChance() == 1)
      list.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item." + getActionType().name().toLowerCase(Locale.ROOT)));
  }
}
