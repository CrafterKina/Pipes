package jp.crafterkina.pipes.common.block.entity;

import com.google.common.collect.Sets;
import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.common.PacketHandler;
import jp.crafterkina.pipes.common.block.BlockPipe;
import jp.crafterkina.pipes.common.capability.wrapper.InvFlowWrapper;
import jp.crafterkina.pipes.common.network.MessagePipeFlow;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyDefault;
import jp.crafterkina.pipes.util.NBTStreams;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Kina on 2016/12/14.
 */
public class TileEntityPipe extends TileEntity implements ITickable{
    private final IItemFlowHandler[] faceFlows = Arrays.stream(EnumFacing.VALUES).map(f -> new PipeFlowHandler()).toArray(IItemFlowHandler[]::new);
    private final IItemHandler[] faceInsertions = Arrays.stream(EnumFacing.VALUES).map(f -> new FaceInsertion(new Vec3d(f.getDirectionVec()), faceFlows[f.getIndex()])).toArray(IItemHandler[]::new);
    private final IStrategy.IStrategyHandler processorHandler = new StrategyHandler();
    private final IStrategy DEFAULT_STRATEGY = new StrategyDefault(this::getWorld);
    public Set<FlowingItem> flowingItems = Sets.newConcurrentHashSet();
    public int coverColor = -1;
    private IStrategy strategy = DEFAULT_STRATEGY;
    private ItemStack processor = ItemStack.EMPTY;

    public static ItemStack getProcessorStack(TileEntityPipe pipe){
        return pipe.processor.copy();
    }

    private boolean hasProcessor(){
        return !processor.isEmpty();
    }

    private boolean setProcessor(@Nonnull ItemStack processor){
        this.processor = processor;
        strategy = processor.getItem() instanceof IStrategy.StrategySupplier ? ((IStrategy.StrategySupplier) processor.getItem()).getStrategy(this, processor) : DEFAULT_STRATEGY;
        strategy = strategy == null ? DEFAULT_STRATEGY : strategy;
        return true;
    }

    public IStrategy getStrategy(){
        return strategy;
    }

    public boolean covered(){
        return coverColor != -1;
    }

