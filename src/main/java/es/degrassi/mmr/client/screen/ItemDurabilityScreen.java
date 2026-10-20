package es.degrassi.mmr.client.screen;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ItemDurabilityScreen {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen;
//
// import es.degrassi.mmr.client.container.ItemDurabilityContainer;
// import es.degrassi.mmr.client.screen.widget.tabs.AutoInputTabWidget;
// import es.degrassi.mmr.client.screen.widget.tabs.ITabGroupScreen;
// import es.degrassi.mmr.client.screen.widget.tabs.TabGroupWidget;
// import es.degrassi.mmr.common.entity.DurabilityHatchEntity;
// import lombok.Getter;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.network.chat.Component;
// import net.minecraft.world.entity.player.Inventory;
//
// @Getter
// public class ItemDurabilityScreen extends BaseScreen<ItemDurabilityContainer, DurabilityHatchEntity> implements ITabGroupScreen {
//   private TabGroupWidget tabs;
//   public ItemDurabilityScreen(ItemDurabilityContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
//     super(pMenu, pPlayerInventory, pTitle, false);
//   }
//
//   @Override
//   protected void init() {
//     super.init();
//
//     tabs = TabGroupWidget.createRight(getGuiLeft() + getXSize() + 2, getGuiTop());
//     tabs.addTab(new AutoInputTabWidget<>(this.entity));
//
//     addRenderableWidget(tabs);
//   }
//
//   @Override
//   protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
//     renderBgWithSlotSize(GuiGraphicsExtractor, getMenu().getEntity().getSize().cols, getMenu().getEntity().getSlots());
//     renderSlots(GuiGraphicsExtractor);
//   }
// }
//
