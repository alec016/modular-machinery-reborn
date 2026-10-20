package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.experiencelib.util.ExperienceUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

/**
 * Gauge elements that forward the exact visuals of the legacy gauge widgets through the LDLib2
 * render pipeline: fluid tank fills ({@code FluidTankWidget}), the energy column cropped out of
 * {@code guibar.png} ({@code EnergyHatchScreen}), the fuel gauge ({@code FuelWidget}) and the
 * experience bar ({@code ExperienceWidget}).
 */
public final class MMRGauges {

  /** Fluid tank fill: legacy width/height of every tank gauge area. */
  public static final int TANK_WIDTH = 20;
  public static final int TANK_HEIGHT = 61;

  private MMRGauges() {}

  /** Base gauge: dynamic tooltip lines, mirror of the legacy per-gauge tooltips. */
  public abstract static class Gauge extends UIElement {
    protected Gauge() {
      // The UI resolves tooltips per frame by dispatching HOVER_TOOLTIPS on the hovered element
      // (and walking up from it), so answering here keeps the lines live every frame — including
      // while the gauge is empty (the legacy gauges showed "Empty 0/{capacity}").
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        List<Component> lines = tooltipLines();
        if (!lines.isEmpty()) {
          event.hoverTooltips = HoverTooltips.create(lines.toArray());
        }
      });
    }

    protected abstract List<Component> tooltipLines();

    /** Client-side pass through the loaded {@link GUIContext}. */
    protected static GUIContext clientContext(IGUIContext context) {
      return context instanceof GUIContext guiContext ? guiContext : null;
    }
  }

  /**
   * One fluid tank column of a fluid hatch: 20x61 bottom-anchored tiled fill, tooltip exactly
   * like the legacy {@code FluidTankWidget} ({@code tooltip.fluidhatch.empty} / content name and
   * {@code tooltip.fluidhatch.tank}).
   */
  public static Gauge fluidTank(FluidHandler tank, int index) {
    return new Gauge() {
      @Override
      protected List<Component> tooltipLines() {
        List<Component> text = new ArrayList<>();
        FluidStack content = tank.getStack(index);
        long capacity = tank.getCapacityAsLong(index, FluidResource.EMPTY);
        int amount;
        if (content.getAmount() <= 0) {
          text.add(Component.translatable("tooltip.fluidhatch.empty"));
          amount = 0;
        } else {
          text.add(content.getHoverName());
          amount = content.getAmount();
        }
        text.add(Component.translatable("tooltip.fluidhatch.tank", String.valueOf(amount), String.valueOf(capacity)));
        return text;
      }

      @Override
      protected void drawBackgroundAdditional(IGUIContext context) {
        GUIContext gui = clientContext(context);
        if (gui == null) {
          return;
        }
        FluidStack fluid = tank.getStack(index);
        long capacity = tank.getCapacityAsLong(index, FluidResource.EMPTY);
        if (fluid.getAmount() <= 0 || capacity <= 0) {
          return;
        }
        int scaledAmount = (int) (fluid.getAmount() * TANK_HEIGHT / capacity);
        // Legacy FluidRenderer clamps: at least one pixel and never over the gauge area.
        scaledAmount = Mth.clamp(scaledAmount, 1, TANK_HEIGHT);
        DrawerHelperClient.drawFluidForGui(gui, fluid,
            getContentX(), getContentY() + TANK_HEIGHT - scaledAmount, TANK_WIDTH, scaledAmount, -1);
      }
    };
  }

  /**
   * The energy hatch column: the legacy screen cropped the fill sprite out of the very same
   * {@code guibar.png} texture (source x = 196, bottom anchored) and drew it over the tank outline.
   */
  public static Gauge energyColumn(LongSupplier current, LongSupplier max) {
    return new Gauge() {
      @Override
      protected List<Component> tooltipLines() {
        long currentEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(current.getAsLong());
        long maxEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(max.getAsLong());
        return List.of(Component.translatable("tooltip.energyhatch.charge",
            String.valueOf(currentEnergy),
            String.valueOf(maxEnergy),
            Component.translatable(EnergyDisplayUtil.type.getUnlocalizedFormat())));
      }

      @Override
      protected void drawBackgroundAdditional(IGUIContext context) {
        GUIContext gui = clientContext(context);
        if (gui == null) {
          return;
        }
        float percFilled = max.getAsLong() <= 0 ? 0f
            : Mth.clamp(current.getAsLong() / (float) max.getAsLong(), 0f, 1f);
        int pxFilled = Mth.ceil(percFilled * TANK_HEIGHT);
        if (pxFilled <= 0) {
          return;
        }
        pxFilled = Math.min(pxFilled, TANK_HEIGHT);
        context.drawTexture(MMRMachineUI.sprite(MMRMachineUI.TEX_GUIBAR, 196, TANK_HEIGHT - pxFilled, TANK_WIDTH, pxFilled),
            getContentX(), getContentY() + TANK_HEIGHT - pxFilled, TANK_WIDTH, pxFilled);
      }
    };
  }

  /**
   * Fuel gauge (14x14): legacy {@code FuelWidget} — the empty sprite plus the filled sprite whose
   * bottom part is blitted from the source texture, one pixel inset.
   */
  public static Gauge fuel(LongSupplier fuel, LongSupplier maxFuel) {
    return new Gauge() {
      @Override
      protected List<Component> tooltipLines() {
        return List.of(Component.translatable("tooltip.fuel_tank.tank",
            String.valueOf(fuel.getAsLong()), String.valueOf(maxFuel.getAsLong())));
      }

      @Override
      protected void drawBackgroundAdditional(IGUIContext context) {
        GUIContext gui = clientContext(context);
        if (gui == null) {
          return;
        }
        int width = 14;
        int height = 14;
        context.drawTexture(MMRMachineUI.sprite(ModularMachineryReborn.rl("textures/gui/empty_fuel.png"), 0, 0, width, height),
            getContentX(), getContentY(), width, height);
        long max = maxFuel.getAsLong();
        long value = fuel.getAsLong();
        if (value > 0 && max > 0) {
          int calcHeight = (int) (value * 1d / max * height);
          calcHeight = Mth.clamp(calcHeight, 0, height);
          int offset = height - calcHeight;
          context.drawTexture(MMRMachineUI.sprite(ModularMachineryReborn.rl("textures/gui/filled_fuel.png"), 0, offset, width, calcHeight),
              getContentX() + 1, getContentY() + offset + 1, width, calcHeight);
        }
      }
    };
  }

  /**
   * Experience hatch bar ({@code width x 16}): the level text centered on the first row, a black
   * 3px track and the green progress pixel line, exactly like the legacy {@code ExperienceWidget}.
   */
  public static Gauge experienceBar(int width, LongSupplier amount, LongSupplier capacity) {
    return new Gauge() {
      @Override
      protected List<Component> tooltipLines() {
        long value = amount.getAsLong();
        long cap = capacity.getAsLong();
        String literal = ExperienceUtils.format(value);
        String capacityLiteral = ExperienceUtils.format(cap) + "XP";
        String level = ExperienceUtils.getLevelFromXp(value) + "";
        String capacityLevel = ExperienceUtils.getLevelFromXp(cap) + " levels";
        return List.of(
            Component.translatable("mmr.gui.element.experience.tooltip", literal, capacityLiteral)
                .withStyle(ChatFormatting.GRAY),
            Component.translatable("mmr.gui.element.experience.tooltip", level, capacityLevel)
                .withStyle(ChatFormatting.GRAY));
      }

      @Override
      protected void drawBackgroundAdditional(IGUIContext context) {
        GUIContext gui = clientContext(context);
        if (gui == null) {
          return;
        }
        int x = (int) getContentX();
        int y = (int) getContentY();
        long value = amount.getAsLong();
        long level = ExperienceUtils.getLevelFromXp(value);
        String levels = Long.toString(level);
        DrawerHelperClient.drawStringSized(gui, levels, x + width / 2f, y, 0xFF80FF20, true, 1f, true);
        DrawerHelperClient.drawSolidRect(gui, x, y + 9, width, 3, 0xFF000000);
        long xpDiff = value - ExperienceUtils.getXpFromLevel(level);
        if (xpDiff > 0) {
          double percent = (double) xpDiff / ExperienceUtils.getXpNeededForNextLevel(level);
          int fill = Math.max((int) Math.ceil(width * percent) - 2, 0);
          DrawerHelperClient.drawSolidRect(gui, x + 1, y + 10, fill, 1, 0xFF80FF20);
        }
      }
    };
  }

}
