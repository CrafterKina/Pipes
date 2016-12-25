package jp.crafterkina.pipes.client;

import jp.crafterkina.pipes.client.state.PipeStateMapper;
import jp.crafterkina.pipes.client.tesr.TileEntityPipeRenderer;
import jp.crafterkina.pipes.common.CommonProxy;
import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.block.BlockPipe;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyOneway;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    private static ModelResourceLocation getModelLocation(String name){
        return new ModelResourceLocation(getResourceLocation(name), "inventory");
    }

    @Override
    @SubscribeEvent
    protected void registerItems(RegistryEvent.Register<Item> event){
        super.registerItems(event);
    }

    @Override
    @SubscribeEvent
    protected void registerBlocks(RegistryEvent.Register<Block> event){
        super.registerBlocks(event);
    }

    @SubscribeEvent
    protected void registerModels(ModelRegistryEvent event){
        {
            ModelLoader.setCustomModelResourceLocation(RegistryEntries.ITEM.merchant_phone, 0, getModelLocation("merchant_phone"));
            ModelLoader.setCustomMeshDefinition(RegistryEntries.ITEM.pipe, s -> getModelLocation(ItemPipe.getModelName(s)));
            ModelBakery.registerItemVariants(RegistryEntries.ITEM.pipe, getModelLocation("pipe"), getModelLocation("pipe_covered"), getModelLocation("pipe_metallic"), getModelLocation("pipe_covered_metallic"));
            ModelLoader.setCustomModelResourceLocation(RegistryEntries.ITEM.processor_base, 0, getModelLocation("processor_base"));
            ModelLoader.setCustomModelResourceLocation(RegistryEntries.ITEM.strategy_acceleration, 0, getModelLocation("processor_chipped_arrow"));
            ModelLoader.setCustomModelResourceLocation(RegistryEntries.ITEM.strategy_extraction, 0, getModelLocation("processor_hopper"));
            ModelLoader.setCustomModelResourceLocation(RegistryEntries.ITEM.strategy_oneway, 0, getModelLocation("processor_pentagon"));
        }
        {
            ModelLoader.setCustomStateMapper(RegistryEntries.BLOCK.pipe, new PipeStateMapper());
        }
    }

    @Override
    protected void registerTileEntities(){
        super.registerTileEntities();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new TileEntityPipeRenderer());
    }

    @Override
    protected void registerBlockColors(){
        FMLClientHandler.instance().getClient().getBlockColors().registerBlockColorHandler(BlockPipe::getColor, RegistryEntries.BLOCK.pipe);
    }

    @Override
    protected void registerItemColors(){
        FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(ItemPipe::getColor, RegistryEntries.ITEM.pipe);
        FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(StrategyAcceleration.ItemAccelerateProcessor::getColor, RegistryEntries.ITEM.strategy_acceleration);
        FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(StrategyExtraction.ItemExtractionProcessor::getColor, RegistryEntries.ITEM.strategy_extraction);
        FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(StrategyOneway.ItemOnewayProcessor::getColor, RegistryEntries.ITEM.strategy_oneway);
    }
}
