package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import lombok.extern.log4j.Log4j2;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Kina on 2016/12/14.
 */
@Log4j2
public class ItemPipe extends ItemBlock{
    public ItemPipe(){
        super(RegistryEntries.BLOCK.pipe);
    }


    public static String getModelName(ItemStack stack){
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) return "missing";
        String s = compound.getBoolean("covered") ? "_covered" : "";
        s += EnumPipeMaterial.VALUES.get(compound.getInteger("material")).TYPE == EnumPipeMaterial.TextureType.GRADATION ? "_metallic" : "";
        return "pipe" + s;
    }

    public static int getColor(ItemStack stack, int layer){
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) return 0xffffff;
        if(layer == 0)
            return compound.hasKey("material", Constants.NBT.TAG_STRING) ? EnumPipeMaterial.valueOf(compound.getString("material")).COLOR : compound.hasKey("material", Constants.NBT.TAG_INT) ? EnumPipeMaterial.VALUES.get(compound.getInteger("material")).COLOR : 0xffffff;
        return compound.getInteger("color");
    }

    public static ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material, EnumDyeColor color){
        return createPipeStack(stack, material, true, ItemDye.DYE_COLORS[color.getDyeDamage()]);
    }

    public static ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material){
        return createPipeStack(stack, material, false, -1);
    }

    public static ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material, boolean covered, int color){
        NBTTagCompound compound = new NBTTagCompound();
        if(material == null){
            log.warn("Cannot create pipe stack! Instead it returns an empty stack.", new NullPointerException("Material is null"));
            return ItemStack.EMPTY;
        }
        {
            stack.setTagCompound(compound);
            compound.setString("material", material.name());
            compound.setBoolean("covered", covered);
            compound.setInteger("color", color);
        }
        return stack;
    }
}
