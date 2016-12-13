package jp.crafterkina.pipes.server;

import jp.crafterkina.pipes.common.CommonProxy;
import jp.crafterkina.pipes.common.PipesCore;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@EventBusSubscriber(value = Side.SERVER, modid = PipesCore.MOD_ID)
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
}
