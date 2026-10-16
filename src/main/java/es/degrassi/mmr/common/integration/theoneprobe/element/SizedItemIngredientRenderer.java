package es.degrassi.mmr.common.integration.theoneprobe.element;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.CycleTimer;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.List;

public class SizedItemIngredientRenderer implements IElement {
  public static final Identifier ID = ModularMachineryReborn.rl("sized_item_ingredient");
  private final SizedIngredient item;
  private final float scale;
  private final CycleTimer timer = new CycleTimer(() -> 1000, true);
  public static final SizedItemIngredientRenderer EMPTY =
      new SizedItemIngredientRenderer(new SizedIngredient(Ingredient.EMPTY, 1), 1, 0.5f);
  private final List<ItemStack> itemList;
  private final float chance;

  public SizedItemIngredientRenderer(SizedIngredient item, float chance, float scale) {
    this.item = item;
    this.scale = scale;
    this.chance = chance;
    this.itemList = Arrays.stream(this.item.getItems()).map(s -> s.copyWithCount(item.count())).toList();
  }

  public SizedItemIngredientRenderer(SizedIngredient item, float chance) {
    this(item, chance, 1f);
  }

  public SizedItemIngredientRenderer(RegistryFriendlyByteBuf buffer) {
    this.item = buffer.readJsonWithCodec(SizedIngredient.FLAT_CODEC);
    this.chance = buffer.readFloat();
    this.scale = buffer.readFloat();
    this.itemList = Arrays.stream(this.item.getItems()).map(s -> s.copyWithCount(item.count())).toList();
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    timer.onDraw();
    var font = Minecraft.getInstance().font;
    GuiGraphicsExtractor.pose().pushPose();
    GuiGraphicsExtractor.pose().translate(x, y, 0);
    GuiGraphicsExtractor.pose().scale(scale, scale, scale);
    GuiGraphicsExtractor.renderFakeItem(timer.getOrDefault(itemList, ItemStack.EMPTY), 0, 0);
    GuiGraphicsExtractor.renderItemDecorations(Minecraft.getInstance().font, timer.getOrDefault(itemList, ItemStack.EMPTY), 0, 0);
    GuiGraphicsExtractor.pose().popPose();

    // Chance rendering
    if (chance < 1f) {
      GuiGraphicsExtractor.pose().pushPose();
      GuiGraphicsExtractor.pose().translate(x + 19 - 2, y, 220);
      GuiGraphicsExtractor.pose().scale(0.75f, 0.75f, 0f);
      String s = String.valueOf(this.chance * 100) + '%';
      if (chance == 0f) s = "NC";
      GuiGraphicsExtractor.drawString(font, s, - font.width(s), 0, 16777215, true);
      GuiGraphicsExtractor.pose().popPose();
    }
  }

  public Vector2i getSize() {
    int size = Mth.floor(18.0F * this.scale);
    return new Vector2i(size, size);
  }

  @Override
  public int getWidth() {
    return getSize().x();
  }

  @Override
  public int getHeight() {
    return getSize().y();
  }

  @Override
  public void toBytes(RegistryFriendlyByteBuf buffer) {
    buffer.writeJsonWithCodec(SizedIngredient.FLAT_CODEC, this.item);
    buffer.writeFloat(this.chance);
    buffer.writeFloat(this.scale);
  }

  @Override
  public Identifier getID() {
    return ID;
  }

  public static class SizedItemIngredientFactory implements IElementFactory {

    @Override
    public SizedItemIngredientRenderer createElement(RegistryFriendlyByteBuf buffer) {
      return new SizedItemIngredientRenderer(buffer);
    }

    @Override
    public Identifier getId() {
      return ID;
    }
  }
}
