package es.degrassi.mmreborn.common.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.api.client.machine.TooltipUse;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.integration.kubejs.KubeJSIntegration;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.BlockRegistration;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ControllerItem extends ItemBlockMachineComponent {
  public static final ResourceLocation DUMMY = ModularMachineryReborn.rl("dummy");

  private static final Map<ResourceLocation, List<Component>> CACHED_COMPONENTS = Maps.newHashMap();

  public ControllerItem() {
    super(
        BlockRegistration.CONTROLLER.get(),
        new Properties()
            .component(DataComponentRegistration.MACHINE_DATA, DUMMY)
    );
  }

  public static Optional<DynamicMachine> getMachine(ItemStack stack) {
    return Optional.ofNullable(stack.get(DataComponentRegistration.MACHINE_DATA)).flatMap(id -> Optional.ofNullable(ModularMachineryReborn.MACHINES.get(id))).or(() -> Optional.of(DynamicMachine.DUMMY));
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.tooltip.0"));
    tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.tooltip.1"));
    getMachine(stack).ifPresentOrElse(machine -> {
      if (tooltipFlag.hasAltDown()) {
        machine.getPattern().getMinBlocksPredicate()
            .minBlocks()
            .forEach((key, value) -> {
              tooltipComponents.add(Component.translatable(
                  "modular_machinery_reborn.controller.required.block",
                  key.getNamesUnified().append(value.guiText())
              ).withStyle(ChatFormatting.GRAY));
            });
      } else if (tooltipFlag.hasShiftDown()) {
        tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY));
        machine.getPattern()
            .getPattern()
            .asList()
            .stream()
            .flatMap(List::stream)
            .flatMap(s -> s.chars().mapToObj(c -> (char) c))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .map(entry -> {
              BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(entry.getKey());
              if (ingredient == null) return null;
              HashMap<BlockIngredient, Long> ing = new HashMap<>();
              ing.put(ingredient, entry.getValue());
              return ing.entrySet();
            })
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.groupingBy(entry -> entry.getKey().getNamesUnified(), Collectors.summingLong(Map.Entry::getValue)))
            .forEach((component, amount) -> {
              if (component != null && amount > 0) {
                tooltipComponents.add(
                    Component.translatable(
                        "modular_machinery_reborn.controller.required.item",
                        Component.literal(String.format("%dx", amount)).withStyle(ChatFormatting.GOLD),
                        component.withStyle(ChatFormatting.GRAY)
                    )
                );
              }
            });
      } else if (tooltipFlag.hasControlDown()) {
        tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.modifier").withStyle(ChatFormatting.GRAY));
        machine.getPattern()
            .getPattern()
            .getModifiers()
            .forEach(ingredient -> {
              MutableComponent component = Component.empty();
              ingredient.getDescriptionLines().forEach(component::append);
              tooltipComponents.add(component);
            });
      } else {
        tooltipComponents.add(
            Component.empty()
                .append(Component.translatable("modular_machinery_reborn.controller.alt").withStyle(ChatFormatting.YELLOW))
                .append(" ")
                .append(Component.translatable("modular_machinery_reborn.controller.alt.minmax").withStyle(ChatFormatting.GRAY))
        );
        tooltipComponents.add(
            Component.empty()
                .append(Component.translatable("modular_machinery_reborn.controller.shift").withStyle(ChatFormatting.YELLOW))
                .append(" ")
                .append(Component.translatable("modular_machinery_reborn.controller.shift.blocks").withStyle(ChatFormatting.GRAY))
        );
        tooltipComponents.add(
            Component.empty()
                .append(Component.translatable("modular_machinery_reborn.controller.control").withStyle(ChatFormatting.YELLOW))
                .append(" ")
                .append(Component.translatable("modular_machinery_reborn.controller.control.modifier").withStyle(ChatFormatting.GRAY))
        );
      }
      tooltipComponents.addAll(CACHED_COMPONENTS.compute(machine.getRegistryName(), (key, c) -> {
        List<Component> newC = c == null ? Lists.newArrayList() : c;
        if (Optional.ofNullable(context.level())
            .map(Level::getGameTime)
            .orElse((long) MMRConfig.get().dynamicTooltipTicks.get()) % MMRConfig.get().dynamicTooltipTicks.get() == 0) {
          Optional.ofNullable(ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(machine.getRegistryName()))
              .map(enumTooltips -> enumTooltips.get(TooltipUse.ITEM))
              .ifPresent(extra -> {
                if (!extra.isEmpty()) {
                  newC.clear();
                  newC.add(Component.literal(""));
                  for (Either<ResourceLocation, Component> either : extra) {
                    either.ifRight(newC::add);
                    either.ifLeft(eventId -> newC.add(KubeJSIntegration.sendDynamicTooltipEvent(eventId)));
                  }
                }
              });
        }
        return newC;
      }));
    }, () -> tooltipComponents.add(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY)));
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    return getMachine(stack).map(DynamicMachine::getMachineColor).orElse(super.getColorFromItemstack(stack, tintIndex));
  }

  public static ItemStack makeMachineItem(ResourceLocation machine) {
    if (ModularMachineryReborn.MACHINES_BLOCK.containsKey(machine))
      return ModularMachineryReborn.MACHINES_BLOCK.get(machine).asItem().getDefaultInstance();

    ItemStack stack = ItemRegistration.CONTROLLER.get().getDefaultInstance();
    stack.set(DataComponentRegistration.MACHINE_DATA, machine);
    return stack;
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    if (!context.canPlace()) return InteractionResult.FAIL;
    BlockPlaceContext context2 = updatePlacementContext(context);
    if (context2 == null) return InteractionResult.FAIL;
    BlockState state = getPlacementState(context2);
    if (state == null) return InteractionResult.FAIL;
    if (!this.placeBlock(context2, state)) return InteractionResult.FAIL;
    BlockPos pos = context2.getClickedPos();
    Level level = context2.getLevel();
    Player player = context2.getPlayer();
    ItemStack stack = context2.getItemInHand();
    BlockState state2 = level.getBlockState(pos);
    if (state2.is(state.getBlock())) {
      updateCustomBlockEntityTag(pos, level, player, stack, state2);
      state2.getBlock().setPlacedBy(level, pos, state2, player, stack);
      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.PLACED_BLOCK.trigger(sPlayer, pos, stack);
      }
    }

    if (state2.getBlock() instanceof BlockController controller) {
      SoundType soundType = controller.getSoundType(state, level, pos, player);
      level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1F) / 2F, soundType.getPitch() * 0.8F);
      level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
    }

    if (player == null || !player.getAbilities().instabuild) {
      stack.shrink(1);
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public Component getName(ItemStack pStack) {
    return getMachine(pStack).map(DynamicMachine::getName).orElse(super.getName(pStack));
  }
}