    public boolean recolor(int color){
        if(!covered()) return false;
        coverColor = color;
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        flowingItems.addAll(NBTStreams.nbtListStream(compound.getTagList("flowingItems", Constants.NBT.TAG_COMPOUND)).map(FlowingItem::new).collect(Collectors.toSet()));
        setProcessor(compound.hasKey("processor", Constants.NBT.TAG_COMPOUND) ? new ItemStack(compound.getCompoundTag("processor")) : ItemStack.EMPTY);
        coverColor = compound.hasKey("CoverColor", Constants.NBT.TAG_INT) ? compound.getInteger("CoverColor") : -1;
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound.setTag("flowingItems", flowingItems.parallelStream().parallel().map(FlowingItem::serializeNBT).collect(NBTStreams.toNBTList()));
        if(hasProcessor()) compound.setTag("processor", processor.serializeNBT());
        compound.setInteger("CoverColor", coverColor);
        return super.writeToNBT(compound);
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 1, nbtTagCompound);
    }

    @Nonnull
    public NBTTagCompound getUpdateTag(){
        return serializeNBT();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
            return (T) faceInsertions[facing.getIndex()];
        if(capability == IItemFlowHandler.CAPABILITY && facing != null)
            return (T) faceFlows[facing.getIndex()];
        if(capability == IStrategy.IStrategyHandler.CAPABILITY)
            return (T) processorHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || (capability == IItemFlowHandler.CAPABILITY && facing != null) || capability == IStrategy.IStrategyHandler.CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public void update(){
        if(world == null) return;

        // Extract
        {

            Set<FlowingItem> remove = Sets.newHashSet();
            Set<FlowItem> overs = Sets.newHashSet();
            remove.addAll(flowingItems.parallelStream()
                    .filter(i -> (world.getTotalWorldTime() - i.tick) * i.item.getSpeed() >= 1).filter(i -> i.turned)
                    .peek(p -> {
                        BlockPos pos = this.pos.add(p.item.getDirection().xCoord, p.item.getDirection().yCoord, p.item.getDirection().zCoord);
                        TileEntity te = world.getTileEntity(pos);
                        FlowItem over;
                        IItemFlowHandler handler = getFlowHandlerFromTileEntity(te, p.item.getDirectionFace().getOpposite());
                        if(handler == null){
                            Block.spawnAsEntity(getWorld(), getPos(), p.item.getStack());
                            return;
                        }

                        over = handler.flow(p.item);
                        if(over.getStack().isEmpty()) return;
                        overs.add(over);
                    })
                    .collect(Collectors.toSet())
            );

            flowingItems.removeAll(remove);
            flowingItems.addAll(overs.parallelStream().map(strategy::onFilledInventoryInsertion).filter(f -> !f.getStack().isEmpty()).map(f -> new FlowingItem(f, world.getTotalWorldTime(), false)).collect(Collectors.toSet()));

        }
        // Turn
        flowingItems.parallelStream().filter(i -> (world.getTotalWorldTime() - i.tick) * i.item.getSpeed() >= 1).filter(i -> !i.turned).forEach(p -> {
            p.item = strategy.turn(p.item, connectingDirections());
            p.tick = world.getTotalWorldTime();
            p.turned = true;
        });

        flowingItems.removeAll(flowingItems.parallelStream().filter(i -> i.item.getStack().isEmpty()).collect(Collectors.toSet()));

        getWorld().updateComparatorOutputLevel(pos, getBlockType());
        if(FMLCommonHandler.instance().getSide().isClient())
            //noinspection deprecation
            world.notifyBlockUpdate(pos, world.getBlockState(pos), getBlockType().getActualState(world.getBlockState(pos), world, pos), 8);
        if(!getWorld().isRemote)
            PacketHandler.INSTANCE.sendToAll(new MessagePipeFlow(pos, flowingItems));
        strategy.tick();
    }

    @Override
    protected void setWorldCreate(World worldIn){
        setWorld(worldIn);
    }

    private Vec3d[] connectingDirections(){
        @SuppressWarnings("deprecation") IBlockState state = getBlockType().getActualState(getWorld().getBlockState(getPos()), getWorld(), getPos());
        return Arrays.stream(EnumFacing.VALUES).filter(f -> state.getValue(BlockPipe.CONNECT[f.getIndex()])).map(f -> new Vec3d(f.getDirectionVec())).toArray(Vec3d[]::new);
    }

    @Nullable
    private IItemFlowHandler getFlowHandlerFromTileEntity(TileEntity te, EnumFacing facing){
        IItemFlowHandler result = null;
        if(te == null) return null;
        if(te.hasCapability(IItemFlowHandler.CAPABILITY, facing)){
            result = te.getCapability(IItemFlowHandler.CAPABILITY, facing);
        }else if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)){
            result = new InvFlowWrapper(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing));
        }
        return result;
    }

    public void onRedstonePowered(int level){
        strategy.onRedstonePowered(level);
    }

    public int getWeakPower(EnumFacing side){
        return strategy.redstonePower(side);
    }

    // Split to IStrategy?
    public int getComparatorPower(){
        return Math.min(flowingItems.size(), 15);
    }

    public void dropItems(){
        flowingItems.parallelStream().map(i -> i.item.getStack()).forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        if(processor != null) InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), processor);
    }

    //TODO Split to Handler
    public double getBaseSpeed(){
        return 1 / 40d;
    }

    public boolean rotateProcessor(EnumFacing axis){
        IStrategy cache = this.strategy;
        strategy = strategy.rotate(axis);
        return cache != strategy;
    }

    /* Internal Classes */

    class PipeFlowHandler implements IItemFlowHandler{
        @Override
        public FlowItem flow(FlowItem item){
            TileEntityPipe.this.flowingItems.add(new FlowingItem(item, TileEntityPipe.this.getWorld().getTotalWorldTime(), false));
            if(!getWorld().isRemote)
                PacketHandler.INSTANCE.sendToAll(new MessagePipeFlow(TileEntityPipe.this.getPos(), TileEntityPipe.this.flowingItems));
            return FlowItem.EMPTY;
        }

        @Override
        public int insertableMaximumStackSizeAtOnce(){
            return 1;
        }
    }

    class StrategyHandler implements IStrategy.IStrategyHandler{
        @Override
        public boolean attach(@Nonnull ItemStack stack){
            return !hasProcessor() && TileEntityPipe.this.setProcessor(stack.splitStack(1).copy());
        }

        @Nonnull
        @Override
        public ItemStack remove(){
            ItemStack result = TileEntityPipe.this.processor;
            TileEntityPipe.this.setProcessor(ItemStack.EMPTY);
            return result;
        }
    }

    class FaceInsertion implements IItemHandler{
        private final Vec3d facing;
        private final IItemFlowHandler pipe;

        FaceInsertion(Vec3d facing, IItemFlowHandler pipe){
            this.facing = facing.lengthSquared() == 1 ? facing : facing.normalize();
            this.pipe = pipe;
        }

        @Override
        public int getSlots(){
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot){
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
            ItemStack stack1 = (stack = simulate ? stack.copy() : stack).splitStack(Math.min(stack.getCount(), pipe.insertableMaximumStackSizeAtOnce()));
            if(simulate) return stack;
            pipe.flow(new FlowItem(stack1, facing.scale(-1).scale(TileEntityPipe.this.getBaseSpeed())));
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot){
            return 64;
        }
    }
}