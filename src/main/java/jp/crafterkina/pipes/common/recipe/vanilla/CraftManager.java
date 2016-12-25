package jp.crafterkina.pipes.common.recipe.vanilla;

import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import jp.crafterkina.pipes.common.recipe.CoverPipeRecipe;
import jp.crafterkina.pipes.common.recipe.ProcessorAccelerationRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.*;

public enum CraftManager{
    INSTANCE;

    public void register(){
        GameRegistry.addRecipe(new ItemStack(merchant_phone), "IRI", "IGI", "IEI", 'G', Items.EMERALD, 'R', Blocks.REDSTONE_TORCH, 'E', Items.ENDER_PEARL, 'I', Items.IRON_INGOT);
        GameRegistry.addShapelessRecipe(new ItemStack(merchant_phone), merchant_phone);
        GameRegistry.addRecipe(new ItemStack(processor_base), "ISI", "SSS", "ISI", 'I', Items.STICK, 'S', Blocks.STONE);
        GameRegistry.addRecipe(StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(strategy_acceleration), 1.25), " S ", "SPS", " S ", 'S', Items.SUGAR, 'P', processor_base);
        GameRegistry.addRecipe(new ProcessorAccelerationRecipe());
        RecipeSorter.register("jp.crafterkina.pipes:processor_acceleration", ProcessorAccelerationRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped before:minecraft:shapeless");
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.ItemExtractionProcessor.createStack(new ItemStack(strategy_extraction), 50, 1, 1, 0x9F844D), "W W", "WPW", " W ", 'W', "plankWood", 'P', processor_base));
        GameRegistry.addRecipe(new ItemStack(strategy_oneway), " I ", "IPI", " I ", 'I', Items.IRON_INGOT, 'P', processor_base);
        registerPipeRecipes();
    }

    private void registerPipeRecipes(){
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
