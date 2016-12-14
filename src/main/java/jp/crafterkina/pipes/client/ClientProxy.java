package jp.crafterkina.pipes.client;

import jp.crafterkina.pipes.client.tesr.TileEntityPipeRenderer;
import jp.crafterkina.pipes.common.CommonProxy;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.merchant_phone;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    protected void registerItems(RegistryEvent.Register<Item> event){
        super.registerItems(event);
        ModelLoader.setCustomModelResourceLocation(merchant_phone, 0, new ModelResourceLocation(getResourceLocation("merchant_phone"), "inventory"));
    }

    @Override
    protected void registerBlocks(RegistryEvent.Register<Block> event){
        super.registerBlocks(event);
    }

    @Override
    protected void registerTileEntities(){
        super.registerTileEntities();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new TileEntityPipeRenderer());
    }
}
