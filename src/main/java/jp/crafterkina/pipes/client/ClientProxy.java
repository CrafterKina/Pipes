package jp.crafterkina.pipes.client;

import jp.crafterkina.pipes.common.CommonProxy;
import jp.crafterkina.pipes.common.PipesCore;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(value = Side.CLIENT, modid = PipesCore.MOD_ID)
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
}
