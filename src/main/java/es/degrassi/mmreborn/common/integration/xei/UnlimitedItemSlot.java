package es.degrassi.mmreborn.common.integration.xei;

import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelper;

public class UnlimitedItemSlot extends ItemSlot
{
    private ItemStack stack = ItemStack.EMPTY;

    public UnlimitedItemSlot(Slot slot)
    {
        super(slot);
    }

    @Override
    public ItemStack getValue()
    {
        return this.stack;
    }

    @Override
    public ItemSlot setValue(ItemStack value, boolean notify)
    {
        if(value == null)
        {
            value = ItemStack.EMPTY;
        }

        if(ItemStack.matches(value, this.stack))
        {
            return this;
        }

        this.stack = value;

        ItemStack clamped = this.stack.copy();
        if(!clamped.isEmpty() && clamped.getCount() > 64)
        {
            clamped.setCount(64);
        }

        return super.setValue(clamped, notify);
    }

    @Override
    public ItemSlot setItem(ItemStack itemStack)
    {
        return this.setValue(itemStack, true);
    }

    @Override
    public ItemSlot setItem(ItemStack itemStack, boolean notify)
    {
        return this.setValue(itemStack, notify);
    }

    @Override
    protected void drawItemStack(GUIContext guiContext, ItemStack itemStack)
    {
        String text = this.stack.getCount() > 1 ? String.valueOf(this.stack.getCount()) : null;
        DrawerHelper.drawItemStack(guiContext.graphics, this.stack, 0, 0, guiContext.elementColor, text);
    }

    public static Slot createInfiniteSlot()
    {
        SimpleContainer container = new SimpleContainer(1) {
            
            @Override
            public int getMaxStackSize()
            {
                return Integer.MAX_VALUE;
            }
        };

        return new Slot(container, 0, 0, 0) {
            @Override
            public int getMaxStackSize()
            {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getMaxStackSize(ItemStack stack)
            {
                return Integer.MAX_VALUE;
            }
        };
    }
}
