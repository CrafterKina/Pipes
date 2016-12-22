package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Kina on 2016/12/23.
 */
public class StrategyOneway implements IStrategy{
    private final EnumFacing to;

    private StrategyOneway(EnumFacing to){
        this.to = to;
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        return new FlowItem(item.getStack(), to, item.getSpeed());
    }
}
