package jp.crafterkina.pipes.plugin.jei.crafting;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kina on 2016/12/25.
 */
public class CoverPipeWithCarpetRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper{
    private final List<ItemStack> inputs;
    private final ItemStack output;

    public CoverPipeWithCarpetRecipeWrapper(EnumDyeColor color, EnumPipeMaterial material){
        ItemStack uncovered = createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), material);
        ItemStack covered = createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), material, color);
        ItemStack cover = new ItemStack(Blocks.CARPET, 1, color.getMetadata());
        inputs = Arrays.asList(cover, cover, cover, cover, uncovered, cover, cover, cover, cover);
        output = covered;
    }

    public static List<CoverPipeWithCarpetRecipeWrapper> getRecipes(){
        return Arrays.stream(EnumDyeColor.values()).flatMap(c -> EnumPipeMaterial.VALUES.stream().map(m -> new CoverPipeWithCarpetRecipeWrapper(c, m))).collect(Collectors.toList());
    }

    @Override
    public int getWidth(){
        return 3;
    }

    @Override
    public int getHeight(){
        return 3;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients){
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }

    private ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material, EnumDyeColor color){
        return createPipeStack(stack, material, true, ItemDye.DYE_COLORS[color.getDyeDamage()]);
    }

    private ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material){
        return createPipeStack(stack, material, false, -1);
    }

    private ItemStack createPipeStack(ItemStack stack, EnumPipeMaterial material, boolean covered, int color){
        NBTTagCompound compound = new NBTTagCompound();
        {
            stack.setTagCompound(compound);
            compound.setInteger("material", material.ordinal());
            compound.setBoolean("covered", covered);
            compound.setInteger("color", color);
        }
        return stack;
    }
}
