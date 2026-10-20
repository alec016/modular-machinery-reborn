package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.MachineRecipeSerializer;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeRegistration {
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModularMachineryReborn.MODID);
  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModularMachineryReborn.MODID);

  public static final DeferredHolder<RecipeType<?>, RecipeType<MachineRecipe>> RECIPE_TYPE =
      RECIPE_TYPES.register(rootLC("machine_recipe"), () -> new RecipeType<>() {});
  public static final Supplier<RecipeSerializer<MachineRecipe>> RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(rootLC(
      "machine_recipe"), () -> MachineRecipeSerializer.INSTANCE);

  public static void register(IEventBus bus) {
    RECIPE_SERIALIZERS.register(bus);
    RECIPE_TYPES.register(bus);
  }
}
