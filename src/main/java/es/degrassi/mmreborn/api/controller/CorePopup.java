package es.degrassi.mmreborn.api.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record CorePopup(int maxCores, Map<String, List<CompoundTag>> pages) {
  private static final Gson GSON = new Gson();
  private static final Codec<Map<String, List<CompoundTag>>> PAGES = NamedCodec.unboundedMap(
      NamedCodec.STRING,
      NamedCodec.of(CompoundTag.CODEC).listOf(),
      "CorePages"
  ).codec();

  public static final StreamCodec<RegistryFriendlyByteBuf, CorePopup> STREAM_CODEC = new StreamCodec<>() {
    @Override
    public @NotNull CorePopup decode(RegistryFriendlyByteBuf buffer) {
      int maxCores = buffer.readInt();
      JsonElement jsonelement = GsonHelper.fromJson(GSON, buffer.readUtf(Integer.MAX_VALUE), JsonElement.class);
      DataResult<Map<String, List<CompoundTag>>> dataresult = PAGES.parse(JsonOps.INSTANCE, jsonelement);
      Map<String, List<CompoundTag>> pages = dataresult.getOrThrow(s -> new DecoderException("Failed to decode json: " + s));
      return new CorePopup(maxCores, pages);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CorePopup data) {
      buffer.writeInt(data.maxCores);
      DataResult<JsonElement> dataresult = PAGES.encodeStart(JsonOps.INSTANCE, data.pages);
      buffer.writeUtf(GSON.toJson(dataresult.getOrThrow(s -> new EncoderException("Failed to encode: " + s + " " + data.pages))), Integer.MAX_VALUE);
    }
  };
}
