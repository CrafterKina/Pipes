package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Created by Kina on 2016/12/20.
 */
public class StrategyDefault implements IStrategy{
    Supplier<World> world;

    public StrategyDefault(Supplier<World> world){
        this.world = world;
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        if(world.get() == null){
            return item;
        }
        Vec3d[] ds = Arrays.stream(connecting).filter(d -> item.getVelocity().scale(-1).dotProduct(d) / Math.sqrt(item.getVelocity().scale(-1).lengthSquared() * d.lengthSquared()) != 1).toArray(Vec3d[]::new);
        switch(ds.length){
            case 0:
                return item;
            case 1:
                return new FlowItem(item.getStack(), ds[0].scale(item.getSpeed()));
            default:
                return new FlowItem(item.getStack(), ds[world.get().rand.nextInt(ds.length)].scale(item.getSpeed()));
        }
    }
}
