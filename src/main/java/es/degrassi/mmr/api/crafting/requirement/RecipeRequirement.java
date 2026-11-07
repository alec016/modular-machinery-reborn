package es.degrassi.mmr.api.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.common.crafting.requirement.RequirementTexts;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.ComponentManager;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RecipeRequirement<C extends MachineComponent<T>, R extends IRequirement<C, T>, T> {
  public static final NamedCodec<RecipeRequirement<?, ?, ?>> CODEC = NamedCodec.record(recipeRequirementInstance ->
      recipeRequirementInstance.group(
          IRequirement.CODEC.forGetter(RecipeRequirement::requirement),
          NamedCodec.floatRange(0.0f, 1.0f).optionalFieldOf("chance", 1.0f).forGetter(requirement -> requirement.chance),
          DisplayInfoTemplate.CODEC.optionalFieldOf("info").forGetter(req -> Optional.ofNullable(req.info))
      ).apply(recipeRequirementInstance, (req, chance, info) ->
          new RecipeRequirement<>(req, chance, info.orElse(null))),
      "Recipe requirement"
  );

  private final R requirement;
  private float chance;
  @Nullable
  public DisplayInfoTemplate info;

  public RecipeRequirement(R requirement, float chance, @Nullable DisplayInfoTemplate info) {
    this.requirement = requirement;
    this.chance = chance;
    this.info = info;
  }

  public RecipeRequirement(R requirement, float chance) {
    this(requirement, chance, null);
  }

  public RecipeRequirement(R requirement) {
    this(requirement, 1.0f, null);
  }

  @SuppressWarnings("unchecked")
  public RecipeRequirement<C, R, T> castRequirement(RecipeRequirement<?, ?, ?> requirement) {
    return (RecipeRequirement<C, R, T>) requirement;
  }

  @SuppressWarnings("unchecked")
  public RequirementType<R, C, T> getType() {
    return (RequirementType<R, C, T>) this.requirement.getType();
  }

  public R requirement() {
    return this.requirement;
  }

  public float chance() {
    return this.chance;
  }

  public void setChance(float chance) {
    this.chance = Mth.clamp(chance, 0.0f, 1.0f);
  }

  public C findComponent(ComponentManager manager, ICraftingContext context) {
    return manager.getComponent(this.requirement, context).orElse(null);
  }

  public CraftingResult test(ComponentManager manager, ICraftingContext context) {
    C component = findComponent(manager, context);
    if (component == null) return CraftingResult.error(requirement.getMissingComponentErrorMessage(requirement.getMode()));
    return this.requirement.test(component, context) ? CraftingResult.success() : CraftingResult.error(Component.empty());
  }

  public boolean shouldSkip(RandomSource rand, ICraftingContext context) {
    float chance = context.getModifiedValue(this.chance, this.requirement);
    return rand.nextFloat() > chance;
  }

  public boolean isModified() {
    return requirement().isModified();
  }

  public void getDisplayInfo(RequirementDisplayInfo info) {
    this.requirement.getDefaultDisplayInfo(info, this);
  }

  public boolean hasControllerText() {
    return requirement.hasControllerText();
  }

  public Component toUI() {
    if (chance == 1) return requirement.toControllerUI();
    if (chance == 0) return RequirementTexts.notConsumed.text(requirement.toControllerUI());
    return RequirementTexts.chance.text(Utils.decimalFormatWithPercentage(chance * 100), requirement.toControllerUI());
  }

  public JsonObject asJson() {
    JsonObject json = requirement.asJson();
    json.addProperty("chance", chance);
    return json;
  }
}
