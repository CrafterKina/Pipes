package jp.crafterkina.pipes.common.capability.wrapper;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
        return new FlowItem(ItemHandlerHelper.insertItem(handler, item.getStack(), false), item.getVelocity());
    }
}
