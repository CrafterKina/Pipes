package jp.crafterkina.pipes.common.capability.wrapper;

import lombok.AllArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * Created by Kina on 2017/01/09.
 */
@Immutable
@AllArgsConstructor
public class MultiTankWrapper implements IFluidHandler{
    private final FluidStack fluid;
    private final IFluidHandler[] tanks;

    @Override
    public IFluidTankProperties[] getTankProperties(){
        return Arrays.stream(tanks).map(IFluidHandler::getTankProperties).toArray(IFluidTankProperties[]::new);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill){
        if(fluid != null && !resource.isFluidEqual(fluid)){
            return 0;
        }
        int filled = 0;
        for(IFluidHandler handler : tanks){
            if(filled >= resource.amount) break;
            filled += handler.fill(new FluidStack(resource, resource.amount - filled), doFill);
        }
        return filled;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain){
        if(fluid != null && !resource.isFluidEqual(fluid)){
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain){
        int drainAmount = Math.min(Arrays.stream(tanks).mapToInt(t -> t.drain(Integer.MAX_VALUE, false).amount).sum(), maxDrain);
        if(fluid == null) return null;
        FluidStack drained = fluid.copy();
        drained.amount = drainAmount;
        if(!doDrain) return drained;
        Arrays.stream(tanks).forEach(t -> t.drain(drained, true));
        return drained;
    }
}
