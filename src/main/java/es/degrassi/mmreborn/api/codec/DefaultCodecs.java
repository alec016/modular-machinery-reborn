package es.degrassi.mmreborn.api.codec;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.machine.MachineJsonReloadListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class DefaultCodecs {

  public static final NamedCodec<ResourceLocation> RESOURCE_LOCATION = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeResourceLocation, ResourceLocation::toString, "Resource location");
  public static final NamedCodec<Character> CHARACTER = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeCharacter, Object::toString, "Character");

  public static final NamedCodec<SoundEvent> SOUND_EVENT = RESOURCE_LOCATION.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation, "Sound event");

  public static final NamedCodec<Direction> DIRECTION = NamedCodec.enumCodec(Direction.class);
  public static <A> Codec<Optional<A>> optionalEmptyMap(final Codec<A> pCodec) {
    return new Codec<>() {
      @Override
      public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> p_330879_, T p_330924_) {
        return isEmptyMap(p_330879_, p_330924_)
          ? DataResult.success(Pair.of(Optional.empty(), p_330924_))
          : pCodec.decode(p_330879_, p_330924_).map(p_337591_ -> p_337591_.mapFirst(Optional::of));
      }

      private static <T> boolean isEmptyMap(DynamicOps<T> p_338754_, T p_338581_) {
        Optional<MapLike<T>> optional = p_338754_.getMap(p_338581_).result();
        return optional.isPresent() && optional.get().entries().findAny().isEmpty();
      }

      public <T> DataResult<T> encode(Optional<A> p_338508_, DynamicOps<T> p_331521_, T p_331876_) {
        return p_338508_.isEmpty() ? DataResult.success(p_331521_.emptyMap()) : pCodec.encode(p_338508_.get(), p_331521_, p_331876_);
      }
    };
  }
  public static final Codec<ItemStack> OPTIONAL_ITEM_CODEC = optionalEmptyMap(ItemStack.CODEC)
    .xmap(stack -> stack.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
  public static final Codec<FluidStack> OPTIONAL_FLUID_CODEC = optionalEmptyMap(FluidStack.CODEC)
    .xmap(stack -> stack.orElse(FluidStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
  public static final NamedCodec<ItemStack> ITEM_OR_STACK = NamedCodec.either(RegistrarCodec.ITEM, NamedCodec.of(OPTIONAL_ITEM_CODEC), "ItemStack").xmap(either -> either.map(Item::getDefaultInstance, Function.identity()), Either::right, "Item Stack");
  public static final NamedCodec<FluidStack> FLUID_OR_STACK = NamedCodec.either(RegistrarCodec.FLUID, NamedCodec.of(OPTIONAL_FLUID_CODEC), "FluidStack").xmap(either -> either.map(fluid -> new FluidStack(fluid, 1000), Function.identity()), Either::right, "Fluid Stack");

  public static final NamedCodec<Ingredient> INGREDIENT = NamedCodec.of(Ingredient.CODEC, "Ingredient");

  public static final NamedCodec<AABB> BOX = NamedCodec.DOUBLE_STREAM.comapFlatMap(stream -> {
    double[] arr = stream.toArray();
    if(arr.length == 3)
      return DataResult.success(new AABB(arr[0], arr[1], arr[2], arr[0], arr[1], arr[2]));
    else if(arr.length == 6)
      return DataResult.success(new AABB(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]));
    else
      return DataResult.error(() -> Arrays.toString(arr) + " is not an array of 3 or 6 elements");
  }, aabb -> DoubleStream.of(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ), "Box");

  public static final NamedCodec<Integer> HEX = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeHexColor, DefaultCodecs::encodeHexColor, "Hex color");

//  public static final NamedCodec<Either<TagKey<Item>, ItemStack>> ITEMSTACK_OR_TAG = NamedCodec.either(
//    DefaultCodecs.ITEM_OR_STACK,
//    DefaultCodecs.registryValueOrTag(BuiltInRegistries.ITEM)
//  ).comapFlatMap(either -> {
//    if (either.left().isPresent()) {
//      return DataResult.success(Either.right(either.left().get()));
//    } else if (either.right().isPresent()) {
//      var e = either.right().get();
//      if (e.left().isPresent()) {
//        return DataResult.success(Either.left(e.left().get()));
//      } else if(e.right().isPresent()) {
//        return DataResult.success(Either.right(e.right().get().value().getDefaultInstance()));
//      }
//    }
//    return DataResult.error(() -> "Either values can not be empty");
//  }, either -> either.map(key -> "#" + key.location(), stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()), "");

  public static <T> NamedCodec<TagKey<T>> tagKey(ResourceKey<Registry<T>> registry) {
    return RESOURCE_LOCATION.xmap(rl -> TagKey.create(registry, rl), TagKey::location, "Tag: " + registry.location());
  }

  public static <T> NamedCodec<TagKey<T>> registryKey(Registry<T> registry) {
    return NamedCodec.STRING.comapFlatMap(s -> {
      if(s.startsWith("#")) {
        try {
          TagKey<T> key = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
          if(MachineJsonReloadListener.context != null && MachineJsonReloadListener.context.getTag(key).isEmpty())
            return DataResult.error(() -> "Invalid tag: " + s);
          return DataResult.success(key);
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      }
      return DataResult.error(() -> "Invalid tag, must start with #");
    }, key -> "#" + key.location(), "Value or Tag: " + registry.key().location());
  }

  public static <T> NamedCodec<Either<TagKey<T>, Holder<T>>> registryValueOrTag(Registry<T> registry) {
    return NamedCodec.STRING.comapFlatMap(s -> {
      if(s.startsWith("#")) {
        try {
          TagKey<T> key = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
          if(MachineJsonReloadListener.context != null && MachineJsonReloadListener.context.getTag(key).isEmpty())
            return DataResult.error(() -> "Invalid tag: " + s);
          return DataResult.success(Either.left(key));
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      } else {
        try {
          Optional<Holder.Reference<T>> ref = registry.getHolder(registry.getId(ResourceLocation.parse(s)));
          return ref.map(reference -> DataResult.success(Either.<TagKey<T>, Holder<T>>right(reference))).orElse(DataResult.error(() -> "Invalid item: " + s));
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      }
    }, either -> either.map(key -> "#" + key.location(), holder -> holder.unwrapKey().get().location().toString()), "Value or Tag: " + registry.key().location());
  }

  private static DataResult<ResourceLocation> decodeResourceLocation(String encoded) {
    try {
      return DataResult.success(ResourceLocation.parse(encoded));
    } catch (ResourceLocationException e) {
      return DataResult.error(e::getMessage);
    }
  }

  private static DataResult<Character> decodeCharacter(String encoded) {
    if(encoded.length() != 1)
      return DataResult.error(() -> "Invalid character : \"" + encoded + "\" must be a single character !");
    return DataResult.success(encoded.charAt(0));
  }

  private static final List<Character> validHex = Lists.charactersOf("0123456789AaBbCcDdEeFf");

  private static DataResult<Integer> decodeHexColor(String encoded) {
    if (!encoded.startsWith("#"))
      return DataResult.error(() -> "Invalid hex color format, must starts with '#'");
    if(encoded.length() != 9)
      return DataResult.error(() -> "Invalid length : \"" + encoded + "\" must be 9 characters !(#FFFFFFFF [alpha alpha red red green green blue blue])");
    char[] chars = encoded.substring(1).toCharArray();
    for (char c : chars) {
      if (!validHex.contains(c))
        return DataResult.error(() -> "Invalid character: \"" + c + "\", valid characters are: " + validHex);
    }
    return DataResult.success(Config.toInt(encoded));
  }

  public static String encodeHexColor(Integer color) {
    return String.format("#%08x", color);
  }
}
