package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.api.render.ISpecialRenderer;
import jp.crafterkina.pipes.client.tesr.processor.ExtractionProcessorRenderer;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Created by Kina on 2016/12/23.
 */
public class StrategyOneway implements IStrategy{
    private final ItemStack stack;
    private final EnumFacing to;

    private StrategyOneway(ItemStack stack, EnumFacing to){
        this.stack = stack;
        this.to = to;
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        return new FlowItem(item.getStack(), to, item.getSpeed());
    }

    @Override
    public IStrategy rotate(EnumFacing axis){
        return new StrategyOneway(stack, to);
    }

    public static class ItemOnewayProcessor extends ItemProcessor{
        public ItemOnewayProcessor(){
            setUnlocalizedName("oneway");
            setMaxStackSize(1);
        }

        public static int getColor(ItemStack stack, int layer){
            return layer == 1 ? 0x535353 : 0xffffff;
        }

        @Override
        protected ItemStack attachItem(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
            if(!(worldIn.getTileEntity(pos) instanceof TileEntityPipe)) return ItemStack.EMPTY;
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null){
                compound = new NBTTagCompound();
                stack.setTagCompound(compound);
            }
            compound.setByte("to", (byte) facing.getIndex());
            return stack;
        }

        @Override
        protected BiFunction<ItemStack, TileEntity, IStrategy> getStrategy(){
            return (s, t) -> {
                if(!(t instanceof TileEntityPipe)) return null;
                NBTTagCompound compound = s.getTagCompound();
                if(compound == null) return null;
                EnumFacing to = EnumFacing.VALUES[compound.getByte("to")];
                return new StrategyOneway(s, to);
            };
        }

        @SuppressWarnings("MethodCallSideOnly")
        @Override
        protected boolean hasAdditionalCapability(ItemStack stack, @Nonnull Capability<?> capability, @Nullable EnumFacing facing){
            return FMLCommonHandler.instance().getSide().isClient() && hasCapabilityClient(stack, capability, facing);
        }

        @SuppressWarnings("MethodCallSideOnly")
        @Nullable
        @Override
        protected <T> T getAdditionalCapability(ItemStack stack, @Nonnull Capability<T> capability, @Nullable EnumFacing facing){
            if(FMLCommonHandler.instance().getSide().isClient())
                return getCapabilityClient(stack, capability, facing);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private boolean hasCapabilityClient(@SuppressWarnings("unused") ItemStack stack, @Nonnull Capability<?> capability, @Nullable EnumFacing facing){
            return capability == ISpecialRenderer.CAPABILITY;
        }

        @SuppressWarnings("unchecked")
        @SideOnly(Side.CLIENT)
        private <T> T getCapabilityClient(ItemStack stack, @Nonnull Capability<T> capability, @Nullable EnumFacing facing){
            if(capability == ISpecialRenderer.CAPABILITY)
                return (T) new ExtractionProcessorRenderer(stack, facing, 1);
            return null;
        }
    }
}
