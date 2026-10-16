package es.degrassi.mmr.common.crafting.requirement;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.integration.kubejs.KubeJSIntegration;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.FunctionComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RequirementFunction implements IRequirement<FunctionComponent, Void> {
  public static final NamedCodec<RequirementFunction> CODEC = NamedCodec.record(functionRequirementInstance ->
      functionRequirementInstance.group(
          NamedCodec.enumCodec(Phase.class).fieldOf("phase").forGetter(RequirementFunction::getPhase),
          NamedCodec.STRING.fieldOf("id").forGetter(RequirementFunction::getIdentifier),
          NamedCodec.STRING.listOf().optionalFieldOf("args", Lists.newArrayList()).forGetter(RequirementFunction::getArgs)
      ).apply(functionRequirementInstance, RequirementFunction::new), "Function requirement"
  );
  public static final List<RequirementFunction> errors = new ArrayList<>();

  private final Phase phase;
  private final String identifier;
  private final List<String> args;

  public RequirementFunction(Phase phase, String id, List<String> args) {
    this.phase = phase;
    this.identifier = id;
    this.args = args;
  }

  @Override
  public RequirementType<RequirementFunction, FunctionComponent, Void> getType() {
    return RequirementTypeRegistration.FUNCTION.get();
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_FUNCTION.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(FunctionComponent component, ICraftingContext context) {
    return this.phase != Phase.CHECK || this.execute(component, context).isSuccess();
  }

  @Override
  public void gatherRequirements(IRequirementList<FunctionComponent> list) {
    switch(this.phase) {
      case START -> list.processOnStart(this::execute);
      case TICK -> list.processEachTick(this::execute);
      case END -> list.processOnEnd(this::execute);
    }
  }

  private CraftingResult execute(FunctionComponent component, ICraftingContext context) {
    if(errors.contains(this))
      return CraftingResult.error(Component.translatable("craftcheck.failure.function"));

    try {
      if(ModList.get().isLoaded("kubejs"))
        return KubeJSIntegration.sendFunctionRequirementEvent(this.identifier, context, this.args);
      else
        throw new IllegalStateException("Trying to process function requirement for identifier: " + this.identifier + " without KubeJS installed!");
    } catch (Throwable error) {
      errors.add(this);
      if(ModList.get().isLoaded("kubejs"))
        KubeJSIntegration.logError(error);
      return CraftingResult.error(Component.translatable("craftcheck.failure.function"));
    }
  }

  @Override
  public PositionedRequirement getPosition() {
    return new PositionedRequirement(0, 0);
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.function");
  }

  @Override
  public JsonObject asJson() {
    var json = IRequirement.super.asJson();
    json.addProperty("identifier", identifier);
    json.addProperty("phase", phase.name().toLowerCase());
    JsonArray arguments = new JsonArray();
    args.forEach(arguments::add);
    json.add("args", arguments);
    return json;
  }

  @Override
  public boolean isComponentValid(FunctionComponent m, ICraftingContext context) {
    return true;
  }

  public enum Phase {
    CHECK, START, TICK, END
  }
}
