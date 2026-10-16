package es.degrassi.mmr.common.integration.kubejs.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.machine.MachineHatchType;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.MachineModelLocation;
import es.degrassi.mmr.common.util.sound.Sounds;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MachineBuilderJS {
  @NotNull
  @Getter
  private final Identifier id;
  private String name;
  private String color;
  private Integer intColor;
  private StructureBuilderJS structure;
  private final Map<MachineStatus, MachineModelLocation> controllerModels;
  private final List<ModifierReplacement> modifiers;
  private final Map<MachineStatus, Sounds> sounds;
  private final Map<MachineHatchType, Pair<Boolean, Pair<Optional<Identifier>, Optional<Identifier>>>> textureMap;

  public MachineBuilderJS(Identifier id) {
    this.id = id;
    modifiers = Lists.newArrayList();
    sounds = Maps.newEnumMap(MachineStatus.class);
    textureMap = Maps.newHashMap();
    controllerModels = Maps.newEnumMap(MachineStatus.class);
  }

  public MachineBuilderJS name(String name) {
    this.name = name;
    return this;
  }

  public MachineBuilderJS color(String color) {
    this.color = color;
    return this;
  }

  public MachineBuilderJS color(Integer color) {
    this.intColor = color;
    return this;
  }

  public MachineBuilderJS structure(StructureBuilderJS structure) {
    this.structure = structure;
    return this;
  }

  public MachineBuilderJS controllerModel(MachineModelLocation modelLocation) {
    for (var status : MachineStatus.values())
      controllerModel(status, modelLocation);
    return this;
  }

  public MachineBuilderJS controllerModel(MachineStatus status, MachineModelLocation modelLocation) {
    this.controllerModels.put(status, modelLocation);
    return this;
  }

  public MachineBuilderJS texture(Identifier type, boolean shouldColor, @Nullable Identifier baseTexture,
                                  @Nullable Identifier overlayTexture) {
    Objects.requireNonNull(type, "HatchType cant be null");
    MachineHatchType t = ModularMachineryReborn.getMachineHatchTypeRegistrar().get(type).map(Holder::value).orElse(null);
    Objects.requireNonNull(t, String.format("Invalid HatchType, given: %s", type));
    var base = Optional.ofNullable(baseTexture);
    var overlay = Optional.ofNullable(overlayTexture);
    var pair = Pair.of(base, overlay);
    textureMap.put(t, Pair.of(shouldColor, pair));
    return this;
  }

  public MachineBuilderJS addModifier(ModifierBuilderJS modifier) {
    this.modifiers.add(modifier.build());
    return this;
  }

  public MachineBuilderJS sound(MachineStatus status, Sounds sounds) {
    this.sounds.put(status, sounds);
    return this;
  }

  public DynamicMachine build() {
    DynamicMachine machine = new DynamicMachine(id, sounds, textureMap);
    machine.setPattern(structure == null ? Structure.EMPTY : structure.build(modifiers));
    machine.setControllerModels(controllerModels);
    machine.setLocalizedName(Optional.ofNullable(name));
    if (intColor != null)
      machine.setDefinedColor(intColor);
    else if(color != null)
      machine.setDefinedColor(DefaultCodecs.HEX.decode(JsonOps.INSTANCE, new JsonPrimitive(color)).result().orElse(new Pair<>(Config.toInt(MMRConfig.get().general_casing_color.get(), 0xFF4900), null)).getFirst());
    return machine;
  }
}
