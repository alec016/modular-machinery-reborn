package es.degrassi.mmr.common.crafting.modifier;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ModifierReplacement implements Predicate<BlockInWorld> {
  public static final NamedCodec<ModifierReplacement> CODEC = NamedCodec.record(instance -> instance.group(
      BlockIngredient.CODEC.fieldOf("replacement").forGetter(ModifierReplacement::getIngredient),
      RecipeModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierReplacement::getModifiers),
      DefaultCodecs.BLOCK_POS.fieldOf("position").forGetter(ModifierReplacement::getPosition)
  ).apply(instance, ModifierReplacement::new), "Modifier Replacement");

  private final BlockIngredient info;
  private final List<RecipeModifier<?, ?, ?>> modifier;
  private final List<Component> description;
  @Getter
  private final BlockPos position;

  public ModifierReplacement(BlockIngredient info, List<RecipeModifier<?, ?, ?>> modifier, BlockPos pos) {
    this.info = info;
    this.modifier = modifier;
    this.position = pos;
    this.description = Lists.newArrayList();
    description.add(info.getNamesUnified());
    description.addAll(getModifiers().stream().map(RecipeModifier::getTooltip).toList());
  }

  public BlockIngredient getIngredient() {
    return info;
  }

  public List<RecipeModifier<?, ?, ?>> getModifiers() {
    return Collections.unmodifiableList(modifier);
  }

  public List<Component> getDescriptionLines() {
    return description;
  }

  public boolean test(BlockInWorld biw) {
    return getIngredient().test(biw);
  }

  public List<String> getDescriptionLinesString() {
    return description.stream().map(Component::getString).toList();
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.add("replacement", info.asJson());
    JsonArray modifiers = new JsonArray();
    modifier.stream().map(RecipeModifier::asJson).forEach(modifiers::add);
    json.add("modifiers", modifiers);
    JsonArray desc = new JsonArray();
    description.stream().map(Component::getString).map(JsonOps.INSTANCE::createString).forEach(desc::add);
    json.add("description", desc);
    json.add("position", DefaultCodecs.BLOCK_POS.encodeStart(JsonOps.INSTANCE, position).getOrThrow());
    return json;
  }

  public void asTag(ValueOutput output) {
    output.store("replacement", BlockIngredient.CODEC.codec(), info);
    var modifiers = output.childrenList("modifiers");
    modifier.forEach(mod -> mod.asTag(modifiers.addChild()));
    var desc = output.list("description", NamedCodec.STRING.codec());
    description.stream().map(Component::getString).forEach(desc::add);
    var position = output.child("position");
    position.putInt("x", this.position.getX());
    position.putInt("y", this.position.getY());
    position.putInt("z", this.position.getZ());
  }

  public String getString() {
    return asJson().toString();
  }
}
