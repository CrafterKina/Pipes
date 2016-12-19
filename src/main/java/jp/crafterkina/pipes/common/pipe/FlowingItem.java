package jp.crafterkina.pipes.common.pipe;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import lombok.AllArgsConstructor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by Kina on 2016/12/17.
 */
@AllArgsConstructor
public class FlowingItem implements INBTSerializable<NBTTagCompound>{
    public FlowItem item;
    public long tick;
    public boolean turned;

    public FlowingItem(NBTTagCompound compound){
        deserializeNBT(compound);
    }

    @Override
    public NBTTagCompound serializeNBT(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("item", FlowItem.toNBT(item));
        compound.setLong("tick", tick);
        compound.setBoolean("turned", turned);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt){
        item = FlowItem.fromNBT(nbt.getCompoundTag("item"));
        tick = nbt.getLong("tick");
        turned = nbt.getBoolean("turned");
    }
}
