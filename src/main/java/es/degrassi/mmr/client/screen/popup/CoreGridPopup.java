package es.degrassi.mmr.client.screen.popup;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class CoreGridPopup extends BasePopup {

  private static final int WIDTH = 220;
  private static final int HEIGHT = 180;

  private final UIElement uiRoot;
  private final MachineControllerEntity entity;
  private final int[] page = {1};

  public CoreGridPopup(UIElement parent, MachineControllerEntity entity) {
    super(WIDTH, HEIGHT);
    this.uiRoot = parent;
    this.entity = entity;
    rebuild();
    show(parent);
  }

  private void rebuild() {
    contentContainer.clearAllChildren();
    addCloseButton();
    MachineStatus status = entity.getStatus();
    if (status.isPaused() || status.isMissingStructure()) {
      popupLabel(Component.translatable(status.isPaused()
              ? "mmr.gui.paused"
              : "mmr.gui.missing_structure"),
          0, 60, WIDTH).setId("core_status");
    } else {
      int pages = pagesNumber(entity);
      int current = Math.clamp(page[0], 1, pages);
      List<MachineProcessorCore> cores = entity.getPages().get(current);
      if (cores != null) {
        for (int i = 0; i < cores.size(); i++) {
          MachineProcessorCore core = cores.get(i);
          int coreIndex = core.getCore();
          IconButton coreButton = IconButton.iconButton(contentContainer,
              core.isActive() ? Icon.CORE_ACTIVE : Icon.CORE_INACTIVE,
              5 + (i % 10) * 18, 30 + (i / 10) * 21,
              event -> {
                close();
                new CorePopup(uiRoot, entity, current, coreIndex);
              }, null);
          coreButton.style(style ->
              style.tooltips(Component.translatable("mmr.gui.core.button", core.getCore())));
          coreButton.setId("core_cell");
        }
      }
    }
    int pages = pagesNumber(entity);
    boolean prevEnabled = Math.min(page[0], pages) > 1;
    boolean nextEnabled = Math.min(page[0], pages) < pages;
    Button prev = SpriteButton.spriteButton(contentContainer, Component.literal("<"), 30, 150, 20, 20,
        event -> {
          if (prevEnabled) {
            page[0]--;
            rebuild();
          }
        });
    prev.buttonStyle(style -> {
      IGuiTexture tex = prevEnabled ? SpriteButton.buttonBase() : SpriteButton.buttonDisabled();
      IGuiTexture hover = prevEnabled ? SpriteButton.buttonHovered() : SpriteButton.buttonDisabled();
      style.baseTexture(tex);
      style.hoverTexture(hover);
      style.pressedTexture(hover);
    });
    prev.style(style -> style.tooltips(Component.translatable("mmr.gui.button.page.prev")));
    MMRMachineUI.spriteCaption(contentContainer,
        Component.literal(Math.min(page[0], pages) + "/" + pages),
        77, 155, 34, 10);
    Button next = SpriteButton.spriteButton(contentContainer, Component.literal(">"), 138, 150, 20, 20,
        event -> {
          if (nextEnabled) {
            page[0]++;
            rebuild();
          }
        });
    next.buttonStyle(style -> {
      IGuiTexture tex = nextEnabled ? SpriteButton.buttonBase() : SpriteButton.buttonDisabled();
      IGuiTexture hover = nextEnabled ? SpriteButton.buttonHovered() : SpriteButton.buttonDisabled();
      style.baseTexture(tex);
      style.hoverTexture(hover);
      style.pressedTexture(hover);
    });
    next.style(style -> style.tooltips(Component.translatable("mmr.gui.button.page.next")));
  }

  private static int pagesNumber(MachineControllerEntity entity) {
    if (entity.getPages() == null) {
      return 1;
    }
    int size = entity.getPages().size();
    return Math.max(1, size);
  }
}
