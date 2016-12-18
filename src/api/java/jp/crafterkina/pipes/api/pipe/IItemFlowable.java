package jp.crafterkina.pipes.api.pipe;

import net.minecraft.util.math.Vec3d;

/**
 * Created by Kina on 2016/12/16.
 */
public interface IItemFlowable{
    void flow(FlowItem item);

    Vec3d[] getExtractableVec();

    Vec3d[] getInsertableVec();

    int insertableMaximumStackSizeAtOnce();
}
