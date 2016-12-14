package jp.crafterkina.pipes.client;

import jp.crafterkina.pipes.api.PipesConstants;
import jp.crafterkina.pipes.common.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.merchant_phone;

@EventBusSubscriber(value = Side.CLIENT, modid = PipesConstants.MOD_ID)
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    protected void registerItemModels(){
        ModelLoader.setCustomModelResourceLocation(merchant_phone, 0, new ModelResourceLocation(getResourceLocation("merchant_phone"), "inventory"));
    }
}
