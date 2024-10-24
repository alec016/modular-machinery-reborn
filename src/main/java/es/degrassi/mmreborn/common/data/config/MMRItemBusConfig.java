package es.degrassi.mmreborn.common.data.config;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "item_bus")
public class MMRItemBusConfig implements ConfigData {
  @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
  public Tier TINY = new Tier(ItemBusSize.TINY);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier SMALL = new Tier(ItemBusSize.SMALL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier NORMAL = new Tier(ItemBusSize.NORMAL);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier REINFORCED = new Tier(ItemBusSize.REINFORCED);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier BIG = new Tier(ItemBusSize.BIG);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier HUGE = new Tier(ItemBusSize.HUGE);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier LUDICROUS = new Tier(ItemBusSize.LUDICROUS);

//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 1]")
//  public int TINY_E = 1;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 4]")
//  public int SMALL_E = 4;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 6]")
//  public int NORMAL_E = 6;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 9]")
//  public int REINFORCED_E = 9;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 12]")
//  public int BIG_E = 12;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 16]")
//  public int HUGE_E = 16;
//  @ConfigEntry.Category("Size")
//  @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
//  @Comment("Slot number of the item bus, default: 32]")
//  public int LUDICROUS_E = 32;

  public static class Tier {
    @ConfigEntry.BoundedDiscrete(max = Integer.MAX_VALUE)
    @Comment("Slot number of the item bus")
    public int size;

    public Tier(ItemBusSize tier) {
      size = tier.defaultConfigSize;
    }
  }

  public int itemSize(ItemBusSize size) {
    return switch(size) {
      case TINY -> TINY.size;
      case SMALL -> SMALL.size;
      case NORMAL -> NORMAL.size;
      case REINFORCED -> REINFORCED.size;
      case BIG -> BIG.size;
      case HUGE -> HUGE.size;
      case LUDICROUS -> LUDICROUS.size;
    };
  }
}
