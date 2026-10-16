package es.degrassi.mmr.common.machine;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.api.codec.RegistrarCodec;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.MachineModelLocation;
import es.degrassi.mmr.common.util.sound.AmbientSound;
import es.degrassi.mmr.common.util.sound.Sounds;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class DynamicMachine {
  public static final NamedCodec<DynamicMachine> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.IDENTIFIER.fieldOf("registryName").forGetter(DynamicMachine::getRegistryName),
      NamedCodec.STRING.optionalFieldOf("localizedName").forGetter(machine -> Optional.of(machine.getLocalizedName())),
      Structure.CODEC.fieldOf("structure").forGetter(DynamicMachine::getPattern),
      DefaultCodecs.HEX.optionalFieldOf("color", Config.machineColor).forGetter(DynamicMachine::getMachineColor),
      NamedCodec.unboundedMap(MachineStatus.CODEC, MachineModelLocation.CODEC, "Controller model by status").optionalFieldOf("controller", Maps.newHashMap()).forGetter(DynamicMachine::getControllerModels),
      NamedCodec.unboundedMap(MachineStatus.CODEC, Sounds.CODEC, "Sounds by status").optionalFieldOf("sound", new HashMap<>()).forGetter(DynamicMachine::getSounds),
      NamedCodec.unboundedMap(
          RegistrarCodec.HATCH_TYPE,
          TextureableMachineEntity.CODEC,
          "Formed Textures by HatchType"
      ).optionalFieldOf("formed_textures", Maps.newHashMap()).forGetter(DynamicMachine::getFormedTextures)
  ).apply(instance,
      (registryName, localizedName, pattern, color, controllerModels, sounds, formedTextures) -> {
    DynamicMachine machine = new DynamicMachine(registryName, sounds, formedTextures);
    machine.setPattern(pattern);
    machine.setLocalizedName(localizedName);
    machine.setDefinedColor(color);
    machine.setControllerModels(controllerModels);
    return machine;
  }), "Dynamic Machine");

  public static final DynamicMachine DUMMY;
  static {
    Map<MachineStatus, Sounds> sounds = new HashMap<>();
    for (MachineStatus status : MachineStatus.values()) {
      sounds.put(status, Sounds.DEFAULT);
    }
    DUMMY = new DynamicMachine(ModularMachineryReborn.rl("dummy"), sounds, Maps.newHashMap());
  }

  @Nonnull
  private Identifier registryName;
  private Optional<String> localizedName = Optional.empty();
  private Structure pattern = Structure.EMPTY;
  private int definedColor = Config.machineColor;
  private final Map<MachineStatus, MachineModelLocation> controllerModels;
  private final Map<MachineStatus, Sounds> sounds;
  private final Map<MachineHatchType, Pair<Boolean, Pair<Optional<Identifier>, Optional<Identifier>>>> formedTextures;

  public DynamicMachine(Identifier registryName, Map<MachineStatus, Sounds> sounds,
                        Map<MachineHatchType,
                            Pair<Boolean, Pair<Optional<Identifier>, Optional<Identifier>>>> formedTextures) {
    this.registryName = registryName;
    this.sounds = sounds;
    this.formedTextures = formedTextures;
    this.controllerModels = new EnumMap<>(MachineStatus.class);
  }

  public void setControllerModels(Map<MachineStatus, MachineModelLocation> controllerModels) {
    this.controllerModels.putAll(controllerModels);
  }

  public MachineModelLocation getControllerModel(MachineStatus status) {
    return Optional.ofNullable(this.controllerModels.get(status)).orElse(MachineModelLocation.DEFAULT);
  }

  public List<ModifierReplacement> getModifiers() {
    return getPattern().getPattern().getModifiers();
  }

  public String getLocalizedName() {
    return getName().getString();
  }

  public Component getName() {
    String localizationKey = registryName.getNamespace() + "." + registryName.getPath().replaceAll("/", ".");
    return Component.translatableWithFallback(localizationKey, localizedName.orElse(localizationKey));
  }

  public AmbientSound getAmbientSound(MachineStatus status) {
    return Optional.ofNullable(sounds.get(status)).map(Sounds::ambientSound).orElse(AmbientSound.DEFAULT);
  }

  public SoundType getInteractionSound(MachineStatus status) {
    return Optional.ofNullable(this.sounds.get(status)).orElse(Sounds.DEFAULT).interaction();
  }

  public int getMachineColor() {
    return definedColor;
  }

  public boolean isDummy() {
    return this.equals(DUMMY);
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("registryName", registryName.toString());
    json.addProperty("localizedName", localizedName.orElse("null"));
    json.add("pattern", pattern.asJson());
    json.addProperty("definedColor", definedColor);
    JsonObject controllers = new JsonObject();
    controllerModels.forEach((status, model) -> {
      controllers.addProperty(status.getSerializedName(), model.toString());
    });
    json.add("controllerModels", controllers);
    JsonObject sounds = new JsonObject();
    this.sounds.forEach((status, s) -> {
      sounds.add(status.getSerializedName(), s.asJson());
    });
    json.add("sounds", sounds);
    JsonObject formedTexts = new JsonObject();
    formedTextures.forEach((hatchType, pair) -> {
      var shouldColor = pair.getFirst();
      var texts = pair.mapSecond(second -> {
        var baseTexture = second.getFirst();
        var overlayTexture = second.getSecond();
        JsonObject textures = new JsonObject();
        baseTexture.ifPresent(rl -> textures.addProperty("base_texture", rl.toString()));
        overlayTexture.ifPresent(rl -> textures.addProperty("overlay_texture", rl.toString()));
        return textures;
      }).getSecond();
      JsonObject withColor = new JsonObject();
      withColor.addProperty("should_color", shouldColor);
      withColor.add("textures", texts);
      formedTexts.add(hatchType.getId().toString(), withColor);
    });
    json.add("formed_textures", formedTexts);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public List<List<ItemStack>> getStacks() {
    return getPattern()
        .getPattern()
        .asList()
        .stream()
        .flatMap(List::stream)
        .flatMap(s -> s.chars().mapToObj(c -> (char) c))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .map(entry -> {
          BlockIngredient ingredient = getPattern().getPattern().asMap().get(entry.getKey());
          if (ingredient == null) return null;
          return ingredient.getStacks(entry.getValue().intValue());
        })
        .filter(Objects::nonNull)
        .toList();
  }
}
