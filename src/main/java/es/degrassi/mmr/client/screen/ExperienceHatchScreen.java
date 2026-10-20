package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ExperienceHatchScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import com.google.common.collect.Maps;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.ExperienceButtonType;
// import es.degrassi.mmr.client.container.ExperienceHatchContainer;
// import es.degrassi.mmr.client.screen.widget.ExperienceButton;
// import es.degrassi.mmr.client.screen.widget.ExperienceWidget;
// import es.degrassi.mmr.client.screen.widget.GuiElement;
// import es.degrassi.mmr.client.screen.widget.IGuiWrapper;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoInputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoOutputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ITabGroupScreen;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.client.util.GuiUtils;
// import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
// import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
// import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
// import es.degrassi.mmr.common.network.client.CExperienceButtonClickedPacket;
// import lombok.Getter;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.components.events.GuiEventListener;
// import net.minecraft.client.input.CharacterEvent;
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.entity.player.Inventory;
// import net.neoforged.neoforge.client.network.ClientPacketDistributor;
// import org.jetbrains.annotations.Nullable;
// import org.lwjgl.glfw.GLFW;
//
// import java.util.Map;
// import java.util.concurrent.atomic.AtomicInteger;
//
// public class ExperienceHatchScreen extends BaseScreen<ExperienceHatchContainer, ExperienceHatchEntity> implements IGuiWrapper, ITabGroupScreen {
//   private ExperienceWidget experienceWidget;
//   @Getter
//   private TabGroupWidget tabs;
//   private final Map<ExperienceButtonType, ExperienceButton> experienceButtons = Maps.newEnumMap(ExperienceButtonType.class);
//
//   public ExperienceHatchScreen(ExperienceHatchContainer menu, Inventory playerInventory, Component title) {
//     super(menu, playerInventory, title, false);
//   }
//
//   @Override
//   @Nullable
//   public Identifier getTexture() {
//     return ModularMachineryReborn.rl("textures/gui/guiexperience.png");
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//
//     tabs = TabGroupWidget.createRight(getLeftPos() + getImageWidth(), getTopPos());
//     if (this.entity.getMode().isInput()) tabs.addTab(new AutoInputTabWidget<>((ExperienceInputHatchEntity)this.entity));
//     else tabs.addTab(new AutoOutputTabWidget<>((ExperienceOutputHatchEntity)this.entity));
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     super.renderBg(GuiGraphicsExtractor, partialTick, mouseX, mouseY);
//     clearWidgets();
//     experienceWidget = addRenderableWidget(new ExperienceWidget(getLeftPos(), getTopPos() + 20, imageWidth,
//         getMenu().getEntity()));
//     AtomicInteger x = new AtomicInteger(8 + getLeftPos());
//     if (getMenu().getEntity() instanceof ExperienceInputHatchEntity e) {
//       for (ExperienceButtonType type : ExperienceButtonType.insertions()) {
//         experienceButtons.put(
//             type,
//             new ExperienceButton(
//                 x.getAndAdd(18),
//                 45 + getTopPos(),
//                 t -> ClientPacketDistributor.sendToServer(new CExperienceButtonClickedPacket(e.getBlockPos(), t, t.extract())),
//                 type
//             )
//         );
//
//         addRenderableWidget(experienceButtons.get(type));
//       }
//       x.getAndAdd(16);
//     } else if (getMenu().getEntity() instanceof ExperienceOutputHatchEntity e) {
//       x.set(getLeftPos() + imageWidth - 8 - 17);
//       for (ExperienceButtonType type : ExperienceButtonType.extractions().reversed()) {
//         experienceButtons.put(
//             type,
//             new ExperienceButton(
//                 x.getAndAdd(-18),
//                 45 + getTopPos(),
//                 t -> ClientPacketDistributor.sendToServer(new CExperienceButtonClickedPacket(e.getBlockPos(), t,
//                     t.extract())),
//                 type
//             )
//         );
//
//         addRenderableWidget(experienceButtons.get(type));
//       }
//     }
//
//     if (tabs != null)
//       addRenderableWidget(tabs);
//     renderSlots(GuiGraphicsExtractor);
//   }
//
//   @Override
//   protected void renderTooltip(GuiGraphicsExtractor graphics, int x, int y) {
//     super.renderTooltip(graphics, x, y);
//
//     if (experienceWidget.isHovered()) {
//       graphics.setTooltipForNextFrame(font, experienceWidget.getTooltipMessage(), x, y);
//     }
//
//     for (ExperienceButton button : experienceButtons.values()) {
//       if (button.isHovered()) {
//         graphics.setTooltipForNextFrame(font, button.getTooltipMessage().stream().map(Component::getVisualOrderText).toList(), x, y);
//       }
//     }
//   }
//
//   @Override
//   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
//     hasClicked = true;
//     for (var element : children()) {
//       if (element instanceof TabGroupWidget widget) {
//         if (widget.mouseClicked(event, doubleClick)) return true;
//       }
//     }
//     GuiEventListener clickedChild = GuiUtils.findChild(children(), event, doubleClick, GuiEventListener::mouseClicked);
//
//     if (clickedChild != null) {
//       setFocused(clickedChild);
//       if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
//         setDragging(true);
//       }
//       return super.mouseClicked(event, doubleClick);
//     } else {
//       //If we can't find a child, allow clearing whatever focus we currently have
//       clearFocus();
//     }
//     return super.mouseClicked(event, doubleClick);
//   }
//
//   @Override
//   public boolean mouseReleased(MouseButtonEvent event) {
//     if (hasClicked) {
//       // always pass mouse released events to windows for drag checks
//       return super.mouseReleased(event);
//     }
//     return false;
//   }
//
//   @Override
//   public boolean keyPressed(KeyEvent event) {
//     return GuiUtils.checkChildren(children(), event,
//         (child, k) -> child instanceof GuiElement && child.keyPressed(k)) ||
//         super.keyPressed(event);
//   }
//
//   @Override
//   public boolean charTyped(CharacterEvent event) {
//     return GuiUtils.checkChildrenChar(children(), event,
//         (child, ev) -> child instanceof GuiElement && child.charTyped(ev)) || super.charTyped(event);
//   }
// }
//
