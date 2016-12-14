package jp.crafterkina.pipes.server;

import jp.crafterkina.pipes.api.PipesConstants;
import jp.crafterkina.pipes.common.CommonProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@EventBusSubscriber(value = Side.SERVER, modid = PipesConstants.MOD_ID)
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
}
