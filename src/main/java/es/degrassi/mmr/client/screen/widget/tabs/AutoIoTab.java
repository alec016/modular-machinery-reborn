package es.degrassi.mmr.client.screen.widget.tabs;

import com.lowdragmc.lowdraglib2.gui.sync.rpc.RPCEvent;
import com.lowdragmc.lowdraglib2.gui.sync.rpc.RPCEventBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class AutoIoTab<T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>> extends UIElement {
  public static final int AUTOIO_OPEN_WIDTH = 74;
  public static final int AUTOIO_OPEN_HEIGHT = 124;
  private static final int SCENE_X = 4;
  private static final int SCENE_Y = 32;
  private static final int SCENE_SIZE = 66;
  private static final int BUTTON_Y = AUTOIO_OPEN_HEIGHT - 16;
  private static final int CLOSE_X = 4;
  private static final int CLOSE_Y = 3;
  private static final int SMALL_MINUS_X = (AUTOIO_OPEN_WIDTH - (3 * 8 + 2 * 4)) / 2;
  private static final long OPEN_MS = 240L;

  final T entity;
  final boolean input;
  final String infoKey;
  private boolean opened = false;
  private long animStart = -1L;
  private int lastWidth = RightTab.RIGHT_TAB_WIDTH;
  private int lastHeight = RightTab.RIGHT_TAB_HEIGHT;
  private boolean contentBuilt = false;

  final RPCEvent setSideRpc;
  private final RPCEvent enableAllRpc;
  private final RPCEvent disableAllRpc;

  public AutoIoTab(UIElement parent, T entity, int x, int y, boolean input) {
    this.entity = entity;
    this.input = input;
    this.infoKey = input ? "mmr.gui.tooltip.auto_input" : "mmr.gui.tooltip.auto_output";
    this.setSideRpc = RPCEventBuilder.create()
        .arg(String.class, RelativeSide.FRONT.name())
        .arg(Boolean.class, false)
        .executor(args -> {
          if (args.length >= 1 && args[0] != null) {
            config().setNext(RelativeSide.valueOf((String) args[0]));
          }
          return null;
        })
        .build();
    this.enableAllRpc = RPCEventBuilder.simple(() -> setAllSides(true));
    this.disableAllRpc = RPCEventBuilder.simple(() -> setAllSides(false));
    addRPCEvent(this.setSideRpc);
    addRPCEvent(this.enableAllRpc);
    addRPCEvent(this.disableAllRpc);
    addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (!opened) {
        UISoundUtils.playButtonClickSound();
        open();
      }
    });
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      if (!opened && event.target == this) {
        event.hoverTooltips = HoverTooltips.create(Component.translatable("mmr.tooltip.open_side_config"));
      }
    });
    lss("width", RightTab.RIGHT_TAB_WIDTH).lss("height", RightTab.RIGHT_TAB_HEIGHT);
    MMRMachineUI.add(parent, this, x, y);
  }

  private IOSideConfig config() {
    return (IOSideConfig) entity.getConfig();
  }

  private void toggleSide(RelativeSide side, IOSideMode target) {
    IOSideConfig config = config();
    if (config.getSideMode(side) != target) {
      config.setNext(side);
    }
  }

  private void setAllSides(boolean enabled) {
    IOSideMode target = enabled ? IOSideMode.ENABLED : IOSideMode.DISABLED;
    for (RelativeSide side : RelativeSide.values()) {
      toggleSide(side, target);
    }
  }

  Component info() {
    return Component.translatable(infoKey);
  }

  boolean isInput() {
    return input;
  }

  private void open() {
    opened = true;
    animStart = System.currentTimeMillis();
    if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
      clearAllChildren();
      buildContent();
    }
  }

  private void close() {
    if (!opened) {
      return;
    }
    opened = false;
    animStart = System.currentTimeMillis();
    if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
      clearAllChildren();
    }
  }

  private float currentProgress() {
    if (animStart < 0L) {
      return opened ? 1f : 0f;
    }
    float t = (System.currentTimeMillis() - animStart) / (float) OPEN_MS;
    return opened ? Math.min(1f, t) : 1f - Math.min(1f, t);
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext gui)) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    if (!contentBuilt) {
      if (isHover()) {
        gui.drawTexture(MMRMachineUI.sprite(RightTab.TEX_TAB_RIGHT_HOVERED, 0, 0, 29, RightTab.RIGHT_TAB_HEIGHT),
            x - 2, y, 29, RightTab.RIGHT_TAB_HEIGHT);
      } else {
        gui.drawTexture(MMRMachineUI.sprite(RightTab.TEX_TAB_RIGHT, 0, 0, RightTab.RIGHT_TAB_WIDTH, RightTab.RIGHT_TAB_HEIGHT),
            x, y, RightTab.RIGHT_TAB_WIDTH, RightTab.RIGHT_TAB_HEIGHT);
      }
      drawSpriteIcon(gui, Icon.SIDE_CONFIG, x + 5, y + 5, 0);
      return;
    }
    float progress = currentProgress();
    int width = Math.round(RightTab.RIGHT_TAB_WIDTH + (AUTOIO_OPEN_WIDTH - RightTab.RIGHT_TAB_WIDTH) * progress);
    int height = Math.round(RightTab.RIGHT_TAB_HEIGHT + (AUTOIO_OPEN_HEIGHT - RightTab.RIGHT_TAB_HEIGHT) * progress);
    if (animStart >= 0L && ((opened && progress >= 1f) || (!opened && progress <= 0f))) {
      animStart = -1L;
      if (!opened) {
        contentBuilt = false;
        return;
      }
    }
    if (width != lastWidth || height != lastHeight) {
      lss("width", width).lss("height", height);
      lastWidth = width;
      lastHeight = height;
    }
    gui.drawTexture(MMRMachineUI.nineSliceTexture(MMRMachineUI.TEX_BACKGROUND), x, y, width, height);
  }

  /** Creates the 3D preview and the half-size button row (client side only). */
  private void buildContent() {
    contentBuilt = true;
    if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
      SideScene<T> scene = new SideScene<>(this);
      scene.lss("width", SCENE_SIZE).lss("height", SCENE_SIZE - 6);
      MMRMachineUI.add(this, scene, SCENE_X, SCENE_Y);
      // Close affordance on the preview corner: reverses the opening animation.
      IconButton.smallIconButton(this, Icon.CLEAR, CLOSE_X, CLOSE_Y, event -> close(),
          () -> List.of(Component.translatable("mmr.gui.tooltip.close_side_config")));
      IconButton.smallIconButton(this, Icon.HELP,
          SMALL_MINUS_X + 12, BUTTON_Y, null,
          () -> List.of(
              Component.translatable("mmr.gui.autoio.hint.select"),
              Component.translatable("mmr.gui.autoio.hint.toggle")));
    }
    IconButton.smallIconButton(this, Icon.MINUS, SMALL_MINUS_X, BUTTON_Y,
        event -> sendEvent(disableAllRpc),
        () -> List.of(Component.translatable("mmr.gui.autoio.disable_all")));
    IconButton.smallIconButton(this, Icon.PLUS, SMALL_MINUS_X + 24, BUTTON_Y,
        event -> sendEvent(enableAllRpc),
        () -> List.of(Component.translatable("mmr.gui.autoio.enable_all")));
  }

  private static void drawSpriteIcon(GUIContext gui, Icon icon, int x, int y, int hoverShift) {
    gui.drawTexture(MMRMachineUI.sprite(icon.TEXTURE, icon.x, icon.y, icon.width, icon.height),
        x, y + hoverShift, icon.width, icon.height);
  }
}
