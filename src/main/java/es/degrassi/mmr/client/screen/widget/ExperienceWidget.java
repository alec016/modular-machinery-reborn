package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ExperienceWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.google.common.collect.Lists;
// import es.degrassi.experiencelib.util.ExperienceUtils;
// import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.AbstractWidget;
// import net.minecraft.client.gui.narration.NarrationElementOutput;
// import net.minecraft.client.sounds.SoundManager;
// import net.minecraft.network.chat.Component;
// import net.minecraft.util.FormattedCharSequence;
//
// import java.util.List;
//
// public class ExperienceWidget extends AbstractWidget {
//   private final ExperienceHatchEntity entity;
//
//   public ExperienceWidget(int x, int y, int screenWidth, ExperienceHatchEntity entity) {
//     super(x + 8, y, screenWidth - 16, 16, Component.empty());
//     this.entity = entity;
//   }
//
//   @Override
//   protected void renderWidget(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
//     if (this.visible) {
//       long level = ExperienceUtils.getLevelFromXp(entity.getTank().getExperience());
//       String levels = "" + level;
//       int xPos = this.getX() + this.width / 2 - Minecraft.getInstance().font.width(levels) / 2;
//       graphics.drawString(Minecraft.getInstance().font, levels, xPos, this.getY(), 0x80FF20, true);
//       graphics.fill(this.getX(), this.getY() + 9, this.getX() + this.width, this.getY() + 12, 0xFF000000);
//       long xpDiff = entity.getTank().getExperience() - ExperienceUtils.getXpFromLevel(level);
//       if (xpDiff > 0) {
//         double percent = (double) xpDiff / ExperienceUtils.getXpNeededForNextLevel(level);
//         graphics.fill(this.getX() + 1, this.getY() + 10, this.getX() + 1 + Math.max((int) Math.ceil(this.width * percent) - 2, 0), this.getY() + 11, 0xFF80FF20);
//       }
//     }
//   }
//
//   @Override
//   public void playDownSound(SoundManager handler) {
//     // Do not play a sound on click
//   }
//
//   @Override
//   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
//     this.defaultButtonNarrationText(narrationElementOutput);
//   }
//
//   public List<FormattedCharSequence> getTooltipMessage() {
//     String literal = ExperienceUtils.format(entity.getTank().getExperience());
//     String capacityLiteral = ExperienceUtils.format(entity.getTank().getExperienceCapacity()) + "XP";
//     String level = ExperienceUtils.getLevelFromXp(entity.getTank().getExperience()) + "";
//     String capacityLevel = ExperienceUtils.getLevelFromXp(entity.getTank().getExperienceCapacity()) + " levels";
//     return Lists.newArrayList(
//         Component.translatable("mmr.gui.element.experience.tooltip",
//             literal,
//             capacityLiteral
//         ).withStyle(ChatFormatting.GRAY),
//         Component.translatable(
//             "mmr.gui.element.experience.tooltip",
//             level,
//             capacityLevel
//         ).withStyle(ChatFormatting.GRAY)
//     ).stream().map(Component::getVisualOrderText).toList();
//   }
// }
//
