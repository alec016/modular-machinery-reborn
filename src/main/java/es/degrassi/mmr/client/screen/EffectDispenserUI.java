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

public final class EffectDispenserUI {
  private EffectDispenserUI() {}

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, EffectDispenserEntity hatch) {
    UIElement panel = InfoUIs.panel(6);
    InfoUIs.caption(panel, Component.translatable("modular_machinery_reborn.gui.title.effect_dispenser"));

    InfoUIs.line(panel, 0,
        () -> hatch.isApplyingEffect()
            ? UITexts.applyingEffect.text().getString()
            : null, UITexts.noRecipe.text().getString());
    InfoUIs.line(panel, 1,
        () -> hatch.getEffect()
            .map(effect -> UITexts.effectText.text(Component.translatable(effect.getDescriptionId())).getString())
            .orElse(null), null);
    InfoUIs.line(panel, 2,
        () -> hatch.getEffect()
            .map(effect -> UITexts.amplifier.text(RomanNumber.toRoman(effect.getAmplifier() + 1)).getString())
            .orElse(null), null);
    InfoUIs.line(panel, 3,
        () -> hatch.getEffect()
            .map(effect -> UITexts.duration.text(effect.getDuration()).getString())
            .orElse(null), null);
    InfoUIs.line(panel, 4,
        () -> UITexts.radiusBlocks.text(hatch.getSize().radius).getString(), null);
    InfoUIs.line(panel, 5,
        () -> hatch.getSize().interdimensional ? UITexts.interdimensional.text().getString() : null, null);

    InfoUIs.areaButton(panel, hatch.getBlockPos(),
        () -> new AABB(hatch.getBlockPos()).inflate(hatch.getSize().radius));
    return MMRMachineUI.menu(panel, holder.player);
  }
}
