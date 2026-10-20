package es.degrassi.mmr.client.screen.widget.tabs;

import es.degrassi.mmr.api.entity.IAutoEntity;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public abstract class AutoTabWidget<T extends IAutoEntity<?>> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import com.google.common.collect.Maps;
// import es.degrassi.mmr.api.capability.config.IOSideMode;
// import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
// import es.degrassi.mmr.api.capability.config.RelativeSide;
// import es.degrassi.mmr.api.client.Icon;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.block.BlockController;
// import es.degrassi.mmr.common.entity.MachineControllerEntity;
// import es.degrassi.mmr.api.entity.IAutoEntity;
// import es.degrassi.mmr.common.network.client.CChangeIOSideConfigPacket;
// import lombok.Getter;
// import lombok.Setter;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.sounds.SoundManager;
// import net.minecraft.core.BlockPos;
// import net.minecraft.network.chat.Component;
// import net.minecraft.world.item.Item;
// import net.neoforged.neoforge.network.PacketDistributor;
//
// import javax.annotation.Nullable;
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// @Getter
// @ParametersAreNonnullByDefault
// public abstract class AutoTabWidget<T extends IAutoEntity<?> & ISideConfigComponent<IOSideMode>> extends RightTabWidget {
//   private final T entity;
//   private final Map<RelativeSide, VariableItemOrIconButton<T>> sideConfigs = Maps.newEnumMap(RelativeSide.class);
//   @Setter
//   private boolean opened = false;
//   private final Component info;
//
//   private static final int startX = 2, startY = 2;
//   private static final int configWidth = 18, configHeight = 20;
//   private final ItemOrIconButton infoButton;
//
//   public AutoTabWidget(T entity, Component info) {
//     super(
//         0,
//         0,
//         new ItemOrIconButton(0, 0, Icon.SIDE_CONFIG, (b) -> {})
//             .setTooltips(Component.translatable("mmr.tooltip.open_side_config"))
//             .setRenderTooltip(true),
//         null
//     );
//     this.info = info;
//     this.entity = entity;
//
//     this.infoButton = new ItemOrIconButton(startX, startY, Icon.HELP, b -> {})
//         .setRenderTooltip(true)
//         .setTooltips(Component.translatable("mmr.config.tooltip.info", Component.translatable("mmr." + this.entity.getControllerFacing().getName())))
//         .setDisableClickSound(true);
//
//     createSide(RelativeSide.LEFT, startX, startY + configHeight);
//     createSide(RelativeSide.FRONT, startX + configWidth, startY + configHeight);
//     createSide(RelativeSide.TOP, startX + configWidth, startY);
//     createSide(RelativeSide.BOTTOM, startX + configWidth, startY + (configHeight * 2));
//     createSide(RelativeSide.RIGHT, startX + (configWidth * 2), startY + configHeight);
//     createSide(RelativeSide.BACK, startX + (configWidth * 2), startY + (configHeight * 2));
//   }
//
//   @Override
//   public void playDownSound(SoundManager handler) {
//     if (!opened) super.playDownSound(handler);
//   }
//
//   @Override
//   public int getWidth() {
//     return opened ? (startX * 2) + (configWidth * 3) : super.getWidth();
//   }
//
//   @Override
//   public int getHeight() {
//     return opened ? (startY * 2) + (configHeight * 3) : super.getHeight();
//   }
//
//   private void createSide(RelativeSide side, int x, int y) {
//     sideConfigs.put(side, new VariableItemOrIconButton<>(
//         x,
//         y,
//         b -> {},
//         entity,
//         side,
//         info
//     ));
//   }
//
//   @Override
//   public void setX(int x) {
//     super.setX(x);
//     this.infoButton.setX(startX + x);
//     sideConfigs.values().forEach(side -> side.setX(side.getInitialX() + x));
//   }
//
//   @Override
//   public void setY(int y) {
//     super.setY(y);
//     this.infoButton.setY(startY + y);
//     sideConfigs.values().forEach(side -> side.setY(side.getInitialY() + y));
//   }
//
//   @Override
//   public boolean mouseClicked(double mouseX, double mouseY, int button) {
//     if (!opened) return super.mouseClicked(mouseX, mouseY, button);
//     this.sideConfigs.values().forEach(v -> v.setFocused(false));
//     return this.sideConfigs
//         .values()
//         .stream()
//         .anyMatch(config -> config.mouseClicked(mouseX, mouseY, button));
//   }
//
//   @Override
//   public void onClick(double mouseX, double mouseY, int button) {
//     if (!this.opened) {
//       this.opened = true;
//       setFocused(true);
//     }
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     if (!opened) getIconButton().renderTooltip(GuiGraphicsExtractor, x, y);
//     else {
//       this.infoButton.renderTooltip(GuiGraphicsExtractor, x, y);
//       this.sideConfigs.values().forEach(config -> config.renderTooltip(GuiGraphicsExtractor, x, y));
//     }
//   }
//
//   @Override
//   public void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     if (!this.opened) renderNoOpen(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//     else renderOpen(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//   }
//
//   private void renderOpen(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     Icon.AUTOIO_TAB.getBlitter().dest(this.getX() - 2, this.getY()).blit(GuiGraphicsExtractor);
//     this.infoButton.renderWidget(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//     this.sideConfigs.values().forEach(config -> config.render(GuiGraphicsExtractor, mouseX, mouseY, partialTick));
//   }
//
//   private void renderNoOpen(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     super.renderWidget(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//   }
//
//   protected static class VariableItemOrIconButton<T extends IAutoEntity<?> & ISideConfigComponent<IOSideMode>> extends ItemOrIconButton {
//     private final T entity;
//     private final Component info;
//     private final RelativeSide side;
//     @Getter
//     private final int initialX, initialY;
//     public VariableItemOrIconButton(int x, int y, OnPress onPress, T entity, RelativeSide side, Component info) {
//       super(x, y, (Item) null, onPress);
//       this.entity = entity;
//       this.side = side;
//       this.info = info;
//       this.initialX = x;
//       this.initialY = y;
//       this.setDisableBackground(false);
//       this.setRenderTooltip(true);
//     }
//
//     @Override
//     public boolean mouseClicked(double mouseX, double mouseY, int button) {
//       if (!this.clicked(mouseX, mouseY) || Minecraft.getInstance().player == null)
//         return false;
//       playDownSound(Minecraft.getInstance().getSoundManager());
//       sendClickPacket(button == 0);
//       return true;
//     }
//
//     private void sendClickPacket(boolean next) {
//       assert Minecraft.getInstance().player != null;
//       PacketDistributor.sendToServer(new CChangeIOSideConfigPacket(Minecraft.getInstance().player.containerMenu.containerId, side, next));
//     }
//
//     @Override
//     @Nullable
//     public Icon getIcon() {
//       return null;
//     }
//
//     @Override
//     public List<Component> getTooltips() {
//       var firstComponentTemplate = "%s: %s";
//       Component firstComponent = side.getTranslationName();
//       if (getBlockName() != null) {
//         firstComponent = Component.translatable(firstComponentTemplate, side.getTranslationName(), getBlockName());
//       }
//       return List.of(
//           firstComponent,
//           info,
//           Component.translatable(
//               "mmr.gui.tooltip.auto_output.change",
//               Component.translatable("mmr.gui.tooltip.enabled." + entity.getConfig().getSideMode(side).isEnabled()).withStyle(ChatFormatting.AQUA),
//               Component.translatable("mmr.gui.tooltip.enabled." + entity.getConfig().getSideMode(side).isDisabled()).withStyle(ChatFormatting.AQUA)
//           )
//       );
//     }
//
//     private boolean isEnabled() {
//       return this.entity.getConfig().getSideMode(this.side).isEnabled();
//     }
//
//     @Override
//     public void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partial) {
//       if (this.visible) {
//         // Icon icon = this.getIcon();
//         if (this.isHalfSize()) {
//           this.width = getWidth() / 2;
//           this.height = getHeight() / 2;
//         }
//
//         int yOffset = this.isHovered() ? 1 : 0;
//         if (this.isHalfSize()) {
//           if (!this.isDisableBackground()) {
//             Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(this.getX(), this.getY()).zOffset(10).blit(GuiGraphicsExtractor);
//           }
//
//           renderBlockItem(GuiGraphicsExtractor, mouseX, mouseY, partial);
//         } else {
//           if (!this.isDisableBackground()) {
//             Icon bgIcon = this.isHovered()
//                 ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
//                 : (isEnabled() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND);
//             bgIcon.getBlitter().dest(this.getX() - 1, this.getY() + yOffset, configWidth, configHeight).zOffset(2).blit(GuiGraphicsExtractor);
//           }
//
//           renderBlockItem(GuiGraphicsExtractor, mouseX, mouseY, partial);
//         }
//       }
//     }
//
//     private @Nullable Component getBlockName() {
//       BlockPos pos = entity.getBlockPos();
//       if (Minecraft.getInstance().player == null) return null;
//       var direction = side.getDirection(entity.getControllerFacing());
//       var relativePos = pos.relative(direction);
//       if (entity.getLevel() == null) return null;
//       var blockstate = entity.getLevel().getBlockState(relativePos);
//       if (blockstate.isAir()) return null;
//       Optional<MachineControllerEntity> controller = Optional.empty();
//       if (blockstate.getBlock() instanceof BlockController) {
//         controller = Optional.ofNullable((MachineControllerEntity) entity.getLevel().getBlockEntity(relativePos));
//       }
//       return controller
//           .map(c -> c.getFoundMachine().getName())
//           .orElse(blockstate.getBlock().getName());
//     }
//
//     private void renderBlockItem(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partial) {
//       BlockPos pos = entity.getBlockPos();
//       if (Minecraft.getInstance().player == null) return;
//       var direction = side.getDirection(entity.getControllerFacing());
//       var relativePos = pos.relative(direction);
//       if (entity.getLevel() == null) return;
//
//       var blockstate = entity.getLevel().getBlockState(relativePos);
//       if (blockstate.isAir()) return;
//       var item = blockstate.getBlock();
//
//       new ItemOrIconButton(
//           getX(),
//           getY(),
//           item,
//           b -> {}
//       ).setDisableBackground(true)
//           .renderTooltip(false)
//           .setHalfSize(true)
//           .renderWidget(GuiGraphicsExtractor, mouseX, mouseY, partial);
//     }
//   }
// }
//
