package jp.crafterkina.pipes.common.block.entity;

import com.google.common.collect.Sets;
import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IGate;
import jp.crafterkina.pipes.api.pipe.IItemFlowable;
import jp.crafterkina.pipes.common.PacketHandler;
import jp.crafterkina.pipes.common.gate.DefaultGate;
import jp.crafterkina.pipes.common.network.MessagePipeFlow;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Kina on 2016/12/14.
 */
public class TileEntityPipe extends TileEntity implements IItemFlowable, ITickable{
    @SideOnly(Side.CLIENT)
    public Set<Pair<ItemStack, Vec3d>> clientItems = Collections.emptySet();
    private IItemHandler[] FACES = Arrays.stream(EnumFacing.VALUES).map(f -> new FaceInsertion(new Vec3d(f.getDirectionVec()), this)).toArray(FaceInsertion[]::new);
    private IGate[] DEFAULTS = Arrays.stream(EnumFacing.VALUES).map(
            f -> new DefaultGate(new Random(), this))
            .toArray(IGate[]::new);
    private IGate[] GATES = Arrays.copyOf(DEFAULTS, DEFAULTS.length);
    private Set<FlowingItem> flowingItems = Sets.newConcurrentHashSet();

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

    public Set<FlowingItem> getFlowingItems(){
        return Collections.unmodifiableSet(flowingItems);
    }

    public void setFlowingItems(Set<FlowingItem> items){
        flowingItems = items;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){

        return super.writeToNBT(compound);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
            return (T) FACES[facing.getIndex()];
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || super.hasCapability(capability, facing);
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
                        if(te instanceof IItemFlowable){
                            IItemFlowable pipe = (IItemFlowable) te;
                            pipe.flow(p.item);
                        }else if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.item.getDirectionFace().getOpposite())){
                            IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.item.getDirectionFace().getOpposite());
                            assert handler != null;
                            ItemStack stack = p.item.getStack();
                            for(int i = 0; i < handler.getSlots(); i++){
                                stack = handler.insertItem(i, stack, false);
                                if(stack.isEmpty()) return;
                            }
                            EntityItem entityItem = new EntityItem(world);
                            entityItem.setPosition(this.pos.getX() + 0.5, this.pos.getY() - 0.5, this.pos.getZ() + 0.5);
                            entityItem.setEntityItemStack(stack);
                            Vec3d d = p.item.getVelocity();
                            entityItem.addVelocity(d.xCoord, d.yCoord, d.zCoord);
                            world.spawnEntity(entityItem);
                        }
                    })
                    .collect(Collectors.toSet())
            );

            if(getExtractableVec().length == 1)
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

    @Override
    public void flow(FlowItem item){
        flowingItems.add(new FlowingItem(item, world.getTotalWorldTime(), false));
        PacketHandler.INSTANCE.sendToAll(new MessagePipeFlow(pos, flowingItems));
    }

    @Override
    public Vec3d[] getExtractableVec(){
        return Arrays.stream(EnumFacing.VALUES).filter(f -> getBlockType().canBeConnectedTo(world, pos, f)).map(f -> new Vec3d(f.getDirectionVec())).toArray(Vec3d[]::new);
    }

    @Override
    public Vec3d[] getInsertableVec(){
        return Arrays.stream(EnumFacing.VALUES).filter(f -> getBlockType().canBeConnectedTo(world, pos, f)).map(f -> new Vec3d(f.getDirectionVec())).toArray(Vec3d[]::new);
    }

    @Override
    public int insertableMaximumStackSizeAtOnce(){
        return 1;
    }
}

class FaceInsertion implements IItemHandler{
    private final Vec3d facing;
    private final IItemFlowable pipe;

    FaceInsertion(Vec3d facing, IItemFlowable pipe){
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
