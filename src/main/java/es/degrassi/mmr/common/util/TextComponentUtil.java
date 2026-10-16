package es.degrassi.mmr.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class TextComponentUtil {
  public static MutableComponent build(Object... components) {
    MutableComponent result = null;
    Style cachedStyle = Style.EMPTY;
    for (Object component : components) {
      if (component == null) {
        //If the component doesn't exist just skip it
        continue;
      } else if (component instanceof Holder<?> holder && holder.isBound()) {
        //Try to get the value of a holder if it has one and get the translation based on the stored value
        component = holder.value();
      }
      MutableComponent current = null;
      switch (component) {
        case TextColor color -> cachedStyle = cachedStyle.withColor(color);
        //Just append if a text component is being passed
        case Component c -> current = c.copy();
        case ChatFormatting formatting -> cachedStyle = cachedStyle.applyFormat(formatting);
        case ClickEvent event -> cachedStyle = cachedStyle.withClickEvent(event);
        case HoverEvent event -> cachedStyle = cachedStyle.withHoverEvent(event);
        case Block block -> current = block.getName().copy();
        case Item item -> current = item.getName(item.getDefaultInstance()).copy();
        case ItemStack stack -> current = stack.getHoverName().copy();
        case FluidStack stack -> current = stack.getHoverName().copy();
        case Fluid fluid -> current = fluid.getFluidType().getDescription().copy();
        case EntityType<?> entityType -> current = entityType.getDescription().copy();
        case Level level -> current = level.getDescription().copy();
        case Direction direction -> current = Component.literal(direction.getName());
        case Boolean bool -> current = Component.literal(bool.toString());
        //Fallback to a generic replacement
        // this handles strings, numbers, and any type we don't necessarily know about
        default -> current = getString(component.toString());
      }
      if (current == null) {
        //If we don't have a component to add, don't
        continue;
      }
      if (!cachedStyle.isEmpty()) {
        //Apply the style and reset
        current.setStyle(cachedStyle);
        cachedStyle = Style.EMPTY;
      }
      if (result == null) {
        result = current;
      } else {
        result.append(current);
      }
    }
    if (result == null) result = Component.empty();
    return result;
  }

  public static MutableComponent getString(String component) {
    return Component.literal(cleanString(component));
  }

  private static String cleanString(String component) {
    return component.replace("\u00A0", " ")//non-breaking space
        .replace("\u202f", " ");//narrow non-breaking space
  }
}
