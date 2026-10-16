package es.degrassi.mmr.common.machine;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.codec.DefaultCodecs;

import java.io.File;
import java.util.Locale;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

@Getter
public class MachineLocation {
  public static final NamedCodec<MachineLocation> CODEC = NamedCodec.record(machineLocationInstance -> machineLocationInstance.group(
      DefaultCodecs.IDENTIFIER.fieldOf("id").forGetter(MachineLocation::getId),
      Loader.CODEC.fieldOf("loader").forGetter(MachineLocation::getLoader),
      NamedCodec.STRING.fieldOf("packName").forGetter(MachineLocation::getPackName)
    ).apply(machineLocationInstance, MachineLocation::new), "Machine location"
  );

  private final Identifier id;
  private final Loader loader;
  private final String packName;

  private MachineLocation(Identifier id, Loader loader, String packName) {
    this.id = id;
    this.loader = loader;
    this.packName = packName;
  }

  public static MachineLocation fromLoader(Loader loader, Identifier id, String packName) {
    return switch (loader) {
      case DEFAULT -> fromDefault(id, packName);
      case DATAPACK -> fromDatapack(id, packName);
      case DATAPACK_ZIP -> fromDatapackZip(id, packName);
      case KUBEJS -> fromKubeJS(id, packName);
      case KUBEJS_SCRIPT -> fromKubeJSScript(id, packName);
    };
  }

  public static MachineLocation fromDefault(Identifier id, String packName) {
    return new MachineLocation(id, Loader.DEFAULT, packName);
  }

  public static MachineLocation fromDatapack(Identifier id, String packName) {
    if(packName.startsWith("file"))
      packName = packName.substring(5);
    return new MachineLocation(id, Loader.DATAPACK, packName);
  }

  public static MachineLocation fromDatapackZip(Identifier id, String packName) {
    return new MachineLocation(id, Loader.DATAPACK_ZIP, packName);
  }

  public static MachineLocation fromKubeJS(Identifier id, String packName) {
    return new MachineLocation(id, Loader.KUBEJS, packName);
  }

  public static MachineLocation fromKubeJSScript(Identifier id, String packName) {
    return new MachineLocation(id, Loader.KUBEJS_SCRIPT, packName);
  }

  @Nullable
  public File getFile(MinecraftServer server) {
    String pathFromData = "data" + File.separator + this.id.getNamespace() + File.separator + "machines" + File.separator + this.id.getPath() + ".json";
    String kubejsPath = server.getFile("kubejs" + File.separator + pathFromData).toFile().getPath();
    kubejsPath = kubejsPath.substring(2);
    return switch(this.loader) {
      case DATAPACK -> server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(this.packName + File.separator + pathFromData).normalize().toFile();
      case KUBEJS -> new File(kubejsPath);
      default -> null;
    };
  }

  public boolean canEdit() {
    return this.loader.canEdit;
  }

  public MutableComponent getText() {
    return this.loader.getTranslatedName().append(Component.literal(" : " + this.packName));
  }

  public enum Loader {
    DEFAULT(false, ChatFormatting.BLACK),
    DATAPACK(true, ChatFormatting.DARK_GREEN),
    DATAPACK_ZIP(false, ChatFormatting.DARK_RED),
    KUBEJS(true, ChatFormatting.DARK_PURPLE),
    KUBEJS_SCRIPT(false, ChatFormatting.DARK_RED);

    public static final NamedCodec<Loader> CODEC = NamedCodec.enumCodec(Loader.class);

    private final boolean canEdit;
    private final ChatFormatting color;

    Loader(boolean canEdit, ChatFormatting color) {
      this.canEdit = canEdit;
      this.color = color;
    }

    public MutableComponent getTranslatedName() {
      return Component.translatable("modular_machinery_reborn.machine.loader." + this.name().toLowerCase(Locale.ROOT)).withStyle(this.color);
    }
  }
}
