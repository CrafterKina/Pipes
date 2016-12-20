package jp.crafterkina.pipes.api.pipe;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Kina on 2016/12/20.
 */
public interface IStrategy{
    FlowItem turn(FlowItem item, Vec3d... connecting);

    FlowItem onFilledInventoryInsertion(FlowItem item);

    IStrategy onRedstonePowered(int level);

    int redstonePower(EnumFacing side);

    void tick();
}
