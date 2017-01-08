package jp.crafterkina.pipes.common.block.entity;

import com.google.common.collect.Maps;
import jp.crafterkina.pipes.common.capability.wrapper.MultiTankWrapper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Kina on 2016/12/27.
 */
public class TileEntityFluidTank extends TileFluidHandler{

    public TileEntityFluidTank(){
        tank = new FluidTank(Fluid.BUCKET_VOLUME * 8);
    }

    public IFluidHandler getHandler(){
        FluidTank[] tanks = getConnectedTanks();
        FluidStack fluid = null;
        for(FluidTank t : tanks){
            fluid = t.getFluid();
            if(fluid != null) break;
        }
        return new MultiTankWrapper(fluid, tanks);
    }

    private FluidTank[] getConnectedTanks(){
        Map<BlockPos, IFluidHandler> result = Maps.newHashMap();
        result.put(pos, tank);
        for(BlockPos pos = this.pos.down(); world.getBlockState(pos).getBlock().canBeConnectedTo(world, pos, EnumFacing.UP); pos = pos.down()){
            result.put(pos, world.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
        }
        for(BlockPos pos = this.pos.up(); world.getBlockState(pos).getBlock().canBeConnectedTo(world, pos, EnumFacing.DOWN); pos = pos.up()){
            result.put(pos, world.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
        }
        return result.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).toArray(FluidTank[]::new);
    }
}
