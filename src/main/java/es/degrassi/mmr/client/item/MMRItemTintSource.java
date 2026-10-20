package es.degrassi.mmr.client.item;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.item.ItemDynamicColor;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record MMRItemTintSource() implements ItemTintSource {
  public static final Identifier ID = ModularMachineryReborn.rl("mmr_item");
  public static final MapCodec<MMRItemTintSource> MAP_CODEC = MapCodec.unit(MMRItemTintSource::new);
  @Override
  public int calculate(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
    if (stack.getItem() instanceof ItemDynamicColor item) {
      return ARGB.opaque(item.getColorFromItemStack(stack));
    }
    return -1;
  }

  @Override
  public MapCodec<MMRItemTintSource> type() {
    return MAP_CODEC;
  }
}
