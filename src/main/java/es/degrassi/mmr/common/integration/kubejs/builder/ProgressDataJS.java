package es.degrassi.mmr.common.integration.kubejs.builder;

import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.common.crafting.helper.Direction;
import es.degrassi.mmr.common.crafting.helper.ProgressData;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ProgressDataJS {
  private Optional<Integer> x = Optional.empty(), y = Optional.empty();
  private Optional<Identifier> empty = Optional.empty(), filled = Optional.empty();
  private Optional<Direction> direction = Optional.empty();
  private ProgressDataJS() {}

  public static ProgressDataJS create() {
    return new ProgressDataJS();
  }

  public static ProgressDataJS of(@Nullable Integer x, @Nullable Integer y, @Nullable Direction direction,
                                  @Nullable Identifier emptyTexture, @Nullable Identifier filledTexture) {
    return new ProgressDataJS()
        .x(x)
        .y(y)
        .direction(direction)
        .emptyTexture(emptyTexture)
        .filledTexture(filledTexture);
  }

  public static ProgressDataJS of(@Nullable Integer x, @Nullable Integer y) {
    return new ProgressDataJS()
        .x(x)
        .y(y);
  }

  public static ProgressDataJS of(@Nullable Identifier emptyTexture, @Nullable Identifier filledTexture) {
    return new ProgressDataJS()
        .emptyTexture(emptyTexture)
        .filledTexture(filledTexture);
  }

  public static ProgressDataJS of(@Nullable Direction direction,
                                  @Nullable Identifier emptyTexture, @Nullable Identifier filledTexture) {
    return new ProgressDataJS()
        .direction(direction)
        .emptyTexture(emptyTexture)
        .filledTexture(filledTexture);
  }

  public static ProgressDataJS of(@Nullable Integer x, @Nullable Integer y,
                                  @Nullable Identifier emptyTexture, @Nullable Identifier filledTexture) {
    return new ProgressDataJS()
        .x(x)
        .y(y)
        .emptyTexture(emptyTexture)
        .filledTexture(filledTexture);
  }

  public ProgressDataJS x(@Nullable Integer x) {
    this.x = Optional.ofNullable(x);
    return this;
  }

  public ProgressDataJS y(@Nullable Integer y) {
    this.y = Optional.ofNullable(y);
    return this;
  }

  public ProgressDataJS emptyTexture(@Nullable Identifier texture) {
    this.empty = Optional.ofNullable(texture);
    return this;
  }

  public ProgressDataJS filledTexture(@Nullable Identifier texture) {
    this.filled = Optional.ofNullable(texture);
    return this;
  }

  public ProgressDataJS direction(@Nullable Direction direction) {
    this.direction = Optional.ofNullable(direction);
    return this;
  }

  @HideFromJS
  public ProgressData build() {
    return new ProgressData(
        direction.orElse(Direction.LEFT),
        new PositionedRequirement(
            x.orElse(74),
            y.orElse(8)
        ),
        empty.orElse(ProgressData.defaultEmpty),
        filled.orElse(ProgressData.defaultFilled)
    );
  }
}
