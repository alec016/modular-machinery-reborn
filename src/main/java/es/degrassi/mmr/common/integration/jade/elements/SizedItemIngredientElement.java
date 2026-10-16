package es.degrassi.mmr.common.integration.jade.elements;

import es.degrassi.mmr.common.util.CycleTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;

import java.util.List;

public class SizedItemIngredientElement extends Element {
  private final SizedIngredient item;
  private final float scale;
  private final CycleTimer timer = new CycleTimer(() -> 1000, true);
  public static final SizedItemIngredientElement EMPTY = new SizedItemIngredientElement(new SizedIngredient(Ingredient.of(Items.AIR), 1), 1, 0.5f);
  private final List<ItemStack> itemList;
  private final float chance;

  private SizedItemIngredientElement(SizedIngredient item, float chance, float scale) {
    this.item = item;
    this.scale = scale;
    this.chance = chance;
    this.itemList = this.item
        .ingredient()
        .display()
        .resolve(ContextMap.EMPTY, SlotDisplay.ItemStackContentsFactory.INSTANCE)
        .map(s -> s.copyWithCount(item.count()))
        .toList();
  }

  public static SizedItemIngredientElement of(SizedIngredient stack, float chance) {
    return of(stack, chance, 1.0F);
  }
  public static SizedItemIngredientElement of(SizedIngredient stack, float chance, float scale) {
    return stack.ingredient().isEmpty() ? EMPTY : new SizedItemIngredientElement(stack, chance, scale);
  }

  public Vec2 getSize() {
    int size = Mth.floor(18.0F * this.scale);
    return new Vec2((float)size, (float)size);
  }

  @Override
  public @Nullable Component getNarration() {
    return null;
  }

  public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
    timer.onDraw();
    int x = getX(), y = getY();
    var item = timer.getOrDefault(itemList, ItemStack.EMPTY);
    if (!(DisplayHelper.INSTANCE.opacity() < 0.5F)) {
      GuiGraphicsExtractor.pose().pushMatrix();
      GuiGraphicsExtractor.pose().translate(x, y);
      GuiGraphicsExtractor.pose().scale(scale, scale);
      GuiGraphicsExtractor.fakeItem(item, 0, 0);
      GuiGraphicsExtractor.itemDecorations(Minecraft.getInstance().font, item, 0, 0);
      GuiGraphicsExtractor.pose().popMatrix();
    }
    if (this.chance < 1f) {
      GuiGraphicsExtractor.pose().pushMatrix();
      GuiGraphicsExtractor.pose().translate(x + 19 - 2, y);
      String s = DisplayHelper.INSTANCE.humanReadableNumber(this.chance * 100, "%", false, null);
      if (chance == 0f) s = "NC";
      boolean smaller = s.length() > 3;
      float scale = 0.75F;
      int i = smaller ? 32 : 22;
      int j = smaller ? 23 : 13;
      GuiGraphicsExtractor.pose().scale(scale, scale);
      var font = Minecraft.getInstance().font;
      int color = IThemeHelper.get().theme().text.colors().info();
      GuiGraphicsExtractor.text(font, s, -font.width(s), 0, color, true);
      GuiGraphicsExtractor.pose().popMatrix();
    }
  }
}
