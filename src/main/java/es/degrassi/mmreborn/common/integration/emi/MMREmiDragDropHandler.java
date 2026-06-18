package es.degrassi.mmreborn.common.integration.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.runtime.EmiFavorite;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import es.degrassi.mmreborn.client.screen.BaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class MMREmiDragDropHandler implements EmiDragDropHandler<Screen> {
  private static final int targetColor = 0x4013C90A;
  private static final int hoverColor = 0x804CC919;

  protected MMREmiDragDropHandler() {}

  @Override
  public boolean dropStack(Screen screen, EmiIngredient stack, int x, int y) {
    if (!(screen instanceof BaseScreen<?,?> baseScreen)) return false;
    return baseScreen.getMenu().slots.stream()
        .filter(slot -> {
          if (slot instanceof FilterSlotComponent<?, ?> s) {
            return new Bounds(baseScreen.getGuiLeft() + s.x, baseScreen.getGuiTop() + s.y, 16, 16).contains(x, y);
          }
          return false;
        })
        .findFirst()
        .map(slot -> {
          var s = stack;
          if (s instanceof EmiFavorite fe) {
            s = fe.getStack();
          }
          if (s instanceof ItemEmiStack e) {
            ((FilterSlotComponent<?,?>) slot).setFromClient(e.getItemStack());
            return true;
          }
          return false;
        }).orElse(false);
  }

  @Override
  public void render(Screen screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
    if (!(screen instanceof BaseScreen<?,?> baseScreen)) return;
    RenderSystem.disableDepthTest();
    baseScreen.getMenu().slots.stream()
        .filter(slot -> slot instanceof FilterSlotComponent<?, ?>)
        .forEach(slot -> {
          var bounds = new Bounds(baseScreen.getGuiLeft() + slot.x, baseScreen.getGuiTop() + slot.y, 16, 16);
          draw.fill(
              bounds.x(),
              bounds.y(),
              bounds.right(),
              bounds.bottom(),
              bounds.contains(mouseX, mouseY) ? hoverColor : targetColor
          );
        });
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
  }
}
