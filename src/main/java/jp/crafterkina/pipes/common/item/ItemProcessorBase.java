package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * @author Kina
 */
public class ItemProcessorBase extends Item{
    public ItemProcessorBase(){
        setCreativeTab(EnumCreativeTab.PROCESSOR.tab);
        setUnlocalizedName(MOD_ID + ".processor_base");
    }

    public static int getColor(ItemStack stack, int layer){
        return stack.getItemDamage() != 0 ? 0x9fc2fc : 0xffffff;
    }

    @Override
    public int getItemStackLimit(ItemStack stack){
        return stack.getItemDamage() != 0 ? 1 : super.getItemStackLimit(stack);
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack stack){
        return super.getUnlocalizedName(stack) + (stack.getItemDamage() != 0 ? "_wet" : "");
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items){
        if(!isInCreativeTab(tab)) return;
        super.getSubItems(tab, items);
        items.add(new ItemStack(this, 1, 1));
    }
}
