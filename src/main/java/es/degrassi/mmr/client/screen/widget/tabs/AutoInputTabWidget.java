package es.degrassi.mmr.client.screen.widget.tabs;

import es.degrassi.mmr.api.entity.IAutoEntity;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class AutoInputTabWidget<T extends IAutoEntity<?>> {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import es.degrassi.mmr.api.capability.config.IOSideMode;
// import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
// import es.degrassi.mmr.api.entity.IAutoEntity;
// import es.degrassi.mmr.api.entity.IAutoInputEntity;
// import net.minecraft.network.chat.Component;
//
// public class AutoInputTabWidget<T extends IAutoEntity<?> & IAutoInputEntity & ISideConfigComponent<IOSideMode>> extends AutoTabWidget<T> {
//   public AutoInputTabWidget(T entity) {
//     super(entity, Component.translatable("mmr.gui.tooltip.auto_input"));
//   }
// }
//
