package jp.crafterkina.pipes.common.network;

import io.netty.buffer.ByteBuf;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

/**
 * Created by Kina on 2017/01/22.
 */
@NoArgsConstructor
@AllArgsConstructor
public class MessagePipeState implements IMessage{
    BlockPos pos;
    EnumPipeMaterial material;
    int color;
    ItemStack processor;

    public static MessagePipeStateHandler getHandler(){
        return new MessagePipeStateHandler();
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer pck = new PacketBuffer(buf);
        pos = pck.readBlockPos();
        material = EnumPipeMaterial.valueOf(pck.readString(Short.MAX_VALUE));
        color = pck.readInt();
        try{
            processor = pck.readItemStack();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer pck = new PacketBuffer(buf);
        pck.writeBlockPos(pos);
        pck.writeString(material.name());
        pck.writeInt(color);
        pck.writeItemStack(processor);
    }
}

class MessagePipeStateHandler implements IMessageHandler<MessagePipeState, IMessage>{
    @SuppressWarnings({"MethodCallSideOnly"})
    @Override
    public IMessage onMessage(MessagePipeState mes, MessageContext ctx){
        if(Minecraft.getMinecraft().world == null) return null;
        TileEntity te = Minecraft.getMinecraft().world.getTileEntity(mes.pos);
        if(te instanceof TileEntityPipe){
            TileEntityPipe pipe = (TileEntityPipe) te;
            pipe.coverColor = mes.color;
            pipe.material = mes.material;
            pipe.processor = mes.processor;
        }
        return null;
    }
}
