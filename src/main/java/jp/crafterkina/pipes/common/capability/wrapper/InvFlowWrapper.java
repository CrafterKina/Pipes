package jp.crafterkina.pipes.common.capability.wrapper;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by Kina on 2016/12/19.
 */
public class InvFlowWrapper implements IItemFlowHandler{
    private IItemHandler handler;

    public InvFlowWrapper(IItemHandler handler){
        this.handler = handler;
    }


    @Override
    public FlowItem flow(FlowItem item){
        ItemStack stack = item.getStack();
        for(int i = 0; i < handler.getSlots(); i++){
            stack = handler.insertItem(i, stack, false);
            if(stack.isEmpty()) return FlowItem.EMPTY;
        }
        return new FlowItem(stack, item.getVelocity());
    }
}
