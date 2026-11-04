package es.degrassi.mmr.common.network.client.emi;

import com.google.common.collect.Lists;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class FillRecipeC2SPacket implements CustomPacketPayload {
  public static final Type<FillRecipeC2SPacket> TYPE = new Type<>(ModularMachineryReborn.rl("fill_recipe_mmr"));
  public static final StreamCodec<RegistryFriendlyByteBuf, FillRecipeC2SPacket> CODEC = StreamCodec.ofMember(FillRecipeC2SPacket::write, FillRecipeC2SPacket::new);

  private final int syncId;
  private final int action;
  private final List<Integer> slots, crafting;
  private final int output;
  private final List<ItemStack> stacks;

  public FillRecipeC2SPacket(AbstractContainerMenu handler, int action, List<Slot> slots, List<Slot> crafting, @Nullable Slot output, List<ItemStack> stacks) {
    this.syncId = handler.containerId;
    this.action = action;
    this.slots = slots.stream().map(s -> s == null ? -1 : s.index).toList();
    this.crafting = crafting.stream().map(s -> s == null ? -1 : s.index).toList();
    this.output = output == null ? -1 : output.index;
    this.stacks = stacks;
  }

  public FillRecipeC2SPacket(RegistryFriendlyByteBuf buf) {
    syncId = buf.readInt();
    action = buf.readByte();
    slots = parseCompressedSlots(buf);
    crafting = Lists.newArrayList();
    int craftingSize = buf.readVarInt();
    for (int i = 0; i < craftingSize; i++) {
      int s = buf.readVarInt();
      crafting.add(s);
    }
    if (buf.readBoolean()) {
      output = buf.readVarInt();
    } else {
      output = -1;
    }
    int size = buf.readVarInt();
    stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      stacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }
  }

  public void write(RegistryFriendlyByteBuf buf) {
    buf.writeInt(syncId);
    buf.writeByte(action);
    writeCompressedSlots(slots, buf);
    buf.writeVarInt(crafting.size());
    for (Integer s : crafting) {
      buf.writeVarInt(s);
    }
    if (output != -1) {
      buf.writeBoolean(true);
      buf.writeVarInt(output);
    } else {
      buf.writeBoolean(false);
    }
    buf.writeVarInt(stacks.size());
    for (ItemStack stack : stacks) {
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
    }
  }

  public void apply(Player player) {
}

  private static List<Integer> parseCompressedSlots(FriendlyByteBuf buf) {
    List<Integer> list = Lists.newArrayList();
    int amount = buf.readVarInt();
    for (int i = 0; i < amount; i++) {
      int low = buf.readVarInt();
      int high = buf.readVarInt();
      if (low < 0) {
        return null;
      }
      for (int j = low; j <= high; j++) {
        list.add(j);
      }
    }
    return list;
  }

  private static void writeCompressedSlots(List<Integer> list, FriendlyByteBuf buf) {
    List<Consumer<FriendlyByteBuf>> postWrite = Lists.newArrayList();
    int groups = 0;
    int i = 0;
    while (i < list.size()) {
      groups++;
      int start = i;
      int startValue = list.get(start);
      while (i < list.size() && i - start == list.get(i) - startValue) {
        i++;
      }
      int end = i - 1;
      postWrite.add(b -> {
        b.writeVarInt(startValue);
        b.writeVarInt(list.get(end));
      });
    }
    buf.writeVarInt(groups);
    for (Consumer<FriendlyByteBuf> consumer : postWrite) {
      consumer.accept(buf);
    }
  }

  private static int grabMatching(Player player, ItemHandler inventory, List<Slot> slots,
                                  List<ItemStack> rubble,
                                  List<Slot> crafting,
                                  ItemStack stack) {
    // LEGACY: only called from apply(), which is a no-op now. Used the removed
    // setStackInSlot()/Slot API of the legacy vanilla menu.
    return 0;
}

  @Override
  @NotNull
  public Type<FillRecipeC2SPacket> type() {
    return TYPE;
  }

  @Override
  public String toString() {
    return "FillRecipeC2SPacket{" +
        "syncId=" + syncId +
        ", action=" + action +
        ", slots=" + slots +
        ", crafting=" + crafting +
        ", output=" + output +
        ", stacks=" + stacks +
        '}';
  }

  public static void handle(FillRecipeC2SPacket packet, IPayloadContext context) {
    if (!context.flow().isServerbound()) {
      throw new IllegalArgumentException("Trying to handle serverbound packet on client: " + packet);
    }
    var player = context.player();
    context.enqueueWork(() -> packet.apply(player));
  }
}
