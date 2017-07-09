package jp.crafterkina.pipes.api.pipe;

import com.google.common.base.MoreObjects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Created by Kina on 2016/12/16.
 */
@Immutable
public final class FlowItem{
    public static final FlowItem EMPTY = new FlowItem(ItemStack.EMPTY, Vec3d.ZERO);

    private final ItemStack stack;
    private final Vec3d velocity;
    private EnumFacing to;

    public FlowItem(@Nonnull ItemStack stack, @Nonnull Vec3d velocity){
        this.stack = stack;
        this.velocity = velocity;
    }

    public FlowItem(@Nonnull ItemStack stack, @Nonnull EnumFacing to, @Nonnegative double speed){
        this(stack, new Vec3d(to.getDirectionVec()).scale(speed));
        this.to = to;
    }

    public static NBTTagCompound toNBT(@Nonnull FlowItem item){
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("stack", item.getStack().serializeNBT());
        nbt.setDouble("vx", item.getVelocity().x);
        nbt.setDouble("vy", item.getVelocity().y);
        nbt.setDouble("vz", item.getVelocity().z);
        return nbt;
    }

    public static FlowItem fromNBT(@Nonnull NBTTagCompound nbt){
        if(!nbt.hasKey("stack", Constants.NBT.TAG_COMPOUND) || !nbt.hasKey("vx", Constants.NBT.TAG_DOUBLE) || !nbt.hasKey("vy", Constants.NBT.TAG_DOUBLE) || !nbt.hasKey("vz", Constants.NBT.TAG_DOUBLE))
            throw new IllegalArgumentException("Missing nbt keys.");
        return new FlowItem(new ItemStack(nbt.getCompoundTag("stack")), new Vec3d(nbt.getDouble("vx"), nbt.getDouble("vy"), nbt.getDouble("vz")));
    }

    public ItemStack getStack(){
        return stack.copy();
    }

    public Vec3d getVelocity(){
        return velocity;
    }

    public double getSpeed(){
        return velocity.lengthVector();
    }

    public Vec3d getDirection(){
        return new Vec3d(Math.signum(velocity.x), Math.signum(velocity.y), Math.signum(velocity.z));
    }

    public EnumFacing getDirectionFace(){
        if(to != null) return to;
        return to = EnumFacing.getFacingFromVector((float) velocity.x, (float) velocity.y, (float) velocity.z);
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("stack", stack)
                .add("velocity", velocity)
                .toString();
    }

    // Copied from InventoryPlayer
    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2){
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FlowItem flowItem = (FlowItem) o;
        return stackEqualExact(stack, flowItem.stack) &&
                Objects.equals(velocity, flowItem.velocity);
    }

    @Override
    public int hashCode(){
        return Objects.hash(stack, velocity);
    }
}
