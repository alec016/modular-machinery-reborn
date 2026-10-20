package es.degrassi.mmr.common.registration;

import es.degrassi.experiencelib.api.definition.ItemDefinition;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.item.ItemBlueprint;
import es.degrassi.mmr.common.item.ItemModularium;
import es.degrassi.mmr.common.item.StructureCreatorItem;
import es.degrassi.mmr.common.item.StructureCreatorItemMode;
import es.degrassi.mmr.common.item.StructureTemplateItem;
import es.degrassi.mmr.common.item.WrenchItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static es.degrassi.mmr.ModularMachineryReborn.rootLC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRegistration {
  private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
  public static final DeferredRegister.Items DR = DeferredRegister.createItems(ModularMachineryReborn.MODID);

  public static final ItemDefinition<ItemBlueprint> BLUEPRINT = item(
      "Machine Blueprint", 
      rootLC("blueprint"),
      ItemBlueprint::new
  );
  public static final ItemDefinition<ItemModularium> MODULARIUM = item(
      "Modularium", 
      rootLC("modularium"),
      ItemModularium::new
  );
  public static final ItemDefinition<WrenchItem> WRENCH = item(
      "Wrench",
      rootLC("wrench"), 
      WrenchItem::new
  );
  public static final ItemDefinition<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_SINGLE = item(
      "Structure Creator (Single)",
      rootLC("structure_creator_single"),
      props -> new StructureCreatorItem(props, StructureCreatorItemMode.SINGLE)
  );
  public static final ItemDefinition<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_VEIN = item(
      "Structure Creator (Proximity (Vein))",
      rootLC("structure_creator_vein"),
      props -> new StructureCreatorItem(props, StructureCreatorItemMode.VEIN)
  );
  public static final ItemDefinition<StructureCreatorItem> STRUCTURE_CREATOR_ITEM_BOX = item(
      "Structure Creator (Box)",
      rootLC("structure_creator_box"),
      props -> new StructureCreatorItem(props.component(DataComponentRegistration.STRUCTURE_CREATOR_BOX_CURRENT, true), StructureCreatorItemMode.BOX)
  );
  public static final ItemDefinition<StructureTemplateItem> STRUCTURE_TEMPLATE_ITEM = item(
      "Structure Template (Incomplete)",
      rootLC("structure_template_item"), 
      StructureTemplateItem::new
  );

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }
  
  public static List<ItemDefinition<?>> getItems() {
    return Collections.unmodifiableList(ITEMS);
  }
  
  static <T extends Item> ItemDefinition<T> item(
    String englishName,
    String id,
    Function<Item.Properties, T> factory
  ) {
    var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
    ITEMS.add(definition);
    return definition;
  }
}
