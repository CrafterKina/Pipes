package jp.crafterkina.pipes.common;

import jp.crafterkina.pipes.common.block.BlockPipe;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemMerchantPhone;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.item.ItemProcessorBase;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyOneway;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;
import static jp.crafterkina.pipes.common.item.ItemPipe.createPipeStack;
import static org.apache.commons.lang3.tuple.Pair.of;

public class CommonProxy{
    protected static ResourceLocation getResourceLocation(String name){
        return new ResourceLocation(MOD_ID, name);
    }

    @SafeVarargs
    private static <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event, Pair<T, String>... targets){
        Arrays.stream(targets).map(t -> t.getLeft().setRegistryName(getResourceLocation(t.getRight()))).forEach(event.getRegistry()::register);
    }

    @SafeVarargs
    private static void register(Class<? extends TileEntity>... te){
        Arrays.stream(te).forEach(t -> GameRegistry.registerTileEntity(t, t.getCanonicalName()));
    }

    @OverridingMethodsMustInvokeSuper
    @SubscribeEvent
    protected void registerRecipes(RegistryEvent.Register<IRecipe> event){
        event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("wood_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.WOOD), "SLS", "L L", "SLS", 'S', "stickWood", 'L', Item.getItemFromBlock(Blocks.LADDER)).setRegistryName(MOD_ID, "wood_pipe"));
        event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("stone_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.STONE), "SLS", "L L", "SLS", 'S', "cobblestone", 'L', Item.getItemFromBlock(Blocks.LADDER)).setRegistryName(MOD_ID, "stone_pipe"));
        event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("iron_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.IRON), "SLS", "L L", "SLS", 'S', "ingotIron", 'L', Item.getItemFromBlock(Blocks.RAIL)).setRegistryName(MOD_ID, "iron_pipe"));
        event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("diamond_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.DIAMOND), "SLS", "L L", "SLS", 'S', "gemDiamond", 'L', Item.getItemFromBlock(Blocks.RAIL)).setRegistryName(MOD_ID, "diamond_pipe"));
        event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("gold_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.GOLD), "SLS", "L L", "SLS", 'S', "nuggetGold", 'L', Item.getItemFromBlock(Blocks.GOLDEN_RAIL)).setRegistryName(MOD_ID, "gold_pipe"));
        for(EnumDyeColor color : EnumDyeColor.values()){
            event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("wood_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.WOOD, color), "SLS", "LWL", "SLS", 'S', "stickWood", 'L', Item.getItemFromBlock(Blocks.LADDER), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "covered_wood_pipe_" + color.getMetadata()));
            event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("stone_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.STONE, color), "SLS", "LWL", "SLS", 'S', "cobblestone", 'L', Item.getItemFromBlock(Blocks.LADDER), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "covered_stone_pipe_" + color.getMetadata()));
            event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("iron_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.IRON, color), "SLS", "LWL", "SLS", 'S', "ingotIron", 'L', Item.getItemFromBlock(Blocks.RAIL), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "covered_iron_pipe_" + color.getMetadata()));
            event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("diamond_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.DIAMOND, color), "SLS", "LWL", "SLS", 'S', "gemDiamond", 'L', Item.getItemFromBlock(Blocks.RAIL), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "covered_diamond_pipe_" + color.getMetadata()));
            event.getRegistry().register(new ShapedOreRecipe(getResourceLocation("gold_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), EnumPipeMaterial.GOLD, color), "SLS", "LWL", "SLS", 'S', "nuggetGold", 'L', Item.getItemFromBlock(Blocks.GOLDEN_RAIL), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "covered_gold_pipe_" + color.getMetadata()));
            for(EnumPipeMaterial material : EnumPipeMaterial.VALUES){
                event.getRegistry().register(new ShapedOreRecipe(getResourceLocation(material.name().toLowerCase() + "_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), material, color), "PPP", "PWP", "PPP", 'P', createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), material), 'W', new ItemStack(Blocks.CARPET, 1, color.getMetadata())).setRegistryName(MOD_ID, "cover_" + material.name() + "_pipe_with_carpet_" + color.getMetadata()));
                event.getRegistry().register(new ShapedOreRecipe(getResourceLocation(material.name().toLowerCase() + "_pipe"), createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe, 8), material, color), "PPP", "PWP", "PPP", 'P', createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), material), 'W', new ItemStack(Blocks.WOOL, 1, color.getMetadata())).setRegistryName(MOD_ID, "cover_" + material.name() + "_pipe_with_wool_" + color.getMetadata()));
            }
        }
    }

    @OverridingMethodsMustInvokeSuper
    @SubscribeEvent
    protected void registerItems(RegistryEvent.Register<Item> event){
        register(event,
                of(new ItemMerchantPhone(), "merchant_phone"),
                of(new ItemPipe(), "pipe"),
                of(new ItemProcessorBase(), "processor_base"),
                of(new StrategyAcceleration.ItemAccelerateProcessor(), "strategy_acceleration"),
                of(new StrategyExtraction.ItemExtractionProcessor(), "strategy_extraction"),
                of(new StrategyOneway.ItemOnewayProcessor(), "strategy_oneway")
        );
    }

    @OverridingMethodsMustInvokeSuper
    @SubscribeEvent
    protected void registerBlocks(RegistryEvent.Register<Block> event){
        register(event,
                of(new BlockPipe(), "pipe")
        );
    }

    @OverridingMethodsMustInvokeSuper
    protected void registerTileEntities(){
        register(
                TileEntityPipe.class
        );
    }

    protected void registerBlockColors(){
    }

    protected void registerItemColors(){
    }
}
