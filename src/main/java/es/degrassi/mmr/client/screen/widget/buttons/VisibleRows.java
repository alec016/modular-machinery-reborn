package es.degrassi.mmr.client.screen.widget.buttons;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.uistate.UIState;
import es.degrassi.mmr.api.uistate.UIStateType;
import es.degrassi.mmr.common.registration.UIStateRegistration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.IntSupplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisibleRows implements UIState {
  public static final VisibleRows SMALL = new VisibleRows(4);
  public static final VisibleRows MEDIUM = new VisibleRows(7);
  public static final VisibleRows TALL = new VisibleRows(10);
  public static final VisibleRows FULL = new VisibleRows(-1);

  public static final NamedCodec<VisibleRows> CODEC = NamedCodec.record(i -> i.group(
      NamedCodec.intRange(-1, Integer.MAX_VALUE).fieldOf("visible_rows").forGetter(VisibleRows::fixedRows)
  ).apply(i, rows -> {
    if (rows == SMALL.fixedRows()) return SMALL;
    if (rows == MEDIUM.fixedRows()) return MEDIUM;
    if (rows == TALL.fixedRows()) return TALL;
    return FULL;
  }), "Visible Rows");

  private final int fixedRows;

  public static VisibleRows[] values() {
    return new VisibleRows[]{ VisibleRows.SMALL, VisibleRows.MEDIUM, VisibleRows.TALL, VisibleRows.FULL };
  }

  public int ordinal() {
    if (this == SMALL) return 0;
    if (this == MEDIUM) return 1;
    if (this == TALL) return 2;
    if (this == FULL) return 3;
    return -1;
  }

  public int fixedRows() {
    return fixedRows;
  }

  public boolean isFullScreen() {
    return this == FULL;
  }

  public VisibleRows next() {
    VisibleRows[] states = values();
    return states[(ordinal() + 1) % states.length];
  }

  public int resolve(IntSupplier screenMaxRows) {
    return isFullScreen() ? Math.max(SMALL.fixedRows(), screenMaxRows.getAsInt()) : fixedRows;
  }

  @Override
  public UIStateType<VisibleRows> getType() {
    return UIStateRegistration.VISIBLE_ROWS.get();
  }
}
