package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.client.scene.SceneRenderContext;
import com.lowdragmc.lowdraglib2.gui.sync.rpc.RPCEvent;
import com.lowdragmc.lowdraglib2.gui.sync.rpc.RPCEventBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scene;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.lowdraglib2.utils.data.BlockPosFace;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.WrappedBlockAndTintGetter;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.IOSideMode;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.api.entity.IAutoEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * LDLib2 rebuilds of the legacy tab widgets ({@code TabWidget}, {@code TopTabWidget},
 * {@code RightTabWidget}, {@code AutoTabWidget}). The tab plates are the original MMR sprites and
 * the auto-IO panel drives the same server-side side configuration as the old packets.
 */
public final class MMRTabs {

  /** Legacy tab sizes: {@code base_tab_top.png} 28x27, {@code base_tab_right.png} 27x28. */
  public static final int TOP_TAB_WIDTH = 28;
  public static final int TOP_TAB_HEIGHT = 27;
  public static final int RIGHT_TAB_WIDTH = 27;
  public static final int RIGHT_TAB_HEIGHT = 28;
  /** Consecutive tab pitch of the legacy {@code TabGroupWidget} top strip. */
  public static final int TOP_TAB_PITCH = 28;

  public static final Identifier TEX_TAB_TOP = ModularMachineryReborn.rl("textures/gui/widget/base_tab_top.png");
  public static final Identifier TEX_TAB_TOP_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_top.png");
  public static final Identifier TEX_TAB_RIGHT = ModularMachineryReborn.rl("textures/gui/widget/base_tab_right.png");
  public static final Identifier TEX_TAB_RIGHT_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_right.png");
  public static final Identifier TEX_AUTOIO_TAB = ModularMachineryReborn.rl("textures/gui/widget/autoio_tab.png");

  private MMRTabs() {}

  // ------------------------------------------------------------------
  // Simple one-shot tabs
  // ------------------------------------------------------------------

