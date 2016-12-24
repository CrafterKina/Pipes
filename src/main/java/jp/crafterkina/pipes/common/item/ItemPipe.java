package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.RegistryEntries;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Kina on 2016/12/14.
 */
public class ItemPipe extends ItemBlock{
    public ItemPipe(){
        super(RegistryEntries.BLOCK.pipe);
    }


    public static String getModelName(ItemStack stack){
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) return "missing";
        String s = compound.getBoolean("covered") ? "_covered" : "";
        return "pipe" + s;
    }

    public static int getColor(ItemStack stack, int layer){
        if(layer == 0) return 0x9F844D;
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) return 0xffffff;
        return compound.getInteger("color");
    }
}
