package jp.crafterkina.pipes.common.gate;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IGate;
import jp.crafterkina.pipes.api.pipe.IItemFlowable;
import net.minecraft.util.math.Vec3d;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Kina on 2016/12/17.
 */
@Immutable
public class DefaultGate implements IGate{
    private final Random rnd;
    private final IItemFlowable pipe;

    public DefaultGate(Random rnd, IItemFlowable pipe){
        this.rnd = rnd;
        this.pipe = pipe;
    }

    @Override
    public FlowItem turn(FlowItem item){
        Vec3d[] ds = Arrays.stream(pipe.getExtractableVec()).filter(d -> item.getVelocity().scale(-1).dotProduct(d) / Math.sqrt(item.getVelocity().scale(-1).lengthSquared() * d.lengthSquared()) != 1).toArray(Vec3d[]::new);
        switch(ds.length){
            case 0:
                return item;
            case 1:
                return new FlowItem(item.getStack(), ds[0].scale(item.getSpeed()));
            default:
                return new FlowItem(item.getStack(), ds[rnd.nextInt(ds.length)].scale(item.getSpeed()));
        }
    }
}
