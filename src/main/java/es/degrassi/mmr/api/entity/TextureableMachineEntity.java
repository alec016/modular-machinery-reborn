package es.degrassi.mmr.api.entity;

import com.mojang.datafixers.util.Pair;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.machine.MachineHatchType;
import net.minecraft.resources.Identifier;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface TextureableMachineEntity {
  NamedMapCodec<Pair<Optional<Identifier>, Optional<Identifier>>> TEXTURES_CODEC =
      NamedCodec.record(instance -> instance.group(
        DefaultCodecs.IDENTIFIER.optionalFieldOf("base_texture").forGetter(Pair::getFirst),
        DefaultCodecs.IDENTIFIER.optionalFieldOf("overlay_texture").forGetter(Pair::getSecond)
      ).apply(instance, Pair::of), "Textures Pair without coloring"
  );

  NamedCodec<Pair<Boolean, Pair<Optional<Identifier>, Optional<Identifier>>>> CODEC =
      NamedCodec.record(instance2 -> instance2.group(
        NamedCodec.BOOL.fieldOf("should_color").forGetter(Pair::getFirst),
        TEXTURES_CODEC.fieldOf("textures").forGetter(Pair::getSecond)
      ).apply(instance2, Pair::of), "Textures Pair"
  );

  Identifier getMachineBaseTexture();

  void setMachineBaseTexture(Identifier newTexture);

  Identifier getMachineOverlayTexture();

  void setMachineOverlayTexture(Identifier newTexture);

  void setRequestModelUpdate(boolean request);

  boolean isRequestModelUpdate();

  MachineHatchType getHatchType();

  void resetTextures();
}
