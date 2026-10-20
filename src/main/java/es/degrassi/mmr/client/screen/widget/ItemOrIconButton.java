package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ItemOrIconButton {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import com.google.common.collect.Lists;
// import es.degrassi.mmr.api.client.Blitter;
// import es.degrassi.mmr.api.client.Icon;
// import es.degrassi.mmr.api.client.screen.TooltipRender;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.experimental.Accessors;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.Button;
// import net.minecraft.client.renderer.Rect2i;
// import net.minecraft.client.sounds.SoundManager;
// import net.minecraft.network.chat.Component;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.level.ItemLike;
// import org.jetbrains.annotations.Nullable;
//
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
//
// @Getter
// @Setter
// @Accessors(chain = true)
// @ParametersAreNonnullByDefault
// public class ItemOrIconButton extends Button implements TooltipRender {
//   private boolean halfSize = false;
//   private boolean disableClickSound = false;
//   private boolean disableBackground = false;
//   @Nullable
//   private final ItemLike item;
//   @Nullable
//   private final Icon icon;
//
//   private boolean renderTooltip = true;
//   private List<Component> tooltips;
//
//   public ItemOrIconButton(int x, int y, OnPress onPress) {
//     super(x, y, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
//     this.item = null;
//     this.icon = null;
//   }
//
//   public ItemOrIconButton(int x, int y, ItemLike item, OnPress onPress) {
//     super(x, y, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
//     this.item = item;
//     this.icon = null;
//   }
//
//   public ItemOrIconButton(int x, int y, Icon icon, OnPress onPress) {
//     super(x, y, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
//     this.item = null;
//     this.icon = icon;
//   }
//
//   public ItemOrIconButton(Builder builder) {
//     super(builder);
//     this.item = null;
//     this.icon = null;
//   }
//
//   public ItemOrIconButton renderTooltip(boolean render) {
//     this.renderTooltip = render;
//     return this;
//   }
//
//   public ItemOrIconButton setTooltips(Component... components) {
//     this.tooltips = Lists.newArrayList(components);
//     return this;
//   }
//
//   public ItemOrIconButton setVisibility(boolean vis) {
//     this.visible = vis;
//     this.active = vis;
//     return this;
//   }
//
//   public void playDownSound(SoundManager soundHandler) {
//     if (!this.disableClickSound) {
//       super.playDownSound(soundHandler);
//     }
//   }
//
//   public void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partial) {
//     if (this.visible) {
//       Icon icon = this.getIcon();
//       ItemLike item = this.getItem();
//       if (this.halfSize) {
//         this.width = getWidth() / 2;
//         this.height = getHeight() / 2;
//       }
//
//       int yOffset = this.isHovered() ? 1 : 0;
//       if (this.halfSize) {
//         if (!this.disableBackground) {
//           Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(this.getX(), this.getY()).zOffset(10).blit(GuiGraphicsExtractor);
//         }
//
//         if (item != null) {
//           GuiGraphicsExtractor.pose().pushPose();
//           GuiGraphicsExtractor.pose().translate(getX(), getY(), 0);
//           GuiGraphicsExtractor.pose().scale(0.75f, 0.75f, 0.75f);
//           GuiGraphicsExtractor.renderItem(new ItemStack(item), (int) (getWidth() * 0.45), (int) (getHeight() * 0.65), 0, 20);
//           GuiGraphicsExtractor.pose().popPose();
//         } else if (icon != null) {
//           Blitter blitter = icon.getBlitter();
//           if (!this.active) {
//             blitter.opacity(0.5F);
//           }
//
//           blitter.dest(this.getX(), this.getY()).zOffset(20).blit(GuiGraphicsExtractor);
//         }
//       } else {
//         if (!this.disableBackground) {
//           Icon bgIcon = this.isHovered() ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER : (this.isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND);
//           bgIcon.getBlitter().dest(this.getX() - 1, this.getY() + yOffset, 18, 20).zOffset(2).blit(GuiGraphicsExtractor);
//         }
//
//         if (item != null) {
//           GuiGraphicsExtractor.renderItem(new ItemStack(item), this.getX(), this.getY() + 1 + yOffset, 0, 3);
//         } else if (icon != null) {
//           icon.getBlitter().dest(this.getX(), this.getY() + 1 + yOffset).zOffset(3).blit(GuiGraphicsExtractor);
//         }
//       }
//     }
//   }
//
//   @Override
//   public boolean isMouseOver(double mouseX, double mouseY) {
//     return getTooltipArea().contains((int) mouseX, (int) mouseY);
//   }
//
//   public List<Component> getTooltipMessage() {
//     return getTooltips();
//   }
//
//   public Rect2i getTooltipArea() {
//     return new Rect2i(this.getX(), this.getY(), this.halfSize ? getWidth() / 2 : getWidth(), this.halfSize ? getHeight() / 2 : getHeight());
//   }
//
//   public boolean isTooltipAreaVisible() {
//     return this.visible;
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
//     if (isMouseOver(mouseX, mouseY) && renderTooltip) {
//       GuiGraphicsExtractor.renderTooltip(Minecraft.getInstance().font, getTooltipMessage().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
//     }
//   }
//
//   @Override
//   public String toString() {
//     return "ItemButton{" +
//         "halfSize=" + halfSize +
//         ", disableClickSound=" + disableClickSound +
//         ", disableBackground=" + disableBackground +
//         ", item=" + (getItem() == null ? "null" : getItem()) +
//         ", icon=" + (getIcon() == null ? "null" : getIcon()) +
//         ", renderTooltip=" + renderTooltip +
//         ", x=" + getX() +
//         ", y=" + getY() +
//         ", width=" + getWidth() +
//         ", height=" + getHeight() +
//         '}';
//   }
// }
//