  /**
   * Adds a top tab at {@code (x, y)} (the legacy strip stacks across {@code x + i * 28} above the
   * panel). The icon is drawn bare at {@code (5, 5)}; the plate comes from
   * {@code base_tab(_hovered)_top.png}. This overload draws an atals icon instead of an item.
   */
  public static TopTab topTab(UIElement parent, int x, int y,
                              Icon spriteIcon, Supplier<List<Component>> tooltips,
                              @Nullable Consumer<UIEvent> onClick) {
    TopTab tab = new TopTab(() -> ItemStack.EMPTY, tooltips, spriteIcon);
    tab.lss("width", TOP_TAB_WIDTH).lss("height", TOP_TAB_HEIGHT);
    if (onClick != null) {
      tab.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        UISoundUtils.playButtonClickSound();
        onClick.accept(event);
      });
    }
    return MMRMachineUI.add(parent, tab, x, y);
  }

  /**
   * Adds a right-side tab at {@code (x, y)}, traditionally right past the panel's edge; the hovered
   * plate grows two pixels to the left, exactly like the legacy implementation.
   */
  public static TopTab topTab(UIElement parent, int x, int y,
                              Supplier<ItemStack> icon, Supplier<List<Component>> tooltips,
                              @Nullable Consumer<UIEvent> onClick) {
    TopTab tab = new TopTab(icon, tooltips);
    tab.lss("width", TOP_TAB_WIDTH).lss("height", TOP_TAB_HEIGHT);
    if (onClick != null) {
      tab.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        UISoundUtils.playButtonClickSound();
        onClick.accept(event);
      });
    }
    return MMRMachineUI.add(parent, tab, x, y);
  }

  /**
   * Adds a right-side tab at {@code (x, y)}, traditionally right past the panel's edge; the hovered
   * plate grows two pixels to the left, exactly like the legacy implementation.
   */
  public static RightTab rightTab(UIElement parent, int x, int y,
                                  Supplier<ItemStack> icon, Supplier<List<Component>> tooltips,
                                  @Nullable Consumer<UIEvent> onClick) {
    RightTab tab = new RightTab(icon, tooltips);
    tab.lss("width", RIGHT_TAB_WIDTH).lss("height", RIGHT_TAB_HEIGHT);
    if (onClick != null) {
      tab.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        UISoundUtils.playButtonClickSound();
        onClick.accept(event);
      });
    }
    return MMRMachineUI.add(parent, tab, x, y);
  }

  // ------------------------------------------------------------------
  // Tab elements
  // ------------------------------------------------------------------

  public static class TopTab extends TabElement {
    public TopTab(Supplier<ItemStack> icon, Supplier<List<Component>> tooltips) {
      super(icon, tooltips);
    }

    public TopTab(Supplier<ItemStack> icon, Supplier<List<Component>> tooltips, Icon spriteIcon) {
      super(icon, tooltips);
      this.spriteIcon = spriteIcon;
    }

    @Override
    protected void drawTab(GUIContext gui, int x, int y) {
      if (isHover()) {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_TOP_HOVERED, 0, 0, TOP_TAB_WIDTH, TOP_TAB_HEIGHT + 2),
            x, y, TOP_TAB_WIDTH, TOP_TAB_HEIGHT + 2);
      } else {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_TOP, 0, 0, TOP_TAB_WIDTH, TOP_TAB_HEIGHT),
            x, y, TOP_TAB_WIDTH, TOP_TAB_HEIGHT);
      }
      ItemStack stack = icon.get();
      if (stack != null && !stack.isEmpty()) {
        DrawerHelperClient.drawItemStack(gui, stack, x + 5, y + 5, 0);
      } else if (spriteIcon != null) {
        gui.drawTexture(MMRMachineUI.sprite(spriteIcon.TEXTURE, spriteIcon.x, spriteIcon.y, spriteIcon.width, spriteIcon.height),
            x + 5, y + 5, spriteIcon.width, spriteIcon.height);
      }
    }
  }

  public static class RightTab extends TabElement {
    public RightTab(Supplier<ItemStack> icon, Supplier<List<Component>> tooltips) {
      super(icon, tooltips);
    }

    @Override
    protected void drawTab(GUIContext gui, int x, int y) {
      if (isHover()) {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT_HOVERED, 0, 0, 29, RIGHT_TAB_HEIGHT),
            x - 2, y, 29, RIGHT_TAB_HEIGHT);
      } else {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT, 0, 0, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT),
            x, y, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT);
      }
      ItemStack stack = icon.get();
      if (stack != null && !stack.isEmpty()) {
        DrawerHelperClient.drawItemStack(gui, stack, x + 5, y + 5, 0);
      }
    }
  }

  abstract static class TabElement extends UIElement {
    protected final Supplier<ItemStack> icon;
    protected final Supplier<List<Component>> tooltips;
    @Nullable
    protected Icon spriteIcon;

    protected TabElement(Supplier<ItemStack> icon, Supplier<List<Component>> tooltips) {
      this.icon = icon;
      this.tooltips = tooltips;
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        // The legacy tabs forwarded the icon button's tooltips; the UI resolves them per frame
        // with a direct HOVER_TOOLTIPS dispatch on the hovered element.
        List<Component> lines = this.tooltips == null ? null : this.tooltips.get();
        if (lines != null && !lines.isEmpty()) {
          event.hoverTooltips = HoverTooltips.create(lines.toArray());
        }
      });
    }

    protected abstract void drawTab(GUIContext gui, int x, int y);

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui)) {
        return;
      }
      drawTab(gui, (int) getContentX(), (int) getContentY());
    }
  }

  // ------------------------------------------------------------------
  // Auto-IO configuration tab (GTCEu-style 3D side configurator)
  // ------------------------------------------------------------------

  /**
   * The opened panel area: the 27x28 right tab grows out into this size over the opening
   * animation, hosting the 3D preview and the two batch buttons.
   */
  public static final int AUTOIO_OPEN_WIDTH = 74;
  public static final int AUTOIO_OPEN_HEIGHT = 124;

  /**
   * The legacy {@code AutoInputTabWidget}/{@code AutoOutputTabWidget}: a right tab that opens the
   * side configuration panel. Rebuilt as a GTCEu-like configurator: opening animates the tab into
   * a small panel showing this component's block as a 3D preview (LDLib2 {@link Scene} on a dummy
   * world). Clicking a face toggles that relative side's auto-IO on the server (the old packet
   * cycled the mode list; with only the enabled/disabled modes left, cycling is a plain toggle),
   * and the two mini buttons below the preview enable/disable all sides at once.
   */
  public static <T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>>
  AutoIoTab<T> autoIoTab(UIElement parent, T entity, int x, int y, boolean input) {
    AutoIoTab<T> tab = new AutoIoTab<>(entity, input, input ? "mmr.gui.tooltip.auto_input" : "mmr.gui.tooltip.auto_output");
    tab.lss("width", RIGHT_TAB_WIDTH).lss("height", RIGHT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
    return tab;
  }

  public static class AutoIoTab<T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>>
      extends UIElement {
    /** The GTCEu-style 3D preview bounds inside the opened panel (panel-relative). */
    private static final int SCENE_X = 4;
    private static final int SCENE_Y = 32;
    private static final int SCENE_SIZE = 66;
    /**
     * Row of the batch buttons below the 3D preview — the bottom gap the user asked for sits
     * two pixels farther from the preview than the old {@code +2}.
     */
    private static final int BUTTON_Y = AUTOIO_OPEN_HEIGHT - 16;
    /** Close affordance pinned right on the 3D preview's top-left corner. */
    private static final int CLOSE_X = 4;
    private static final int CLOSE_Y = 3;
    /** Half-size batch row: minus / help / plus, 8px each with 4px gaps, centered in the panel. */
    private static final int SMALL_MINUS_X = (AUTOIO_OPEN_WIDTH - (3 * 8 + 2 * 4)) / 2;
    /** Open/close animation duration in milliseconds. */
    private static final long OPEN_MS = 240L;

    private final T entity;
    private final boolean input;
    private final String infoKey;
    private boolean opened = false;
    /** Millisecond timestamp while the open/close animation runs; negative when settled. */
    private long animStart = -1L;
    private int lastWidth = RIGHT_TAB_WIDTH;
    private int lastHeight = RIGHT_TAB_HEIGHT;
    private boolean contentBuilt = false;

    /**
     * One RPC per server mutation, registered in the constructor: the {@code UISyncManager} couples
     * the client and server UI instances by registration order, so both sides must build identical
     * event lists. Building them lazily (on the first open click) would desync the orders.
     */
    private final RPCEvent setSideRpc;
    private final RPCEvent enableAllRpc;
    private final RPCEvent disableAllRpc;

    public AutoIoTab(T entity, boolean input, String infoKey) {
      this.entity = entity;
      this.input = input;
      this.infoKey = infoKey;
      this.setSideRpc = RPCEventBuilder.create()
          .arg(String.class, RelativeSide.FRONT.name())
          // The boolean only keeps the event shape stable; the server flips authoritatively.
          .arg(Boolean.class, false)
          .executor(args -> {
            if (args.length >= 1 && args[0] != null) {
              // setNext flips around the SERVER's own current mode. Gating on the client's
              // requested target (the mode computed from its possibly-not-yet-synced copy) left
              // the toggle dead from the second click on: the flip did apply once, but without a
              // sync-back every later target equaled the server's current mode and got skipped.
              // configChanged now echoes the flip to clients via SUpdateSideConfigPacket.
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
        // The UI resolves tooltips per frame with a direct HOVER_TOOLTIPS dispatch; the open
        // panel stops answering so the 3D scene shows its own face tooltip instead.
        if (!opened) {
          event.hoverTooltips = HoverTooltips.create(Component.translatable("mmr.tooltip.open_side_config"));
        }
      });
    }

    private IOSideConfig config() {
      return (IOSideConfig) entity.getConfig();
    }

    /** Flips one side to the requested mode (only calls the server toggle when it changes). */
    private void toggleSide(RelativeSide side, IOSideMode target) {
      IOSideConfig config = config();
      if (config.getSideMode(side) != target) {
        config.setNext(side); // flips to the other mode and refreshes the actual IO faces
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

    /** Whether this is the auto-input (blue enabled faces) or auto-output (orange faces) variant. */
    boolean isInput() {
      return input;
    }

    private void open() {
      opened = true;
      // (Re)starts the open animation, even if a closing one happened to still be in flight.
      animStart = System.currentTimeMillis();
      // Build the Scene outside the render pass: adding it while the element tree was already
      // extracting gave it a zero-size layout for that frame and vanilla's picture-in-picture
      // renderer crashed creating a 0x0 texture ("mipLevels...").
      if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
        clearAllChildren();
        buildContent();
      }
    }

    /**
     * Close affordance: reverse the opening animation and restore the closed tab plate. The scene
     * and buttons are removed right away; {@code contentBuilt} holds until the reverse animation
     * finished so the shrinking panel keeps painting, and the content rebuilds on the next open.
     */
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

    /** Animation progress of the panel growth: one while open and settled, zero when closed. */
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
        // Closed tab (the server never builds the panel): plain plate with the hover shift.
        if (isHover()) {
          gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT_HOVERED, 0, 0, 29, RIGHT_TAB_HEIGHT),
              x - 2, y, 29, RIGHT_TAB_HEIGHT);
        } else {
          gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_RIGHT, 0, 0, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT),
              x, y, RIGHT_TAB_WIDTH, RIGHT_TAB_HEIGHT);
        }
        drawSpriteIcon(gui, Icon.SIDE_CONFIG, x + 5, y + 5, 0);
        return;
      }
      // Animating the tab plate sliding out into the full panel (and back in when closing).
      float progress = currentProgress();
      int width = Math.round(RIGHT_TAB_WIDTH + (AUTOIO_OPEN_WIDTH - RIGHT_TAB_WIDTH) * progress);
      int height = Math.round(RIGHT_TAB_HEIGHT + (AUTOIO_OPEN_HEIGHT - RIGHT_TAB_HEIGHT) * progress);
      if (animStart >= 0L && ((opened && progress >= 1f) || (!opened && progress <= 0f))) {
        animStart = -1L;
        if (!opened) {
          // The reverse animation finished: free the panel and restore the tab plate next frame.
          contentBuilt = false;
          return;
        }
      }
      if (width != lastWidth || height != lastHeight) {
        lss("width", width).lss("height", height);
        lastWidth = width;
        lastHeight = height;
      }
      // Panel body: the legacy nine-slice background stretched over the animated size. The tab
      // plate itself is not painted again — the grown panel replaces it.
      gui.drawTexture(MMRMachineUI.nineSliceTexture(MMRMachineUI.TEX_BACKGROUND), x, y, width, height);
    }

    /** Creates the 3D preview and the half-size button row (client side only). */
    private void buildContent() {
      contentBuilt = true;
      // The Scene (and the whole dummy world) is client-only; the server side of this tab stays an
      // empty panel and only ever carries the RPC registrations.
      if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
        SideScene<T> scene = new SideScene<>(this);
        scene.lss("width", SCENE_SIZE).lss("height", SCENE_SIZE - 6);
        MMRMachineUI.add(this, scene, SCENE_X, SCENE_Y);
        // Close affordance on the preview corner: reverses the opening animation.
        MMRWidgets.smallIconButton(this, Icon.CLEAR, CLOSE_X, CLOSE_Y, event -> close(),
            () -> List.of(Component.translatable("mmr.gui.tooltip.close_side_config")));
        // Help affordance: explains the select-then-toggle face flow (hover only, no click).
        MMRWidgets.smallIconButton(this, Icon.HELP,
            SMALL_MINUS_X + 12, BUTTON_Y, null,
            () -> List.of(
                Component.translatable("mmr.gui.autoio.hint.select"),
                Component.translatable("mmr.gui.autoio.hint.toggle")));
      }
      // "clear all" (minus, left) / help (middle) / "enable all" (plus, right) — compact batch row.
      MMRWidgets.smallIconButton(this, Icon.MINUS, SMALL_MINUS_X, BUTTON_Y,
          event -> sendEvent(disableAllRpc),
          () -> List.of(Component.translatable("mmr.gui.autoio.disable_all")));
      MMRWidgets.smallIconButton(this, Icon.PLUS, SMALL_MINUS_X + 24, BUTTON_Y,
          event -> sendEvent(enableAllRpc),
          () -> List.of(Component.translatable("mmr.gui.autoio.enable_all")));
    }

    private static void drawSpriteIcon(GUIContext gui, Icon icon, int x, int y, int hoverShift) {
      gui.drawTexture(MMRMachineUI.sprite(icon.TEXTURE, icon.x, icon.y, icon.width, icon.height),
          x, y + hoverShift, icon.width, icon.height);
    }
  }

  // ------------------------------------------------------------------
  // GTCEu-style 3D side configurator
  // ------------------------------------------------------------------

  /**
   * The GTCEu-style 3D preview of this component's block: the block is copied into a dummy world
   * and rendered by LDLib2's {@link Scene}. Hovering highlights the visible face under the cursor
   * (the scene's select outline) and shows that side's auto-IO state; a click toggles that side.
   * The preview can be rotated (drag) and zoomed (wheel) to reach the hidden faces, exactly like
   * the GTCEu machine preview.
   */
  public static class SideScene<T extends BlockEntity & ISideConfigComponent<IOSideMode> & IAutoEntity<?>>
      extends Scene {
    private final AutoIoTab<T> tab;
    /** Step one of the click flow: the face highlighted by clicking, waiting for its toggle click. */
    @Nullable
    private Direction selectedFace;
    /** The machine's real surrounding blocks, shown as 0.5-opacity ghosts around the preview. */
    private final List<GhostBlock> ghosts = new ArrayList<>();
    /** The dummy world adapted for ghost tessellation (AO/light sampling). */
    private final BlockAndTintGetter region;
    private final ModelBlockRenderer ghostRenderer;
    private final BlockStateModelSet modelSet;

    /** One surrounding block: its preview position (dummy world) and its real state. */
    private record GhostBlock(BlockPos pos, BlockState state) {}

    SideScene(AutoIoTab<T> tab) {
      this.tab = tab;
      // The component's block, copied into a tiny dummy world at the origin.
      var world = new TrackedDummyWorld();
      var pos = new BlockPos(0, 0, 0);
      world.addBlock(pos, new BlockInfo(tab.entity.getBlockState()));
      createScene(world, false, null);
      setRenderedCore(List.of(pos));
      setCameraYawAndPitch(-45f, 25f);
      // The scene's selection machinery paints a dark-red cube overlay on the selected block
      // (Scene.renderOverlay calls RenderUtils.renderBlockOverLay with r=0.6). The GT-style look
      // only wants the green facing border, so the overlay pass goes off while the selection
      // itself (and with it the border) stays intact.
      this.renderSelect = false;
      // Ghost support: only the component's block ever exists in the dummy world (the scene's
      // block filter stays core-only), so ghosts render but can never consume a ray-clip hit —
      // the scene's hover and clicks stay exclusive to the component's own six faces.
      this.region = world instanceof BlockAndTintGetter getter ? getter : new WrappedBlockAndTintGetter(world);
      var mc = Minecraft.getInstance();
      this.modelSet = mc.getModelManager().getBlockStateModelSet();
      this.ghostRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), true, mc.getBlockColors());
      setOnSelected((p, face) -> {
        if (p.equals(pos)) onFaceClicked(face);
        else onFaceClicked(null);
      });
      // A press that lands on the preview's background rather than on one of the six faces drops
      // the pending face selection; the scene-side block selection (facing border) is untouched.
      addEventListener(UIEvents.MOUSE_DOWN, event -> {
        if (getLastHoverPosFace() == null) {
          selectedFace = null;
        }
      });
      // Per-frame overlay (runs after the scene's translucent dispatch, camera still bound):
      // 0.5-alpha neighbor ghosts plus the colored enabled faces.
      this.overlay = this::renderOverlay;
      collectGhostNeighbors();
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        // Per-frame tooltip resolution; the face under the cursor makes the lines dynamic.
        BlockPosFace hover = getLastHoverPosFace();
        if (hover == null) {
          return;
        }
        RelativeSide side = RelativeSide.fromDirections(tab.entity.getControllerFacing(), hover.facing());
        IOSideMode mode = tab.entity.getConfig().getSideMode(side);
        Component blockName = blockName(side);
        List<Component> lines = new ArrayList<>();
        lines.add(blockName == null
            ? side.getTranslationName()
            : Component.translatable("%s: %s", side.getTranslationName(), blockName));
        lines.add(tab.info());
        lines.add(Component.translatable(tab.infoKey + ".change",
            Component.translatable("mmr.gui.tooltip.enabled." + mode.isEnabled()).withStyle(ChatFormatting.AQUA),
            Component.translatable("mmr.gui.tooltip.enabled." + mode.isDisabled()).withStyle(ChatFormatting.AQUA)));
        event.hoverTooltips = HoverTooltips.create(lines.toArray());
      });
    }

    /**
     * The two-step click flow: the first click on a face only selects it (the scene keeps its own
     * selection outline on that face for as long as the panel is open); clicking the already
     * selected face toggles that side's auto-IO on the server.
     */
    private void onFaceClicked(@Nullable Direction face) {
      if (face == null) {
        // A scene event with no face (click missed the component) resets the pending side
        // selection; the component's block itself keeps its scene-side selection.
        selectedFace = null;
        return;
      }
      if (selectedFace != face) {
        selectedFace = face;
        return;
      }
      toggleSide(face);
    }

    /** Toggles the already-selected face's world side on the server via the set-side RPC. */
    private void toggleSide(Direction face) {
      RelativeSide side = RelativeSide.fromDirections(tab.entity.getControllerFacing(), face);
      boolean turnOn = tab.entity.getConfig().getSideMode(side).isDisabled();
      // Runs client-side; the RPC flips the side on the server, which syncs it back.
      tab.sendEvent(tab.setSideRpc, side.name(), turnOn);
    }

    /**
     * Snapshots the six real blocks around the component into the ghost list. Ghosts keep their
     * real-world states (and therefore their true orientation); only non-air blocks get in, and
     * they are drawn per frame in {@link #renderOverlay} — never added to the dummy world.
     */
    private void collectGhostNeighbors() {
      // Client-side only in practice: the SideScene is built by the client-side open() call.
      var level = tab.entity.getLevel();
      if (level == null) {
        return;
      }
      for (Direction dir : Direction.values()) {
        BlockState state = level.getBlockState(tab.entity.getBlockPos().relative(dir));
        if (state.isAir()) {
          continue;
        }
        // Preview-space position: the same offset around the origin block as around the
        // real component, so a ghost always sits on the face it belongs to.
        ghosts.add(new GhostBlock(new BlockPos(0, 0, 0).relative(dir), state));
      }
    }

    /** Overlay hook: ghost neighbors first, then the auto-IO face colors on top. */
    private void renderOverlay(SceneRenderContext ctx) {
      MultiBufferSource.BufferSource bufferSource = ctx.bufferSource();
      drawGhostNeighbors(bufferSource);
      drawFaceTints(bufferSource);
    }

    private void drawGhostNeighbors(MultiBufferSource.BufferSource bufferSource) {
      if (ghosts.isEmpty()) {
        return;
      }
      var buffer = MMRSceneFx.ghostBuffer(bufferSource);
      for (GhostBlock ghost : ghosts) {
        MMRSceneFx.tesselateGhost(ghostRenderer, modelSet, region, buffer, ghost.pos(), ghost.state());
      }
    }

    /**
     * Auto-IO enabled faces: blue for the input variant, orange for the output variant — a
     * per-face indicator of which sides currently take part in the auto-IO, exactly the
     * color language the user asked for.
     */
    private void drawFaceTints(MultiBufferSource.BufferSource bufferSource) {
      float[] tint = tab.isInput() ? MMRSceneFx.INPUT_TINT : MMRSceneFx.OUTPUT_TINT;
      var config = tab.entity.getConfig();
      var controllerFacing = tab.entity.getControllerFacing();
      for (Direction face : Direction.values()) {
        RelativeSide side = RelativeSide.fromDirections(controllerFacing, face);
        if (config.getSideMode(side).isEnabled()) {
          MMRSceneFx.drawFaceTint(bufferSource, new BlockPos(0, 0, 0), face,
              tint[0], tint[1], tint[2], MMRSceneFx.TINT_ALPHA);
        }
      }
    }

    /** The block right next to the hovered face, like the old cell tooltip showed. */
    @Nullable
    private Component blockName(RelativeSide side) {
      if (tab.entity.getLevel() == null) {
        return null;
      }
      BlockState state = tab.entity.getLevel().getBlockState(
          tab.entity.getBlockPos().relative(side.getDirection(tab.entity.getControllerFacing())));
      if (state.isAir()) {
        return null;
      }
      return state.getBlock().getName();
    }
  }
}
