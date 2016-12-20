package jp.crafterkina.pipes.api.pipe;

import com.google.common.base.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

/**
 * Created by Kina on 2016/12/20.
 */
public interface IStrategy extends INBTSerializable<NBTTagCompound>{
    FlowItem turn(FlowItem item, Vec3d... connecting);

    FlowItem onFilledInventoryInsertion(FlowItem item);

    IStrategy onRedstonePowered(int level);

    int redstonePower(EnumFacing side);

    void tick();

    interface IStrategyHandler{
        @CapabilityInject(IStrategyHandler.class)
        Capability<IStrategyHandler> CAPABILITY = null;

        boolean attach(@Nonnull ItemStack stack);

        @Nonnull
        ItemStack remove();
    }

    interface StrategySupplier extends Function<Pair<TileEntity, ItemStack>, IStrategy>{
        default IStrategy apply(Pair<TileEntity, ItemStack> pair){
            return getStrategy(pair.getLeft(), pair.getRight());
        }

        IStrategy getStrategy(TileEntity entity, ItemStack stack);
    }
}
