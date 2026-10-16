package es.degrassi.mmr.client.screen;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.screen.TooltipRender;
import es.degrassi.mmr.client.container.RedstonePortContainer;
import es.degrassi.mmr.client.screen.widget.EnumButton;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.network.client.CRedstoneButtonModeClickedPacket;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstonePortScreen extends BaseScreen<RedstonePortContainer, RedstonePortEntity> {
  public RedstonePortScreen(RedstonePortContainer menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title, true);
  }

  @Override
  protected void renderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
    GuiGraphicsExtractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
  }

  @Override
  public @Nullable Identifier getTexture() {
    return ModularMachineryReborn.rl("background");
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected void renderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY) {
    clearWidgets();
    if (getTexture() != null) {
      GuiGraphicsExtractor.pose().pushMatrix();
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0, 0, 176, 176, 176, 176);
      GuiGraphicsExtractor.pose().popMatrix();
    }
    GridLayout layout = new GridLayout(leftPos + 8, topPos + 25);
    GridLayout.RowHelper row = layout.createRowHelper(5);
    row.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().paddingHorizontal(20);
    row.addChild(
        Button
            .builder(Component.empty(), b -> {
              if (b instanceof EnumButton enumButton && enumButton.getValue() instanceof IOType v) {
                ClientPacketDistributor.sendToServer(new CRedstoneButtonModeClickedPacket(v, entity.getBlockPos()));
                getMenu().broadcastChanges();
                entity.setMode(v);
              }
            })
            .build(builder -> new EnumButton<>(
                builder,
                value -> Component.translatable("mmr.gui.tooltip.redstone.button.mode." + value.getSerializedName()),
                List.of(IOType.INPUT, IOType.OUTPUT),
                entity.getMode()
            ))
    );
    layout.arrangeElements();
    layout.visitWidgets(this::addRenderableWidget);
    for (Slot slot : getMenu().slots) {
      GuiGraphicsExtractor.blit(BASE_SLOT, slot.x + getLeftPos() - 1, slot.y + getTopPos() - 1, 0, 0,
          TextureSizeHelper.getWidth(BASE_SLOT),
          TextureSizeHelper.getHeight(BASE_SLOT),
          TextureSizeHelper.getWidth(BASE_SLOT),
          TextureSizeHelper.getHeight(BASE_SLOT));
    }
  }

  @Override
  protected void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y) {
    super.renderTooltip(GuiGraphicsExtractor, x, y);
    this.children()
        .stream()
        .filter(listener -> listener instanceof TooltipRender)
        .map(listener -> (TooltipRender) listener)
        .forEach(renderer -> renderer.renderTooltip(GuiGraphicsExtractor, x, y));
  }
}
