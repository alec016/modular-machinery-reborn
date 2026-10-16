package es.degrassi.mmr.common.crafting.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CraftingStatus {
  public static final CraftingStatus SUCCESS = new CraftingStatus(Type.CRAFTING, null);
  public static final CraftingStatus MISSING_STRUCTURE = new CraftingStatus(Type.MISSING_STRUCTURE, null);
  public static final CraftingStatus NO_RECIPE = new CraftingStatus(Type.NO_RECIPE, null);

  public static final StreamCodec<RegistryFriendlyByteBuf, CraftingStatus> STREAM_CODEC = new StreamCodec<>() {
    @Override
    public CraftingStatus decode(RegistryFriendlyByteBuf buffer) {
      Type type = buffer.readEnum(Type.class);
      Component message = null;
      if (buffer.readBoolean()) {
        String json = buffer.readUtf();
        message = ComponentSerialization.CODEC.parse(
            buffer.registryAccess().createSerializationContext(JsonOps.INSTANCE),
            JsonParser.parseString(json)
        ).getOrThrow();
      }
      return new CraftingStatus(type, message);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CraftingStatus value) {
      buffer.writeEnum(value.status);
      buffer.writeBoolean(value.message != null);
      if (value.message != null) {
        DynamicOps<JsonElement> ops = buffer.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        String json = GsonHelper.toStableString(ComponentSerialization.CODEC.encodeStart(ops, value.message).getOrThrow());
        buffer.writeUtf(json);
      }
    }
  };

  @Getter
  private final Type status;
  @Nullable
  private final Component message;

  private CraftingStatus(Type status, @Nullable Component unlocMessage) {
    this.status = status;
    this.message = unlocMessage;
  }

  public Component getUnlocMessage() {
    return message != null ? message : this.status.getUnlocalizedDescription();
  }

  public boolean isCrafting() {
    return this.status.isCrafting();
  }

  public boolean isFailure() {
    return this.status.isFailure();
  }

  public boolean isMissingStructure() {
    return this.status == Type.MISSING_STRUCTURE;
  }

  public static CraftingStatus working() {
    return SUCCESS;
  }

  public static CraftingStatus failure(Component message) {
    return new CraftingStatus(Type.FAILURE, message);
  }

  public CompoundTag serializeNBT(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    tag.putString("type", this.status.getSerializedName());
    if (this.message != null) {
      DynamicOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
      tag.putString("message", GsonHelper.toStableString(
          ComponentSerialization.CODEC.encodeStart(ops, this.message).getOrThrow()
      ));
    }
    return tag;
  }

  public static CraftingStatus deserialize(ValueInput tag, HolderLookup.Provider registries) {
    return tag.getString("type").map(type ->
        tag.getString("message").map(json ->
            new CraftingStatus(Type.fromString(type), ComponentSerialization.CODEC.parse(
              RegistryOps.create(JsonOps.INSTANCE, registries),
              JsonParser.parseString(json)
            ).result().orElse(null))
        ).orElse(CraftingStatus.MISSING_STRUCTURE)
    ).orElse(CraftingStatus.MISSING_STRUCTURE);
  }

  public static CraftingStatus of(Type type, Component message) {
    return new CraftingStatus(type, message);
  }

  public static CraftingStatus of(String type, Component message) {
    return new CraftingStatus(Type.fromString(type), message);
  }

  public enum Type implements StringRepresentable {
    MISSING_STRUCTURE,
    NO_RECIPE,
    FAILURE,
    CRAFTING;

    public static Type fromString(String value) {
      return switch (value.toLowerCase(Locale.ROOT)) {
        case "missing_structure" -> MISSING_STRUCTURE;
        case "crafting" -> CRAFTING;
        case "failure" -> FAILURE;
        default -> NO_RECIPE;
      };
    }

    public boolean isCrafting() {
      return this == CRAFTING;
    }

    public boolean isFailure() {
      return this == FAILURE;
    }

    public Component getUnlocalizedDescription() {
      return Component.translatable("gui.controller.status." + getSerializedName());
    }

    @Override
    public @NotNull String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
}
