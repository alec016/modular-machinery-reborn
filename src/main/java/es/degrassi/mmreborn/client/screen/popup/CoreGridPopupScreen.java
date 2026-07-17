package es.degrassi.mmreborn.client.screen.popup;

import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.api.client.Icon;
import es.degrassi.mmreborn.client.screen.widget.ItemOrIconButton;
import es.degrassi.mmreborn.client.screen.widget.StringButton;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessorCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class CoreGridPopupScreen extends PopupScreen<ControllerContainer> {
  private boolean closeButton = false;
  private final int initialXSize;

  public CoreGridPopupScreen(ControllerScreen parent, int xSize, int ySize) {
    super(parent, xSize, ySize);
    this.initialXSize = xSize;
  }

  public CoreGridPopupScreen addCloseButton() {
    this.closeButton = true;
    return this;
  }

  @Override
  protected void init() {
    Font font = Minecraft.getInstance().font;
    super.init();
    GridLayout layout = new GridLayout(this.x + 5, this.y).columnSpacing(2).rowSpacing(5);
    int cols = 10;
    GridLayout.RowHelper row = layout.createRowHelper(cols);
    row.defaultCellSetting().alignHorizontallyCenter();

    row.addChild(new StringWidget(this.initialXSize, 0, Component.literal(""), font), cols);

    if (closeButton) {
      row.addChild(new StringWidget(5, 0, Component.literal(""), font));
      row.addChild(
          Button
              .builder(Component.literal("X"), b -> this.parent.closePopup(this))
              .bounds(
                  0,
                  0,
                  20,
                  20
              )
              .build(builder -> new StringButton(builder, true).setTooltips(Component.translatable("mmr.gui.button.close"))),
          cols - 1,
          row.newCellSettings().alignHorizontallyRight().paddingRight(5)
      );
    }

    var status = getMenu().getEntity().getStatus();
    if (status.isPaused()) {
      var text = Component.translatable("mmr.gui.paused");
      row.addChild(new StringWidget(font.width(text), font.lineHeight, text, font), cols);
    } else if (!status.isMissingStructure()) {
      for (MachineProcessorCore core : getMenu().getPage()) {
        row.addChild(createCoreButton(core));
      }
    } else {
      var text = Component.translatable("mmr.gui.missing_structure");
      row.addChild(new StringWidget(font.width(text), font.lineHeight, text, font), cols);
    }

    layout.arrangeElements();
    GridLayout prevLayout = layout;
    layout = new GridLayout(this.x, this.y + layout.getHeight() + 20);
    row = layout.createRowHelper(cols);
    row.defaultCellSetting().alignHorizontallyCenter();

    AtomicReference<Button> add = new AtomicReference<>(null);
    AtomicReference<Button> rest = new AtomicReference<>(null);

    rest.set(Button.builder(Component.literal("<"), restB -> updateButton(add.get(), restB, false))
        .bounds(0, 0, 20, 20)
        .build(builder -> new StringButton(builder, true).setTooltips(Component.translatable("mmr.gui.button.page.prev"))));

    add.set(Button.builder(Component.literal(">"), addB -> updateButton(addB, rest.get(), true))
        .bounds(0, 0, 20, 20)
        .build(builder -> new StringButton(builder, true).setTooltips(Component.translatable("mmr.gui.button.page.next"))));

    rest.get().active = getPage() > 1;
    add.get().active = getPage() < getMenu().getPagesNumber();

    row.addChild(new StringWidget(this.initialXSize, 0, Component.literal(""), font), 10);

    row.addChild(
        rest.get(),
        4,
        row.newCellSettings().alignHorizontallyCenter()
    );
    row.addChild(
        new StringWidget(Component.literal(String.format("%s/%s", getPage(), getMenu().getPagesNumber())), font),
        2,
        row.newCellSettings().alignHorizontallyCenter().alignVerticallyMiddle()
    );
    row.addChild(
        add.get(),
        4,
        row.newCellSettings().alignHorizontallyCenter()
    );
    layout.arrangeElements();
    layout.setY(this.y + this.ySize - layout.getHeight() - 10);
    this.xSize = Math.max(this.initialXSize, Math.max(layout.width + 8, prevLayout.width + 8));
    prevLayout.visitWidgets(this::addRenderableWidget);
    layout.visitWidgets(this::addRenderableWidget);
  }

  private void updateButton(Button addB, Button restB, boolean add) {
    if (add && getPage() < getMenu().getPagesNumber()) setPage(getPage() + 1);
    else if (!add && getPage() > 1) setPage(this.getPage() - 1);

    this.rebuildWidgets();
    addB.active = getPage() < getMenu().getPagesNumber();
    restB.active = getPage() > 1;
  }

  private @NotNull ItemOrIconButton createCoreButton(MachineProcessorCore core) {
    ItemOrIconButton button = new ItemOrIconButton(0, 0, core.isActive() ? Icon.CORE_ACTIVE : Icon.CORE_INACTIVE, (btn) -> {
      parent.closePopup(this);
      parent.openPopup(new CorePopupScreen((ControllerScreen) parent, 180, 96, getPage(), core.getCore()).addCloseButton(), "popup");
    }).renderTooltip(true).setTooltips(Component.translatable("mmr.gui.core.button", core.getCore()));
    button.setTooltip(null);
    return button;
  }

  public int getPage() {
    return getMenu().getCurrentCorePage();
  }

  public void setPage(int page) {
    getMenu().setPage(page);
  }
}
