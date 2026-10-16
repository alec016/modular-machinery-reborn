package es.degrassi.mmr.common.integration.jade.elements;

import es.degrassi.mmr.common.util.CycleTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackContentsFactory;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.impl.ui.FluidStackElement;
import snownee.jade.overlay.DisplayHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SizedFluidIngredientElement extends FluidStackElement {
  private static final Vec2 DEFAULT_SIZE = new Vec2(16.0F, 16.0F);
  private final CycleTimer timer = new CycleTimer(() -> 1000, true);
  private final List<JadeFluidObject> fluids;
  private final float chance;
  private final SizedFluidIngredient ingredient;

  public SizedFluidIngredientElement(SizedFluidIngredient fluid, float chance) {
    super(JadeFluidObject.empty());
    Objects.requireNonNull(fluid);
    this.chance = chance;
    this.ingredient = fluid;
    this.fluids = fluid
        .ingredient()
        .display()
        .resolve(ContextMap.EMPTY, FluidStackContentsFactory.INSTANCE)
        .map(s -> JadeFluidObject.of(s.getFluid(), s.getAmount(), s.getComponentsPatch()))
        .toList();
  }

  public Vec2 getSize() {
    return DEFAULT_SIZE;
  }

  public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
    timer.onDraw();
    Vec2 size = this.getSize();
    int x = getX(), y = getY();
    DisplayHelper.INSTANCE.drawFluid(GuiGraphicsExtractor, x, y, timer.get(this.fluids), size.x, size.y, JadeFluidObject.bucketVolume());

    if (!(DisplayHelper.INSTANCE.opacity() < 0.5F)) {
      var font = Minecraft.getInstance().font;
      int color = IThemeHelper.get().theme().text.itemAmountColor();
      // Chance Rendering
      if (this.chance < 1f) {
        GuiGraphicsExtractor.pose().pushMatrix();
        GuiGraphicsExtractor.pose().translate(x + 1.0F, y + 1.0F);
        GuiGraphicsExtractor.pose().translate(0, -size.y);
        String s = DisplayHelper.INSTANCE.humanReadableNumber(this.chance * 100, "%", false, null);
        if (chance == 0f) s = "NC";
        boolean smaller = s.length() > 3;
        float scale = smaller ? 0.5F : 0.75F;
        int i = smaller ? 32 : 22;
        int j = smaller ? 23 : 13;
        GuiGraphicsExtractor.pose().scale(scale, scale);
        GuiGraphicsExtractor.text(font, s, i - font.width(s), j, color, true);
        GuiGraphicsExtractor.pose().popMatrix();
      }

      // Amount rendering
      GuiGraphicsExtractor.pose().pushMatrix();
      GuiGraphicsExtractor.pose().translate(x, y);
      String s = DisplayHelper.INSTANCE.humanReadableNumber(this.ingredient.amount(), "B", true, null);
      boolean smaller = s.length() > 3;
      int i = smaller ? 32 : 22;
      int j = smaller ? 23 : 13;
      GuiGraphicsExtractor.pose().scale(0.75F, 0.75F);
      GuiGraphicsExtractor.text(font, s, i - font.width(s), j, color, true);
      GuiGraphicsExtractor.pose().popMatrix();
    }
  }

  public @Nullable String getMessage() {
    return null;
  }
}
