/**
 * This item is mainly copied from
 * <url>https://github.com/Frinn38/Custom-Machinery/blob/1.21/src/main/java/fr/frinn/custommachinery/common/init/StructureCreatorItem.java</url>
 */
package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class StructureCreatorItem extends Item {
  private final StructureCreatorItemMode mode;

  public StructureCreatorItem(StructureCreatorItemMode mode, Properties props) {
    super(props.stacksTo(1));
    this.mode = mode;
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return true;
  }

  private InteractionResult sidedSuccess(boolean isClientSide) {
    return InteractionResult.sidedSuccess(isClientSide);
  }

  public static int findSlotMatchingItem(Inventory inv, ItemStack stack) {
    for(int i = 0; i < inv.items.size(); ++i) {
      if (!inv.items.get(i).isEmpty() && ItemStack.isSameItem(stack, inv.items.get(i))) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player == null) return InteractionResult.FAIL;
    BlockPos pos = context.getClickedPos();
    BlockState state = context.getLevel().getBlockState(pos);
    ItemStack stack = context.getItemInHand();
    boolean isClientSide = context.getLevel().isClientSide;
    ItemStack s;
    if (isClientSide) {
      return sidedSuccess(true);
    }
    var slot = findSlotMatchingItem(player.getInventory(), ItemRegistration.STRUCTURE_TEMPLATE_ITEM.toStack());
    if (slot >= 0) {
      s = player.getInventory().getItem(slot);
    } else {
      slot = findSlotMatchingItem(player.getInventory(), Items.PAPER.getDefaultInstance());
      if (slot < 0) {
        player.sendSystemMessage(Component.translatable("mmr.no_paper_template"));
        return InteractionResult.SUCCESS_NO_ITEM_USED;
      }
      player.getInventory().removeItem(slot, 1);
      s = ItemRegistration.STRUCTURE_TEMPLATE_ITEM.toStack();
      player.addItem(s);
    }

    if (mode.isSingle()) {
      if (!(state.getBlock() instanceof BlockController)) {
        if (!StructureTemplateItem.contains(s, pos)) {
          StructureTemplateItem.addSelectedBlock(s, pos);
        } else if (StructureTemplateItem.contains(s, pos)) {
          StructureTemplateItem.removeSelectedBlock(s, pos);
        }
      }
      return sidedSuccess(false);
    } else if (mode.isBox()) {
      if (!(state.getBlock() instanceof BlockController)) {
        if (isFirst(stack)) {
          selectFirst(stack, s, pos);
          setSecond(stack);
        } else {
          selectSecond(stack, s, pos);
          setFirst(stack);
        }
      }
      return sidedSuccess(false);
    } else if (mode.isVein()) {
      if (!(state.isAir() || state.getBlock() instanceof BlockController)) {
        applyVeinSelection(context.getLevel(), pos, s);
        return sidedSuccess(false);
      }
    }

    return super.useOn(context);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.mode", mode.component()));
    if (mode.isBox()) {
      if (isFirst(stack)) {
        tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.mode.box.first").withStyle(ChatFormatting.GRAY));
      } else {
        tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.mode.box.second").withStyle(ChatFormatting.GRAY));
      }
    } else if (mode.isSingle()) {
      tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.select").withStyle(ChatFormatting.GREEN));
    } else if (mode.isVein()) {
      tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.vein.select").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.vein.max", MMRConfig.get().maxVeinNumber.get()).withStyle(ChatFormatting.DARK_AQUA));
    }
    tooltip.add(Component.empty());
    tooltip.add(Component.translatable("modular_machinery_reborn.structure_creator.reset").withStyle(ChatFormatting.GOLD));
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (player.isCrouching() && stack.getItem() == this) {
      stack.remove(DataComponentRegistration.STRUCTURE_CREATOR_BOX);
      return InteractionResultHolder.success(stack);
    }
    return super.use(level, player, hand);
  }

  protected void applyVeinSelection(Level level, BlockPos startPos, ItemStack template) {
    Queue<BlockPos> queue = new LinkedList<>();
    Set<BlockPos> visited = new HashSet<>();

    queue.add(startPos);
    visited.add(startPos);

    int addedCount = 0;
    final int LIMIT = MMRConfig.get().maxVeinNumber.get();

    final Block type = level.getBlockState(startPos).getBlock();

    while(!queue.isEmpty() && addedCount < LIMIT) {
      BlockPos current = queue.poll();
      BlockState currentState = level.getBlockState(current);

      if(!currentState.isAir() && !(currentState.getBlock() instanceof BlockController)) {
        if(!StructureTemplateItem.contains(template, current) && currentState.is(type)) {
          StructureTemplateItem.addSelectedBlock(template, current);
          addedCount++;
        }

        for(Direction dir : Direction.values()) {
          BlockPos neighbor = current.relative(dir);

          if(!visited.contains(neighbor)) {
            BlockState neighborState = level.getBlockState(neighbor);

            if(!neighborState.isAir() && !(neighborState.getBlock() instanceof BlockController)) {
              visited.add(neighbor);
              queue.add(neighbor);
            }
          }
        }
      }
    }
  }

  public static boolean isFirst(ItemStack stack) {
    return stack.getOrDefault(DataComponentRegistration.STRUCTURE_CREATOR_BOX_CURRENT, true);
  }

  public static void setSecond(ItemStack stack) {
    stack.update(
        DataComponentRegistration.STRUCTURE_CREATOR_BOX_CURRENT,
        true,
        current -> false
    );
  }

  public static void setFirst(ItemStack stack) {
    stack.update(
        DataComponentRegistration.STRUCTURE_CREATOR_BOX_CURRENT,
        true,
        current -> true
    );
  }

  public static BlockPos getBox(ItemStack stack) {
    return stack.getOrDefault(DataComponentRegistration.STRUCTURE_CREATOR_BOX, new BlockPos(0, 0, 0));
  }

  public static void selectFirst(ItemStack stack, ItemStack template,  BlockPos pos) {
    stack.update(
        DataComponentRegistration.STRUCTURE_CREATOR_BOX,
        new BlockPos(0, 0, 0),
        box -> pos.immutable()
    );
    StructureTemplateItem.addSelectedBlock(template, pos);
  }

  public static void selectSecond(ItemStack stack, ItemStack template,  BlockPos pos) {
    BlockPos stored = getBox(stack);
    int minX = Math.min(stored.getX(), pos.getX()),
        maxX = Math.max(stored.getX(), pos.getX()),
        minY = Math.min(stored.getY(), pos.getY()),
        maxY = Math.max(stored.getY(), pos.getY()),
        minZ = Math.min(stored.getZ(), pos.getZ()),
        maxZ = Math.max(stored.getZ(), pos.getZ());
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    for (int x = minX; x <= maxX; ++x) {
      for (int y = minY; y <= maxY; ++y) {
        for (int z = minZ; z <= maxZ; ++z) {
          mutable.set(x, y, z);
          if (StructureTemplateItem.contains(template, mutable.immutable())) continue;
          StructureTemplateItem.addSelectedBlock(template, mutable.immutable());
        }
      }
    }
    stack.remove(DataComponentRegistration.STRUCTURE_CREATOR_BOX);
  }
}
