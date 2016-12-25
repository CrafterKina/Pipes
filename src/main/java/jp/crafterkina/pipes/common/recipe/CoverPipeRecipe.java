package jp.crafterkina.pipes.common.recipe;

import jp.crafterkina.pipes.common.RegistryEntries;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

/**
 * Created by Kina on 2016/12/25.
 */
public class CoverPipeRecipe implements IRecipe{
    public static final IRecipe CARPET = new ShapedOreRecipe(new ItemStack(RegistryEntries.ITEM.pipe, 1), "CCC", "CPC", "CCC", 'C', new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE), 'P', new ItemStack(RegistryEntries.ITEM.pipe, 1, OreDictionary.WILDCARD_VALUE));
    public static final IRecipe WOOL = new ShapedOreRecipe(new ItemStack(RegistryEntries.ITEM.pipe, 1), " W ", "WPW", " W ", 'W', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), 'P', new ItemStack(RegistryEntries.ITEM.pipe, 1, OreDictionary.WILDCARD_VALUE));

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn){
        if(CARPET.matches(inv, worldIn)){
            int damage = inv.getStackInSlot(0).getItemDamage();
            for(int i = 0; i < inv.getSizeInventory(); i++){
                ItemStack slot = inv.getStackInSlot(i);
                if(slot.getItem() == Item.getItemFromBlock(Blocks.CARPET) && damage != slot.getItemDamage())
                    return false;
            }
            return true;
        }else if(WOOL.matches(inv, worldIn)){
            int damage = inv.getStackInSlot(1).getItemDamage();
            for(int i = 0; i < inv.getSizeInventory(); i++){
                ItemStack slot = inv.getStackInSlot(i);
                if(slot.getItem() == Item.getItemFromBlock(Blocks.WOOL) && damage != slot.getItemDamage()) return false;
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv){
        ItemStack result = inv.getStackInRowAndColumn(1, 1).copy().splitStack(1);
        int color = ItemDye.DYE_COLORS[EnumDyeColor.byMetadata(inv.getStackInSlot(1).getItemDamage()).getDyeDamage()];
        NBTTagCompound compound = result.getTagCompound();
        if(compound == null) return ItemStack.EMPTY;
        compound.setBoolean("covered", true);
        compound.setInteger("color", color);
        return result;
    }

    @Override
    public int getRecipeSize(){
        return 9;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput(){
        return new ItemStack(RegistryEntries.ITEM.pipe);
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv){
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
