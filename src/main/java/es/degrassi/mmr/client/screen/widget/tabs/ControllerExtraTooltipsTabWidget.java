package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class ControllerExtraTooltipsTabWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import com.google.common.collect.Lists;
// import com.mojang.datafixers.util.Either;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.client.Icon;
// import es.degrassi.mmr.api.client.machine.TooltipUse;
// import es.degrassi.mmr.client.container.ControllerContainer;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.data.MMRConfig;
// import es.degrassi.mmr.common.integration.kubejs.CKubeJSIntegration;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.FormattedText;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.inventory.tooltip.TooltipComponent;
// import net.minecraft.world.item.ItemStack;
//
// import java.util.List;
// import java.util.Optional;
//
// public class ControllerExtraTooltipsTabWidget extends TopTabWidget{
//   private final Identifier machineId;
//   private final ControllerContainer container;
//   private final List<Either<Identifier, Component>> tooltipList;
//
//   public ControllerExtraTooltipsTabWidget(Identifier id, ControllerContainer container) {
//     super(0, 0, new ItemOrIconButton(5, 5, Icon.HELP, b -> {}));
//     this.machineId = id;
//     this.container = container;
//     var enumTooltips = ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(machineId);
//     tooltipList = Optional.ofNullable(enumTooltips).map(map -> map.get(TooltipUse.GUI)).orElse(Lists.newArrayList());
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     List<Either<FormattedText, TooltipComponent>> components = Lists.newArrayList();
//     if (container.needFullSync(MMRConfig.get().dynamicTooltipTicks.get()))
//       CKubeJSIntegration.collectDynamicTooltip(container.containerId);
//     gatherComponents(components);
//     container.dynamicTooltips.forEach((id, comp) -> components.add(Either.left(comp)));
//     GuiGraphicsExtractor.renderComponentTooltipFromElements(
//         Minecraft.getInstance().font,
//         components,
//         x,
//         y,
//         ItemStack.EMPTY
//     );
//   }
// }
//
