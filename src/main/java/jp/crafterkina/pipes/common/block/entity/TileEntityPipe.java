package jp.crafterkina.pipes.common.block.entity;

import com.google.common.collect.Sets;
import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IGate;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import jp.crafterkina.pipes.common.PacketHandler;
import jp.crafterkina.pipes.common.block.BlockPipe;
import jp.crafterkina.pipes.common.capability.wrapper.InvFlowWrapper;
import jp.crafterkina.pipes.common.network.MessagePipeFlow;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import jp.crafterkina.pipes.util.NBTStreams;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
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
    private final IItemFlowHandler[] faceFlows = Arrays.stream(EnumFacing.VALUES).map(f -> new PipeFlowHandler(this)).toArray(IItemFlowHandler[]::new);
    private final IItemHandler[] faceInsertions = Arrays.stream(EnumFacing.VALUES).map(f -> new FaceInsertion(new Vec3d(f.getDirectionVec()), faceFlows[f.getIndex()])).toArray(IItemHandler[]::new);
    private final IGate[] DEFAULTS = Arrays.stream(EnumFacing.VALUES).map(
            f -> new DefaultGate(this))
            .toArray(IGate[]::new);
    public Set<FlowingItem> flowingItems = Sets.newConcurrentHashSet();
    private IGate[] GATES = Arrays.copyOf(DEFAULTS, DEFAULTS.length);

    public boolean hasGate(EnumFacing facing){
        return getGate(facing) != DEFAULTS[facing.getIndex()];
    }

    public IGate getGate(EnumFacing facing){
        return GATES[facing.getIndex()];
    }

    public void setGate(EnumFacing facing, IGate gate){
        GATES[facing.getIndex()] = gate;
    }

    public void removeGate(EnumFacing facing){
        setGate(facing, DEFAULTS[facing.getIndex()]);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        flowingItems.addAll(NBTStreams.nbtListStream(compound.getTagList("flowingItems", Constants.NBT.TAG_COMPOUND)).map(FlowingItem::new).collect(Collectors.toSet()));
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound.setTag("flowingItems", flowingItems.parallelStream().parallel().map(FlowingItem::serializeNBT).collect(NBTStreams.toNBTList()));
        return super.writeToNBT(compound);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
            return (T) faceInsertions[facing.getIndex()];
        if(capability == IItemFlowHandler.CAPABILITY && facing != null)
            return (T) faceFlows[facing.getIndex()];
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || (capability == IItemFlowHandler.CAPABILITY && facing != null) || super.hasCapability(capability, facing);
    }

    @Override
    public void update(){
        // Extract
        {

            Set<FlowingItem> remove = Sets.newHashSet();
            remove.addAll(flowingItems.parallelStream()
                    .filter(i -> (world.getTotalWorldTime() - i.tick) * i.item.getSpeed() >= 1).filter(i -> i.turned)
                    .peek(p -> {
                        BlockPos pos = this.pos.add(p.item.getDirection().xCoord, p.item.getDirection().yCoord, p.item.getDirection().zCoord);
                        TileEntity te = world.getTileEntity(pos);
                        ItemStack over = p.item.getStack();
                        IItemFlowHandler handler;
                        over:
                        {
                            if(te == null) break over;

                            if(te.hasCapability(IItemFlowHandler.CAPABILITY, p.item.getDirectionFace().getOpposite())){
                                handler = te.getCapability(IItemFlowHandler.CAPABILITY, p.item.getDirectionFace().getOpposite());
                            }else if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.item.getDirectionFace().getOpposite())){
                                handler = new InvFlowWrapper(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.item.getDirectionFace().getOpposite()));
                            }else break over;
                            if(handler == null) break over;

                            over = handler.flow(p.item);
                            if(over.isEmpty()) return;
                        }
                        EntityItem entityItem = new EntityItem(world);
                        entityItem.setPosition(this.pos.getX() + 0.5, this.pos.getY() - 0.5, this.pos.getZ() + 0.5);
                        entityItem.setEntityItemStack(over);
                        Vec3d d = p.item.getVelocity();
                        entityItem.addVelocity(d.xCoord, d.yCoord, d.zCoord);
                        world.spawnEntity(entityItem);
                    })
                    .collect(Collectors.toSet())
            );

            if(connectingDirections().length == 1)
                remove.addAll(
                        flowingItems.parallelStream()
                                .filter(i -> (world.getTotalWorldTime() - i.tick) * i.item.getSpeed() >= 1).filter(i -> !i.turned)
                                .peek(p -> {
                                    ItemStack stack = p.item.getStack();
                                    EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                                    Vec3d d = p.item.getVelocity();
                                    entityItem.addVelocity(d.xCoord, d.yCoord, d.zCoord);
                                    world.spawnEntity(entityItem);
                                }).collect(Collectors.toSet())
                );
            flowingItems.removeAll(remove);

        }
        // Turn
        flowingItems.parallelStream().filter(i -> (world.getTotalWorldTime() - i.tick) * i.item.getSpeed() >= 1).filter(i -> !i.turned).forEach(p -> {
            p.item = getGate(p.item.getDirectionFace().getOpposite()).turn(p.item);
            p.tick = world.getTotalWorldTime();
            p.turned = true;
        });

        PacketHandler.INSTANCE.sendToAll(new MessagePipeFlow(pos, flowingItems));
    }

    Vec3d[] connectingDirections(){
        @SuppressWarnings("deprecation") IBlockState state = getBlockType().getActualState(getWorld().getBlockState(getPos()), getWorld(), getPos());
        return Arrays.stream(EnumFacing.VALUES).filter(f -> state.getValue(BlockPipe.CONNECT[f.getIndex()])).map(f -> new Vec3d(f.getDirectionVec())).toArray(Vec3d[]::new);
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
        pipe.flow(new FlowItem(stack1, facing.scale(-1).scale(1 / 40d)));
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

class PipeFlowHandler implements IItemFlowHandler{
    private TileEntityPipe pipe;

    PipeFlowHandler(TileEntityPipe pipe){
        this.pipe = pipe;
    }

    @Override
    public ItemStack flow(FlowItem item){
        pipe.flowingItems.add(new FlowingItem(item, pipe.getWorld().getTotalWorldTime(), false));
        PacketHandler.INSTANCE.sendToAll(new MessagePipeFlow(pipe.getPos(), pipe.flowingItems));
        return ItemStack.EMPTY;
    }

    @Override
    public int insertableMaximumStackSizeAtOnce(){
        return 1;
    }
}

class DefaultGate implements IGate{
    private final TileEntityPipe pipe;

    DefaultGate(TileEntityPipe pipe){
        this.pipe = pipe;
    }

    @Override
    public FlowItem turn(FlowItem item){
        Vec3d[] ds = Arrays.stream(pipe.connectingDirections()).filter(d -> item.getVelocity().scale(-1).dotProduct(d) / Math.sqrt(item.getVelocity().scale(-1).lengthSquared() * d.lengthSquared()) != 1).toArray(Vec3d[]::new);
        switch(ds.length){
            case 0:
                return item;
            case 1:
                return new FlowItem(item.getStack(), ds[0].scale(item.getSpeed()));
            default:
                return new FlowItem(item.getStack(), ds[pipe.getWorld().rand.nextInt(ds.length)].scale(item.getSpeed()));
        }
    }
}
