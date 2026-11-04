package es.degrassi.mmr.client.screen.popup;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public final class CorePopup extends BasePopup {

  private static final int WIDTH = 180;
  private static final int HEIGHT = 96;

  public CorePopup(UIElement uiRoot, MachineControllerEntity entity, int page, int coreIndex) {
    super(WIDTH, HEIGHT);
    MachineProcessorCore core = entity.getProcessor().cores().get(coreIndex - 1);
    IconButton back = IconButton.iconButton(contentContainer, Icon.BACK, 159, 5,
        event -> {
          close();
          new CoreGridPopup(uiRoot, entity);
        }, null);
    back.style(style -> style.tooltips(Component.translatable("mmr.gui.button.back")));
    Label state = popupLabel(Component.empty(), 5, 24, 170);
    popupLabel(Component.translatable("mmr.core.number", core.getCore()),
        5, 38, 170);
    Label status = new Label();
    status.setText(Component.empty());
    MMRMachineUI.add(contentContainer, status, 5, 52).lss("width", 170);
    status.addEventListener(UIEvents.TICK, event -> {
      MachineProcessorCore live = entity.getProcessor().cores().get(coreIndex - 1);
      state.setText(Component.translatable("mmr.core.active." + live.isActive()));
      if (!live.isActive()) {
        status.setText(Component.empty());
        return;
      }
      MutableComponent text = Component.translatable("gui.controller.status");
      if (live.hasSyncedActiveRecipe()) {
        String perc = Utils.formatWithPercentage(Mth.clamp(Math.round(live.getCurrentActiveRecipeProgress() * 100F), 0f, 100f));
        text.append(Component.translatable("gui.controller.status.%s.progress".formatted(MachineStatus.RUNNING.getSerializedName()), perc));
      } else {
        text.append(MachineStatus.IDLE.text());
      }
      status.setText(text);
    });
    show(uiRoot);
  }
}
