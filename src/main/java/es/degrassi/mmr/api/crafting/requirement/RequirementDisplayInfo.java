package es.degrassi.mmr.api.crafting.requirement;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RequirementDisplayInfo implements IDisplayInfo {

  private static final IDisplayInfoRenderer DEFAULT_RENDERER = new Texture(ModularMachineryReborn.rl("textures/gui/create_icon.png"), 10,
      10, 0, 0);

  private IDisplayInfoRenderer renderer = DEFAULT_RENDERER;
  @Getter
  private final List<Pair<Component, TooltipPredicate>> tooltips = new ArrayList<>();
  private ClickAction clickAction;

  @Override
  public RequirementDisplayInfo setTextureIcon(Identifier icon, int width, int height, int u, int v) {
    this.renderer = new Texture(icon, width, height, u, v);

    return this;
  }

  @Override
  public IDisplayInfo setSpriteIcon(Identifier atlas, Identifier sprite) {
    this.renderer = new Sprite(atlas, sprite);
    return this;
  }

  @Override
  public RequirementDisplayInfo setItemIcon(ItemStack stack) {
    this.renderer = new Item(stack);
    return this;
  }

  @Override
  public RequirementDisplayInfo addTooltip(Component tooltip, TooltipPredicate predicate) {
    this.tooltips.add(Pair.of(tooltip, predicate));
    return this;
  }

  @Override
  public void setClickAction(ClickAction clickAction) {
    this.clickAction = clickAction;
  }

  public void renderIcon(GuiGraphicsExtractor graphics, int size) {
    this.renderer.render(graphics, size);
  }

  public boolean hasClickAction() {
    return this.clickAction != null;
  }

  // TODO: modify this to use new syntax
  public boolean handleClick(DynamicMachine machine, MachineRecipe recipe, int button) {
    if (hasClickAction()) {
      this.clickAction.handleClick(machine, recipe, button);
      return true;
    }
    return false;
  }

  public boolean shouldRender() {
    return this.renderer != DEFAULT_RENDERER || !this.tooltips.isEmpty() || this.hasClickAction();
  }

  public interface IDisplayInfoRenderer {
    void render(GuiGraphicsExtractor graphics, int size);
  }

  private record Item(ItemStack stack) implements IDisplayInfoRenderer {
    @Override
    public void render(GuiGraphicsExtractor graphics, int size) {
      graphics.pose().scale(size / 16.0F, size / 16.0F);
      graphics.item(this.stack, 0, 0);
    }
  }

  private record Texture(Identifier icon, int width, int height, int u, int v) implements IDisplayInfoRenderer {
    @Override
    public void render(GuiGraphicsExtractor graphics, int size) {
      int textureWidth = TextureSizeHelper.getWidth(this.icon);
      int textureHeight = TextureSizeHelper.getHeight(this.icon);
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.icon, 0, 0, size, size, this.u, this.v, textureWidth, textureHeight, textureWidth, textureHeight);
    }
  }

  private record Sprite(Identifier atlas, Identifier sprite) implements IDisplayInfoRenderer {
    @Override
    public void render(GuiGraphicsExtractor graphics, int size) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graphics.getSprite(new SpriteId(this.atlas, this.sprite)), 0, 0, size, size);
    }
  }
}