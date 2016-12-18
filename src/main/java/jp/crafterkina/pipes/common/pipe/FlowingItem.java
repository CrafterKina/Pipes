package jp.crafterkina.pipes.common.pipe;

import jp.crafterkina.pipes.api.pipe.FlowItem;

/**
 * Created by Kina on 2016/12/17.
 */
public class FlowingItem{
    public FlowItem item;
    public long tick;
    public boolean turned;

    public FlowingItem(FlowItem item, long tick, boolean turned){
        this.item = item;
        this.tick = tick;
        this.turned = turned;
    }
}
