package jp.crafterkina.pipes.common.recipe.vanilla;

import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import jp.crafterkina.pipes.common.recipe.CoverPipeRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.merchant_phone;
import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.pipe;

public enum CraftManager{
    INSTANCE;

    public void register(){
        GameRegistry.addRecipe(new ItemStack(merchant_phone), "IRI", "IGI", "IEI", 'G', Items.EMERALD, 'R', Blocks.REDSTONE_TORCH, 'E', Items.ENDER_PEARL, 'I', Items.IRON_INGOT);
        GameRegistry.addShapelessRecipe(new ItemStack(merchant_phone), merchant_phone);
        GameRegistry.addRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.WOOD), "SLS", "L L", "SLS", 'S', Items.STICK, 'L', Blocks.LADDER);
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.STONE), "SLS", "L L", "SLS", 'S', "cobblestone", 'L', Blocks.LADDER));
        GameRegistry.addRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.IRON), "SLS", "L L", "SLS", 'S', Items.IRON_INGOT, 'L', Blocks.RAIL);
        GameRegistry.addRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.DIAMOND), "SLS", "L L", "SLS", 'S', Items.DIAMOND, 'L', Blocks.RAIL);
        GameRegistry.addRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.GOLD), "SLS", "L L", "SLS", 'S', Items.GOLD_NUGGET, 'L', Blocks.GOLDEN_RAIL);
        for(EnumDyeColor color : EnumDyeColor.values()){
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.WOOD, color), "SLS", "LWL", "SLS", 'S', Items.STICK, 'L', Blocks.LADDER, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.STONE, color), "SLS", "LWL", "SLS", 'S', "cobblestone", 'L', Blocks.LADDER, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.IRON, color), "SLS", "LWL", "SLS", 'S', Items.IRON_INGOT, 'L', Blocks.RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.DIAMOND, color), "SLS", "LWL", "SLS", 'S', Items.DIAMOND, 'L', Blocks.RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.GOLD, color), "SLS", "LWL", "SLS", 'S', Items.GOLD_NUGGET, 'L', Blocks.GOLDEN_RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
        }
        GameRegistry.addRecipe(new CoverPipeRecipe());
        RecipeSorter.register("jp.crafterkina.pipes:pipe_cover", CoverPipeRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
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
