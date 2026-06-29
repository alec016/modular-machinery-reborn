package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.BiomeComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class RequirementBiome implements IRequirement<BiomeComponent, List<ResourceLocation>> {
  public static final NamedCodec<RequirementBiome> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.listOf().fieldOf("filter").forGetter(RequirementBiome::filter),
      NamedCodec.BOOL.optionalFieldOf("blacklist", false).forGetter(RequirementBiome::blacklist)
  ).apply(instance, RequirementBiome::new), "Biome Requirement");

  @Getter
  private final IOType actionType;
  @Getter
  private final PositionedRequirement position;
  private final List<ResourceLocation> filter;
  private final boolean blacklist;

  public RequirementBiome(List<ResourceLocation> filter, boolean blacklist) {
    this.filter = filter;
    this.blacklist = blacklist;
    this.position = new PositionedRequirement(0, 0);
    this.actionType = IOType.INPUT;
  }

  public RequirementType<RequirementBiome, BiomeComponent, List<ResourceLocation>> getType() {
    return RequirementTypeRegistration.BIOME.get();
  }

  public List<ResourceLocation> filter() {
    return filter;
  }

  public boolean blacklist() {
    return blacklist;
  }

  @Override
  public ComponentType<List<ResourceLocation>> getComponentType() {
    return ComponentRegistration.COMPONENT_BIOME.get();
  }

  @Override
  public IOType getMode() {
    return getActionType();
  }

  @Override
  public boolean test(BiomeComponent component, ICraftingContext context) {
    Registry<Biome> biomeRegistry = context.getMachineTile().getLevel().registryAccess().registryOrThrow(Registries.BIOME);
    return this.filter.stream().anyMatch(biome -> biomeRegistry.get(biome) == context.getMachineTile().getLevel().getBiome(context.getMachineTile().getBlockPos()).value()) != this.blacklist;
  }

  @Override
  public void gatherRequirements(IRequirementList<BiomeComponent> list) {
    // If the {@link #test(BiomeComponent, ICraftingContext)} success then this requirement is OK to proceed
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("blacklist", blacklist);
    JsonArray array = new JsonArray();
    filter.stream().map(ResourceLocation::toString).forEach(array::add);
    json.add("filter", array);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.biome");
  }

  @Override
  public boolean isComponentValid(BiomeComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    StringBuilder biomes = new StringBuilder();
    filter.forEach(biome -> biomes.append(biome.toString()).append(","));
    int index = biomes.lastIndexOf(",");
    if (index >= biomes.length() - 1)
      biomes.deleteCharAt(index);
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.biome." + blacklist(),
        biomes.toString()
    ));
    info.setItemIcon(Items.MAP);
  }
}
