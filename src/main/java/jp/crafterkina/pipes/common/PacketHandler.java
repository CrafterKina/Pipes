package jp.crafterkina.pipes.common;

import jp.crafterkina.pipes.common.network.MessagePipeFlow;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/18.
 */
public class PacketHandler{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    static void init(){
        INSTANCE.registerMessage(MessagePipeFlow.getHandler(), MessagePipeFlow.class, 0, Side.CLIENT);
    }
}
