package jp.crafterkina.pipes.api.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

/**
 * Strategy how pipes work.
 *
 * @author Kina
 * @version 1.0
 * @see net.minecraftforge.common.util.INBTSerializable
 * @since 1.0
 */
public interface IStrategy extends INBTSerializable<NBTTagCompound>{
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
     * @param item about to insert
     * @return residual items
     * @apiNote example: spawn {@link net.minecraft.entity.item.EntityItem}
     * @since 1.0
     */
    FlowItem onFilledInventoryInsertion(FlowItem item);

    /**
     * Receive changing redstone power level.
     *
     * @param level changed level
     * @return changed strategy.
     * @apiNote change your strategy on this.
     * @since 1.0
     */
    IStrategy onRedstonePowered(int level);

    /**
     * Emit redstone power.
     *
     * @param side direction
     * @return level
     * @since 1.0
     */
    int redstonePower(EnumFacing side);

    /**
     * ticking pipe
     *
     * @since 1.0
     */
    void tick();

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
