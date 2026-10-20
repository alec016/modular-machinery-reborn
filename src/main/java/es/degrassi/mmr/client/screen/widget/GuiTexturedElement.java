package es.degrassi.mmr.client.screen.widget;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public abstract class GuiTexturedElement {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget;
//
// import es.degrassi.mmr.client.screen.BaseScreen;
// import lombok.AccessLevel;
// import lombok.Getter;
// import net.minecraft.resources.Identifier;
//
// public abstract class GuiTexturedElement extends GuiElement {
//   @Getter(AccessLevel.PROTECTED)
//   protected final Identifier resource;
//
//   public GuiTexturedElement(Identifier resource, BaseScreen<? ,?> gui, int x, int y, int width, int height) {
//     super(gui, x, y, width, height);
//     this.resource = resource;
//   }
// }
//
