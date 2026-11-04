package es.degrassi.mmr.api.codec;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Item stack codec without the vanilla count limits.
 *
 * <p>Vanilla's {@link ItemStack#CODEC} stores the count as an {@code intRange(1, 99)} and its
 * strict validation refuses any stack above the item's maximum stack size. Handlers configured for
 * far more items per slot (a bus with a capacity of 10k per slot for example) would fail to save
 * with it. AE2 solves this the same way: the amount is a plain number in the data and the slot
 * renders it itself.</p>
 *
 * <p>The layout matches the vanilla one ({@code id}, {@code count}, {@code components}) and an
 * empty stack is stored as an empty map, so the data stays compatible with what vanilla writes.</p>
 */
@UtilityClass
public final class BigStackCodecs {

  public static final NamedCodec<ItemStack> ITEM_STACK = NamedCodec.record(instance -> instance.group(
      NamedCodec.of(Item.CODEC_WITH_BOUND_COMPONENTS).fieldOf("id").forGetter(ItemStack::typeHolder),
      NamedCodec.of(ExtraCodecs.POSITIVE_INT).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
      NamedCodec.of(DataComponentPatch.CODEC).optionalFieldOf("components", DataComponentPatch.EMPTY)
          .forGetter(ItemStack::getComponentsPatch)
  ).apply(instance, ItemStack::new), "ItemStack");

  /** The optional variant: an empty stack is stored as an empty map, like the vanilla codec. */
  public static final NamedCodec<ItemStack> OPTIONAL = NamedCodec.of(
      ExtraCodecs.optionalEmptyMap(ITEM_STACK.codec())
          .xmap(
              stack -> stack.orElse(ItemStack.EMPTY),
              stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)
          ),
      "ItemStack"
  );
}
