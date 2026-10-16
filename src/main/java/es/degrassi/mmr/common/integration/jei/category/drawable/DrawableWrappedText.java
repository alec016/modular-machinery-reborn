package es.degrassi.mmr.common.integration.jei.category.drawable;

import lombok.Getter;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.util.Pair;
import mezz.jei.common.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fStack;

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
  public void draw(GuiGraphicsExtractor GuiGraphicsExtractor, int xOffset, int yOffset) {
    Language language = Language.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    GuiGraphicsExtractor.pose().pushMatrix();

    transformations.forEach(transformation -> transformation.compute(GuiGraphicsExtractor.pose(), 1));

    int yPos = 0;
    for (FormattedText descriptionLine : descriptionLines) {
      FormattedCharSequence charSequence = language.getVisualOrder(descriptionLine);
      GuiGraphicsExtractor.text(font, charSequence, xOffset, yPos + yOffset, 0xFF000000, dropShadow);
      yPos += lineHeight;
    }
    GuiGraphicsExtractor.pose().popMatrix();
  }

  public enum Operation {
    ADD, REMOVE, MULTIPLY, DIVIDE, SET
  }

  @Getter
  public enum State {
    SCALE(true, true),
    TRANSLATE(true, true),
    SCALEX(true, false),
    SCALEY(false, true),
    TRANSLATEX(true, false),
    TRANSLATEY(false, true);

    private final boolean x, y;
    State(boolean x, boolean y) {
      this.x = x;
      this.y = y;
    }

    public boolean isScale() {
      return this == SCALE || this == SCALEX || this == SCALEY;
    }

    public boolean isTranslate() {
      return this == TRANSLATE || this == TRANSLATEX || this == TRANSLATEY;
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

    public void compute(Matrix3x2fStack graphics, double toCompute) {
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
            state.isY() ? (float) modifiedValue : 0
        );
      }
      if (state.isTranslate()) {
        graphics.translate(
            state.isX() ? (float) modifiedValue : 0f,
            state.isY() ? (float) modifiedValue : 0f
        );
      }
    }
  }
}