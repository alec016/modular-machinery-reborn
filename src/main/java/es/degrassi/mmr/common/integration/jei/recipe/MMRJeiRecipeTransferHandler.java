package es.degrassi.mmr.common.integration.jei.recipe;

/**
 * LEGACY: JEI recipe transfer into the controller. Its registration in
 * {@code MMRJeiPlugin#registerRecipeTransferHandlers} was already commented out because it
 * depended on {@code MenuType<ControllerContainer>} and the removed menu types.
 * Kept as a shell with the original implementation commented out below.
 */
public class MMRJeiRecipeTransferHandler {}

// ==== original implementation, commented out ====
// package es.degrassi.mmr.common.integration.jei.recipe;
// 
// import com.google.common.collect.Lists;
// import com.google.common.collect.Maps;
// import es.degrassi.mmr.ModularMachineryReborn;
// import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
// import es.degrassi.mmr.client.container.ControllerContainer;
// import es.degrassi.mmr.common.crafting.MachineRecipe;
// import es.degrassi.mmr.common.crafting.requirement.RequirementItem;
// import es.degrassi.mmr.common.machine.IOType;
// import es.degrassi.mmr.common.machine.component.ItemComponent;
// import es.degrassi.mmr.common.network.client.emi.FillRecipeC2SPacket;
// import es.degrassi.mmr.common.registration.ContainerRegistration;
// import es.degrassi.mmr.common.util.MMRLogger;
// import it.unimi.dsi.fastutil.objects.Object2IntMap;
// import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
// import mezz.jei.api.constants.VanillaTypes;
// import mezz.jei.api.gui.ingredient.IRecipeSlotView;
// import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
// import mezz.jei.api.recipe.RecipeIngredientRole;
// import mezz.jei.api.recipe.transfer.IRecipeTransferError;
// import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
// import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
// import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
// import mezz.jei.api.recipe.types.IRecipeType;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.util.context.ContextMap;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.inventory.MenuType;
// import net.minecraft.world.inventory.Slot;
// import net.minecraft.world.item.ItemStack;
// import net.neoforged.neoforge.client.network.ClientPacketDistributor;
// import net.neoforged.neoforge.common.crafting.SizedIngredient;
// import org.jetbrains.annotations.Nullable;
// 
// import javax.annotation.ParametersAreNonnullByDefault;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// 
// @ParametersAreNonnullByDefault
// public class MMRJeiRecipeTransferHandler implements IRecipeTransferHandler<ControllerContainer, MachineRecipe>, IRecipeTransferInfo<ControllerContainer, MachineRecipe> {
//   private final IRecipeType<MachineRecipe> type;
//   private final IRecipeTransferHandlerHelper helper;
//   public static final Component NO_ITEMS = Component.translatable("mmr.jei.no_items");
//   public static final Component ERROR_INGREDIENT = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
// 
//   private static final Map<ControllerContainer, List<Slot>> slots = Maps.newHashMap();
// 
//   public MMRJeiRecipeTransferHandler(Identifier machine, IRecipeTransferHandlerHelper helper,
//                                      IRecipeType<MachineRecipe> type) {
//     this.type = type;
//     this.helper = helper;
//   }
// 
//   @Override
//   public Class<? extends ControllerContainer> getContainerClass() {
//     return ControllerContainer.class;
//   }
// 
//   @Override
//   public Optional<MenuType<ControllerContainer>> getMenuType() {
//     return Optional.of(ContainerRegistration.CONTROLLER.get());
//   }
// 
//   @Override
//   public IRecipeType<MachineRecipe> getRecipeType() {
//     return type;
//   }
// 
//   @Override
//   public boolean canHandle(ControllerContainer container, MachineRecipe recipe) {
//     return true;
//   }
// 
//   @Override
//   public @Nullable IRecipeTransferError transferRecipe(ControllerContainer container, MachineRecipe recipe, IRecipeSlotsView recipeSlots, Player player,
//                                                        boolean maxTransfer, boolean doTransfer) {
//     ModularMachineryReborn.LOGGER.debug("trying to transfer recipe...");
//     if (!helper.recipeTransferHasServerSupport()) return helper.createUserErrorWithTooltip(Component.translatable("jei.tooltip.error.recipe.transfer.no.server"));
// 
//     ModularMachineryReborn.LOGGER.debug("jei is on server to fill recipe correctly");
//     if (!canHandle(container, recipe)) {
//       ModularMachineryReborn.LOGGER.debug("this is not a valid recipe for actual menu");
//       IRecipeTransferError handlingError = getHandlingError(container, recipe);
//       if (handlingError != null) {
//         return handlingError;
//       }
//       return helper.createInternalError();
//     }
//     List<ItemStack> stacks = getStacks(recipe, container, 1);
//     ModularMachineryReborn.LOGGER.debug("getted stacks to process: {}", stacks.stream().map(ItemStack::getDisplayName).map(Component::getString).toList());
//     if (!stacks.isEmpty()) {
//       ModularMachineryReborn.LOGGER.debug("trying to actually fill recipe");
//       return clientFill(recipe, container, stacks, recipeSlots, doTransfer);
//     }
//     return helper.createInternalError();
//   }
// 
//   public void sendFillRecipe(ControllerContainer screenHandler, int action, List<ItemStack> stacks, MachineRecipe recipe) {
//     ClientPacketDistributor.sendToServer(new FillRecipeC2SPacket(
//         screenHandler,
//         action,
//         getInventorySlots(screenHandler, recipe),
//         getRecipeSlots(screenHandler, recipe),
//         null,
//         stacks
//     ));
//   }
// 
//   public @Nullable IRecipeTransferError clientFill(MachineRecipe recipe,
//                                                    ControllerContainer container,
//                                                    List<ItemStack> stacks,
//                                                    IRecipeSlotsView recipeSlots,
//                                                    boolean doTransfer
//   ) {
//     List<IRecipeSlotView> filteredSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT)
//         .stream()
//         .filter(slot -> slot
//             .getAllIngredients()
//             .allMatch(ing -> ing
//                 .getType()
//                 .equals(VanillaTypes.ITEM_STACK)
//             )
//         )
//         .toList();
// 
//     if (filteredSlots.isEmpty()) {
//       ModularMachineryReborn.LOGGER.debug("no items in the current recipe");
//       return helper.createUserErrorWithTooltip(NO_ITEMS);
//     }
// 
//     List<IRecipeSlotView> errorSlots = Lists.newArrayList();
//     List<SizedIngredient> ingredients = recipe.getRequirements().stream()
//         .map(RecipeRequirement::requirement)
//         .filter(req -> req instanceof RequirementItem)
//         .map(req -> (RequirementItem) req)
//         .map(RequirementItem::getIngredient)
//         .toList();
// 
//     filteredSlots.forEach(slot -> {
//       if (ingredients.stream().noneMatch(ingredient -> slot.getItemStacks().noneMatch(ingredient::test)))
//         errorSlots.add(slot);
//     });
// 
//     if (!errorSlots.isEmpty()) {
//       ModularMachineryReborn.LOGGER.debug("recipe has missing ingredients, sending slots with missing ingredients...");
//       return helper.createUserErrorForMissingSlots(ERROR_INGREDIENT, errorSlots);
//     }
// 
//     if (doTransfer) {
//       ModularMachineryReborn.LOGGER.debug("sending packet to fill recipe...");
//       sendFillRecipe(container, 2, stacks, recipe);
//     }
//     return null;
//   }
// 
//   public List<ItemStack> getStacks(MachineRecipe recipe, ControllerContainer screenHandler, int amount) {
//     List<ItemStack> stacks = Lists.newArrayList();
//     try {
//       screenHandler.getEntity().getComponentManager()
//           .getItemComponent(IOType.INPUT)
//           .map(ItemComponent::getContainerProvider)
//           .ifPresent(inventory -> {
//             Map<SizedIngredient, Boolean> ingredientsMap = Maps.newHashMap();
//             recipe.getRecipeRequirements()
//                 .stream()
//                 .map(RecipeRequirement::requirement)
//                 .filter(req -> req instanceof RequirementItem)
//                 .map(req -> (RequirementItem) req)
//                 .forEach(req -> {
//                   ingredientsMap.put(req.getIngredient(), req.isUsesDataComponents());
//                 });
//             Player player = screenHandler.getPlayer();
//             List<SizedIngredient> ingredients = ingredientsMap.keySet().stream().toList();
//             List<Slot> slots = inventory.createSlots(player);
//             List<Slot> crafting = inventory.createInventorySlots(Lists.newArrayList(), 0);
//             ModularMachineryReborn.LOGGER.debug("slots: {}", slots);
//             ModularMachineryReborn.LOGGER.debug("craftingSlots: {}", crafting);
//             List<DiscoveredItem> discovered = Lists.newArrayList();
//             Object2IntMap<ItemStack> weightDivider = new Object2IntOpenHashMap<>();
//             for (int i = 0; i < ingredients.size(); i++) {
//               List<DiscoveredItem> d = Lists.newArrayList();
//               SizedIngredient ingredient = ingredients.get(i);
//               var itemStacks = ingredient.ingredient().display().resolveForStacks(ContextMap.EMPTY);
//               if (ingredient.ingredient().isEmpty()) {
//                 discovered.add(null);
//                 continue;
//               }
//               for (ItemStack stack : itemStacks) {
//                 slotLoop:
//                 for (Slot slot : slots) {
//                   ItemStack is = slot.getItem();
//                   if (ItemStack.matches(is, stack)) {
//                     for (DiscoveredItem di : d) {
//                       if (ingredientsMap.get(ingredient) ? ItemStack.isSameItemSameComponents(is, di.stack) : ItemStack.isSameItem(is, di.stack)) {
//                         di.amount += is.getCount();
//                         continue slotLoop;
//                       }
//                     }
//                     d.add(new DiscoveredItem(stack, is, is.getCount(), ingredient.count(), is.getMaxStackSize(), ingredientsMap.get(ingredient)));
//                   }
//                 }
//               }
//               DiscoveredItem biggest = null;
//               for (DiscoveredItem di : d) {
//                 if (biggest == null) biggest = di;
//                 else {
//                   int a = di.amount / (weightDivider.getOrDefault(di.ingredient, 0) + di.consumed);
//                   int ba = biggest.amount / (weightDivider.getOrDefault(biggest.ingredient, 0) + biggest.consumed);
//                   if (ba < a) {
//                     biggest = di;
//                   }
//                 }
//               }
//               if (biggest == null || i >= crafting.size()) return;
//               Slot slot = crafting.get(i);
//               if (slot == null) return;
//               weightDivider.put(biggest.ingredient,
//                   weightDivider.getOrDefault(biggest.ingredient, 0) + biggest.consumed);
//               biggest.max = Math.min(biggest.max, slot.getItem().getMaxStackSize());
//               discovered.add(biggest);
//             }
//             if (discovered.isEmpty()) return;
// 
//             List<DiscoveredItem> unique = Lists.newArrayList();
//             outer:
//             for (DiscoveredItem di : discovered) {
//               if (di == null) continue;
//               for (DiscoveredItem ui : unique) {
//                 if (ItemStack.isSameItemSameComponents(di.stack, ui.stack)) {
//                   ui.consumed += di.consumed;
//                   continue outer;
//                 }
//               }
//               unique.add(new DiscoveredItem(di.ingredient, di.stack, di.amount, di.consumed, di.max, di.components));
//             }
//             int maxAmount = Integer.MAX_VALUE;
//             for (DiscoveredItem ui : unique) {
//               maxAmount = Math.min(maxAmount, Math.min(ui.amount / ui.consumed, ui.max));
//             }
//             maxAmount = Math.min(maxAmount, amount + batchesAlreadyPresent(recipe, screenHandler));
//             if (maxAmount == 0) return;
//             for (DiscoveredItem di : discovered) {
//               if (di != null) {
//                 ItemStack is = di.stack.copy();
//                 int a = di.consumed * maxAmount;
//                 is.setCount(a);
//                 stacks.add(is);
//               } else stacks.add(ItemStack.EMPTY);
//             }
//           });
//     } catch (Exception e) {
//       MMRLogger.INSTANCE.error(e);
//     }
//     return stacks;
//   }
// 
//   public int batchesAlreadyPresent(MachineRecipe recipe, ControllerContainer screen) {
//     List<SizedIngredient> ingredients = recipe.getRecipeRequirements()
//         .stream()
//         .map(RecipeRequirement::requirement)
//         .filter(req -> req instanceof RequirementItem)
//         .map(req -> (RequirementItem) req)
//         .map(RequirementItem::getIngredient)
//         .toList();
//     List<ItemStack> stacks = Lists.newArrayList();
//     for (Slot slot : getRecipeSlots(screen, recipe)) {
//       if (slot != null)
//         stacks.add(slot.getItem());
//       else
//         stacks.add(ItemStack.EMPTY);
//     }
//     long amount = Long.MAX_VALUE;
//     outer:
//     for (int i = 0; i < ingredients.size(); i++) {
//       SizedIngredient input = ingredients.get(i);
//       if (input.ingredient().isEmpty()) {
//         if (stacks.get(i).isEmpty()) continue;
//         return 0;
//       }
//       if (i >= stacks.size()) return 0;
//       ItemStack es = stacks.get(i);
//       for (ItemStack v : input.ingredient().display().resolveForStacks(ContextMap.EMPTY)) {
//         if (v.isEmpty()) continue;
//         if (ItemStack.matches(v, es) && es.getCount() >= v.getCount()) {
//           amount = Math.min(amount, es.getCount() / v.getCount());
//           continue outer;
//         }
//       }
//       return 0;
//     }
//     if (amount < Long.MAX_VALUE && amount > 0) {
//       return (int) amount;
//     }
//     return 0;
//   }
// 
//   private static class DiscoveredItem {
//     public ItemStack ingredient;
//     public ItemStack stack;
//     public int consumed;
//     public int amount;
//     public int max;
//     public boolean components;
// 
//     public DiscoveredItem(ItemStack ingredient, ItemStack stack, int amount, int consumed, int max, boolean components) {
//       this.ingredient = ingredient;
//       this.stack = stack.copy();
//       this.amount = amount;
//       this.consumed = consumed;
//       this.max = max;
//       this.components = components;
//     }
//   }
// 
//   public static List<Slot> getSlots(ControllerContainer handler) {
//     return slots.computeIfAbsent(handler, MMRJeiRecipeTransferHandler::createSlots);
//   }
// 
//   @Override
//   public List<Slot> getRecipeSlots(ControllerContainer container, MachineRecipe recipe) {
//     List<Slot> slots = getSlots(container);
//     return slots.subList(36, slots.size());
//   }
// 
//   @Override
//   public List<Slot> getInventorySlots(ControllerContainer container, MachineRecipe recipe) {
//     return getSlots(container);
//   }
// 
//   private static List<Slot> createSlots(ControllerContainer handler) {
//     return handler.getEntity()
//         .getComponentManager()
//         .getItemComponent(IOType.INPUT)
//         .map(ItemComponent::getContainerProvider)
//         .map(inventory -> inventory.createSlots(handler.getPlayer()))
//         .stream()
//         .flatMap(List::stream)
//         .toList();
//   }
// }
// 
