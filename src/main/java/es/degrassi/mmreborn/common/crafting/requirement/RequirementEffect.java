package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.capability.EffectHandler;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EffectComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.RomanNumber;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequirementEffect implements IRequirement<EffectComponent, EffectHandler> {
  public static final NamedCodec<RequirementEffect> CODEC = NamedCodec.record(instance -> instance.group(
      RegistrarCodec.EFFECT.fieldOf("effect").forGetter(req -> req.effect.value()),
      NamedCodec.INT.fieldOf("time").forGetter(req -> req.time),
      NamedCodec.INT.optionalFieldOf("level", 1).forGetter(req -> req.level),
      RegistrarCodec.ENTITY.listOf().optionalFieldOf("filter", new ArrayList<>()).forGetter(req -> req.filter)
  ).apply(instance, (effect, time, level, filter) ->
      new RequirementEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect).getDelegate(), time, level, filter)),
      "Requirement Effect");

  private final Holder<MobEffect> effect;
  private final int time, level;
  private final List<EntityType<?>> filter;
  @Getter
  private final PositionedRequirement position;

  public RequirementEffect(Holder<MobEffect> effect, int time, int level, List<EntityType<?>> filter) {
    this.effect = effect;
    this.time = time;
    this.level = level;
    this.filter = filter;
    this.position = new PositionedRequirement(0, 0);
  }

  @Override
  public RequirementType<RequirementEffect, EffectComponent, EffectHandler> getType() {
    return RequirementTypeRegistration.EFFECT.get();
  }

  @Override
  public ComponentType<EffectHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_EFFECT.get();
  }

  @Override
  public IOType getMode() {
    return IOType.NONE;
  }

  @Override
  public boolean test(EffectComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<EffectComponent> list) {
    list.processEachTick(this::processTick);
  }

  public CraftingResult processTick(EffectComponent component, ICraftingContext context) {
    int time = (int) context.getPerTickIntegerModifiedValue(this.time, this);
    int level = Mth.clamp((int)context.getPerTickIntegerModifiedValue(this.level, this) - 1, 0, 255);
    component.getContainerProvider()
        .applyEffect(
            new MobEffectInstance(this.effect, time, level),
            entity -> this.filter.isEmpty() || this.filter.contains(entity.getType())
        );
    return CraftingResult.success();
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.effect");
  }

  @Override
  public boolean isComponentValid(EffectComponent m, ICraftingContext context) {
    return true;
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    Component effect = Component.literal(this.effect.value().getDisplayName().getString()).withStyle(ChatFormatting.AQUA);
    Component level = this.level <= 0 ? Component.empty() : Component.literal(RomanNumber.toRoman(this.level)).withStyle(ChatFormatting.GOLD);
    info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.effect.info.tick", effect, level, this.time));
    if(!this.filter.isEmpty()) {
      info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.effect.info.whitelist").withStyle(ChatFormatting.AQUA));
      this.filter.forEach(type -> info.addTooltip(Component.literal("* ").append(Component.translatable(type.getDescriptionId()))));
    }
    info.setItemIcon(PotionContents.createItemStack(Items.POTION, Potions.HEALING));
  }
}
