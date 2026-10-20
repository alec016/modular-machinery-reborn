package es.degrassi.mmr.client.screen.widget.tabs;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
public class StructurePlacerWidget {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.client.screen.widget.tabs;
//
// import com.google.common.collect.Lists;
// import com.google.common.collect.Maps;
// import com.mojang.blaze3d.platform.InputConstants;
// import com.mojang.datafixers.util.Either;
// import com.mojang.datafixers.util.Pair;
// import es.degrassi.mmr.api.BlockIngredient;
// import es.degrassi.mmr.client.item.MMRItemTooltipComponent;
// import es.degrassi.mmr.client.screen.ControllerScreen;
// import es.degrassi.mmr.client.screen.popup.ConfirmationPopup;
// import es.degrassi.mmr.client.screen.widget.ItemOrIconButton;
// import es.degrassi.mmr.common.network.client.CPlaceStructurePacket;
// import es.degrassi.mmr.common.util.CycleTimer;
// import net.minecraft.ChatFormatting;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.GuiGraphicsExtractor;
// import net.minecraft.client.gui.screens.Screen;
// import net.minecraft.core.BlockPos;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.FormattedText;
// import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.inventory.tooltip.TooltipComponent;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.level.block.Blocks;
// import net.neoforged.neoforge.network.PacketDistributor;
// import org.lwjgl.glfw.GLFW;
//
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Optional;
// import java.util.function.Function;
// import java.util.stream.Collectors;
//
// @ParametersAreNonnullByDefault
// public class StructurePlacerWidget extends TopTabWidget {
//   private final ControllerScreen parentScreen;
//
//   private final Identifier machine;
//   private final BlockPos controllerPos;
//   private static final CycleTimer timer = new CycleTimer(() -> 1000, true);
//
//   public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_placer_button");
//
//   public StructurePlacerWidget(ControllerScreen parentScreen, Identifier machine, BlockPos controllerPos) {
//     super(0, 0, new ItemOrIconButton(5, 5, Blocks.STRUCTURE_BLOCK.asItem(), button -> {}));
//     this.parentScreen = parentScreen;
//     this.machine = machine;
//     this.controllerPos = controllerPos;
//   }
//
//   @Override
//   public void renderWidget(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
//     timer.onDraw();
//     super.renderWidget(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
//   }
//
//   @Override
//   public void onClick(double mouseX, double mouseY, int button) {
//     parentScreen.setFocused(this);
//     parentScreen.getMenu().getEntity().setLastFocus(0);
//     parentScreen.openPopup(new ConfirmationPopup<>(
//         parentScreen,
//         180,
//         96,
//         () -> parentScreen.openPopup(
//             new ConfirmationPopup<>(
//                 parentScreen,
//                 180,
//                 96,
//                 () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, true))
//             )
//                 .cancelCallback(() -> onClick(mouseX, mouseY, button))
//                 .text(Component.translatable("mmr.gui.structure.place.modifier.true"))
//         )
//     )
//         .confirmText(Component.translatable("mmr.gui.structure.place.confirm.modifier"))
//         .cancelText(Component.translatable("mmr.gui.structure.place.cancel.modifier"))
//         .text(Component.translatable("mmr.gui.structure.place.modifier"))
//         .addCloseButton()
//         .cancelCallback(() -> parentScreen.openPopup(
//                 new ConfirmationPopup<>(
//                     parentScreen,
//                     180,
//                     96,
//                     () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, false))
//                 )
//                     .cancelCallback(() -> onClick(mouseX, mouseY, button))
//                     .text(Component.translatable("mmr.gui.structure.place.modifier.false"))
//             )
//         ), "popup");
//   }
//
//   @Override
//   public void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
//     super.renderTooltip(GuiGraphicsExtractor, x, y);
//     List<Either<FormattedText, TooltipComponent>> components = Lists.newArrayList();
//     gatherComponents(components);
//     GuiGraphicsExtractor.renderComponentTooltipFromElements(
//           Minecraft.getInstance().font,
//           components,
//           x,
//           y,
//           ItemStack.EMPTY
//       );
//   }
//
//   public void gatherComponents(List<Either<FormattedText, TooltipComponent>> components) {
//     components.add(Either.left(component));
//     Optional.of(parentScreen.getMenu().getEntity().getFoundMachine())
//         .ifPresentOrElse(machine -> {
//               if (Screen.hasShiftDown() && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_N)) {
//                 machine.getPattern().getMinBlocksPredicate()
//                     .minBlocks()
//                     .forEach((key, value) -> {
//                       components.add(Either.left(Component.translatable(
//                           "modular_machinery_reborn.controller.required.block",
//                           key.getNamesUnified().append(value.guiText())
//                       ).withStyle(ChatFormatting.GRAY)));
//                     });
//               } else if (Screen.hasShiftDown()) {
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
//                     Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
//                         .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY)));
//                 Map<MutableComponent, List<ItemStack>> map = Maps.newHashMap();
//                 machine.getPattern()
//                     .getPattern()
//                     .asList()
//                     .stream()
//                     .flatMap(List::stream)
//                     .flatMap(s -> s.chars().mapToObj(c -> (char) c))
//                     .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                     .entrySet()
//                     .stream()
//                     .map(entry -> {
//                       BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(entry.getKey());
//                       if (ingredient == null) return null;
//                       return Pair.of(ingredient.getStacks(entry.getValue().intValue()), ingredient.getNamesUnified());
//                     })
//                     .filter(Objects::nonNull)
//                     .forEachOrdered(pair -> {
//                       List<ItemStack> stacks = pair.getFirst();
//                       MutableComponent component = pair.getSecond();
//                       List<ItemStack> s2 = Lists.newArrayList();
//                       if (map.containsKey(component)) {
//                         stacks.forEach(stack -> map.forEach((c, s) -> {
//                           if (c.getString().equals(component.getString())) {
//                             s.forEach(s1 -> {
//                               if (s1.is(stack.getItem())) {
//                                 s1.grow(stack.getCount());
//                               }
//                               s2.add(s1);
//                             });
//                           }
//                         }));
//                       } else {
//                         s2.addAll(stacks);
//                       }
//
//                       map.put(component, s2);
//                     });
//                 map.forEach((c, stacks) -> {
//                   if (c != null && !stacks.isEmpty()) {
//                     MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
//                     component.setComponent(
//                         Component.translatable(
//                             "modular_machinery_reborn.controller.required.block",
//                             c
//                         ).withStyle(ChatFormatting.GRAY)
//                     );
//                     components.add(Either.right(component));
//                   }
//                 });
//               } else {
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
//                     Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
//                         .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
//                     Component.translatable("modular_machinery_reborn.controller.required.shift")
//                         .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
//               }
//
//               if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.modifier").withStyle(ChatFormatting.GRAY)));
//                 Map<MutableComponent, List<ItemStack>> modifierMap = Maps.newHashMap();
//                 machine.getPattern()
//                     .getPattern()
//                     .getModifiers()
//                     .forEach(ingredient -> {
//                       MutableComponent component = Component.empty();
//                       ingredient.getDescriptionLines().forEach(component::append);
//                       modifierMap.put(component, ingredient.getIngredient().getStacks(1));
//                     });
//
//                 if (!modifierMap.isEmpty()) {
//                   modifierMap.forEach((c, stacks) -> {
//                     if (c != null && !stacks.isEmpty()) {
//                       MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
//                       component.setComponent(
//                           Component.translatable(
//                               "modular_machinery_reborn.controller.required.block",
//                               c
//                           ).withStyle(ChatFormatting.GRAY)
//                       );
//                       components.add(Either.right(component));
//                     }
//                   });
//                 }
//               } else {
//                 components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
//                     Component.translatable("modular_machinery_reborn.controller.required.control").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
//               }
//             },
//             () -> components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY))));
//   }
// }
//
