package es.degrassi.mmreborn.common.integration.xei;

import com.google.common.collect.Maps;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scene;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.SplitView;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.DummyWorld;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MultiblockRecipe {
  public static final int WIDTH = 200;
  public static final int HEIGHT = 180;
  protected final DynamicMachine multiblock;

  public MultiblockRecipe(DynamicMachine multiblock) {
    this.multiblock = multiblock;
  }

  public @Nullable ResourceLocation getId() {
    return multiblock.getRegistryName().withPrefix("/multiblock_preview/");
  }

  public ModularUI createModularUI() {
    DummyWorld scene = MultiblockScene.createTestScene(multiblock);
    Map<ItemSlot, List<ItemStack>> requiredItems = Maps.newHashMap();
    var itemsView = new ScrollerView();
    itemsView.addScrollViewChildren(new ItemSlot()
        .setItem(ControllerItem.makeMachineItem(multiblock.getRegistryName()))
        .xeiRecipeIngredient(IngredientIO.INPUT)
        .xeiRecipeSlot()
    );
    multiblock.getStacks()
        .stream()
        .filter(stacks -> !stacks.isEmpty() && !stacks.stream().allMatch(ItemStack::isEmpty))
        .forEach(stacks -> {
            var slot = new UnlimitedItemSlot();
            var first = stacks.getFirst();
            ItemStack displayStack = MultiblockScene.blockTimer.getOrDefault(stacks, first).copy();
            if (displayStack.isEmpty()) {
                displayStack = first.copy();
            }
            displayStack.setCount(first.getCount());
            requiredItems.put(
                slot
                    .setItem(displayStack)
                    .xeiRecipeIngredient(IngredientIO.INPUT)
                    .xeiRecipeSlot(),
                stacks
            );
      });
    
    var blockLabel = new Label().setText("");
    AtomicReference<BlockPos> selectedPos = new AtomicReference<>(null);
    return ModularUI.of(UI.of(new UIElement()
            .layout(layout -> layout.widthPercent(100).heightPercent(100))
            .addChildren(
                new SplitView.Horizontal()
                    .left(itemsView.addScrollViewChildren(requiredItems.keySet().toArray(ItemSlot[]::new))
                        .layout(layout -> layout.widthPercent(100).heightPercent(100))
                    )
                    .right(new UIElement()
                        .layout(layout -> layout.widthPercent(100).heightPercent(100))
                        .addChildren(
                            new SplitView.Vertical()
                                .top(blockLabel)
                                .bottom(new Scene()
                                    .createScene(scene)
                                    .useOrtho()
                                    .setOrthoRange(.5f)
                                    .setTickWorld(true)
                                    .setRenderedCore(scene.getFilledBlocks().longStream().mapToObj(BlockPos::of).toList())
                                    .setBeforeWorldRender(s -> {
                                      MultiblockScene.tick(scene, multiblock);
                                    })
                                    .setAfterWorldRender(s -> {
                                      itemsView.viewContainer.getChildren()
                                          .forEach(ele -> {
                                            if (ele instanceof ItemSlot slot) {
                                              if (requiredItems.containsKey(slot)) {
                                                var stack = MultiblockScene.blockTimer.getOrDefault(requiredItems.get(slot), ItemStack.EMPTY);
                                                slot.setItem(stack);
                                              }
                                            }
                                          });
                                      if (selectedPos.get() != null) {
                                        var state = scene.getBlockState(selectedPos.get());
                                        if (state.isAir()) {
                                          blockLabel.setText("");
                                        } else {
                                          if (state.getBlock() instanceof BlockController) {
                                            blockLabel.setText(multiblock.getName());
                                          } else {
                                            blockLabel.setText(state.getBlock().getName());
                                          }
                                        }
                                      }
                                    })
                                    .setOnSelected((pos, side) -> {
                                      blockLabel.setText(scene.getBlockState(pos).getBlock().getName());
                                      selectedPos.set(pos);
                                    })
                                    .setShowHoverBlockTips(true)
                                    .layout(layout -> layout.widthPercent(100).heightPercent(100))
                                    .addClass("panel_bg")
                                ).setPercentage(5).addClass("panel_bg")
                        )
                    ).setPercentage(15f)
            ),
        List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN))
    ));
  }
}
