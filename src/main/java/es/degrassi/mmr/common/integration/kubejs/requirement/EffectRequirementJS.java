package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementEffect;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public interface EffectRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS giveEffect(Identifier effect, int time, int level, Identifier...filter) {
    List<EntityType<?>> entityFilter = new ArrayList<>();
    for (var type : filter) {
      if (BuiltInRegistries.ENTITY_TYPE.containsKey(type)) {
        entityFilter.add(BuiltInRegistries.ENTITY_TYPE.get(type).map(Holder::value).orElseThrow());
      } else {
        return error("Invalid entity ID: {}", type);
      }
    }
    if (BuiltInRegistries.MOB_EFFECT.containsKey(effect)) {
      var requirement = new RequirementEffect(BuiltInRegistries.MOB_EFFECT.get(effect).orElseThrow(), time, level, entityFilter);
      return addRequirement(new RecipeRequirement<>(requirement));
    } else {
      return error("Invalid effect ID: {}", effect);
    }
  }

  default MachineRecipeBuilderJS giveEffect(Identifier effect, int time, Identifier...filter) {
    return giveEffect(effect, time, 1, filter);
  }
}
