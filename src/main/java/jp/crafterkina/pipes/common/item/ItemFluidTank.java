package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.RegistryEntries;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Kina on 2016/12/28.
 */
public class ItemFluidTank extends ItemBlock{
    public ItemFluidTank(){
        super(RegistryEntries.BLOCK.fluid_tank);
    }

    public static int getColor(ItemStack stack, int layer){
        return 0xFFFFFF;
    }
}
