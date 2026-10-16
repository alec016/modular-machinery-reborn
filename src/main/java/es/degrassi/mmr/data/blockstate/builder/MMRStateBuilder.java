package es.degrassi.mmr.data.blockstate.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.degrassi.mmr.data.MMRTags;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class MMRStateBuilder<T extends MMRStateBuilder<T>> implements Supplier<JsonElement> {
  private TagKey<Block> connectTo;

  private Identifier center;
  private Identifier empty;
  private Identifier horizontal;
  private Identifier particle;
  private Identifier vertical;
  private Identifier overlay = null;
  private Identifier ovCenter = null;
  private Identifier ovVertical = null;
  private Identifier ovHorizontal = null;
  private Identifier ovParticle = null;

  protected abstract Identifier loader();

  @Override
  public JsonElement get() {
    return toJson();
  }

  public @NotNull JsonObject toJson() throws IllegalArgumentException {
    JsonObject blockState = new JsonObject();
    JsonObject variants = new JsonObject();
    JsonObject empty = new JsonObject();
    empty.addProperty("model", "minecraft:block/air");
    variants.add("", empty);
    blockState.add("variants", variants);
    blockState.addProperty("athena:loader", loader().toString());
    addCtmTextures(blockState);
    addConnectTo(blockState);
    return blockState;
  }

  private void addCtmTextures(JsonObject blockState) {
    if (center == null) throw new NullTextureException("Center");
    if (empty == null) throw new NullTextureException("Empty");
    if (horizontal == null) throw new NullTextureException("Horizontal");
    if (particle == null) throw new NullTextureException("Particle");
    if (vertical == null) throw new NullTextureException("Vertical");
    JsonObject ctmTextures = new JsonObject();
    ctmTextures.addProperty("center", center.toString());
    ctmTextures.addProperty("empty", empty.toString());
    ctmTextures.addProperty("horizontal", horizontal.toString());
    ctmTextures.addProperty("particle", particle.toString());
    ctmTextures.addProperty("vertical", vertical.toString());
    if (overlay != null)
      ctmTextures.addProperty("overlay", overlay.toString());
    if (ovCenter != null)
      ctmTextures.addProperty("ov_center", ovCenter.toString());
    if (ovVertical != null)
      ctmTextures.addProperty("ov_vertical", ovVertical.toString());
    if (ovHorizontal != null)
      ctmTextures.addProperty("ov_horizontal", ovHorizontal.toString());
    if (ovParticle != null)
      ctmTextures.addProperty("ov_particle", ovParticle.toString());
    blockState.add("ctm_textures", ctmTextures);
  }

  private void addConnectTo(JsonObject blockState) {
    if (connectTo == null) throw new IllegalArgumentException("Connect to can not be null");
    JsonObject tag = new JsonObject();
    tag.addProperty("type", "tag");
    tag.addProperty("tag", connectTo());
    blockState.add("connect_to", tag);
  }

  private @NotNull String connectTo() {
    return connectTo.location().toString();
  }

  private T self() {
    return (T) this;
  }

  public T connectToPlain() {
    connectTo = MMRTags.Blocks.PLAIN_CONNECTABLE;
    return self();
  }

  public T connectToReinforced() {
    connectTo = MMRTags.Blocks.REINFORCED_CONNECTABLE;
    return self();
  }

  public T center(Identifier center) {
    this.center = center;
    return self();
  }

  public T empty(Identifier empty) {
    this.empty = empty;
    return self();
  }

  public T horizontal(Identifier horizontal) {
    this.horizontal = horizontal;
    return self();
  }

  public T particle(Identifier particle) {
    this.particle = particle;
    return self();
  }

  public T vertical(Identifier vertical) {
    this.vertical = vertical;
    return self();
  }

  public T ovCenter(Identifier ovCenter) {
    this.ovCenter = ovCenter;
    return self();
  }

  public T ovVertical(Identifier ovVertical) {
    this.ovVertical = ovVertical;
    return self();
  }

  public T ovHorizontal(Identifier ovHorizontal) {
    this.ovHorizontal = ovHorizontal;
    return self();
  }

  public T ovParticle(Identifier ovParticle) {
    this.ovParticle = ovParticle;
    return self();
  }

  public T overlay(Identifier overlay) {
    this.overlay = overlay;
    return self();
  }

  private static class NullTextureException extends IllegalArgumentException {
    private NullTextureException(String texture) {
      super(texture + " texture can not be null");
    }
  }
}
