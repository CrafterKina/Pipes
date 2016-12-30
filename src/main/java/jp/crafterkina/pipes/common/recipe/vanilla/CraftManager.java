package jp.crafterkina.pipes.common.recipe.vanilla;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import jp.crafterkina.pipes.common.recipe.CoverPipeRecipe;
import jp.crafterkina.pipes.common.recipe.ProcessorAccelerationRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.*;
import static jp.crafterkina.pipes.common.item.ItemPipe.createPipeStack;

public enum CraftManager{
    INSTANCE;

    public void register(){
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(merchant_phone), "IRI", "IGI", "IEI", 'G', "gemEmerald", 'R', Blocks.REDSTONE_TORCH, 'E', Items.ENDER_PEARL, 'I', "ingotIron"));
        GameRegistry.addShapelessRecipe(new ItemStack(merchant_phone), merchant_phone);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(processor_base), "ISI", "SSS", "ISI", 'I', "stickWood", 'S', "stone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(strategy_acceleration), 1.25), " S ", "SPS", " S ", 'S', Items.SUGAR, 'P', processor_base));
        GameRegistry.addRecipe(new ProcessorAccelerationRecipe());
        RecipeSorter.register("jp.crafterkina.pipes:processor_acceleration", ProcessorAccelerationRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped before:minecraft:shapeless");
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.Material.WOOD.getStack(), "W W", "WPW", " W ", 'W', "plankWood", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.Material.STONE.getStack(), "W W", "WPW", " W ", 'W', "cobblestone", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.Material.IRON.getStack(), "W W", "WPW", " W ", 'W', "ingotIron", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.Material.DIAMOND.getStack(), "W W", "WPW", " W ", 'W', "gemDiamond", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(StrategyExtraction.Material.GOLD.getStack(), "W W", "WPW", " W ", 'W', "ingotGold", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(strategy_oneway), " I ", "IPI", " I ", 'I', "ingotIron", 'P', processor_base));
        GameRegistry.addRecipe(new ShapedOreRecipe(RegistryEntries.ITEM.fluid_tank, "GSG", "F F", "GSG", 'G', "paneGlassColorless", 'S', "slabWood", 'F', Blocks.OAK_FENCE));
        registerPipeRecipes();
    }

    private void registerPipeRecipes(){
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.WOOD), "SLS", "L L", "SLS", 'S', "stickWood", 'L', Blocks.LADDER));
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.STONE), "SLS", "L L", "SLS", 'S', "cobblestone", 'L', Blocks.LADDER));
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.IRON), "SLS", "L L", "SLS", 'S', "ingotIron", 'L', Blocks.RAIL));
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.DIAMOND), "SLS", "L L", "SLS", 'S', "gemDiamond", 'L', Blocks.RAIL));
        GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.GOLD), "SLS", "L L", "SLS", 'S', "nuggetGold", 'L', Blocks.GOLDEN_RAIL));
        for(EnumDyeColor color : EnumDyeColor.values()){
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.WOOD, color), "SLS", "LWL", "SLS", 'S', "stickWood", 'L', Blocks.LADDER, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.STONE, color), "SLS", "LWL", "SLS", 'S', "cobblestone", 'L', Blocks.LADDER, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.IRON, color), "SLS", "LWL", "SLS", 'S', "ingotIron", 'L', Blocks.RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.DIAMOND, color), "SLS", "LWL", "SLS", 'S', "gemDiamond", 'L', Blocks.RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
            GameRegistry.addRecipe(new ShapedOreRecipe(createPipeStack(new ItemStack(pipe, 8), EnumPipeMaterial.GOLD, color), "SLS", "LWL", "SLS", 'S', "nuggetGold", 'L', Blocks.GOLDEN_RAIL, 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())));
        }
        GameRegistry.addRecipe(new CoverPipeRecipe());
        RecipeSorter.register("jp.crafterkina.pipes:pipe_cover", CoverPipeRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
    }
}
