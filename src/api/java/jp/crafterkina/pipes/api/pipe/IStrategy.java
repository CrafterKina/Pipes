package jp.crafterkina.pipes.api.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;

/**
 * Strategy how pipes work.
 *
 * @author Kina
 * @version 1.0
 * @since 1.0
 */
public interface IStrategy{
    /**
     * This method will be called on turning direction of flowing items.
     *
     * @param item       about to turn
     * @param connecting which direction the pipe connected.
     * @return turned item state.
     * @since 1.0
     */
    FlowItem turn(FlowItem item, Vec3d... connecting);

    /**
     * This method called on inserting to a filled inventory.
     *
     * @implSpec reverse flowing direction.
     * @param item about to insert
     * @return residual items
     * @apiNote example: spawn {@link net.minecraft.entity.item.EntityItem}
     * @since 1.0
     */
    default FlowItem onFilledInventoryInsertion(FlowItem item){
        return new FlowItem(item.getStack(), item.getVelocity().scale(-1));
    }

    /**
     * Receive changing redstone power level.
     *
     * @implSpec return this, not change.
     * @param level changed level
     * @return changed strategy.
     * @apiNote change your strategy on this.
     * @since 1.0
     */
    default IStrategy onRedstonePowered(int level){
        return this;
    }

    /**
     * Emit redstone power.
     *
     * @implSpec It does not emit a signal under any condition.
     * @param side direction
     * @return level
     * @since 1.0
     */
    default int redstonePower(EnumFacing side){
        return 0;
    }

    /**
     * ticking pipe
     *
     * @implSpec no-operation.
     * @since 1.0
     */
    default void tick(){
    }

    /**
     * Rotate this strategy.
     *
     * @param axis The axis to rotate around
     * @return rotated strategy
     * @implSpec The default implementation is not rotate, just return this.
     * @see net.minecraft.block.Block#rotateBlock(World, BlockPos, EnumFacing)
     */
    default IStrategy rotate(EnumFacing axis){
        return this;
    }

    /**
     * Capability to handle strategy.
     *
     * @author Kina
     * @version 1.0
     * @see Capability
     * @since 1.0
     */
    interface IStrategyHandler{
        @CapabilityInject(IStrategyHandler.class)
        Capability<IStrategyHandler> CAPABILITY = null;

        /**
         * Attach with stack.
         *
         * @param stack attach
         * @return success to true.
         * @since 1.0
         */
        boolean attach(@Nonnull ItemStack stack);

        /**
         * Remove strategy.
         *
         * @return
         * @since 1.0
         */
        @Nonnull
        ItemStack remove();
    }

    /**
     * In other words, stack-strategy converter.
     *
     * @author Kina
     * @version 1.0
     * @since 1.0
     */
    interface StrategySupplier{
        IStrategy getStrategy(TileEntity entity, ItemStack stack);
    }
}
