package es.degrassi.mmr.common.util;


import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

@Getter
public class EmptyRequirementType {
  public static final ResourceKey<Registry<EmptyRequirementType>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("empty_requirement_type"));

  private final int uOffset, vOffset, width, height;

  protected EmptyRequirementType(int uOffset, int vOffset, int width, int height) {
    this.uOffset = uOffset;
    this.vOffset = vOffset;
    this.width = width;
    this.height = height;
  }

  public static EmptyRequirementType create(int uOffset, int vOffset, int width, int height) {
    return new EmptyRequirementType(uOffset, vOffset, width, height);
  }
  public Identifier getId() {
    return ModularMachineryReborn.getEmptyRequirementTypeRegistrar().getKey(this);
  }
}
