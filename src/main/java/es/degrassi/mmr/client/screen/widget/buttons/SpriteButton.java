package es.degrassi.mmr.client.screen.widget.buttons;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class SpriteButton {

  private SpriteButton() {}

  public static IGuiTexture buttonBase() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button.png").toString())
        .setBorder(3);
  }

  public static IGuiTexture buttonHovered() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button_highlighted.png").toString())
        .setBorder(3);
  }

  public static IGuiTexture buttonDisabled() {
    return SpriteTexture.of(ModularMachineryReborn.rl("textures/gui/sprites/widget/button/button_disabled.png").toString())
        .setBorder(3);
  }

  public static Button spriteButton(UIElement parent, Component text, int x, int y, int width, int height,
                                    @Nullable Consumer<UIEvent> onClick) {
    Button button = new Button();
    button.setText(text);
    button.textStyle(style -> {
      style.textWrap(TextWrap.HOVER_ROLL);
    });
    button.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    button.buttonStyle(style -> {
      style.baseTexture(buttonBase());
      style.hoverTexture(buttonHovered());
      style.pressedTexture(buttonHovered());
    });
    if (onClick != null) {
      button.setOnClick(onClick::accept);
    }
    return MMRMachineUI.add(parent, button, x, y);
  }
}
