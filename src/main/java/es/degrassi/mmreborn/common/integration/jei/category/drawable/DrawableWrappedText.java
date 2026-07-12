package es.degrassi.mmreborn.common.integration.jei.category.drawable;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.util.StringUtil;
import mezz.jei.common.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.LinkedList;
import java.util.List;

public class DrawableWrappedText implements IDrawable {
  private static final int lineSpacing = 2;

  private final List<FormattedText> descriptionLines;
  private final int lineHeight;
  private final int width;
  private final int height;
  private final boolean dropShadow;
  private final List<Transformation> transformations = new LinkedList<>();

  public DrawableWrappedText(List<FormattedText> text, int maxWidth, boolean dropShadow) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    this.lineHeight = font.lineHeight + lineSpacing;
    Pair<List<FormattedText>, Boolean> result = StringUtil.splitLines(font, text, maxWidth, Integer.MAX_VALUE);
    this.descriptionLines = result.first();
    this.width = maxWidth;
    this.height = lineHeight * descriptionLines.size() - lineSpacing;
    this.dropShadow = dropShadow;
  }

  public DrawableWrappedText transform(Operation operation, State state, double amount) {
    transformations.add(new Transformation(operation, state, amount));
    return this;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
    Language language = Language.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    guiGraphics.pose().pushPose();

    transformations.forEach(transformation -> transformation.compute(guiGraphics.pose(), 1));

    int yPos = 0;
    for (FormattedText descriptionLine : descriptionLines) {
      FormattedCharSequence charSequence = language.getVisualOrder(descriptionLine);
      guiGraphics.drawString(font, charSequence, xOffset, yPos + yOffset, 0xFF000000, dropShadow);
      yPos += lineHeight;
    }
    guiGraphics.pose().popPose();
  }

  public enum Operation {
    ADD, REMOVE, MULTIPLY, DIVIDE, SET
  }

  @Getter
  public enum State {
    SCALE(true, true, true),
    TRANSLATE(true, true, true),
    SCALEX(true, false, false),
    SCALEY(false, true, true),
    SCALEZ(false, false, true),
    TRANSLATEX(true, false, false),
    TRANSLATEY(false, true, false),
    TRANSLATEZ(false, false, true);

    private final boolean x, y, z;
    State(boolean x, boolean y, boolean z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public boolean isScale() {
      return this == SCALE || this == SCALEX || this == SCALEY || this == SCALEZ;
    }

    public boolean isTranslate() {
      return this == TRANSLATE || this == TRANSLATEX || this == TRANSLATEY || this == TRANSLATEZ;
    }
  }

  public static class Transformation {
    private final Operation operation;
    private final Double amount;
    private final State state;

    public Transformation(Operation operation, State state, Double amount) {
      this.amount = amount;
      this.operation = operation;
      this.state = state;
    }

    public void compute(PoseStack graphics, double toCompute) {
      double modifiedValue = switch (operation) {
        case ADD -> toCompute + amount;
        case REMOVE -> toCompute - amount;
        case MULTIPLY -> toCompute * amount;
        case DIVIDE -> toCompute / amount;
        case SET -> amount;
      };

      if (state.isScale()) {
        graphics.scale(
            state.isX() ? (float) modifiedValue : 0,
            state.isY() ? (float) modifiedValue : 0,
            state.isZ() ? (float) modifiedValue : 0
        );
      }
      if (state.isTranslate()) {
        graphics.translate(
            state.isX() ? modifiedValue : 0,
            state.isY() ? modifiedValue : 0,
            state.isZ() ? modifiedValue : 0
        );
      }
    }
  }
}