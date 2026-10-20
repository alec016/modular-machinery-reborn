package es.degrassi.mmr.api.entity;

import es.degrassi.mmr.client.integration.athena.model.hatch.HatchBakedModel;
import es.degrassi.mmr.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmr.common.machine.MachineComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.model.data.ModelData;

import javax.annotation.Nullable;

public interface MachineComponentEntity<T extends MachineComponent<?>> {
  @Nullable
  T provideComponent();

  default ModelData.Builder getModelDataBuilder(String mode) {
    return ModelData.builder()
        .with(HatchBakedModel.TEXTURE_DATA, getTextureData(mode));
  }

  default HatchTextureData getTextureData(String mode) {
    if (this instanceof BlockEntity blockEntity) {
      Identifier blockId = BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock());
      return HatchTextureData.withBlockDefault(blockId);
    }
    return HatchTextureData.withDefault(mode);
  }
}
