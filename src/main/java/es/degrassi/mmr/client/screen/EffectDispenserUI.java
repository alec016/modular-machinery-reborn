package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import es.degrassi.mmr.client.screen.util.InfoUIs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.util.RomanNumber;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.atomic.AtomicInteger;

public final class EffectDispenserUI {
  private EffectDispenserUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, EffectDispenserEntity hatch) {
    UIElement panel = InfoUIs.panel(5);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.effect_dispenser"));

    AtomicInteger index = new AtomicInteger(0);
    InfoUIs.line(panel, index.getAndIncrement(),
        () -> hatch.isApplyingEffect()
            ? UITexts.applyingEffect.text().getString()
            : null, UITexts.noRecipe.text().getString());
    InfoUIs.line(panel, index.getAndIncrement(),
        () -> hatch.getEffect()
            .map(effect -> UITexts.effectText.text(Component.translatable(effect.getDescriptionId())).getString())
            .orElse(null), null);
    InfoUIs.line(panel, index.getAndIncrement(),
        () -> hatch.getEffect()
            .map(effect -> UITexts.amplifier.text(RomanNumber.toRoman(effect.getAmplifier() + 1)).getString())
            .orElse(null), null);
    InfoUIs.line(panel, index.getAndIncrement(),
        () -> hatch.getEffect()
            .map(effect -> UITexts.duration.text(effect.getDuration()).getString())
            .orElse(null), null);
    if (!hatch.getSize().interdimensional)
      InfoUIs.line(panel, index.getAndIncrement(),
          () -> UITexts.radiusBlocks.text(hatch.getSize().radius).getString(), null);
    else InfoUIs.line(panel, index.getAndIncrement(),
        () -> UITexts.interdimensional.text().getString(), null);
    if(!hatch.getSize().interdimensional)
      InfoUIs.areaButton(panel, hatch.getBlockPos(),
          () -> new AABB(hatch.getBlockPos()).inflate(hatch.getSize().radius));
    return MMRMachineUI.menu(panel, holder.player);
  }
}
