package jp.crafterkina.pipes.common.block.entity;

import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Kina on 2016/12/27.
 */
public class TileEntityFluidTank extends TileFluidHandler implements ITickable{
    private int prevAmount = 0;

    public TileEntityFluidTank(){
        tank = new FluidTank(Fluid.BUCKET_VOLUME * 8);
    }

    public List<IFluidHandler> getConnectedTanks(Comparator<BlockPos> comparator, EnumFacing... facings){
        Map<BlockPos, IFluidHandler> result = Maps.newHashMap();
        for(EnumFacing facing : facings){
            if(facing == null){
                result.put(pos, tank);
                continue;
            }
            for(BlockPos pos = this.pos.offset(facing); ; pos = pos.offset(facing)){
                if(!world.getBlockState(pos).getBlock().canBeConnectedTo(world, pos, facing.getOpposite())) break;
                result.put(pos, ((TileEntityFluidTank) world.getTileEntity(pos)).tank);
            }
        }
        return result.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey, comparator)).map(Map.Entry::getValue).collect(Collectors.toList());

    }

    public List<IFluidHandler> getConnectedTanks(EnumFacing... facings){
        return getConnectedTanks(Comparator.naturalOrder(), facings);
    }

    public FluidStack getContainingFluid(){
        return tank.getFluid();
    }

    public boolean onActivated(IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        ItemStack stack = playerIn.getHeldItem(hand);
        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return false;
        @Nonnull IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        FluidStack drain = handler.drain(Integer.MAX_VALUE, false);
        IFluidHandler tank = new FluidHandlerConcatenate(getConnectedTanks(EnumFacing.UP, null, EnumFacing.DOWN));
        FluidStack fluidStack = tank.drain(Integer.MAX_VALUE, false);
        if(drain == null){
            int filled = handler.fill(fluidStack, true);
            tank.drain(filled, true);
        }else if(fluidStack == null || drain.isFluidEqual(fluidStack)){
            int filled = tank.fill(drain, true);
            handler.drain(filled, true);
        }
        playerIn.setHeldItem(hand, handler.getContainer());
        refill(tank.drain(Integer.MAX_VALUE, false) == null ? new FluidHandlerConcatenate(getConnectedTanks(EnumFacing.UP, null, EnumFacing.DOWN)) : tank);
        world.notifyBlockUpdate(pos, state, state, 8);
        markDirty();
        return true;
    }

    private void refill(IFluidHandler handler){
        FluidStack drain = handler.drain(Integer.MAX_VALUE, true);
        handler.fill(drain, true);
    }

    public void onPlaced(IBlockState state, EntityLivingBase placer, ItemStack stack){
        List<IFluidHandler> tanks = getConnectedTanks(EnumFacing.UP);
        if(tanks.isEmpty()) return;
        FluidHandlerConcatenate wrapper = new FluidHandlerConcatenate(tanks);
        FluidStack drain = wrapper.drain(Integer.MAX_VALUE, true);
        tanks.add(0, tank);
        wrapper = new FluidHandlerConcatenate(tanks);
        wrapper.fill(drain, true);
        markDirty();
    }

    @Override
    public void update(){
        prevAmount = getContainingFluid() == null ? 0 : getContainingFluid().amount;
    }

    public float amountForRender(float partialTicks){
        int result = getContainingFluid() == null ? 0 : getContainingFluid().amount;
        return prevAmount + (result - prevAmount) * partialTicks;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) new FluidHandlerConcatenate(getConnectedTanks(EnumFacing.UP, null, EnumFacing.DOWN));
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        return super.writeToNBT(tag);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag(){
        return serializeNBT();
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 1, nbtTagCompound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean hasFastRenderer(){
        return true;
    }
}
