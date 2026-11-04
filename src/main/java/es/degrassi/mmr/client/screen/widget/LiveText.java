package es.degrassi.mmr.client.screen.widget;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class LiveText extends UIElement {
  public static final int TEXT_COLOR = 0xFF404040;

  private final Supplier<String> text;
  private final String whenEmpty;
  private final int color;
  private final boolean dropShadow;
  private final int width;

  public LiveText(
      UIElement parent, int x, int y, int width,
      Supplier<@Nullable String> text, @Nullable String whenEmpty) {
    this(parent, x, y, width, text, whenEmpty, TEXT_COLOR, false);
  }

  public LiveText(
      UIElement parent, int x, int y, int width,
      Supplier<@Nullable String> text, @Nullable String whenEmpty,
      int color, boolean dropShadow) {
    this.width = width;
    this.text = text;
    this.whenEmpty = whenEmpty;
    this.color = color;
    this.dropShadow = dropShadow;
    lss("width", width).lss("height", 10);
    MMRMachineUI.add(parent, this, x, y);
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext gui)) {
      return;
    }
    String line = text.get();
    if (line == null || line.isBlank()) {
      line = whenEmpty;
    }
    if (line == null || line.isBlank()) {
      return;
    }

    var font = gui.mc.font;
    var lines = font.getSplitter().splitLines(line, width, Style.EMPTY.withColor(color));
    lss("height", font.lineHeight * lines.size());
    for (int i = 0; i < lines.size(); i++) {
      var l = lines.get(i).getString();
      DrawerHelperClient.drawStringSized(gui, l,
          getContentX(), getContentY() + (i * font.lineHeight), color, dropShadow, 1f, false);
    }

  }
}
