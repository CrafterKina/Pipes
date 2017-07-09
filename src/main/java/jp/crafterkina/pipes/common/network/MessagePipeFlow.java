package jp.crafterkina.pipes.common.network;

import io.netty.buffer.ByteBuf;
import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kina on 2016/12/18.
 */
@NoArgsConstructor
@AllArgsConstructor
public class MessagePipeFlow implements IMessage{
    BlockPos pos;
    Set<FlowingItem> flowingItems;

    public static MessagePipeFlowHandler getHandler(){
        return new MessagePipeFlowHandler();
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer pck = new PacketBuffer(buf);
        pos = pck.readBlockPos();
        int size = pck.readInt();
        flowingItems = new HashSet<>(size);
        for(int i = 0; i < size; i++){
            NBTTagCompound stackTag = null;
            try{
                stackTag = pck.readCompoundTag();
            }catch(IOException e){
                new IllegalArgumentException("Broken Message.", e).printStackTrace();
            }
            if(stackTag == null){
                //Garbage Data
                pck.readDouble();
                pck.readDouble();
                pck.readDouble();
                pck.readLong();
                pck.readBoolean();
                continue;
            }

            flowingItems.add(new FlowingItem(new FlowItem(new ItemStack(stackTag), new Vec3d(pck.readDouble(), pck.readDouble(), pck.readDouble())), pck.readLong(), pck.readBoolean()));
        }
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer pck = new PacketBuffer(buf);
        pck.writeBlockPos(pos);
        pck.writeInt(flowingItems.size());
        for(FlowingItem item : flowingItems){
            pck.writeCompoundTag(item.item.getStack().serializeNBT());
            pck.writeDouble(item.item.getVelocity().x);
            pck.writeDouble(item.item.getVelocity().y);
            pck.writeDouble(item.item.getVelocity().z);
            pck.writeLong(item.tick);
            pck.writeBoolean(item.turned);
        }
    }
}

class MessagePipeFlowHandler implements IMessageHandler<MessagePipeFlow, IMessage>{
    @SuppressWarnings("MethodCallSideOnly")
    @Override
    public IMessage onMessage(MessagePipeFlow mes, MessageContext ctx){
        if(Minecraft.getMinecraft().world == null) return null;
        TileEntity te = Minecraft.getMinecraft().world.getTileEntity(mes.pos);
        if(te instanceof TileEntityPipe){
            TileEntityPipe pipe = (TileEntityPipe) te;
            pipe.flowingItems = mes.flowingItems;
        }
        return null;
    }
}
